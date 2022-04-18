package com.pvmeira.wildwest.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
public class UserRequest {

    private String name;
    private String email;
}
