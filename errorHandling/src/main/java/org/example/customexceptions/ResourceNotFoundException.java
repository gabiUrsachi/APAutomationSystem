package org.example.customexceptions;

/**
 * This exception is thrown whenever a non-existent resource is queried
 */
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message, String identifier) {
        super(message + identifier);
    }
}