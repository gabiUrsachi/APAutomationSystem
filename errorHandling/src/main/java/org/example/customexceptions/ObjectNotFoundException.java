package org.example.customexceptions;

import java.util.UUID;

/**
 * This exception is thrown whenever a non-existent object is queried
 */
public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message, String objectKey) {
        super(message + objectKey);
    }
}
