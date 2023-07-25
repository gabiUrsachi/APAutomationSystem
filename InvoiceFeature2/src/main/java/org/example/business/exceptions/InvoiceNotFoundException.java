package org.example.business.exceptions;

import java.util.UUID;
public class InvoiceNotFoundException extends RuntimeException{
        public InvoiceNotFoundException(String message) {
            super(message);
        }
    }

