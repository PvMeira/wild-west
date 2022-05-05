package com.pvmeira.wildwest.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(setterPrefix = "with")
public class SuspctedAccount {
    private String bankName;
    private String bankAgency;
    private String bankAccount;
    private BigDecimal amount;
    private String movimentType;


    public static SuspctedAccount create(String key, BigDecimal value) {
        String[] strings = key.split("<split>");
        return SuspctedAccount.builder()
                .withAmount(value)
                .withBankName(strings[0])
                .withBankAgency(strings[1])
                .withBankAccount(strings[2])
                .withMovimentType(strings[3])
                .build();
    }
}
