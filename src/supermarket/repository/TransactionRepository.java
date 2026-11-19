package supermarket.repository;

import java.util.List;
import supermarket.model.Transaction;

/** Penyimpanan data transaksi. */
public interface TransactionRepository extends Repository<Transaction> {

    /**
     * Mengambil riwayat transaksi milik satu pelanggan.
     *
     * @param customerId ID pelanggan
     * @return daftar transaksi pelanggan tersebut
     */
    List<Transaction> findByCustomerId(int customerId);
}
