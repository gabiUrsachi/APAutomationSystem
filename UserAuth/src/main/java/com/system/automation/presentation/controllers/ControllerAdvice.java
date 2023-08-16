package com.system.automation.presentation.controllers;

import com.system.automation.business.errorhandling.customexceptions.AlreadyExistingUserException;

import com.system.automation.errorhandling.ExceptionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(AlreadyExistingUserException.class)
    public ResponseEntity<ExceptionResponseDTO> handleInvalidUpdateException(AlreadyExistingUserException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }
}
