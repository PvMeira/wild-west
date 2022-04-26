package com.pvmeira.wildwest.service;

import com.pvmeira.wildwest.model.TransactionalPackage;
import com.pvmeira.wildwest.repository.TransactionalPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TransactionalPackageService {

    private final TransactionalPackageRepository repository;

    @Autowired
    public TransactionalPackageService(TransactionalPackageRepository repository) {
        this.repository = repository;
    }


    public TransactionalPackage find(LocalDate packageDate) {
        return this.repository.findById(packageDate).get();
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionalPackage save(TransactionalPackage transactionalPackage) {
        return this.repository.save(transactionalPackage);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionalPackage finishProcess(TransactionalPackage transactionalPackage, LocalDateTime date) {
        TransactionalPackage transactionalPackage1 = this.repository.findById(transactionalPackage.getPackageDate()).orElseThrow( () -> new RuntimeException("No transaction package was found for this date, DATE = " + transactionalPackage.getPackageDate().toString()));
        transactionalPackage1.setProcessEndDate(date);
        return this.repository.save(transactionalPackage1);
    }

    public TransactionalPackage edit(TransactionalPackage transactionalPackage) {
        this.repository.findById(transactionalPackage.getPackageDate()).orElseThrow( () -> new RuntimeException("No transaction package was found for this date, DATE = " + transactionalPackage.getPackageDate().toString()));
        return this.save(transactionalPackage);
    }

    public List<TransactionalPackage> findAll() {
        return StreamSupport.stream(this.repository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public boolean alreadyExist(LocalDate date) {
        return this.repository.findById(date).isPresent();
    }
}
