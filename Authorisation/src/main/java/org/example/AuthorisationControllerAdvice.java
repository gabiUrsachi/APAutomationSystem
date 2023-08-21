package org.example;

import com.auth0.jwt.exceptions.JWTVerificationException;

import org.example.customexceptions.ForbiddenUpdateException;
import org.example.customexceptions.IdentifiersMismatchException;
import org.example.utils.ExceptionResponseDTO;
import org.example.customexceptions.InvalidRoleException;
import org.example.customexceptions.InvalidTokenException;
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

    @ExceptionHandler({InvalidRoleException.class, IdentifiersMismatchException.class, ForbiddenUpdateException.class})
    public ResponseEntity<ExceptionResponseDTO> handleInvalidRoleException(Exception ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.FORBIDDEN;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }
}
