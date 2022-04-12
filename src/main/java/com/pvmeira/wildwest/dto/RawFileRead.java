package com.pvmeira.wildwest.dto;

import com.pvmeira.wildwest.model.Transaction;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder(setterPrefix = "with")
public class RawFileRead {
    private Integer totalRecordsOnFile;
    private Integer totalRecordsWithError;
    private String originalFileName;
    private LocalDate transactionalDate;
    private List<Transaction> records;
}
