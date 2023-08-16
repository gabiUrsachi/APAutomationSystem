package org.example.business.errorhandling.customexceptions;

import java.util.UUID;


public class AlreadyExistingUserException extends RuntimeException{
    public AlreadyExistingUserException(String message, String username) {
        super(message + username);
    }
}
