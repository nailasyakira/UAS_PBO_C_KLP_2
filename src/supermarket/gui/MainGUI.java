package supermarket.gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import supermarket.AppContext;

/** Titik masuk aplikasi Supermarket versi GUI (Swing). */
public class MainGUI {

    /**
     * Menjalankan aplikasi GUI.
     *
     * @param args argumen command line (tidak dipakai)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            System.out.println("Tema sistem tidak tersedia, memakai tema bawaan Java.");
        }
        AppContext context = new AppContext();
        SwingUtilities.invokeLater(() -> new LoginFrame(context).setVisible(true));
    }
}
