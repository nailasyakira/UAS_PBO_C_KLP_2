package supermarket.model;

import supermarket.exception.OutOfStockException;

/**
 * Barang yang dijual di supermarket.
 * Validasi dilakukan di setter agar data tidak pernah berada dalam kondisi tidak valid.
 */
public class Product implements Identifiable {

    private int id;
    private String code;
    private String name;
    private String category;
    private long price; // Rupiah utuh
    private int stock;

    /**
     * Membuat barang baru.
     *
     * @param code     kode/barcode unik, mis. "BRG-001"
     * @param name     nama barang
     * @param category nama kategori
     * @param price    harga satuan (Rupiah)
     * @param stock    jumlah stok awal
     */
    public Product(String code, String name, String category, long price, int stock) {
        this.code = code;
        setName(name);
        this.category = category;
        setPrice(price);
        setStock(stock);
    }

    /** {@inheritDoc} */
    @Override
    public int getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /** @return kode barang */
    public String getCode() {
        return code;
    }

    /** @param code kode barang baru */
    public void setCode(String code) {
        this.code = code;
    }

    /** @return nama barang */
    public String getName() {
        return name;
    }

    /** @param name nama baru (tidak boleh kosong) */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama barang tidak boleh kosong.");
        }
        this.name = name.trim();
    }

    /** @return nama kategori */
    public String getCategory() {
        return category;
    }

    /** @param category nama kategori baru */
    public void setCategory(String category) {
        this.category = category;
    }

    /** @return harga satuan dalam Rupiah */
    public long getPrice() {
        return price;
    }

    /** @param price harga baru (tidak boleh negatif) */
    public void setPrice(long price) {
        if (price < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif.");
        }
        this.price = price;
    }

    /** @return jumlah stok saat ini */
    public int getStock() {
        return stock;
    }

    /** @param stock stok baru (tidak boleh negatif) */
    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif.");
        }
        this.stock = stock;
    }

    /**
     * Mengecek apakah stok cukup untuk jumlah tertentu.
     *
     * @param quantity jumlah yang diminta
     * @return true jika stok mencukupi
     */
    public boolean hasStock(int quantity) {
        return quantity <= stock;
    }

    /**
     * Mengurangi stok setelah barang terjual.
     *
     * @param quantity jumlah yang dibeli
     * @throws OutOfStockException jika stok tidak cukup
     */
    public void decreaseStock(int quantity) throws OutOfStockException {
        if (!hasStock(quantity)) {
            throw new OutOfStockException(name, stock);
        }
        stock -= quantity;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return code + " - " + name;
    }
}
