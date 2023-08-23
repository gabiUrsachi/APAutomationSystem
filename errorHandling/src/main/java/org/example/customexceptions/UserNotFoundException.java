package org.example.customexceptions;

/**
 * This exception is thrown whenever a non-existent user is queried
 */
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message, String username) {
        super(message + username);
    }
}
