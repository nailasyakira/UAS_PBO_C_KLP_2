package supermarket.gui;

/** Kontrak untuk panel yang datanya bisa dimuat ulang (dipanggil saat tab dibuka). */
public interface Refreshable {

    /** Memuat ulang data dari service lalu memperbarui tampilan. */
    void refresh();
}
