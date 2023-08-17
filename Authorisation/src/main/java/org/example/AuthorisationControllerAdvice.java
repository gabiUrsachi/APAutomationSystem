package org.example;

import com.auth0.jwt.exceptions.JWTVerificationException;

import org.example.errorhandling.utils.ExceptionResponseDTO;
import org.example.errorhandling.customexceptions.InvalidRoleException;
import org.example.errorhandling.customexceptions.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class AuthorisationControllerAdvice {

    @ExceptionHandler({InvalidTokenException.class, JWTVerificationException.class})
    public ResponseEntity<ExceptionResponseDTO> handleInvalidTokenException(Exception ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({InvalidRoleException.class})
    public ResponseEntity<ExceptionResponseDTO> handleInvalidRoleException(InvalidRoleException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.FORBIDDEN;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }
}
