package supermarket.model;

/** Akun admin: mengelola data barang dan memverifikasi transaksi. */
public class Admin extends User {

    /**
     * Membuat akun admin.
     *
     * @param username username login
     * @param password password akun
     * @param fullName nama lengkap admin
     */
    public Admin(String username, String password, String fullName) {
        super(username, password, fullName);
    }

    /** {@inheritDoc} */
    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    /** {@inheritDoc} */
    @Override
    public String getWelcomeMessage() {
        return "Selamat datang, Admin " + getFullName() + "!";
    }
}
