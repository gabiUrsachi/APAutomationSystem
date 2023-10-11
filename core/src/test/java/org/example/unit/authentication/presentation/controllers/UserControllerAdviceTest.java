package org.example.unit.authentication.presentation.controllers;


import org.example.customexceptions.AlreadyExistingUserException;
import org.example.customexceptions.InvalidCredentialsException;
import org.example.presentation.controllers.UserControllerAdvice;
import org.example.utils.ExceptionResponseDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerAdviceTest {

    UserControllerAdvice userControllerAdvice;

    @Mock
    AlreadyExistingUserException alreadyExistingUserException;

    @Mock
    InvalidCredentialsException invalidCredentialsException;
    @Mock
    Exception exception;

    @Before
    public void setUp() {
        userControllerAdvice = new UserControllerAdvice();
    }

    @Test
    public void returnConflictStatusWhenUserAlreadyExists() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = userControllerAdvice.handleAlreadyExistingUserException(alreadyExistingUserException);

        assertEquals(HttpStatus.CONFLICT, exceptionResponse.getStatusCode());
    }

    @Test
    public void returnUnauthorizedStatusForInvalidUserCredentials() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = userControllerAdvice.handleInvalidCredentialsException(invalidCredentialsException);

        assertEquals(HttpStatus.UNAUTHORIZED, exceptionResponse.getStatusCode());
    }

    @Test
    public void returnServerErrorStatusForGenericExceptions() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = userControllerAdvice.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exceptionResponse.getStatusCode());
    }

}