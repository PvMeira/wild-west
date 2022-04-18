package com.pvmeira.wildwest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String status;
}
