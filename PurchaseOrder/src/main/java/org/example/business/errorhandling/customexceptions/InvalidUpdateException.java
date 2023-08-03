package org.example.business.errorhandling.customexceptions;

import java.util.UUID;

public class InvalidUpdateException extends RuntimeException{
    public InvalidUpdateException(String message, UUID uuid) {
        super(message + uuid);
    }
}
