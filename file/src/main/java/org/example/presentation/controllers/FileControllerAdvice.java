package org.example.presentation.controllers;

import org.example.customexceptions.ObjectNotFoundException;
import org.example.utils.ExceptionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FileControllerAdvice {
    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ExceptionResponseDTO> handleObjectNotFoundException2(ObjectNotFoundException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }
}
