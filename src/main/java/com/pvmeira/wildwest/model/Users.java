package com.pvmeira.wildwest.model;

import lombok.*;

import javax.persistence.*;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@Entity(name = "users")
public class Users {

    @Id
    @Column(name = "username", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "STATUS", nullable = false, length = 1)
    private String status;
}
