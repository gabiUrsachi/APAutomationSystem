package org.example.customexceptions;

public class AlreadyExistingResourceException extends RuntimeException{
    public AlreadyExistingResourceException(String message, String username) {
        super(message + username);
    }
}
