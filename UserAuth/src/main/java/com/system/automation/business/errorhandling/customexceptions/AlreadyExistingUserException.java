package com.system.automation.business.errorhandling.customexceptions;

import java.util.UUID;


public class AlreadyExistingUserException extends RuntimeException{
    public AlreadyExistingUserException(String message, String username) {
        super(message + username);
    }
}
