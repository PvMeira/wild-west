package com.pvmeira.wildwest.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Entity(name = "TRANSACTIONS")
public class Transaction {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "origin_bank_name", nullable = false)
    private String originBankName;
    @Column(name = "origin_bank_agency", nullable = false)
    private String originBankAgency;
    @Column(name = "origin_account", nullable = false)
    private String originAccount;
    @Column(name = "destiny_bank_name", nullable = false)
    private String destinyBankName;
    @Column(name = "destiny_bank_agency", nullable = false)
    private String destinyBankAgency;
    @Column(name = "destiny_account", nullable = false)
    private String destinyAccount;
    @Column(name = "transaction_value", nullable = false)
    private BigDecimal transactionValue;
    @Column(name = "date_of_transaction", nullable = false)
    private LocalDateTime dateOfTransaction;

    @ManyToOne(optional = false)
    @JoinColumn(name = "transactional_package_date")
    private TransactionalPackage transactionalPackage;


}
