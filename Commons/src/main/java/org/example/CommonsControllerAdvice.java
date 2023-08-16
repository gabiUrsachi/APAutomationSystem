package org.example;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.example.errorhandling.ExceptionResponseDTO;
import org.example.errorhandling.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class CommonsControllerAdvice {

    @ExceptionHandler({InvalidTokenException.class, JWTVerificationException.class})
    public ResponseEntity<ExceptionResponseDTO> handleInvalidUpdateException(Exception ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }
}
