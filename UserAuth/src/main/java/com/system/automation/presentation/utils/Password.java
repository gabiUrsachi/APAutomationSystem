package com.system.automation.presentation.utils;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class Password {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(15);

    public static String encrypt(String password){
        return bCryptPasswordEncoder.encode(password);
    }
}
