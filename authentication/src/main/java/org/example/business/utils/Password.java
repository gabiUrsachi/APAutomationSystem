package org.example.business.utils;


import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * This class is used for handling password checks
 */
public class Password {
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(15);

    /**
     * It encrypts the received plain text password
     *
     * @param password plain text password
     * @return encrypted password
     */
    public static String encrypt(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    /**
     * It verifies if the received plain text password is valid according to the encrypted stored one
     *
     * @param passwordPlaintext plain text password
     * @param storedHash        encrypted stored password
     * @return true if there is a match between the above two, otherwise it returns false
     */
    public static boolean checkPassword(String passwordPlaintext, String storedHash) {
        if (null == storedHash || !storedHash.startsWith("$2a$"))
            throw new RuntimeException("Invalid hash provided for comparison");

        return BCrypt.checkpw(passwordPlaintext, storedHash);
    }
}
