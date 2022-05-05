package com.pvmeira.wildwest.dto;

import com.pvmeira.wildwest.model.Transaction;
import com.pvmeira.wildwest.model.TransactionalPackage;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(setterPrefix = "with")
public class ThreatAssessment {

    private List<Transaction> transactionsAnalysed;
    private List<Transaction> suspiciousTransactions;
    private List<SuspctedAccount> suspiciousAccounts;
    private List<SuspctedAgency> suspiciousAgencies;
}
