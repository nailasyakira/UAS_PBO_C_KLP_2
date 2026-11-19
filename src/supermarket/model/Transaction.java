package supermarket.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Header transaksi pembelian beserta daftar barang yang dibeli (struk/invoice). */
public class Transaction implements Identifiable {

    private int id;
    private final String invoiceNo;
    private final Customer customer;
    private final List<TransactionItem> items = new ArrayList<>();
    private final PaymentMethod paymentMethod;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private TransactionStatus status = TransactionStatus.PENDING;
    private Admin verifiedBy;

    /**
     * Membuat transaksi baru berstatus PENDING.
     *
     * @param invoiceNo     nomor invoice unik
     * @param customer      pelanggan yang bertransaksi
     * @param paymentMethod metode pembayaran
     */
    public Transaction(String invoiceNo, Customer customer, PaymentMethod paymentMethod) {
        this.invoiceNo = invoiceNo;
        this.customer = customer;
        this.paymentMethod = paymentMethod;
    }

    /** {@inheritDoc} */
    @Override
    public int getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /** @return nomor invoice */
    public String getInvoiceNo() {
        return invoiceNo;
    }

    /** @return pelanggan pemilik transaksi */
    public Customer getCustomer() {
        return customer;
    }

    /** @return daftar item (read-only, gunakan addItem untuk menambah) */
    public List<TransactionItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /** @param item item yang ditambahkan ke transaksi */
    public void addItem(TransactionItem item) {
        items.add(item);
    }

    /** @return metode pembayaran */
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    /** @return waktu transaksi dibuat */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** @return status transaksi saat ini */
    public TransactionStatus getStatus() {
        return status;
    }

    /** @param status status baru transaksi */
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    /** @return admin yang memverifikasi, atau null jika belum diverifikasi */
    public Admin getVerifiedBy() {
        return verifiedBy;
    }

    /** @param verifiedBy admin yang memverifikasi transaksi */
    public void setVerifiedBy(Admin verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    /** @return total belanja = jumlah seluruh subtotal item */
    public long getTotal() {
        long total = 0;
        for (TransactionItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
}
