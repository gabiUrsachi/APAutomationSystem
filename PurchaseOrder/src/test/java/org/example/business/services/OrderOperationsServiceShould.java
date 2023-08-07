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
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrderOperationsServiceShould {
    @Mock
    PurchaseOrderRepository purchaseOrderRepository;
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
        purchaseOrder = createPurchaseOrderWithStatusAndUUID(OrderStatus.SAVED, uuid);
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(purchaseOrder));

        assertThrows(InvalidUpdateException.class, () -> orderOperationsService.updatePurchaseOrder(uuid, purchaseOrder));
        verify(purchaseOrderRepository).findById(uuid);
    }

    @Test
    public void successfullyUpdateAValidPurchaseOrder() {
        UUID uuid = UUID.randomUUID();
        purchaseOrder = createPurchaseOrderWithStatusAndUUID(OrderStatus.CREATED, uuid);
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(purchaseOrder));
        given(purchaseOrderRepository.save(purchaseOrder)).willReturn(purchaseOrder);

        PurchaseOrder savedPurchaseOrder = orderOperationsService.updatePurchaseOrder(uuid, purchaseOrder);

        verify(purchaseOrderRepository).findById(uuid);
        verify(purchaseOrderRepository).save(purchaseOrder);
        Assertions.assertEquals(uuid, savedPurchaseOrder.getIdentifier());
        Assertions.assertEquals(purchaseOrder.getOrderStatus(), savedPurchaseOrder.getOrderStatus());
    }

    @Test
    public void successfullyCreateNewPurchaseOrder() {
        purchaseOrder = createRandomPurchaseOrder();
        given(purchaseOrderRepository.save(purchaseOrder)).willReturn(purchaseOrder);

        PurchaseOrder savedPurchaseOrder = orderOperationsService.createPurchaseOrder(purchaseOrder);

        verify(purchaseOrderRepository).save(purchaseOrder);
        Assertions.assertEquals(OrderStatus.CREATED, savedPurchaseOrder.getOrderStatus());
    }

    @Test
    public void returnExistentOrderById() {
        UUID uuid = UUID.randomUUID();
        purchaseOrder = createPurchaseOrderWithStatusAndUUID(OrderStatus.CREATED, uuid);
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(purchaseOrder));

        PurchaseOrder queriedPurchaseOrder = orderOperationsService.getPurchaseOrder(uuid);

        Assertions.assertEquals(purchaseOrder, queriedPurchaseOrder);
    }

    @Test
    public void throwExceptionWhenTryingToQueryNonexistentOrder() {
        UUID uuid = UUID.randomUUID();
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderOperationsService.getPurchaseOrder(uuid));
        verify(purchaseOrderRepository).findById(uuid);
    }

    @Test
    public void throwExceptionWhenTryingToDeleteNonexistentOrder() {
        UUID uuid = UUID.randomUUID();
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderOperationsService.deletePurchaseOrder(uuid));
        verify(purchaseOrderRepository).findById(uuid);
    }


    private PurchaseOrder createPurchaseOrderWithStatusAndUUID(OrderStatus orderStatus, UUID uuid) {
        return PurchaseOrder.builder()
                .identifier(uuid)
                .buyer(UUID.randomUUID())
                .seller(UUID.randomUUID())
                .orderStatus(orderStatus)
                .items(Set.of())
                .build();
    }

    private PurchaseOrder createRandomPurchaseOrder() {
        return PurchaseOrder.builder()
                .identifier(UUID.randomUUID())
                .buyer(UUID.randomUUID())
                .seller(UUID.randomUUID())
                .orderStatus(OrderStatus.APPROVED)
                .items(Set.of())
                .build();
    }
}