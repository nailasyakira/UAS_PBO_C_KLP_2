package supermarket.ui;

/** Kontrak untuk semua menu CLI: setiap menu dapat ditampilkan dan dijalankan. */
public interface Menu {

    /** Menampilkan menu dan memprosesnya sampai pengguna memilih keluar. */
    void show();
}
