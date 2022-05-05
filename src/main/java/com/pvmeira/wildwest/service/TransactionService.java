package com.pvmeira.wildwest.service;

import com.pvmeira.wildwest.model.Transaction;
import com.pvmeira.wildwest.model.TransactionalPackage;
import com.pvmeira.wildwest.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    @Autowired
    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> findAllTransactionsFromPackge(TransactionalPackage tp) {
        return this.repository.findAllByTransactionalPackage(tp);
    }

    public Transaction save (Transaction t) {
        return this.repository.save(t);
    }

    public void saveAll (List<Transaction> t) {
        this.repository.saveAll(t);
    }

    public List<Transaction> findAllByMonth(LocalDate date) {
        LocalDate start = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());
        return this.repository.findAllTransactionsFromAPariod(start, end);
    }
}
