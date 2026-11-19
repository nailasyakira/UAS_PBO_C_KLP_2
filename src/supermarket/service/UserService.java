package supermarket.service;

import java.util.List;
import supermarket.exception.AuthenticationException;
import supermarket.model.User;
import supermarket.repository.UserRepository;

/** Logika bisnis untuk akun: login dan daftar pengguna. */
public class UserService {

    private final UserRepository userRepository;

    /**
     * Membuat service akun.
     *
     * @param userRepository tempat penyimpanan data akun
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Memeriksa username dan password.
     *
     * @param username username yang diketik
     * @param password password yang diketik
     * @return akun yang berhasil login (bisa Admin atau Customer)
     * @throws AuthenticationException jika username/password salah
     */
    public User login(String username, String password) throws AuthenticationException {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !user.checkPassword(password)) {
            throw new AuthenticationException("Username atau password salah.");
        }
        return user;
    }

    /** @return daftar seluruh pengguna (untuk menu admin) */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
