package com.pvmeira.wildwest.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(setterPrefix = "with")
public class UploadFileRegister {

    private LocalDateTime processStart;
    private LocalDateTime processEnd;
    private String fileName;
    private String status;
}
