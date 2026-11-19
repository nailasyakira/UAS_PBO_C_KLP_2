package supermarket.repository;

import java.util.Optional;
import supermarket.model.User;

/** Penyimpanan data akun (Admin dan Customer). */
public interface UserRepository extends Repository<User> {

    /**
     * Mencari akun berdasarkan username.
     *
     * @param username username yang dicari
     * @return akun jika ditemukan
     */
    Optional<User> findByUsername(String username);
}
