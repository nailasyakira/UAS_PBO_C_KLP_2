package supermarket.model;

/**
 * Kelas abstrak induk untuk semua akun (Admin dan Customer).
 * Menerapkan encapsulation: atribut private, diakses lewat getter/setter.
 * Menerapkan abstraction: perilaku khusus tiap akun didefinisikan lewat method abstrak.
 */
public abstract class User implements Identifiable {

    private int id;
    private String username;
    private String password; // TODO (nilai plus): simpan hash, bukan teks asli
    private String fullName;

    /**
     * Membuat akun baru (ID diberikan oleh repository saat disimpan).
     *
     * @param username username unik untuk login
     * @param password password akun
     * @param fullName nama lengkap pemilik akun
     */
    protected User(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
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

    /** @return username akun */
    public String getUsername() {
        return username;
    }

    /** @param username username baru (tidak boleh kosong) */
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username tidak boleh kosong.");
        }
        this.username = username.trim();
    }

    /** @return password akun (dipakai repository saat menyimpan data) */
    public String getPassword() {
        return password;
    }

    /** @param password password baru (tidak boleh kosong) */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong.");
        }
        this.password = password;
    }

    /** @return nama lengkap pemilik akun */
    public String getFullName() {
        return fullName;
    }

    /** @param fullName nama lengkap baru */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Memeriksa kecocokan password. Jika nanti memakai hash, cukup ubah method ini.
     *
     * @param input password yang diketik pengguna
     * @return true jika cocok
     */
    public boolean checkPassword(String input) {
        return password.equals(input);
    }

    /** @return peran akun ini (ADMIN atau CUSTOMER) */
    public abstract Role getRole();

    /** @return kalimat sambutan setelah login (berbeda untuk tiap jenis akun) */
    public abstract String getWelcomeMessage();

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getRole() + " | " + username + " | " + fullName;
    }
}
