package supermarket.repository.memory;

import java.util.ArrayList;
import java.util.List;
import supermarket.model.Transaction;
import supermarket.repository.TransactionRepository;

/** Repository transaksi yang menyimpan data di memori. */
public class InMemoryTransactionRepository extends InMemoryRepository<Transaction>
        implements TransactionRepository {

    /** {@inheritDoc} */
    @Override
    public List<Transaction> findByCustomerId(int customerId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction trx : storage.values()) {
            if (trx.getCustomer().getId() == customerId) {
                result.add(trx);
            }
        }
        return result;
    }
}
