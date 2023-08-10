package org.example.presentation.controllers;

import org.example.business.errorhandling.customexceptions.InvalidUpdateException;
import org.example.business.errorhandling.customexceptions.OrderNotFoundException;
import org.example.presentation.view.ExceptionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ExceptionResponseDTO> handleObjectNotFoundException(OrderNotFoundException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(InvalidUpdateException.class)
    public ResponseEntity<ExceptionResponseDTO> handleInvalidUpdateException(InvalidUpdateException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }
}
