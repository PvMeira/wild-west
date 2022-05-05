package com.pvmeira.wildwest.service;

import com.pvmeira.wildwest.dto.SuspctedAccount;
import com.pvmeira.wildwest.dto.SuspctedAgency;
import com.pvmeira.wildwest.dto.ThreatAssessment;
import com.pvmeira.wildwest.exception.ApplicationException;
import com.pvmeira.wildwest.model.ApplicationVars;
import com.pvmeira.wildwest.model.Transaction;
import com.pvmeira.wildwest.model.TransactionalPackage;
import com.pvmeira.wildwest.repository.TransactionalPackageRepository;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TransactionalPackageServiceTest {

    private ApplicationVarsService applicationVars = mock(ApplicationVarsService.class);
    private TransactionalPackageRepository repository = mock(TransactionalPackageRepository.class);
    private TransactionService transactionService = mock(TransactionService.class);
    private TransactionalPackageService transactionalPackageService = new TransactionalPackageService(repository, transactionService, applicationVars);

    private static final ApplicationVars SUSPECT_TRANSACTION_VARS = new ApplicationVars("SUSPECT_TRANSACTION","100000.00", null);
    private static final ApplicationVars SUSPECT_ACCOUNT_VARS = new ApplicationVars("SUSPECT_ACCOUNT","200000.50", null);
    private static final ApplicationVars SUSPECT_AGENCY_VARS = new ApplicationVars("SUSPECT_AGENCY","29000.00", null);


    @Test
    public void finishProcessWithAVValidTransactionalPackage() {
        LocalDateTime now = LocalDateTime.now();
        when(this.repository.findById(any())).thenReturn(Optional.of(TransactionalPackage.builder().build()));
        when(this.repository.save(any())).thenReturn(TransactionalPackage.builder().build());
        TransactionalPackage transactionalPackage = this.transactionalPackageService.finishProcess(TransactionalPackage.builder().build(), now);
        assertNotNull(transactionalPackage);
    }

    @Test
    public void saveWithContent() {
        when(this.repository.save(any())).thenReturn(TransactionalPackage.builder().build());
        TransactionalPackage transactionalPackage = this.transactionalPackageService.save(TransactionalPackage.builder().build());
        assertNotNull(transactionalPackage);
    }

    @Test
    public void saveWithNoContent() {
        ApplicationException applicationException = assertThrows(ApplicationException.class, () -> this.transactionalPackageService.save(null));
        assertNotNull(applicationException);
    }


    @Test
    public void findWithResult() {
        when(this.repository.findById(any())).thenReturn(Optional.of(TransactionalPackage.builder().build()));
        TransactionalPackage transactionalPackage = this.transactionalPackageService.find(LocalDate.now());
        assertNotNull(transactionalPackage);
    }

    @Test
    public void findWithNoResult() {
        when(this.repository.findById(any())).thenReturn(Optional.empty());
        TransactionalPackage transactionalPackage = this.transactionalPackageService.find(LocalDate.now());
        assertNull(transactionalPackage);
    }

    @Test
    public void findWithGivenData() {
        ApplicationException applicationException = assertThrows(ApplicationException.class, () -> this.transactionalPackageService.find(null));
        assertTrue(applicationException.getMessage().equals("Package Date must no be null"));
    }

    @Test
    void analyseTransactionsWithNullTransactions() {
        when(transactionService.findAllByMonth(any())).thenReturn(null);
        when(this.applicationVars.findAll()).thenReturn(this.getApplicationVarsMap());
        ApplicationException applicationException = assertThrows(ApplicationException.class, () -> this.transactionalPackageService.analyseTransactions(LocalDate.now()));
        assertTrue(applicationException.getMessage().contains("Nenhuma transação encontrada."));

    }

    @Test
    void analyseTransactionsWithNoTransactionsFound() {
        when(transactionService.findAllByMonth(any())).thenReturn(new ArrayList<>());
        when(this.applicationVars.findAll()).thenReturn(this.getApplicationVarsMap());
        ApplicationException applicationException = assertThrows(ApplicationException.class, () -> this.transactionalPackageService.analyseTransactions(LocalDate.now()));
        assertTrue(applicationException.getMessage().contains("Nenhuma transação encontrada."));

    }

    @Test
    void analyseTransactionsWithTwoSuspectedTransactionsFromEveryAngle() {
        when(transactionService.findAllByMonth(any())).thenReturn(this.getListOfTransactionsWithSuspectedTransactions());
        when(this.applicationVars.findAll()).thenReturn(this.getApplicationVarsMap());
        ThreatAssessment threatAssessment = this.transactionalPackageService.analyseTransactions(LocalDate.now());
        assertNotNull(threatAssessment);
        assertEquals(2, threatAssessment.getSuspiciousTransactions().size());
        assertEquals(2, threatAssessment.getSuspiciousAgencies().size());
        assertEquals(2, threatAssessment.getSuspiciousAccounts().size());
    }

    @Test
    void analyseTransactionsWithNoSuspectedTransactionsFromEveryAngle() {
        when(transactionService.findAllByMonth(any())).thenReturn(this.getListOfTransactionsWithoutSuspectedTransactions());
        when(this.applicationVars.findAll()).thenReturn(this.getApplicationVarsMap());
        ThreatAssessment threatAssessment = this.transactionalPackageService.analyseTransactions(LocalDate.now());
        assertNotNull(threatAssessment);
        assertEquals(0, threatAssessment.getSuspiciousTransactions().size());
        assertEquals(0, threatAssessment.getSuspiciousAgencies().size());
        assertEquals(0, threatAssessment.getSuspiciousAccounts().size());
    }

    @Test
    void findSuspiciousTransactionsWithTwoSuspectedTransactions() {
        List<Transaction> twoSuspectedTransactions = this.getListOfTransactionsWithSuspectedTransactions();
        List<Transaction> suspiciousTransactions = this.transactionalPackageService.findSuspiciousTransactions(twoSuspectedTransactions, SUSPECT_TRANSACTION_VARS);
        assertNotNull(suspiciousTransactions);
        assertEquals(2, suspiciousTransactions.size());
    }

    @Test
    void findSuspiciousTransactionsWithNoSuspectedTransaction() {
        List<Transaction> listOfTransactionsWithoutSuspectedTransactions = this.getListOfTransactionsWithoutSuspectedTransactions();
        List<Transaction> suspiciousTransactions1 = this.transactionalPackageService.findSuspiciousTransactions(listOfTransactionsWithoutSuspectedTransactions, SUSPECT_TRANSACTION_VARS);
        assertNotNull(suspiciousTransactions1);
        assertEquals(0, suspiciousTransactions1.size());
    }

    @Test
    void findSuspiciousTransactionsWithNoTransactions() {
        List<Transaction> transactionList = this.transactionalPackageService.findSuspiciousTransactions(new ArrayList<>(), SUSPECT_TRANSACTION_VARS);
        assertNotNull(transactionList);
        assertEquals(0, transactionList.size());
    }

    @Test
    void findSuspiciousTransactionsWithNull() {
        List<Transaction> transactionList = this.transactionalPackageService.findSuspiciousTransactions(null, SUSPECT_TRANSACTION_VARS);
        assertNotNull(transactionList);
        assertEquals(0, transactionList.size());
    }

    @Test
    void findSuspiciousAccountsWithTwoSuspectedAccounts() {
        List<Transaction> transactions = this.getListOfTransactionsWithSuspectedTransactions();
        List<SuspctedAccount> suspiciousAccounts = this.transactionalPackageService.findSuspiciousAccounts(transactions, SUSPECT_ACCOUNT_VARS);
        assertNotNull(suspiciousAccounts);
        assertEquals(2, suspiciousAccounts.size());
    }

    @Test
    void findSuspiciousAccountsWithNoSuspectedAccounts() {
        List<Transaction> transactions = this.getListOfTransactionsWithoutSuspectedTransactions();
        List<SuspctedAccount> suspiciousAccounts = this.transactionalPackageService.findSuspiciousAccounts(transactions, SUSPECT_ACCOUNT_VARS);
        assertNotNull(suspiciousAccounts);
        assertEquals(0, suspiciousAccounts.size());
    }

    @Test
    void findSuspiciousAccountsWithNoTransactions() {
        List<SuspctedAccount> suspiciousAccounts = this.transactionalPackageService.findSuspiciousAccounts(new ArrayList<>(), SUSPECT_ACCOUNT_VARS);
        assertNotNull(suspiciousAccounts);
        assertEquals(0, suspiciousAccounts.size());
    }

    @Test
    void findSuspiciousAccountsWithTransactionsNull() {
        List<SuspctedAccount> suspiciousAccounts = this.transactionalPackageService.findSuspiciousAccounts(null, SUSPECT_ACCOUNT_VARS);
        assertNotNull(suspiciousAccounts);
        assertEquals(0, suspiciousAccounts.size());
    }

    @Test
    void findSuspiciousAgenciesWithTransactionsNull() {
        List<SuspctedAgency> data = this.transactionalPackageService.findSuspiciousAgency(null, SUSPECT_AGENCY_VARS);
        assertNotNull(data);
        assertEquals(0, data.size());
    }

    @Test
    void findSuspiciousAgenciesWithNoTransactions() {
        List<SuspctedAgency> data = this.transactionalPackageService.findSuspiciousAgency(new ArrayList<>(), SUSPECT_AGENCY_VARS);
        assertNotNull(data);
        assertEquals(0, data.size());
    }

    @Test
    void findSuspiciousAgenciesWithTwoSuspectedAccounts() {
        List<Transaction> transactions = this.getListOfTransactionsWithSuspectedTransactions();
        List<SuspctedAgency> suspiciousAccounts = this.transactionalPackageService.findSuspiciousAgency(transactions, SUSPECT_AGENCY_VARS);
        assertNotNull(suspiciousAccounts);
        assertEquals(2, suspiciousAccounts.size());
    }

    @Test
    void findSuspiciousAgenciesWithNoSuspectedAccounts() {
        List<Transaction> transactions = this.getListOfTransactionsWithoutSuspectedTransactions();
        List<SuspctedAgency> suspiciousAccounts = this.transactionalPackageService.findSuspiciousAgency(transactions, SUSPECT_AGENCY_VARS);
        assertNotNull(suspiciousAccounts);
        assertEquals(0, suspiciousAccounts.size());
    }


    private  List<Transaction> getListOfTransactionsWithSuspectedTransactions() {
        List<Transaction> transactionList = new ArrayList<>(10);
        transactionList.add(Transaction.builder()
                .withTransactionValue(new BigDecimal("100000.50"))
                        .withDestinyBankName("Banco do Brasil")
                        .withDestinyBankAgency("0001")
                        .withDestinyAccount("00001-1")
                        .withOriginBankName("Banco Banrisul")
                        .withOriginBankAgency("0001")
                        .withOriginAccount("00001-2")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("200000.50"))
                .withDestinyBankName("Banco do Brasil")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-1")
                .withOriginBankName("Banco Banrisul")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-2")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Santander")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-1")
                .withOriginBankName("Banco do Brasil")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Santander")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-1")
                .withOriginBankName("Banco Banrisul")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-2")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Santander")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-1")
                .withOriginBankName("Banco Banrisul")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-2")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Banrisul")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-2")
                .withOriginBankName("Banco Santander")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1").build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Banrisul")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-2")
                .withOriginBankName("Banco Bradesco")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Banrisul")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-2")
                .withOriginBankName("Banco Bradesco")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Banrisul")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-2")
                .withOriginBankName("Banco Bradesco")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Banrisul")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-2")
                .withOriginBankName("Banco Bradesco")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1")
                .build());
        return transactionList;
    }

    private  List<Transaction> getListOfTransactionsWithoutSuspectedTransactions() {
        List<Transaction> transactionList = new ArrayList<>(10);
        transactionList.add(Transaction.builder()
                .withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco do Brasil")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-1")
                .withOriginBankName("Banco Banrisul")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-2")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco do Brasil")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-1")
                .withOriginBankName("Banco Banrisul")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-2")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Santander")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-1")
                .withOriginBankName("Banco do Brasil")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Santander")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-1")
                .withOriginBankName("Banco Banrisul")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-2")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Santander")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-1")
                .withOriginBankName("Banco Banrisul")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-2")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Banrisul")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-2")
                .withOriginBankName("Banco Santander")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1").build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Banrisul")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-2")
                .withOriginBankName("Banco Bradesco")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Banrisul")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-2")
                .withOriginBankName("Banco Bradesco")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Banrisul")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-2")
                .withOriginBankName("Banco Bradesco")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1")
                .build());
        transactionList.add(Transaction.builder().withTransactionValue(new BigDecimal("1000.00"))
                .withDestinyBankName("Banco Banrisul")
                .withDestinyBankAgency("0001")
                .withDestinyAccount("00001-2")
                .withOriginBankName("Banco Bradesco")
                .withOriginBankAgency("0001")
                .withOriginAccount("00001-1")
                .build());
        return transactionList;
    }

    private Map<String, ApplicationVars> getApplicationVarsMap() {
        Map<String, ApplicationVars> applicationVars = new HashMap<>();
        applicationVars.put("SUSPECT_TRANSACTION", SUSPECT_TRANSACTION_VARS);
        applicationVars.put("SUSPECT_ACCOUNT", SUSPECT_ACCOUNT_VARS);
        applicationVars.put("SUSPECT_AGENCY", SUSPECT_AGENCY_VARS);
        return applicationVars;
    }
}
