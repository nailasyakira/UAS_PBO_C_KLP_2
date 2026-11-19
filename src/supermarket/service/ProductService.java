package supermarket.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import supermarket.exception.DataNotFoundException;
import supermarket.model.Product;
import supermarket.repository.ProductRepository;

/** Logika bisnis untuk barang: tambah, ubah, hapus, cari, dan urutkan. */
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Membuat service barang.
     *
     * @param productRepository tempat penyimpanan data barang
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /** @return seluruh barang */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Mengambil satu barang berdasarkan ID.
     *
     * @param id ID barang
     * @return barang yang dicari
     * @throws DataNotFoundException jika ID tidak ada
     */
    public Product getById(int id) throws DataNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Barang dengan ID " + id + " tidak ditemukan."));
    }

    /**
     * Menambah barang baru.
     *
     * @param code     kode barang (harus unik)
     * @param name     nama barang
     * @param category nama kategori
     * @param price    harga satuan
     * @param stock    stok awal
     * @return barang yang tersimpan (ID sudah terisi)
     */
    public Product addProduct(String code, String name, String category, long price, int stock) {
        for (Product p : productRepository.findAll()) {
            if (p.getCode().equalsIgnoreCase(code)) {
                throw new IllegalArgumentException("Kode barang sudah dipakai: " + code);
            }
        }
        return productRepository.save(new Product(code, name, category, price, stock));
    }

    /**
     * Mengubah data barang yang sudah ada.
     *
     * @param id       ID barang yang diubah
     * @param name     nama baru
     * @param category kategori baru
     * @param price    harga baru
     * @param stock    stok baru
     * @return barang setelah diubah
     * @throws DataNotFoundException jika ID tidak ada
     */
    public Product updateProduct(int id, String name, String category, long price, int stock)
            throws DataNotFoundException {
        Product product = getById(id);
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);
        return productRepository.save(product);
    }

    /**
     * Menyimpan perubahan pada objek barang (mis. stok berkurang setelah transaksi).
     *
     * @param product barang yang sudah dimodifikasi
     */
    public void saveChanges(Product product) {
        productRepository.save(product);
    }

    /**
     * Menghapus barang.
     *
     * @param id ID barang yang dihapus
     * @throws DataNotFoundException jika ID tidak ada
     */
    public void deleteProduct(int id) throws DataNotFoundException {
        if (!productRepository.deleteById(id)) {
            throw new DataNotFoundException("Barang dengan ID " + id + " tidak ditemukan.");
        }
    }

    /**
     * Mencari barang berdasarkan nama.
     *
     * @param keyword kata kunci
     * @return barang yang namanya mengandung kata kunci
     */
    public List<Product> search(String keyword) {
        return productRepository.searchByName(keyword);
    }

    /**
     * Mengurutkan seluruh barang berdasarkan harga.
     *
     * @param ascending true = termurah dulu, false = termahal dulu
     * @return daftar barang yang sudah terurut
     */
    public List<Product> sortByPrice(boolean ascending) {
        return sortByPrice(productRepository.findAll(), ascending);
    }

    /**
     * Mengurutkan daftar barang tertentu (mis. hasil pencarian) berdasarkan harga.
     *
     * @param products  daftar barang yang diurutkan (tidak diubah, hasil berupa salinan)
     * @param ascending true = termurah dulu, false = termahal dulu
     * @return salinan daftar yang sudah terurut
     */
    public List<Product> sortByPrice(List<Product> products, boolean ascending) {
        List<Product> copy = new ArrayList<>(products);
        Comparator<Product> byPrice = Comparator.comparingLong(Product::getPrice);
        copy.sort(ascending ? byPrice : byPrice.reversed());
        return copy;
    }
}
