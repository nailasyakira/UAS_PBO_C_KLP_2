package supermarket.repository;

import java.util.List;
import supermarket.model.Product;

/** Penyimpanan data barang. */
public interface ProductRepository extends Repository<Product> {

    /**
     * Mencari barang yang namanya mengandung kata kunci (tanpa membedakan huruf besar/kecil).
     *
     * @param keyword kata kunci pencarian
     * @return daftar barang yang cocok
     */
    List<Product> searchByName(String keyword);
}
