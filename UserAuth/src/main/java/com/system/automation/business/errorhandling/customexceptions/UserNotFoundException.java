package com.system.automation.business.errorhandling.customexceptions;

import java.util.UUID;

/**
 * This exception is thrown whenever a non-existent user is queried
 */
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message, String username) {
        super(message + username);
    }
}
