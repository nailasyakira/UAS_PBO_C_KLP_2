package supermarket.exception;

/**
 * Induk semua exception khusus aplikasi supermarket.
 * Dengan satu induk, menu cukup menangkap AppException untuk semua error bisnis.
 */
public class AppException extends Exception {

    /**
     * Membuat exception baru.
     *
     * @param message pesan kesalahan yang akan ditampilkan ke pengguna
     */
    public AppException(String message) {
        super(message);
    }
}
