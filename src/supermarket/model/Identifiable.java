package supermarket.model;

/**
 * Kontrak untuk objek yang memiliki ID unik.
 * Dipakai repository agar bisa memberi ID otomatis ke semua jenis data.
 */
public interface Identifiable {

    /** @return ID unik objek (0 jika belum disimpan) */
    int getId();

    /** @param id ID baru untuk objek */
    void setId(int id);
}
