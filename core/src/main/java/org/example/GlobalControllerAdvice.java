package org.example;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.example.customexceptions.*;
import org.example.utils.ExceptionResponseDTO;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler({ResourceNotFoundException.class, NoSuchKeyException.class})
    public ResponseEntity<ExceptionResponseDTO> handleResourceNotFoundException(RuntimeException ex) {
        System.out.println("Resource not found handler: "+ex.getClass()+" -> "+ex.getMessage());
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(AlreadyExistingResourceException.class)
    public ResponseEntity<ExceptionResponseDTO> handleAlreadyExistingResourceException(AlreadyExistingResourceException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(InvalidResourceUpdateException.class)
    public ResponseEntity<ExceptionResponseDTO> handleInvalidUpdateException(InvalidResourceUpdateException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({InvalidFormatException.class, IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ExceptionResponseDTO> handleFormatExceptions(Exception ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), status.toString());

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ExceptionResponseDTO> handleOptimisticLockingFailureException(OptimisticLockingFailureException ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.PRECONDITION_FAILED;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler({ForbiddenActionException.class})
    public ResponseEntity<ExceptionResponseDTO> handleInvalidRoleException(Exception ex) {
        String details = ex.getMessage();
        HttpStatus status = HttpStatus.FORBIDDEN;

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

    @ExceptionHandler(NoSuchBucketException.class)
    public ResponseEntity<ExceptionResponseDTO> handleAWSException(NoSuchBucketException ex) {
        System.out.println("NoSuchBucketException handler: "+ex.getMessage());
        String details = "Server error";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDTO> handleGenericException(Exception ex) {
        System.out.println("Generic handler: "+ex.getClass()+" -> "+ex.getMessage());
        String details = "Server error";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(status.name(), status.value(), details);

        return new ResponseEntity<>(exceptionResponse, status);
    }
}
