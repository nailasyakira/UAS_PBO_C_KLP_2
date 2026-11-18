package supermarket.model;

/**
 * Satu baris isi keranjang dalam sebuah transaksi.
 * Harga satuan disalin (snapshot) saat transaksi dibuat, sehingga riwayat
 * tidak berubah walau admin mengedit harga barang di kemudian hari.
 */
public class TransactionItem {

    private final Product product;
    private final int quantity;
    private final long unitPrice;

    /**
     * Membuat item transaksi.
     *
     * @param product  barang yang dibeli
     * @param quantity jumlah yang dibeli (harus lebih dari 0)
     */
    public TransactionItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah beli harus lebih dari 0.");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    /** @return barang yang dibeli */
    public Product getProduct() {
        return product;
    }

    /** @return jumlah yang dibeli */
    public int getQuantity() {
        return quantity;
    }

    /** @return harga satuan saat transaksi terjadi */
    public long getUnitPrice() {
        return unitPrice;
    }

    /** @return subtotal = harga satuan x jumlah */
    public long getSubtotal() {
        return unitPrice * quantity;
    }
}
