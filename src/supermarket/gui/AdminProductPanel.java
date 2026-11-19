package supermarket.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import supermarket.exception.AppException;
import supermarket.model.Product;
import supermarket.service.ProductService;

/** Panel admin untuk mengelola barang: cari, tambah, edit, dan hapus. */
public class AdminProductPanel extends JPanel implements Refreshable {

    private final ProductService productService;
    private final DefaultTableModel model = GuiUtil.readOnlyModel("ID", "Kode", "Nama", "Kategori", "Harga", "Stok");
    private final JTable table = GuiUtil.createTable(model);
    private final JTextField searchField = new JTextField(18);
    private List<Product> shown = new ArrayList<>(); // barang yang tampil, urutannya sama dengan baris tabel

    /**
     * Membuat panel barang untuk admin.
     *
     * @param productService service barang
     */
    public AdminProductPanel(ProductService productService) {
        super(new BorderLayout(8, 8));
        this.productService = productService;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GuiUtil.alignRight(table, 4, 5);
        add(createSearchBar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createActionBar(), BorderLayout.SOUTH);
        refresh();
    }

    /** {@inheritDoc} */
    @Override
    public void refresh() {
        shown = productService.search(searchField.getText().trim());
        model.setRowCount(0);
        for (Product p : shown) {
            model.addRow(new Object[] {p.getId(), p.getCode(), p.getName(), p.getCategory(),
                GuiUtil.rupiah(p.getPrice()), p.getStock()});
        }
    }

    /**
     * Membuat baris pencarian di atas tabel.
     *
     * @return panel pencarian
     */
    private JPanel createSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("Cari");
        JButton allButton = new JButton("Tampilkan Semua");
        searchButton.addActionListener(e -> refresh());
        searchField.addActionListener(e -> refresh()); // Enter di kolom cari
        allButton.addActionListener(e -> {
            searchField.setText("");
            refresh();
        });
        bar.add(new JLabel("Cari nama barang:"));
        bar.add(searchField);
        bar.add(searchButton);
        bar.add(allButton);
        return bar;
    }

    /**
     * Membuat baris tombol Tambah / Edit / Hapus di bawah tabel.
     *
     * @return panel tombol
     */
    private JPanel createActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Tambah Barang");
        JButton editButton = new JButton("Edit Barang");
        JButton deleteButton = new JButton("Hapus Barang");
        addButton.addActionListener(e -> GuiUtil.safeRun(this, this::addProduct));
        editButton.addActionListener(e -> GuiUtil.safeRun(this, this::editProduct));
        deleteButton.addActionListener(e -> GuiUtil.safeRun(this, this::deleteProduct));
        bar.add(addButton);
        bar.add(editButton);
        bar.add(deleteButton);
        return bar;
    }

    /** Menampilkan form barang baru lalu menyimpannya. */
    private void addProduct() {
        ProductForm form = new ProductForm(null);
        if (form.showDialog(this, "Tambah Barang")) {
            productService.addProduct(form.getCode(), form.getName(), form.getCategory(),
                    form.getPrice(), form.getStock());
            refresh();
        }
    }

    /**
     * Menampilkan form edit untuk barang yang dipilih.
     *
     * @throws AppException jika barang sudah tidak ada
     */
    private void editProduct() throws AppException {
        Product product = shown.get(GuiUtil.requireSelection(table));
        ProductForm form = new ProductForm(product);
        if (form.showDialog(this, "Edit Barang")) {
            productService.updateProduct(product.getId(), form.getName(), form.getCategory(),
                    form.getPrice(), form.getStock());
            refresh();
        }
    }

    /**
     * Menghapus barang yang dipilih setelah konfirmasi.
     *
     * @throws AppException jika barang sudah tidak ada
     */
    private void deleteProduct() throws AppException {
        Product product = shown.get(GuiUtil.requireSelection(table));
        if (GuiUtil.confirm(this, "Yakin hapus '" + product.getName() + "'?")) {
            productService.deleteProduct(product.getId());
            refresh();
        }
    }

    /** Form isian barang (dipakai untuk tambah dan edit). */
    private static class ProductForm {

        private final JTextField codeField = new JTextField(16);
        private final JTextField nameField = new JTextField(16);
        private final JTextField categoryField = new JTextField(16);
        private final JTextField priceField = new JTextField(16);
        private final JTextField stockField = new JTextField(16);
        private final JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));

        /**
         * Membuat form; jika {@code existing} diisi, form terisi data barang itu (mode edit).
         *
         * @param existing barang yang diedit, atau null untuk barang baru
         */
        ProductForm(Product existing) {
            panel.add(new JLabel("Kode"));
            panel.add(codeField);
            panel.add(new JLabel("Nama"));
            panel.add(nameField);
            panel.add(new JLabel("Kategori"));
            panel.add(categoryField);
            panel.add(new JLabel("Harga (Rp)"));
            panel.add(priceField);
            panel.add(new JLabel("Stok"));
            panel.add(stockField);
            if (existing != null) {
                codeField.setText(existing.getCode());
                codeField.setEditable(false); // kode tidak boleh diubah
                nameField.setText(existing.getName());
                categoryField.setText(existing.getCategory());
                priceField.setText(String.valueOf(existing.getPrice()));
                stockField.setText(String.valueOf(existing.getStock()));
            }
        }

        /**
         * Menampilkan form dalam dialog OK/Batal.
         *
         * @param parent komponen induk dialog
         * @param title  judul dialog
         * @return true jika pengguna menekan OK
         */
        boolean showDialog(Component parent, String title) {
            return JOptionPane.showConfirmDialog(parent, panel, title, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
        }

        /** @return kode barang yang diisi (tidak boleh kosong) */
        String getCode() {
            String code = codeField.getText().trim();
            if (code.isEmpty()) {
                throw new IllegalArgumentException("Kode barang tidak boleh kosong.");
            }
            return code;
        }

        /** @return nama barang yang diisi */
        String getName() {
            return nameField.getText();
        }

        /** @return kategori yang diisi */
        String getCategory() {
            return categoryField.getText().trim();
        }

        /** @return harga yang diisi (harus angka bulat) */
        long getPrice() {
            try {
                return Long.parseLong(priceField.getText().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Harga harus berupa angka bulat.");
            }
        }

        /** @return stok yang diisi (harus angka bulat) */
        int getStock() {
            try {
                return Integer.parseInt(stockField.getText().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Stok harus berupa angka bulat.");
            }
        }
    }
}
