package com.pvmeira.wildwest.service;

import com.pvmeira.wildwest.configuration.PasswordEncoderConfig;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

private PasswordEncoderConfig passwordEncoderConfig;


    @Autowired
    public PasswordService(PasswordEncoderConfig passwordEncoderConfig) {
        this.passwordEncoderConfig = passwordEncoderConfig;
    }

    public String createFirstAcessPassword(String randomNumber) {
        return this.passwordEncoderConfig.passwordEncoder().encode(randomNumber);
    }

    public String getRandomNumber() {
        return String.valueOf(RandomUtils.nextLong(1,999999));
    }
 }
