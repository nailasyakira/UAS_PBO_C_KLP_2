package supermarket.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import supermarket.exception.AppException;
import supermarket.exception.DataNotFoundException;
import supermarket.exception.OutOfStockException;
import supermarket.model.Customer;
import supermarket.model.PaymentMethod;
import supermarket.model.Product;
import supermarket.model.Transaction;
import supermarket.service.ProductService;
import supermarket.service.TransactionService;

/** Panel belanja untuk customer: cari/urutkan barang, isi keranjang, lalu checkout. */
public class ShopPanel extends JPanel implements Refreshable {

    private final Customer customer;
    private final ProductService productService;
    private final TransactionService transactionService;

    private final DefaultTableModel productModel =
            GuiUtil.readOnlyModel("ID", "Kode", "Nama", "Kategori", "Harga", "Stok");
    private final JTable productTable = GuiUtil.createTable(productModel);
    private final DefaultTableModel cartModel = GuiUtil.readOnlyModel("ID", "Nama", "Harga", "Jumlah", "Subtotal");
    private final JTable cartTable = GuiUtil.createTable(cartModel);

    private final JTextField searchField = new JTextField(16);
    private final JComboBox<String> sortCombo =
            new JComboBox<>(new String[] {"Urutan awal", "Harga termurah", "Harga termahal"});
    private final JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final JComboBox<PaymentMethod> paymentCombo = new JComboBox<>(PaymentMethod.values());
    private final JLabel totalLabel = new JLabel("Total: Rp0");

    private final Map<Integer, Integer> cart = new LinkedHashMap<>(); // ID barang -> jumlah
    private List<Product> shown = new ArrayList<>(); // barang yang tampil, urutannya sama dengan baris tabel

    /**
     * Membuat panel belanja.
     *
     * @param customer           pelanggan yang sedang login
     * @param productService     service barang
     * @param transactionService service transaksi
     */
    public ShopPanel(Customer customer, ProductService productService, TransactionService transactionService) {
        super(new BorderLayout(8, 8));
        this.customer = customer;
        this.productService = productService;
        this.transactionService = transactionService;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GuiUtil.alignRight(productTable, 4, 5);
        GuiUtil.alignRight(cartTable, 2, 3, 4);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createProductSection(), createCartSection());
        split.setResizeWeight(0.55);

        add(createSearchBar(), BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        refresh();
    }

    /** {@inheritDoc} */
    @Override
    public void refresh() {
        loadProducts();
        reloadCart();
    }

    /**
     * Membuat baris cari dan urutkan di bagian atas.
     *
     * @return panel pencarian
     */
    private JPanel createSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> loadProducts());
        searchField.addActionListener(e -> loadProducts());
        sortCombo.addActionListener(e -> loadProducts());
        bar.add(new JLabel("Cari nama barang:"));
        bar.add(searchField);
        bar.add(searchButton);
        bar.add(new JLabel("   Urutkan:"));
        bar.add(sortCombo);
        return bar;
    }

    /**
     * Membuat bagian tabel barang beserta tombol tambah ke keranjang.
     *
     * @return panel daftar barang
     */
    private JPanel createProductSection() {
        JPanel section = new JPanel(new BorderLayout(4, 4));
        section.setBorder(BorderFactory.createTitledBorder("Daftar Barang"));
        JButton addButton = new JButton("Tambah ke Keranjang");
        addButton.addActionListener(e -> GuiUtil.safeRun(this, this::addToCart));
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bar.add(new JLabel("Jumlah:"));
        bar.add(qtySpinner);
        bar.add(addButton);
        section.add(new JScrollPane(productTable), BorderLayout.CENTER);
        section.add(bar, BorderLayout.SOUTH);
        return section;
    }

    /**
     * Membuat bagian keranjang beserta pilihan pembayaran dan tombol checkout.
     *
     * @return panel keranjang
     */
    private JPanel createCartSection() {
        JPanel section = new JPanel(new BorderLayout(4, 4));
        section.setBorder(BorderFactory.createTitledBorder("Keranjang"));

        JButton removeButton = new JButton("Hapus dari Keranjang");
        removeButton.addActionListener(e -> GuiUtil.safeRun(this, this::removeFromCart));
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> GuiUtil.safeRun(this, this::checkout));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(removeButton);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(totalLabel);
        right.add(new JLabel("  Metode bayar:"));
        right.add(paymentCombo);
        right.add(checkoutButton);

        JPanel bar = new JPanel(new BorderLayout());
        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        section.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        section.add(bar, BorderLayout.SOUTH);
        return section;
    }

    /** Memuat daftar barang sesuai kata kunci dan urutan yang dipilih. */
    private void loadProducts() {
        shown = productService.search(searchField.getText().trim());
        int sort = sortCombo.getSelectedIndex();
        if (sort > 0) {
            shown = productService.sortByPrice(shown, sort == 1);
        }
        productModel.setRowCount(0);
        for (Product p : shown) {
            productModel.addRow(new Object[] {p.getId(), p.getCode(), p.getName(), p.getCategory(),
                GuiUtil.rupiah(p.getPrice()), p.getStock()});
        }
    }

    /** Menampilkan ulang isi keranjang dan total; barang yang sudah dihapus admin dibuang dari keranjang. */
    private void reloadCart() {
        cartModel.setRowCount(0);
        long total = 0;
        Iterator<Map.Entry<Integer, Integer>> it = cart.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> entry = it.next();
            try {
                Product p = productService.getById(entry.getKey());
                long subtotal = p.getPrice() * entry.getValue();
                total += subtotal;
                cartModel.addRow(new Object[] {p.getId(), p.getName(), GuiUtil.rupiah(p.getPrice()),
                    entry.getValue(), GuiUtil.rupiah(subtotal)});
            } catch (DataNotFoundException e) {
                it.remove(); // barang sudah dihapus admin
            }
        }
        totalLabel.setText("Total: " + GuiUtil.rupiah(total));
    }

    /**
     * Menambahkan barang terpilih ke keranjang sebanyak jumlah di spinner.
     *
     * @throws AppException jika jumlah di keranjang akan melebihi stok
     */
    private void addToCart() throws AppException {
        Product product = shown.get(GuiUtil.requireSelection(productTable));
        int quantity = (Integer) qtySpinner.getValue();
        int total = cart.containsKey(product.getId()) ? cart.get(product.getId()) + quantity : quantity;
        if (!product.hasStock(total)) {
            throw new OutOfStockException(product.getName(), product.getStock());
        }
        cart.put(product.getId(), total);
        reloadCart();
    }

    /** Menghapus baris terpilih dari keranjang. */
    private void removeFromCart() {
        int row = GuiUtil.requireSelection(cartTable);
        int productId = new ArrayList<>(cart.keySet()).get(row);
        cart.remove(productId);
        reloadCart();
    }

    /**
     * Memproses pembelian, mengosongkan keranjang, lalu menampilkan invoice.
     *
     * @throws AppException jika stok tidak cukup atau barang tidak ditemukan
     */
    private void checkout() throws AppException {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Keranjang masih kosong.");
        }
        PaymentMethod method = (PaymentMethod) paymentCombo.getSelectedItem();
        Transaction trx = transactionService.checkout(customer, new LinkedHashMap<>(cart), method);
        cart.clear();
        refresh(); // stok barang sudah berkurang
        GuiUtil.showInvoice(this, trx);
    }
}
