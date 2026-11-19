package supermarket.exception;

/** Dilempar ketika data yang dicari (barang, transaksi, dsb.) tidak ditemukan. */
public class DataNotFoundException extends AppException {

    /**
     * Membuat exception data tidak ditemukan.
     *
     * @param message pesan kesalahan
     */
    public DataNotFoundException(String message) {
        super(message);
    }
}
