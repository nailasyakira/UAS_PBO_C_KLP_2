package supermarket.repository.memory;

import java.util.ArrayList;
import java.util.List;
import supermarket.model.Product;
import supermarket.repository.ProductRepository;

/** Repository barang yang menyimpan data di memori. */
public class InMemoryProductRepository extends InMemoryRepository<Product> implements ProductRepository {

    /** {@inheritDoc} */
    @Override
    public List<Product> searchByName(String keyword) {
        List<Product> result = new ArrayList<>();
        String key = keyword.toLowerCase();
        for (Product product : storage.values()) {
            if (product.getName().toLowerCase().contains(key)) {
                result.add(product);
            }
        }
        return result;
    }
}
