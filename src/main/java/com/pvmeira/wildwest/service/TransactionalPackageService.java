package com.pvmeira.wildwest.service;

import com.pvmeira.wildwest.dto.SuspctedAccount;
import com.pvmeira.wildwest.dto.SuspctedAgency;
import com.pvmeira.wildwest.dto.ThreatAssessment;
import com.pvmeira.wildwest.exception.ApplicationException;
import com.pvmeira.wildwest.model.ApplicationVars;
import com.pvmeira.wildwest.model.Transaction;
import com.pvmeira.wildwest.model.TransactionalPackage;
import com.pvmeira.wildwest.repository.TransactionalPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class TransactionalPackageService {

    private final TransactionalPackageRepository repository;
    private final TransactionService transactionService;
    private final ApplicationVarsService varsService;

    @Autowired
    public TransactionalPackageService(TransactionalPackageRepository repository, TransactionService transactionService, ApplicationVarsService varsService) {
        this.repository = repository;
        this.transactionService = transactionService;
        this.varsService = varsService;
    }


    public TransactionalPackage find(LocalDate packageDate) {
        if (null == packageDate)
            throw new ApplicationException("Package Date must no be null");
        return this.repository.findById(packageDate).orElse(null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionalPackage save(TransactionalPackage transactionalPackage) {
        if (null == transactionalPackage)
            throw new ApplicationException("TransactionPackage data is required for this ops.");
        return this.repository.save(transactionalPackage);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionalPackage finishProcess(TransactionalPackage transactionalPackage, LocalDateTime date) {
        TransactionalPackage transactionalPackage1 = this.repository.findById(transactionalPackage.getPackageDate()).orElseThrow( () -> new RuntimeException("No transaction package was found for this date, DATE = " + transactionalPackage.getPackageDate().toString()));
        transactionalPackage1.setProcessEndDate(date);
        return this.repository.save(transactionalPackage1);
    }

    public List<TransactionalPackage> findAll() {
        return StreamSupport.stream(this.repository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public boolean alreadyExist(LocalDate date) {
        return this.repository.findById(date).isPresent();
    }

    public ThreatAssessment analyseTransactions(LocalDate date) {
        List<Transaction> transactions = this.transactionService.findAllByMonth(date);
        Map<String, ApplicationVars> applicationVars = this.varsService.findAll();
        if (null == transactions || transactions.isEmpty())
            throw new ApplicationException("Nenhuma transação encontrada.");

        List<Transaction> suspiciousTransactions = this.findSuspiciousTransactions(transactions, applicationVars.get("SUSPECT_TRANSACTION"));
        List<SuspctedAccount> suspiciousAccounts = this.findSuspiciousAccounts(transactions, applicationVars.get("SUSPECT_ACCOUNT"));
        List<SuspctedAgency> suspiciousAgencies = this.findSuspiciousAgency(transactions, applicationVars.get("SUSPECT_AGENCY"));
        return ThreatAssessment.builder()
                               .withTransactionsAnalysed(transactions)
                               .withSuspiciousTransactions(suspiciousTransactions)
                               .withSuspiciousAccounts(suspiciousAccounts)
                               .withSuspiciousAgencies(suspiciousAgencies)
                               .build();

    }

    public List<Transaction> findSuspiciousTransactions(List<Transaction> baseTransactions, ApplicationVars transactionVars) {
        if (null == baseTransactions || baseTransactions.isEmpty())
            return new ArrayList<>(0);
        return baseTransactions.stream()
                               .filter( transaction -> transaction.getTransactionValue().compareTo(transactionVars.getValueAsBigDecimal()) != -1)
                               .collect(Collectors.toList());
    }

    public List<SuspctedAccount> findSuspiciousAccounts(List<Transaction> baseTransactions, ApplicationVars transactionVars) {
        if (null == baseTransactions || baseTransactions.isEmpty())
            return new ArrayList<>(0);
        Map<String, BigDecimal> accounts = new HashMap<>();
        for (Transaction transaction : baseTransactions) {
            this.registerOnMap(accounts, transaction.getAccountOrigin(), transaction.getTransactionValue());
            this.registerOnMap(accounts, transaction.getAccountDestiny(), transaction.getTransactionValue());

        }
        return accounts.entrySet()
                       .stream()
                       .map(a -> SuspctedAccount.create(a.getKey(), a.getValue()))
                       .filter(a -> a.getAmount().compareTo(transactionVars.getValueAsBigDecimal()) != -1 )
                       .collect(Collectors.toList());
    }

    public List<SuspctedAgency> findSuspiciousAgency(List<Transaction> baseTransactions, ApplicationVars transactionVars) {
        if (null == baseTransactions || baseTransactions.isEmpty())
            return new ArrayList<>(0);
        Map<String, BigDecimal> accounts = new HashMap<>();
        for (Transaction transaction : baseTransactions) {
            this.registerOnMap(accounts, transaction.getAgencyOrigin(), transaction.getTransactionValue());
            this.registerOnMap(accounts, transaction.getAgencyDestiny(), transaction.getTransactionValue());

        }
        return accounts.entrySet()
                .stream()
                .map(a -> SuspctedAgency.create(a.getKey(), a.getValue()))
                .filter(a -> a.getAmount().compareTo(transactionVars.getValueAsBigDecimal()) != -1 )
                .collect(Collectors.toList());
    }

    private void registerOnMap(Map<String, BigDecimal> accounts, String key, BigDecimal value) {
        if (accounts.containsKey(key)) {
            accounts.replace(key, accounts.get(key).add(value));
        } else {
            accounts.put(key, value);
        }
    }
}
