package supermarket.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import supermarket.exception.AppException;
import supermarket.model.Product;
import supermarket.model.Transaction;
import supermarket.model.TransactionItem;

/**
 * Kerangka dasar menu CLI (pola Template Method).
 * Alur tampil -> pilih -> proses sudah diatur di sini; AdminMenu dan CustomerMenu
 * cukup mengisi judul, daftar pilihan, dan penanganan tiap pilihan.
 */
public abstract class BaseMenu implements Menu {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final String LINE = "--------------------------------------------------------------------";

    /** Pembaca input keyboard yang dipakai semua menu. */
    protected final ConsoleInput input;

    /**
     * Membuat menu dasar.
     *
     * @param input pembaca input keyboard
     */
    protected BaseMenu(ConsoleInput input) {
        this.input = input;
    }

    /** @return judul menu yang tampil di layar */
    protected abstract String getTitle();

    /** Menampilkan daftar pilihan menu. */
    protected abstract void printOptions();

    /**
     * Memproses satu pilihan menu.
     *
     * @param choice nomor menu yang dipilih
     * @return true untuk lanjut di menu, false untuk keluar (logout)
     * @throws AppException jika terjadi error bisnis (barang tidak ada, stok kurang, dsb.)
     */
    protected abstract boolean handleChoice(int choice) throws AppException;

    /** {@inheritDoc} */
    @Override
    public final void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== " + getTitle() + " ===");
            printOptions();
            int choice = input.readInt("Pilih menu: ");
            try {
                running = handleChoice(choice);
            } catch (AppException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("[!] " + e.getMessage());
            }
        }
    }

    /**
     * Mengubah angka menjadi format Rupiah, mis. 15000 -> "Rp15.000".
     *
     * @param amount nominal uang
     * @return teks berformat Rupiah
     */
    protected static String rupiah(long amount) {
        return "Rp" + String.format(Locale.US, "%,d", amount).replace(',', '.');
    }

    /**
     * Mencetak tabel daftar barang.
     *
     * @param products barang yang ditampilkan
     */
    protected void printProducts(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("(tidak ada data)");
            return;
        }
        System.out.println(LINE);
        System.out.printf("%-4s %-9s %-24s %-16s %12s %5s%n", "ID", "KODE", "NAMA", "KATEGORI", "HARGA", "STOK");
        System.out.println(LINE);
        for (Product p : products) {
            System.out.printf("%-4d %-9s %-24s %-16s %12s %5d%n",
                    p.getId(), p.getCode(), p.getName(), p.getCategory(), rupiah(p.getPrice()), p.getStock());
        }
        System.out.println(LINE);
    }

    /**
     * Mencetak ringkasan daftar transaksi.
     *
     * @param transactions transaksi yang ditampilkan
     */
    protected void printTransactions(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("(belum ada transaksi)");
            return;
        }
        System.out.println(LINE);
        System.out.printf("%-4s %-18s %-14s %12s %-9s %s%n", "ID", "INVOICE", "CUSTOMER", "TOTAL", "STATUS", "TANGGAL");
        System.out.println(LINE);
        for (Transaction t : transactions) {
            System.out.printf("%-4d %-18s %-14s %12s %-9s %s%n",
                    t.getId(), t.getInvoiceNo(), t.getCustomer().getUsername(), rupiah(t.getTotal()),
                    t.getStatus(), t.getCreatedAt().format(DATE_FORMAT));
        }
        System.out.println(LINE);
    }

    /**
     * Mencetak invoice lengkap satu transaksi.
     *
     * @param trx transaksi yang dicetak
     */
    protected void printInvoice(Transaction trx) {
        System.out.println("\n============== INVOICE ==============");
        System.out.println("No       : " + trx.getInvoiceNo());
        System.out.println("Tanggal  : " + trx.getCreatedAt().format(DATE_FORMAT));
        System.out.println("Customer : " + trx.getCustomer().getFullName());
        System.out.println(LINE);
        for (TransactionItem item : trx.getItems()) {
            System.out.printf("%-24s %3d x %10s = %12s%n", item.getProduct().getName(), item.getQuantity(),
                    rupiah(item.getUnitPrice()), rupiah(item.getSubtotal()));
        }
        System.out.println(LINE);
        System.out.println("TOTAL    : " + rupiah(trx.getTotal()));
        System.out.println("Bayar    : " + trx.getPaymentMethod());
        System.out.println("Status   : " + trx.getStatus());
    }
}
