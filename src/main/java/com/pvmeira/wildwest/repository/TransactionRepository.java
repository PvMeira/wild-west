package com.pvmeira.wildwest.repository;

import com.pvmeira.wildwest.model.Transaction;
import com.pvmeira.wildwest.model.TransactionalPackage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findAllByTransactionalPackage(TransactionalPackage transactionalPackage);
}
