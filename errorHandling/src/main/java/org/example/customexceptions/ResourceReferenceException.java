package org.example.customexceptions;

public class ResourceReferenceException extends RuntimeException{
    public ResourceReferenceException(String message) {
        super(message);
    }
}