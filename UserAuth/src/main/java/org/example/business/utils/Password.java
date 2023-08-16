package org.example.business.utils;


import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class Password {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(15);

    public static String encrypt(String password){
        return bCryptPasswordEncoder.encode(password);
    }

    public static boolean checkPassword(String passwordPlaintext, String storedHash) {
        if(null == storedHash || !storedHash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        return BCrypt.checkpw(passwordPlaintext, storedHash);
    }
}
