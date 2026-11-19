package supermarket.repository.memory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import supermarket.model.Identifiable;
import supermarket.repository.Repository;

/**
 * Kelas abstrak dasar untuk repository yang menyimpan data di memori (HashMap).
 * Menangani CRUD umum dan pemberian ID otomatis, sehingga subclass hanya
 * menambahkan query khusus.
 *
 * @param <T> tipe entitas yang disimpan
 */
public abstract class InMemoryRepository<T extends Identifiable> implements Repository<T> {

    /** Penyimpanan data: ID -> entitas (urutan penyimpanan terjaga). */
    protected final Map<Integer, T> storage = new LinkedHashMap<>();
    private int nextId = 1;

    /** {@inheritDoc} */
    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    /** {@inheritDoc} */
    @Override
    public Optional<T> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    /** {@inheritDoc} */
    @Override
    public T save(T entity) {
        if (entity.getId() == 0) {
            entity.setId(nextId++);
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    /** {@inheritDoc} */
    @Override
    public boolean deleteById(int id) {
        return storage.remove(id) != null;
    }
}
