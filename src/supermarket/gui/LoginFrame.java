package supermarket.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import supermarket.AppContext;
import supermarket.model.Admin;
import supermarket.model.Customer;
import supermarket.model.Role;
import supermarket.model.User;

/** Jendela login: memeriksa akun lalu membuka jendela Admin atau Customer. */
public class LoginFrame extends JFrame {

    private final AppContext context;
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);

    /**
     * Membuat jendela login.
     *
     * @param context komponen aplikasi (service)
     */
    public LoginFrame(AppContext context) {
        super("Supermarket App - Login");
        this.context = context;
        buildUi();
    }

    /** Menyusun form login. */
    private void buildUi() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("SUPERMARKET APP", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        form.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        form.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        form.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        form.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> doLogin());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        form.add(loginButton, gbc);

        JLabel hint = new JLabel("Contoh: admin / admin123  atau  budi / budi123", SwingConstants.CENTER);
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 11f));
        hint.setForeground(Color.GRAY);
        gbc.gridy = 4;
        form.add(hint, gbc);

        setContentPane(form);
        getRootPane().setDefaultButton(loginButton); // tekan Enter = klik Login
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    /** Memproses login lalu membuka jendela sesuai peran akun. */
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        passwordField.setText("");

        GuiUtil.safeRun(this, () -> {
            User user = context.getUserService().login(username, password);
            BaseFrame next = (user.getRole() == Role.ADMIN)
                    ? new AdminFrame(context, (Admin) user)
                    : new CustomerFrame(context, (Customer) user);
            next.setVisible(true); // polymorphism: dua jendela berbeda, diperlakukan sama
            dispose();
        });
    }
}
