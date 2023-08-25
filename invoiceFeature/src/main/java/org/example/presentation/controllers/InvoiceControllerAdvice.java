package org.example.presentation.controllers;

import org.example.customexceptions.InvalidUpdateException;
import org.example.customexceptions.InvoiceNotFoundException;
import org.example.customexceptions.OrderNotFoundException;
import org.example.utils.ExceptionResponseDTO;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;


@org.springframework.web.bind.annotation.ControllerAdvice
public class InvoiceControllerAdvice {

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ResponseEntity<ExceptionResponseDTO> handleObjectNotFoundException(InvoiceNotFoundException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }

}
