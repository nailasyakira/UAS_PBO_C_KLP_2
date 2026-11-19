package supermarket.ui;

import supermarket.exception.AppException;
import supermarket.model.Admin;
import supermarket.model.Product;
import supermarket.model.Transaction;
import supermarket.model.User;
import supermarket.service.ProductService;
import supermarket.service.TransactionService;
import supermarket.service.UserService;

/** Menu CLI untuk admin: kelola barang, lihat pengguna, dan verifikasi transaksi. */
public class AdminMenu extends BaseMenu {

    private final Admin admin;
    private final ProductService productService;
    private final TransactionService transactionService;
    private final UserService userService;

    /**
     * Membuat menu admin.
     *
     * @param admin              admin yang sedang login
     * @param input              pembaca input keyboard
     * @param productService     service barang
     * @param transactionService service transaksi
     * @param userService        service akun
     */
    public AdminMenu(Admin admin, ConsoleInput input, ProductService productService,
                     TransactionService transactionService, UserService userService) {
        super(input);
        this.admin = admin;
        this.productService = productService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    /** {@inheritDoc} */
    @Override
    protected String getTitle() {
        return "MENU ADMIN (" + admin.getUsername() + ")";
    }

    /** {@inheritDoc} */
    @Override
    protected void printOptions() {
        System.out.println("1. Lihat daftar barang");
        System.out.println("2. Tambah barang");
        System.out.println("3. Edit barang");
        System.out.println("4. Hapus barang");
        System.out.println("5. Lihat daftar pengguna");
        System.out.println("6. Lihat daftar transaksi");
        System.out.println("7. Verifikasi transaksi");
        System.out.println("0. Logout");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean handleChoice(int choice) throws AppException {
        switch (choice) {
            case 1: printProducts(productService.getAllProducts()); break;
            case 2: addProduct(); break;
            case 3: editProduct(); break;
            case 4: deleteProduct(); break;
            case 5: showUsers(); break;
            case 6: printTransactions(transactionService.getAllTransactions()); break;
            case 7: verifyTransaction(); break;
            case 0: return false;
            default: System.out.println("[!] Pilihan tidak valid.");
        }
        return true;
    }

    /** Meminta data barang baru dari admin lalu menyimpannya. */
    private void addProduct() {
        String code = input.readString("Kode barang : ");
        String name = input.readString("Nama barang : ");
        String category = input.readString("Kategori    : ");
        long price = input.readLong("Harga (Rp)  : ");
        int stock = input.readInt("Stok        : ");
        Product p = productService.addProduct(code, name, category, price, stock);
        System.out.println("Barang berhasil ditambahkan dengan ID " + p.getId() + ".");
    }

    /**
     * Mengubah data barang yang dipilih admin.
     *
     * @throws AppException jika ID barang tidak ditemukan
     */
    private void editProduct() throws AppException {
        int id = input.readInt("ID barang yang diedit: ");
        Product current = productService.getById(id);
        System.out.println("Mengedit: " + current + " (isi ulang semua data)");
        String name = input.readString("Nama baru     : ");
        String category = input.readString("Kategori baru : ");
        long price = input.readLong("Harga baru (Rp): ");
        int stock = input.readInt("Stok baru      : ");
        productService.updateProduct(id, name, category, price, stock);
        System.out.println("Barang berhasil diubah.");
    }

    /**
     * Menghapus barang setelah konfirmasi admin.
     *
     * @throws AppException jika ID barang tidak ditemukan
     */
    private void deleteProduct() throws AppException {
        int id = input.readInt("ID barang yang dihapus: ");
        Product p = productService.getById(id);
        String confirm = input.readString("Yakin hapus '" + p.getName() + "'? (y/n): ");
        if (confirm.equalsIgnoreCase("y")) {
            productService.deleteProduct(id);
            System.out.println("Barang berhasil dihapus.");
        } else {
            System.out.println("Dibatalkan.");
        }
    }

    /** Menampilkan seluruh akun yang terdaftar. */
    private void showUsers() {
        System.out.println("--- Daftar Pengguna ---");
        for (User u : userService.getAllUsers()) {
            System.out.println(u.getId() + ". " + u);
        }
    }

    /**
     * Memverifikasi transaksi yang dipilih admin lalu menampilkan invoice-nya.
     *
     * @throws AppException jika ID transaksi tidak ditemukan
     */
    private void verifyTransaction() throws AppException {
        printTransactions(transactionService.getAllTransactions());
        int id = input.readInt("ID transaksi yang diverifikasi: ");
        Transaction trx = transactionService.verify(id, admin);
        System.out.println("Transaksi " + trx.getInvoiceNo() + " berhasil diverifikasi.");
    }
}
