package supermarket.ui;

import java.util.HashMap;
import java.util.Map;
import supermarket.exception.AppException;
import supermarket.model.Customer;
import supermarket.model.PaymentMethod;
import supermarket.model.Transaction;
import supermarket.service.ProductService;
import supermarket.service.TransactionService;

/** Menu CLI untuk pelanggan: lihat/cari/urutkan barang, belanja, dan lihat riwayat. */
public class CustomerMenu extends BaseMenu {

    private final Customer customer;
    private final ProductService productService;
    private final TransactionService transactionService;

    /**
     * Membuat menu pelanggan.
     *
     * @param customer           pelanggan yang sedang login
     * @param input              pembaca input keyboard
     * @param productService     service barang
     * @param transactionService service transaksi
     */
    public CustomerMenu(Customer customer, ConsoleInput input, ProductService productService,
                        TransactionService transactionService) {
        super(input);
        this.customer = customer;
        this.productService = productService;
        this.transactionService = transactionService;
    }

    /** {@inheritDoc} */
    @Override
    protected String getTitle() {
        return "MENU CUSTOMER (" + customer.getUsername() + ")";
    }

    /** {@inheritDoc} */
    @Override
    protected void printOptions() {
        System.out.println("1. Lihat daftar barang");
        System.out.println("2. Cari barang");
        System.out.println("3. Urutkan barang berdasarkan harga");
        System.out.println("4. Belanja");
        System.out.println("5. Riwayat transaksi");
        System.out.println("0. Logout");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean handleChoice(int choice) throws AppException {
        switch (choice) {
            case 1: printProducts(productService.getAllProducts()); break;
            case 2: printProducts(productService.search(input.readString("Kata kunci: "))); break;
            case 3: sortProducts(); break;
            case 4: shop(); break;
            case 5: printTransactions(transactionService.getHistory(customer)); break;
            case 0: return false;
            default: System.out.println("[!] Pilihan tidak valid.");
        }
        return true;
    }

    /** Menampilkan barang terurut sesuai pilihan naik/turun. */
    private void sortProducts() {
        String order = input.readString("Urutan harga (1 = termurah, 2 = termahal): ");
        printProducts(productService.sortByPrice(!order.equals("2")));
    }

    /**
     * Alur belanja: isi keranjang, pilih metode bayar, checkout, cetak invoice.
     *
     * @throws AppException jika barang tidak ada atau stok tidak cukup
     */
    private void shop() throws AppException {
        printProducts(productService.getAllProducts());
        Map<Integer, Integer> cart = new HashMap<>(); // ID barang -> jumlah
        while (true) {
            int id = input.readInt("ID barang (0 = selesai): ");
            if (id == 0) {
                break;
            }
            int qty = input.readInt("Jumlah: ");
            cart.merge(id, qty, Integer::sum);
        }
        if (cart.isEmpty()) {
            System.out.println("Keranjang kosong, transaksi dibatalkan.");
            return;
        }
        Transaction trx = transactionService.checkout(customer, cart, choosePayment());
        printInvoice(trx);
        System.out.println("Terima kasih! Transaksi menunggu verifikasi admin.");
    }

    /**
     * Meminta pelanggan memilih metode pembayaran.
     *
     * @return metode pembayaran yang dipilih
     */
    private PaymentMethod choosePayment() {
        PaymentMethod[] methods = PaymentMethod.values();
        for (int i = 0; i < methods.length; i++) {
            System.out.println((i + 1) + ". " + methods[i]);
        }
        int choice = input.readInt("Pilih metode bayar: ");
        if (choice < 1 || choice > methods.length) {
            throw new IllegalArgumentException("Metode pembayaran tidak valid.");
        }
        return methods[choice - 1];
    }
}
