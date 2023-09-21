package org.example.presentation.controllers;

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

}
