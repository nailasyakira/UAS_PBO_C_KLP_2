package supermarket.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import supermarket.exception.AppException;
import supermarket.model.Transaction;
import supermarket.model.TransactionItem;

/** Kumpulan fungsi bantu yang dipakai bersama oleh seluruh komponen GUI. */
public final class GuiUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private GuiUtil() {
    }

    /** Aksi GUI yang boleh melempar error bisnis (AppException). */
    @FunctionalInterface
    public interface GuiAction {

        /**
         * Menjalankan aksi.
         *
         * @throws AppException jika terjadi error bisnis
         */
        void run() throws AppException;
    }

    /**
     * Menjalankan aksi dan menampilkan error bisnis sebagai dialog (bukan crash).
     *
     * @param parent komponen induk dialog
     * @param action aksi yang dijalankan
     */
    public static void safeRun(Component parent, GuiAction action) {
        try {
            action.run();
        } catch (AppException | IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "Terjadi kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Menampilkan dialog informasi.
     *
     * @param parent  komponen induk dialog
     * @param message isi pesan
     */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Menampilkan dialog konfirmasi Ya/Tidak.
     *
     * @param parent  komponen induk dialog
     * @param message pertanyaan konfirmasi
     * @return true jika pengguna memilih Ya
     */
    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Konfirmasi", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    /**
     * Mengubah angka menjadi format Rupiah, mis. 15000 -> "Rp15.000".
     *
     * @param amount nominal uang
     * @return teks berformat Rupiah
     */
    public static String rupiah(long amount) {
        return "Rp" + String.format(Locale.US, "%,d", amount).replace(',', '.');
    }

    /**
     * Memformat tanggal dan jam transaksi.
     *
     * @param time waktu yang diformat
     * @return teks berformat dd-MM-yyyy HH:mm
     */
    public static String formatDate(LocalDateTime time) {
        return time.format(DATE_FORMAT);
    }

    /**
     * Membuat model tabel yang selnya tidak bisa diedit langsung.
     *
     * @param columns nama-nama kolom
     * @return model tabel kosong
     */
    public static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /**
     * Membuat tabel dengan pengaturan standar aplikasi (pilih satu baris).
     *
     * @param model model data tabel
     * @return tabel siap pakai
     */
    public static JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    /**
     * Meratakan isi kolom tertentu ke kanan (cocok untuk angka).
     *
     * @param table   tabel yang diatur
     * @param columns nomor kolom (mulai dari 0)
     */
    public static void alignRight(JTable table, int... columns) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int column : columns) {
            table.getColumnModel().getColumn(column).setCellRenderer(renderer);
        }
    }

    /**
     * Mengambil nomor baris yang sedang dipilih pada tabel.
     *
     * @param table tabel yang diperiksa
     * @return nomor baris terpilih
     * @throws IllegalStateException jika belum ada baris yang dipilih
     */
    public static int requireSelection(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            throw new IllegalStateException("Pilih satu baris pada tabel terlebih dahulu.");
        }
        return row;
    }

    /**
     * Menyusun teks invoice satu transaksi.
     *
     * @param trx transaksi yang dicetak
     * @return teks invoice siap tampil
     */
    public static String invoiceText(Transaction trx) {
        StringBuilder sb = new StringBuilder();
        sb.append("============== INVOICE ==============\n");
        sb.append("No       : ").append(trx.getInvoiceNo()).append('\n');
        sb.append("Tanggal  : ").append(formatDate(trx.getCreatedAt())).append('\n');
        sb.append("Customer : ").append(trx.getCustomer().getFullName()).append('\n');
        sb.append("-------------------------------------\n");
        for (TransactionItem item : trx.getItems()) {
            sb.append(String.format("%-20s %3d x %9s = %11s\n", item.getProduct().getName(), item.getQuantity(),
                    rupiah(item.getUnitPrice()), rupiah(item.getSubtotal())));
        }
        sb.append("-------------------------------------\n");
        sb.append("TOTAL    : ").append(rupiah(trx.getTotal())).append('\n');
        sb.append("Bayar    : ").append(trx.getPaymentMethod()).append('\n');
        sb.append("Status   : ").append(trx.getStatus()).append('\n');
        return sb.toString();
    }

    /**
     * Menampilkan invoice dalam dialog.
     *
     * @param parent komponen induk dialog
     * @param trx    transaksi yang ditampilkan
     */
    public static void showInvoice(Component parent, Transaction trx) {
        JTextArea area = new JTextArea(invoiceText(trx));
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(520, 300));
        JOptionPane.showMessageDialog(parent, scroll, "Invoice " + trx.getInvoiceNo(), JOptionPane.PLAIN_MESSAGE);
    }
}
