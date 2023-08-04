package org.example.business.errorhandling.customexceptions;

import java.util.UUID;

/**
 * This exception is thrown whenever a non-existent order is queried
 */
public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(String message, UUID uuid) {
        super(message + uuid);
    }
}
