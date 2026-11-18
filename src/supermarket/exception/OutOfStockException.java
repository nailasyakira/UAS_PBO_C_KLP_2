package supermarket.exception;

/** Dilempar ketika jumlah yang dibeli melebihi stok yang tersedia. */
public class OutOfStockException extends AppException {

    /**
     * Membuat exception stok tidak cukup.
     *
     * @param productName nama barang yang stoknya kurang
     * @param available   jumlah stok yang masih tersedia
     */
    public OutOfStockException(String productName, int available) {
        super("Stok '" + productName + "' tidak cukup. Tersedia: " + available);
    }
}
