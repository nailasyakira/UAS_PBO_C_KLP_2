package supermarket.gui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import supermarket.model.User;
import supermarket.service.UserService;

/** Panel admin untuk melihat daftar pengguna. */
public class UserPanel extends JPanel implements Refreshable {

    private final UserService userService;
    private final DefaultTableModel model = GuiUtil.readOnlyModel("ID", "Username", "Nama Lengkap", "Role");
    private final JTable table = GuiUtil.createTable(model);

    /**
     * Membuat panel daftar pengguna.
     *
     * @param userService service akun
     */
    public UserPanel(UserService userService) {
        super(new BorderLayout());
        this.userService = userService;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    /** {@inheritDoc} */
    @Override
    public void refresh() {
        model.setRowCount(0);
        for (User u : userService.getAllUsers()) {
            model.addRow(new Object[] {u.getId(), u.getUsername(), u.getFullName(), u.getRole()});
        }
    }
}
