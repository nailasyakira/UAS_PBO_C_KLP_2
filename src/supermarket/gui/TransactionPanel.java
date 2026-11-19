package supermarket.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import supermarket.exception.AppException;
import supermarket.model.Admin;
import supermarket.model.Transaction;
import supermarket.service.TransactionService;

/**
 * Panel daftar transaksi yang dipakai bersama oleh admin dan customer.
 * Sumber datanya dikirim lewat {@link Supplier}; tombol Verifikasi hanya muncul untuk admin.
 */
public class TransactionPanel extends JPanel implements Refreshable {

    private final TransactionService transactionService;
    private final Supplier<List<Transaction>> source;
    private final Admin verifier;
    private final DefaultTableModel model =
            GuiUtil.readOnlyModel("ID", "Invoice", "Customer", "Total", "Metode", "Status", "Tanggal");
    private final JTable table = GuiUtil.createTable(model);
    private List<Transaction> shown = new ArrayList<>(); // urutannya sama dengan baris tabel

    /**
     * Membuat panel transaksi.
     *
     * @param transactionService service transaksi
     * @param source             sumber data (semua transaksi untuk admin, riwayat sendiri untuk customer)
     * @param verifier           admin yang boleh memverifikasi, atau null jika tidak ada tombol verifikasi
     */
    public TransactionPanel(TransactionService transactionService, Supplier<List<Transaction>> source,
                            Admin verifier) {
        super(new BorderLayout(8, 8));
        this.transactionService = transactionService;
        this.source = source;
        this.verifier = verifier;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GuiUtil.alignRight(table, 3);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createActionBar(), BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() { // klik dua kali = lihat invoice
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    GuiUtil.safeRun(TransactionPanel.this, TransactionPanel.this::viewInvoice);
                }
            }
        });
        refresh();
    }

    /** {@inheritDoc} */
    @Override
    public void refresh() {
        shown = new ArrayList<>(source.get());
        Collections.reverse(shown); // transaksi terbaru di atas
        model.setRowCount(0);
        for (Transaction t : shown) {
            model.addRow(new Object[] {t.getId(), t.getInvoiceNo(), t.getCustomer().getUsername(),
                GuiUtil.rupiah(t.getTotal()), t.getPaymentMethod(), t.getStatus(),
                GuiUtil.formatDate(t.getCreatedAt())});
        }
    }

    /**
     * Membuat baris tombol di bawah tabel.
     *
     * @return panel tombol
     */
    private JPanel createActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton invoiceButton = new JButton("Lihat Invoice");
        JButton reloadButton = new JButton("Muat Ulang");
        invoiceButton.addActionListener(e -> GuiUtil.safeRun(this, this::viewInvoice));
        reloadButton.addActionListener(e -> refresh());
        bar.add(invoiceButton);
        if (verifier != null) {
            JButton verifyButton = new JButton("Verifikasi");
            verifyButton.addActionListener(e -> GuiUtil.safeRun(this, this::verifySelected));
            bar.add(verifyButton);
        }
        bar.add(reloadButton);
        return bar;
    }

    /** Menampilkan invoice transaksi yang dipilih. */
    private void viewInvoice() {
        GuiUtil.showInvoice(this, shown.get(GuiUtil.requireSelection(table)));
    }

    /**
     * Memverifikasi transaksi yang dipilih (khusus admin).
     *
     * @throws AppException jika transaksi tidak ditemukan
     */
    private void verifySelected() throws AppException {
        Transaction trx = shown.get(GuiUtil.requireSelection(table));
        transactionService.verify(trx.getId(), verifier);
        refresh();
        GuiUtil.showInfo(this, "Transaksi " + trx.getInvoiceNo() + " berhasil diverifikasi.");
    }
}
