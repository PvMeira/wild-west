package com.pvmeira.wildwest.service;

import com.pvmeira.wildwest.model.Transaction;
import com.pvmeira.wildwest.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository repository;

    @Autowired
    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }


    public Transaction save (Transaction t) {
        return this.repository.save(t);
    }

    public void saveAll (List<Transaction> t) {
        this.repository.saveAll(t);
    }
}
