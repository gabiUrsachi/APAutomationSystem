package org.example.presentation.controllers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
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

    @ExceptionHandler({InvalidFormatException.class, IllegalArgumentException.class})
    public ResponseEntity<ExceptionResponseDTO> handleFormatExceptions(Exception ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), status.toString());

        return new ResponseEntity<>(exceptionResponse, status);
    }

}
