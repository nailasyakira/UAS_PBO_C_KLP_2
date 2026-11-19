package supermarket.repository;

import java.util.List;
import java.util.Optional;
import supermarket.model.Identifiable;

/**
 * Kontrak dasar penyimpanan data (CRUD) untuk semua jenis entitas.
 * Implementasinya bisa berupa memori (ArrayList/HashMap), file, atau SQLite
 * tanpa mengubah kode service dan menu (polymorphism).
 *
 * @param <T> tipe entitas yang disimpan
 */
public interface Repository<T extends Identifiable> {

    /** @return seluruh data yang tersimpan */
    List<T> findAll();

    /**
     * Mencari data berdasarkan ID.
     *
     * @param id ID yang dicari
     * @return data jika ditemukan, kosong jika tidak
     */
    Optional<T> findById(int id);

    /**
     * Menyimpan data: INSERT jika ID masih 0, UPDATE jika sudah punya ID.
     *
     * @param entity data yang disimpan
     * @return data yang tersimpan (ID sudah terisi)
     */
    T save(T entity);

    /**
     * Menghapus data berdasarkan ID.
     *
     * @param id ID data yang dihapus
     * @return true jika ada data yang terhapus
     */
    boolean deleteById(int id);
}
