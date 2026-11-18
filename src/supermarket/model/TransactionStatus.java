package supermarket.model;

/** Status sebuah transaksi. */
public enum TransactionStatus {
    /** Baru dibuat, menunggu verifikasi admin. */
    PENDING,
    /** Sudah diverifikasi admin. */
    VERIFIED,
    /** Dibatalkan. */
    CANCELLED
}
