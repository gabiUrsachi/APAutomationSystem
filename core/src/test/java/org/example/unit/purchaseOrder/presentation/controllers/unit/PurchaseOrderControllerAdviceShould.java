package org.example.unit.purchaseOrder.presentation.controllers.unit;

import org.example.customexceptions.InvalidUpdateException;
import org.example.customexceptions.OrderNotFoundException;
import org.example.presentation.controllers.PurchaseOrderControllerAdvice;
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
public class PurchaseOrderControllerAdviceShould {

    PurchaseOrderControllerAdvice purchaseOrderControllerAdvice;
    @Mock
    OrderNotFoundException orderNotFoundException;
    @Mock
    InvalidUpdateException invalidUpdateException;
    @Mock
    OptimisticLockingFailureException optimisticLockingFailureException;

    @Before
    public void setUp() {
        purchaseOrderControllerAdvice = new PurchaseOrderControllerAdvice();
    }

    @Test
    public void returnNotFoundStatusWhenOrderDoesNotExist() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = purchaseOrderControllerAdvice.handleObjectNotFoundException(orderNotFoundException);

        assertEquals(HttpStatus.NOT_FOUND, exceptionResponse.getStatusCode());
    }

    @Test
    public void returnUnprocessableEntityStatusForInvalidUpdate() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = purchaseOrderControllerAdvice.handleInvalidUpdateException(invalidUpdateException);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionResponse.getStatusCode());
    }

    @Test
    public void returnPreconditionFailedStatusForOptimisticLockingFailureException() {
        ResponseEntity<ExceptionResponseDTO> exceptionResponse = purchaseOrderControllerAdvice.handleOptimisticLockingFailureException(optimisticLockingFailureException);

        assertEquals(HttpStatus.PRECONDITION_FAILED, exceptionResponse.getStatusCode());
    }

}