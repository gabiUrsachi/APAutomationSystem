package org.example.customexceptions;

import java.util.UUID;

/**
 * This exception is thrown when an update request cannot be performed
 */
public class InvalidResourceUpdateException extends RuntimeException{
    public InvalidResourceUpdateException(String message, UUID uuid) {
        super(message + uuid);
    }
}
