package supermarket.gui;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import supermarket.AppContext;
import supermarket.model.Admin;
import supermarket.service.TransactionService;

/** Jendela admin: tab Barang, Pengguna, dan Transaksi. */
public class AdminFrame extends BaseFrame {

    private final Admin admin;

    /**
     * Membuat jendela admin.
     *
     * @param context komponen aplikasi (service)
     * @param admin   admin yang sedang login
     */
    public AdminFrame(AppContext context, Admin admin) {
        super("Supermarket App - Admin", context, admin);
        this.admin = admin;
        buildUi();
    }

    /** {@inheritDoc} */
    @Override
    protected JComponent createContent() {
        TransactionService transactionService = getContext().getTransactionService();
        JTabbedPane tabs = createRefreshingTabs();
        tabs.addTab("Barang", new AdminProductPanel(getContext().getProductService()));
        tabs.addTab("Pengguna", new UserPanel(getContext().getUserService()));
        tabs.addTab("Transaksi",
                new TransactionPanel(transactionService, transactionService::getAllTransactions, admin));
        return tabs;
    }
}
