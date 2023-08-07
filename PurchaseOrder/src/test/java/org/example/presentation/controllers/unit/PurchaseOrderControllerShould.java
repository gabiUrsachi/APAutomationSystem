package org.example.presentation.controllers.unit;

import org.example.business.errorhandling.customexceptions.InvalidUpdateException;
import org.example.business.errorhandling.customexceptions.OrderNotFoundException;
import org.example.business.services.OrderOperationsService;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.OrderStatus;
import org.example.presentation.controllers.ControllerAdvice;
import org.example.presentation.controllers.PurchaseOrderController;
import org.example.presentation.utils.MapperService;
import org.example.presentation.view.ExceptionResponseDTO;
import org.example.presentation.view.OrderRequestDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseOrderControllerShould {

    PurchaseOrderController purchaseOrderController;
    ControllerAdvice controllerAdvice;
    @Mock  OrderOperationsService orderOperationsService;
    @Mock  MapperService mapperService;

    @Mock OrderNotFoundException orderNotFoundException;
    @Mock
    InvalidUpdateException invalidUpdateException;

    @Before
    public void setUp(){
        purchaseOrderController = new PurchaseOrderController(orderOperationsService, mapperService);
        controllerAdvice = new ControllerAdvice();
    }

    @Test
    public void returnNotFoundStatusWhenOrderDoesNotExist(){
        UUID searchedUUID = createUUID();

        given(orderOperationsService.getPurchaseOrder(searchedUUID)).willThrow(OrderNotFoundException.class);

        ResponseEntity<ExceptionResponseDTO> exceptionResponse = controllerAdvice.handleObjectNotFoundException(orderNotFoundException);

        assertThrows(OrderNotFoundException.class, ()-> purchaseOrderController.getPurchaseOrder(searchedUUID));
        assertEquals(HttpStatus.NOT_FOUND, exceptionResponse.getStatusCode());
    }


    @Test
    public void returnUnprocessableEntityStatusForInvalidUpdate(){
        UUID searchedUUID = createUUID();
        OrderRequestDTO orderRequestDTO = Mockito.mock(OrderRequestDTO.class);
        PurchaseOrder purchaseOrder = createPurchaseOrderWithStatus(OrderStatus.SAVED);

        given(mapperService.mapToEntity(orderRequestDTO)).willReturn(purchaseOrder);
        given(orderOperationsService.updatePurchaseOrder(searchedUUID, purchaseOrder)).willThrow(InvalidUpdateException.class);

        ResponseEntity<ExceptionResponseDTO> exceptionResponse = controllerAdvice.handleInvalidUpdateException(invalidUpdateException);

        assertThrows(InvalidUpdateException.class, ()-> purchaseOrderController.updatePurchaseOrder(searchedUUID, orderRequestDTO));
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exceptionResponse.getStatusCode());
    }

    private UUID createUUID(){
        return UUID.randomUUID();
    }

    private PurchaseOrder createPurchaseOrderWithStatus(OrderStatus orderStatus){
        return PurchaseOrder.builder()
                .identifier(createUUID())
                .buyer(createUUID())
                .seller(createUUID())
                .orderStatus(orderStatus)
                .items(Set.of())
                .build();
    }
}