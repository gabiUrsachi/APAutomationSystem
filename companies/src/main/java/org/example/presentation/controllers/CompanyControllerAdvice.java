package org.example.presentation.controllers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.example.customexceptions.CompanyNotFoundException;
import org.example.utils.ExceptionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class CompanyControllerAdvice {

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ExceptionResponseDTO> handleObjectNotFoundException(CompanyNotFoundException ex) {
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
