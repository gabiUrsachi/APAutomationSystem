package org.example.presentation.controllers.unit;


import org.example.business.services.FilteringService;
import org.example.business.services.PurchaseOrderService;
import org.example.business.services.ValidatorService;
import org.example.customexceptions.InvalidUpdateException;
import org.example.customexceptions.OrderNotFoundException;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.OrderStatus;
import org.example.presentation.controllers.PurchaseOrderController;
import org.example.presentation.utils.MapperService;
import org.example.presentation.view.OrderRequestDTO;
import org.example.services.AuthorisationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseOrderControllerShould {
    PurchaseOrderController purchaseOrderController;
    @Mock
    PurchaseOrderService purchaseOrderService;
    @Mock
    MapperService mapperService;

    @Mock
    AuthorisationService authorisationService;

    @Mock
    FilteringService filteringService;

    @Mock
    ValidatorService validatorService;

    @Before
    public void setUp() {
        purchaseOrderController = new PurchaseOrderController(purchaseOrderService, mapperService, authorisationService, filteringService, validatorService);
    }

    @Test
    public void returnNotFoundStatusWhenOrderDoesNotExist() {
        UUID searchedUUID = createUUID();

        given(purchaseOrderService.getPurchaseOrder(searchedUUID)).willThrow(OrderNotFoundException.class);

        assertThrows(OrderNotFoundException.class, () -> purchaseOrderController.getPurchaseOrder(searchedUUID, null));
    }

    @Test
    public void returnUnprocessableEntityStatusForInvalidUpdate() {
        UUID searchedUUID = createUUID();
        OrderRequestDTO orderRequestDTO = Mockito.mock(OrderRequestDTO.class);
        PurchaseOrder purchaseOrder = createPurchaseOrderWithStatus(OrderStatus.SAVED);
        purchaseOrder.setIdentifier(searchedUUID);

        given(mapperService.mapToEntity(orderRequestDTO)).willReturn(purchaseOrder);
        given(purchaseOrderService.updatePurchaseOrder(purchaseOrder)).willThrow(InvalidUpdateException.class);

        assertThrows(InvalidUpdateException.class, () -> purchaseOrderController.updatePurchaseOrder(searchedUUID, orderRequestDTO, null));
    }

    private UUID createUUID() {
        return UUID.randomUUID();
    }

    private PurchaseOrder createPurchaseOrderWithStatus(OrderStatus orderStatus) {
        return PurchaseOrder.builder()
                .identifier(createUUID())
                .buyer(createUUID())
                .seller(createUUID())
                .orderStatus(orderStatus)
                .items(Set.of())
                .build();
    }
}