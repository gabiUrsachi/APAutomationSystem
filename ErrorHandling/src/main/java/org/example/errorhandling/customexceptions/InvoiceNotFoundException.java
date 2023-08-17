package org.example.errorhandling.customexceptions;

import java.util.UUID;
public class InvoiceNotFoundException extends RuntimeException{
        public InvoiceNotFoundException(String message) {
            super(message);
        }
    }

