package org.example.presentation.controllers.unit;

import com.system.automation.errorhandling.ExceptionResponseDTO;
import org.example.business.errorhandling.customexceptions.InvalidUpdateException;
import org.example.business.errorhandling.customexceptions.OrderNotFoundException;

import org.example.presentation.controllers.ControllerAdvice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ControllerAdviceShould {

    ControllerAdvice controllerAdvice;
    @Mock
    OrderNotFoundException orderNotFoundException;
    @Mock
    InvalidUpdateException invalidUpdateException;

    @Before
    public void setUp() {
        controllerAdvice = new ControllerAdvice();
    }

    @Test
    public void returnNotFoundStatusWhenOrderDoesNotExist() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = controllerAdvice.handleObjectNotFoundException(orderNotFoundException);

        assertEquals(HttpStatus.NOT_FOUND, exceptionResponse.getStatusCode());
    }

    @Test
    public void returnUnprocessableEntityStatusForInvalidUpdate() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = controllerAdvice.handleInvalidUpdateException(invalidUpdateException);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionResponse.getStatusCode());
    }

}