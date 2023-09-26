package org.example.customexceptions;

import java.util.UUID;

/**
 * This exception is thrown whenever a non-existent company is queried
 */
public class CompanyNotFoundException extends RuntimeException{
    public CompanyNotFoundException(String message, UUID uuid) {
        super(message + uuid);
    }
}