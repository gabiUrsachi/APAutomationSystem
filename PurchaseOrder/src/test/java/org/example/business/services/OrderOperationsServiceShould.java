package org.example.business.services;

import org.example.business.errorhandling.customexceptions.InvalidUpdateException;
import org.example.business.errorhandling.customexceptions.OrderNotFoundException;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.example.persistence.utils.OrderStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrderOperationsServiceShould {
    @Mock
    PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    PurchaseOrder purchaseOrder;

    OrderOperationsService orderOperationsService;

    @Before
    public void initialize() {
        orderOperationsService = new OrderOperationsService(purchaseOrderRepository);
    }

    @Test
    public void throwExceptionWhenTryingToUpdateNonexistentOrder() {
        UUID uuid = UUID.randomUUID();

        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderOperationsService.updatePurchaseOrder(uuid, purchaseOrder));
        verify(purchaseOrderRepository).findById(uuid);
    }

    @Test
    public void throwExceptionWhenTryingToUpdateASavedOrder() {
        UUID uuid = UUID.randomUUID();

        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(purchaseOrder));
        given(purchaseOrder.getOrderStatus()).willReturn(OrderStatus.SAVED);

        assertThrows(InvalidUpdateException.class, () -> orderOperationsService.updatePurchaseOrder(uuid, purchaseOrder));
        verify(purchaseOrderRepository).findById(uuid);
    }

    @Test
    public void successfullyUpdateAValidPurchaseOrder() {
        UUID uuid = UUID.randomUUID();

        given(purchaseOrder.getOrderStatus()).willReturn(OrderStatus.CREATED);
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(purchaseOrder));
        given(purchaseOrderRepository.save(purchaseOrder)).willReturn(purchaseOrder);

        PurchaseOrder savedPurchaseOrder = orderOperationsService.updatePurchaseOrder(uuid, purchaseOrder);

        verify(purchaseOrderRepository).save(purchaseOrder);
        Assertions.assertNotNull(savedPurchaseOrder);
    }

    @Test
    public void successfullyCreateNewPurchaseOrder() {
        given(purchaseOrderRepository.save(purchaseOrder)).willReturn(purchaseOrder);

        PurchaseOrder savedPurchaseOrder = orderOperationsService.createPurchaseOrder(purchaseOrder);

        verify(purchaseOrderRepository).save(purchaseOrder);
        Assertions.assertNotNull(savedPurchaseOrder);
    }

    @Test
    public void returnExistentOrderById() {
        UUID uuid = UUID.randomUUID();
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(purchaseOrder));

        PurchaseOrder queriedPurchaseOrder = orderOperationsService.getPurchaseOrder(uuid);

        Assertions.assertEquals(purchaseOrder, queriedPurchaseOrder);
    }
}