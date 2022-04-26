package com.pvmeira.wildwest.service;

import com.pvmeira.wildwest.dto.RawFileRead;
import com.pvmeira.wildwest.exception.ApplicationException;
import com.pvmeira.wildwest.exception.PackageAlreadyExistException;
import com.pvmeira.wildwest.exception.TransactionException;
import com.pvmeira.wildwest.model.Transaction;
import com.pvmeira.wildwest.model.TransactionalPackage;
import com.pvmeira.wildwest.model.Users;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class RawFileService {

    private final static String DATE_TIME_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss";
    private final static String SPLIT_DELIMITER = ",";

    private final TransactionService transactionService;
    private final TransactionalPackageService transactionalPackageService;

    @Autowired
    public RawFileService(TransactionService transactionService, TransactionalPackageService transactionalPackageService) {
        this.transactionService = transactionService;
        this.transactionalPackageService = transactionalPackageService;
    }

    public RawFileRead read(MultipartFile file, Users user)  throws  ApplicationException {
        List<Transaction> records = new ArrayList<>();
        Integer totalTransactionsWithError = 0;
        Integer totalTransactions = 0;
        LocalDate transactionalDate = null;
        LocalDateTime start = LocalDateTime.now();


        try {
            Scanner scanner = new Scanner(file.getInputStream());
            while (scanner.hasNextLine()) {
                try {
                    totalTransactions++;
                    Transaction transaction = this.getTransactionFromLine(scanner.nextLine());
                    if (transactionalDate == null) {
                        transactionalDate = transaction.getDateOfTransaction().toLocalDate();
                        if (this.transactionalPackageService.alreadyExist(transactionalDate))
                            throw new PackageAlreadyExistException("Já existe um processamento para o Dia : " + transactionalDate.toString());
                    }
                    if (transactionalDate.isEqual(transaction.getDateOfTransaction().toLocalDate())) {
                        records.add(transaction);
                        continue;
                    }
                    // since the rule is to ignore a transaction that contains a different data
                    totalTransactionsWithError++;

                } catch (TransactionException e) {
                    totalTransactionsWithError++;
                }
                catch (PackageAlreadyExistException e1) {
                    throw new ApplicationException(e1.getMessage());
                }
            }
        } catch (IOException e) {
            throw new ApplicationException("Ocorreu um erro inesperado ao ler o conteúdo do arquivo de nome :  " + file.getOriginalFilename());
        }
        final TransactionalPackage transactionalPackage = this.transactionalPackageService.save(TransactionalPackage.builder()
                .withPackageDate(transactionalDate)
                .withProcessStartDate(start)
                .withUser(user)
                .withTotalTransactions(totalTransactions)
                .withTotalErrorTransactions(totalTransactionsWithError)

                .build());
        records = records.stream().map(a ->a.withTransactionalPackage(transactionalPackage)).collect(Collectors.toList());
        this.transactionService.saveAll(records);
        this.transactionalPackageService.finishProcess(transactionalPackage, LocalDateTime.now());

        return RawFileRead.builder()
                .withRecords(records)
                .withTotalRecordsOnFile(totalTransactions)
                .withTotalRecordsWithError(totalTransactionsWithError)
                .withOriginalFileName(file.getOriginalFilename())
                .withTransactionalDate(transactionalDate)
                .build();
    }

    private Transaction getTransactionFromLine(String line) throws TransactionException {
        String[] data = line.split(SPLIT_DELIMITER);
        if (data.length != 8 )
            throw new TransactionException("A linha da transação não contém todos os dados necessários para o processamento.");
        Transaction.TransactionBuilder builder = this.buildOriginBankInformation(data);
        builder = buildDestinyBankInformation(data, builder);
        builder = buildOtherInfos(data, builder);
        return builder.build();
    }

    private Transaction.TransactionBuilder buildOriginBankInformation(String[] data) throws TransactionException {
        String bankName = data[0];
        String bankAgency = data[1];
        String bankAccount = data[2];

        if (StringUtils.isAnyBlank(bankAccount, bankAgency, bankName))
            throw new TransactionException("There is data missing for the origin data.");
        return Transaction.builder()
                .withOriginBankName(bankName)
                .withOriginBankAgency(bankAgency)
                .withOriginAccount(bankAccount);
    }

    private Transaction.TransactionBuilder buildDestinyBankInformation(String[] data, Transaction.TransactionBuilder builder) throws TransactionException {
        String bankName = data[3];
        String bankAgency = data[4];
        String bankAccount = data[5];

        if (StringUtils.isAnyBlank(bankAccount, bankAgency, bankName))
            throw new TransactionException("There is data missing for the Bank Destiny data.");
        return builder
                .withDestinyBankName(bankName)
                .withDestinyBankAgency(bankAgency)
                .withDestinyAccount(bankAccount);
    }

    private Transaction.TransactionBuilder buildOtherInfos(String[] data, Transaction.TransactionBuilder builder) throws TransactionException {
        String value = data[6];
        String date = data[7];

        if (StringUtils.isAnyBlank(value,date))
            throw new TransactionException("There is data missing for the Remaining data.");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);

        try {
            builder.withDateOfTransaction(LocalDateTime.parse(date, formatter));
        } catch (Exception e) {
            throw new TransactionException("A error has occur while parsing the received date, DATE = ." + date);
        }

        return builder
                .withTransactionValue(new BigDecimal(value));
    }

}
