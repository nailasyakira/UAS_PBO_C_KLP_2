package supermarket.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import supermarket.exception.DataNotFoundException;
import supermarket.exception.OutOfStockException;
import supermarket.model.Admin;
import supermarket.model.Customer;
import supermarket.model.PaymentMethod;
import supermarket.model.Product;
import supermarket.model.Transaction;
import supermarket.model.TransactionItem;
import supermarket.model.TransactionStatus;
import supermarket.repository.TransactionRepository;

/** Logika bisnis transaksi: checkout, verifikasi admin, dan riwayat. */
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductService productService;

    /**
     * Membuat service transaksi.
     *
     * @param transactionRepository tempat penyimpanan transaksi
     * @param productService        service barang (untuk cek dan kurangi stok)
     */
    public TransactionService(TransactionRepository transactionRepository, ProductService productService) {
        this.transactionRepository = transactionRepository;
        this.productService = productService;
    }

    /**
     * Memproses pembelian: validasi semua stok dulu, baru mengurangi stok dan menyimpan transaksi.
     *
     * @param customer pelanggan yang membeli
     * @param cart     keranjang berisi pasangan ID barang -> jumlah
     * @param method   metode pembayaran
     * @return transaksi yang tersimpan (status PENDING)
     * @throws DataNotFoundException jika ada ID barang yang tidak ada
     * @throws OutOfStockException   jika ada barang yang stoknya tidak cukup
     */
    public Transaction checkout(Customer customer, Map<Integer, Integer> cart, PaymentMethod method)
            throws DataNotFoundException, OutOfStockException {
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Keranjang masih kosong.");
        }
        Transaction trx = new Transaction(generateInvoiceNo(), customer, method);

        // Tahap 1: validasi semua barang dan stok (belum ada yang berubah)
        for (Map.Entry<Integer, Integer> entry : cart.entrySet()) {
            Product product = productService.getById(entry.getKey());
            if (!product.hasStock(entry.getValue())) {
                throw new OutOfStockException(product.getName(), product.getStock());
            }
            trx.addItem(new TransactionItem(product, entry.getValue()));
        }

        // Tahap 2: semua aman, baru kurangi stok
        for (TransactionItem item : trx.getItems()) {
            item.getProduct().decreaseStock(item.getQuantity());
            productService.saveChanges(item.getProduct());
        }
        return transactionRepository.save(trx);
    }

    /**
     * Admin memverifikasi transaksi yang masih PENDING.
     *
     * @param transactionId ID transaksi
     * @param admin         admin yang memverifikasi
     * @return transaksi setelah diverifikasi
     * @throws DataNotFoundException jika transaksi tidak ada
     */
    public Transaction verify(int transactionId, Admin admin) throws DataNotFoundException {
        Transaction trx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new DataNotFoundException("Transaksi ID " + transactionId + " tidak ditemukan."));
        if (trx.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaksi ini sudah berstatus " + trx.getStatus() + ".");
        }
        trx.setStatus(TransactionStatus.VERIFIED);
        trx.setVerifiedBy(admin);
        return transactionRepository.save(trx);
    }

    /** @return seluruh transaksi (untuk admin) */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Mengambil riwayat belanja satu pelanggan.
     *
     * @param customer pelanggan yang riwayatnya diminta
     * @return daftar transaksi pelanggan tersebut
     */
    public List<Transaction> getHistory(Customer customer) {
        return transactionRepository.findByCustomerId(customer.getId());
    }

    /**
     * Membuat nomor invoice berformat INV-yyyyMMdd-0001.
     *
     * @return nomor invoice baru
     */
    private String generateInvoiceNo() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        int sequence = transactionRepository.findAll().size() + 1;
        return String.format("INV-%s-%04d", date, sequence);
    }
}
