package supermarket.gui;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import supermarket.AppContext;
import supermarket.model.Customer;
import supermarket.service.TransactionService;

/** Jendela pelanggan: tab Belanja dan Riwayat Transaksi. */
public class CustomerFrame extends BaseFrame {

    private final Customer customer;

    /**
     * Membuat jendela pelanggan.
     *
     * @param context  komponen aplikasi (service)
     * @param customer pelanggan yang sedang login
     */
    public CustomerFrame(AppContext context, Customer customer) {
        super("Supermarket App - Customer", context, customer);
        this.customer = customer;
        buildUi();
    }

    /** {@inheritDoc} */
    @Override
    protected JComponent createContent() {
        TransactionService transactionService = getContext().getTransactionService();
        JTabbedPane tabs = createRefreshingTabs();
        tabs.addTab("Belanja", new ShopPanel(customer, getContext().getProductService(), transactionService));
        tabs.addTab("Riwayat Transaksi",
                new TransactionPanel(transactionService, () -> transactionService.getHistory(customer), null));
        return tabs;
    }
}
