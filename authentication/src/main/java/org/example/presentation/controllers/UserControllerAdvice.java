package org.example.presentation.controllers;


import org.example.customexceptions.AlreadyExistingUserException;
import org.example.utils.ExceptionResponseDTO;
import org.example.customexceptions.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(AlreadyExistingUserException.class)
    public ResponseEntity<ExceptionResponseDTO> handleAlreadyExistingUserException(AlreadyExistingUserException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ExceptionResponseDTO> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDTO> handleGenericException(Exception ex) {
        String details = "";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        System.out.println("Generic exception: "+ex.getMessage());

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }
}
