package supermarket.exception;

/** Dilempar ketika login gagal (username tidak ada atau password salah). */
public class AuthenticationException extends AppException {

    /**
     * Membuat exception autentikasi.
     *
     * @param message pesan kesalahan
     */
    public AuthenticationException(String message) {
        super(message);
    }
}
