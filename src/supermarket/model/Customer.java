package supermarket.model;

/** Akun pelanggan: melihat barang, berbelanja, dan melihat riwayat transaksi. */
public class Customer extends User {

    /**
     * Membuat akun pelanggan.
     *
     * @param username username login
     * @param password password akun
     * @param fullName nama lengkap pelanggan
     */
    public Customer(String username, String password, String fullName) {
        super(username, password, fullName);
    }

    /** {@inheritDoc} */
    @Override
    public Role getRole() {
        return Role.CUSTOMER;
    }

    /** {@inheritDoc} */
    @Override
    public String getWelcomeMessage() {
        return "Halo, " + getFullName() + "! Selamat berbelanja.";
    }
}
