package com.pvmeira.wildwest.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(setterPrefix = "with")
public class SuspctedAgency {

    private String bankName;
    private String bankAgency;
    private BigDecimal amount;
    private String movimentType;

    public static SuspctedAgency create(String key, BigDecimal value) {
        String[] strings = key.split("<split>");
        return SuspctedAgency.builder()
                .withAmount(value)
                .withBankName(strings[0])
                .withBankAgency(strings[1])
                .withMovimentType(strings[2])
                .build();
    }
}
