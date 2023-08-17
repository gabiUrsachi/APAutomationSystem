package org.example.errorhandling.customexceptions;


public class AlreadyExistingUserException extends RuntimeException{
    public AlreadyExistingUserException(String message, String username) {
        super(message + username);
    }
}
