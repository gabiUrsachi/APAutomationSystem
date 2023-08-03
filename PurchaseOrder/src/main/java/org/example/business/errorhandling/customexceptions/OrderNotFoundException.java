package org.example.business.errorhandling.customexceptions;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(String message, UUID uuid) {
        super(message + uuid);
    }
}
