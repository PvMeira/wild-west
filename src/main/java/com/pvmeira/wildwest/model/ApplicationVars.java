package com.pvmeira.wildwest.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Entity(name = "APPLICATION_VARS")
public class ApplicationVars {

    @Id
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "value", nullable = false)
    private String value;
    @Column(name = "STATUS", nullable = false)
    private String status;


    public BigDecimal getValueAsBigDecimal() {
        return new BigDecimal(value);
    }
}
