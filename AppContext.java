package supermarket;

import supermarket.model.Admin;
import supermarket.model.Customer;
import supermarket.repository.ProductRepository;
import supermarket.repository.TransactionRepository;
import supermarket.repository.UserRepository;
import supermarket.repository.memory.InMemoryProductRepository;
import supermarket.repository.memory.InMemoryTransactionRepository;
import supermarket.repository.memory.InMemoryUserRepository;
import supermarket.service.ProductService;
import supermarket.service.TransactionService;
import supermarket.service.UserService;

/**
 * Merangkai seluruh komponen aplikasi (repository, service, data awal) di satu tempat
 * sehingga versi CLI dan GUI memakai wiring yang sama.
 * Untuk pindah ke SQLite/File, cukup ganti tiga baris "new InMemory..." di constructor.
 */
public class AppContext {

    private final UserService userService;
    private final ProductService productService;
    private final TransactionService transactionService;

    /** Membuat seluruh komponen aplikasi dan mengisi data awal. */
    public AppContext() {
        UserRepository userRepo = new InMemoryUserRepository();
        ProductRepository productRepo = new InMemoryProductRepository();
        TransactionRepository trxRepo = new InMemoryTransactionRepository();

        userService = new UserService(userRepo);
        productService = new ProductService(productRepo);
        transactionService = new TransactionService(trxRepo, productService);

        seedData(userRepo);
    }

    /** @return service akun */
    public UserService getUserService() {
        return userService;
    }

    /** @return service barang */
    public ProductService getProductService() {
        return productService;
    }

    /** @return service transaksi */
    public TransactionService getTransactionService() {
        return transactionService;
    }

    /**
     * Mengisi data awal agar aplikasi bisa langsung dicoba.
     *
     * @param userRepo repository akun
     */
    private void seedData(UserRepository userRepo) {
        userRepo.save(new Admin("admin", "admin123", "Administrator"));
        userRepo.save(new Customer("budi", "budi123", "Budi Santoso"));

        productService.addProduct("BRG-001", "Mie Instan Goreng", "Makanan", 3500, 200);
        productService.addProduct("BRG-002", "Roti Tawar", "Makanan", 16000, 40);
        productService.addProduct("BRG-003", "Air Mineral 600ml", "Minuman", 4000, 150);
        productService.addProduct("BRG-004", "Teh Botol 450ml", "Minuman", 6000, 100);
        productService.addProduct("BRG-005", "Sabun Cuci Piring", "Kebersihan", 12500, 60);
        productService.addProduct("BRG-006", "Minyak Goreng 1L", "Kebutuhan Dapur", 19000, 80);
    }
}
