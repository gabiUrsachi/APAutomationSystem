package org.example;

import org.example.customexceptions.AlreadyExistingResourceException;
import org.example.customexceptions.InvalidCredentialsException;
import org.example.customexceptions.InvalidResourceUpdateException;
import org.example.customexceptions.ResourceNotFoundException;
import org.example.utils.ExceptionResponseDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GlobalControllerAdviceShould {

    GlobalControllerAdvice globalControllerAdvice;
    @Mock
    Exception exception;
    @Mock
    ResourceNotFoundException resourceNotFoundException;
    @Mock
    InvalidCredentialsException invalidCredentialsException;
    @Mock
    AlreadyExistingResourceException alreadyExistingResourceException;
    @Mock
    InvalidResourceUpdateException invalidResourceUpdateException;
    @Mock
    OptimisticLockingFailureException optimisticLockingFailureException;

    @Before
    public void setUp() {
        globalControllerAdvice = new GlobalControllerAdvice();
    }

    @Test
    public void returnNotFoundStatusWhenResourceDoesNotExist() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = globalControllerAdvice.handleResourceNotFoundException(resourceNotFoundException);

        assertEquals(HttpStatus.NOT_FOUND, exceptionResponse.getStatusCode());
    }

    @Test
    public void returnUnprocessableEntityStatusForInvalidUpdate() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = globalControllerAdvice.handleInvalidUpdateException(invalidResourceUpdateException);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionResponse.getStatusCode());
    }

    @Test
    public void returnPreconditionFailedStatusForOptimisticLockingFailureException() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = globalControllerAdvice.handleOptimisticLockingFailureException(optimisticLockingFailureException);

        assertEquals(HttpStatus.PRECONDITION_FAILED, exceptionResponse.getStatusCode());
    }

    @Test
    public void returnConflictStatusWhenResourceAlreadyExists() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = globalControllerAdvice.handleAlreadyExistingResourceException(alreadyExistingResourceException);

        assertEquals(HttpStatus.CONFLICT, exceptionResponse.getStatusCode());
    }

    @Test
    public void returnUnauthorizedStatusForInvalidUserCredentials() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = globalControllerAdvice.handleInvalidCredentialsException(invalidCredentialsException);

        assertEquals(HttpStatus.UNAUTHORIZED, exceptionResponse.getStatusCode());
    }

    @Test
    public void returnServerErrorStatusForGenericExceptions() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = globalControllerAdvice.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatusCode());
    }
}