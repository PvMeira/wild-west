package com.pvmeira.wildwest.service;

import org.springframework.stereotype.Service;

@Service
public class PasswordService {


    public String createFirstAcessPassword(String randomNumber) {
        return "u484774387348";
    }

    public String getRandomNumber() {
        return "123456";
    }
 }
