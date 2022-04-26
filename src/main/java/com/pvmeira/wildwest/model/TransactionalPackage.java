package com.pvmeira.wildwest.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Entity(name = "TRANSACTIONS_PACKAGE")
public class TransactionalPackage {

    @Id
    @Column(name = "package_date", nullable = false)
    private LocalDate packageDate;

    @Column(name = "total_transactions")
    private Integer totalTransactions;

    @Column(name = "total_error_transactions")
    private Integer totalErrorTransactions;

    @Column(name = "process_start_date", nullable = false)
    private LocalDateTime processStartDate;

    @Column(name = "process_end_date")
    private LocalDateTime processEndDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "username")
    private Users user;


}
