package com.pvmeira.wildwest.repository;

import com.pvmeira.wildwest.model.Transaction;
import com.pvmeira.wildwest.model.TransactionalPackage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findAllByTransactionalPackage(TransactionalPackage transactionalPackage);

    @Query(value = "SELECT trans FROM TRANSACTIONS trans WHERE trans.transactionalPackage.packageDate BETWEEN :start AND :end")
    List<Transaction> findAllTransactionsFromAPariod(LocalDate start, LocalDate end);
}
