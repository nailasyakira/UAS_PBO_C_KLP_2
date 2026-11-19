package supermarket.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import supermarket.AppContext;
import supermarket.model.User;

/**
 * Kerangka dasar jendela utama setelah login (pola Template Method).
 * Header (sambutan + tombol Logout) dibuat di sini; subclass hanya mengisi isi tengah
 * lewat {@link #createContent()}.
 */
public abstract class BaseFrame extends JFrame {

    private final AppContext context;
    private final User user;

    /**
     * Membuat jendela dasar (belum ditampilkan; panggil buildUi() dari subclass).
     *
     * @param title   judul jendela
     * @param context komponen aplikasi (service)
     * @param user    pengguna yang sedang login
     */
    protected BaseFrame(String title, AppContext context, User user) {
        super(title);
        this.context = context;
        this.user = user;
    }

    /** @return isi bagian tengah jendela (biasanya tab-tab) */
    protected abstract JComponent createContent();

    /** @return komponen aplikasi untuk mengakses service */
    protected AppContext getContext() {
        return context;
    }

    /**
     * Menyusun tampilan jendela. Dipanggil di akhir constructor subclass
     * (setelah field subclass terisi) agar aman memanggil createContent().
     */
    protected final void buildUi() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        setSize(1000, 640);
        setLocationRelativeTo(null);
    }

    /**
     * Membuat TabbedPane yang otomatis me-refresh tab saat dibuka.
     *
     * @return tab kosong siap diisi panel (panel Refreshable akan dimuat ulang)
     */
    protected JTabbedPane createRefreshingTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addChangeListener(e -> {
            Component selected = tabs.getSelectedComponent();
            if (selected instanceof Refreshable) {
                ((Refreshable) selected).refresh();
            }
        });
        return tabs;
    }

    /**
     * Membuat header berisi kalimat sambutan dan tombol Logout.
     *
     * @return panel header
     */
    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel welcome = new JLabel(user.getWelcomeMessage());
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 15f));

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> logout());

        header.add(welcome, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);
        return header;
    }

    /** Menutup jendela ini dan kembali ke halaman login. */
    private void logout() {
        new LoginFrame(context).setVisible(true);
        dispose();
    }
}
