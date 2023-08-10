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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseOrderServiceShould {
    @Mock
    PurchaseOrderRepository purchaseOrderRepository;
    PurchaseOrder purchaseOrder;

    PurchaseOrderService purchaseOrderService;

    @Captor
    ArgumentCaptor<PurchaseOrder> purchaseOrderCaptor;

    @Before
    public void initialize() {
        purchaseOrderService = new PurchaseOrderService(purchaseOrderRepository);
    }

    @Test
    public void throwExceptionWhenTryingToUpdateNonexistentOrder() {
        UUID uuid = UUID.randomUUID();
        purchaseOrder = createRandomPurchaseOrder();
        purchaseOrder.setIdentifier(uuid);

        given(purchaseOrderRepository.updateByIdentifierAndVersion(uuid, purchaseOrder.getVersion(), purchaseOrder)).willReturn(0);

        assertThrows(OrderNotFoundException.class, () -> purchaseOrderService.updatePurchaseOrder(purchaseOrder));
        verify(purchaseOrderRepository).updateByIdentifierAndVersion(uuid, purchaseOrder.getVersion(), purchaseOrder);
    }

    @Test
    public void throwExceptionWhenTryingToUpdateASavedOrder() {
        UUID uuid = UUID.randomUUID();
        purchaseOrder = createPurchaseOrderWithStatusAndUUID(OrderStatus.SAVED, uuid);
        given(purchaseOrderRepository.updateByIdentifierAndVersion(uuid, purchaseOrder.getVersion(), purchaseOrder)).willReturn(0);
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(purchaseOrder));

        assertThrows(InvalidUpdateException.class, () -> purchaseOrderService.updatePurchaseOrder(purchaseOrder));
        verify(purchaseOrderRepository).updateByIdentifierAndVersion(uuid, purchaseOrder.getVersion(), purchaseOrder);
        verify(purchaseOrderRepository).findById(uuid);
    }

    @Test
    public void successfullyUpdateAValidPurchaseOrder() {
        UUID uuid = UUID.randomUUID();
        purchaseOrder = createPurchaseOrderWithStatusAndUUID(OrderStatus.CREATED, uuid);
        given(purchaseOrderRepository.updateByIdentifierAndVersion(uuid, 0, purchaseOrder)).willReturn(1);

        PurchaseOrder savedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(purchaseOrder);

        verify(purchaseOrderRepository).updateByIdentifierAndVersion(uuid, 0, purchaseOrder);
        Assertions.assertEquals(uuid, savedPurchaseOrder.getIdentifier());
        Assertions.assertEquals(purchaseOrder.getOrderStatus(), savedPurchaseOrder.getOrderStatus());
    }

    @Test
    public void successfullyCreateNewPurchaseOrder() {
        PurchaseOrder newPurchaseOrder = createRandomPurchaseOrder();
        newPurchaseOrder.setIdentifier(null);

        given(purchaseOrderRepository.save(any())).willAnswer((answer) -> answer.getArgument(0));

        PurchaseOrder savedPurchaseOrder = purchaseOrderService.createPurchaseOrder(newPurchaseOrder);

        verify(purchaseOrderRepository).save(purchaseOrderCaptor.capture());

        PurchaseOrder capturedPurchaseOrder = purchaseOrderCaptor.getValue();

        Assertions.assertEquals(OrderStatus.CREATED, capturedPurchaseOrder.getOrderStatus());
        Assertions.assertNotNull(capturedPurchaseOrder.getIdentifier());
        Assertions.assertEquals(OrderStatus.CREATED, savedPurchaseOrder.getOrderStatus());
    }

    @Test
    public void returnExistentOrderById() {
        UUID uuid = UUID.randomUUID();
        purchaseOrder = createPurchaseOrderWithStatusAndUUID(OrderStatus.CREATED, uuid);
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(purchaseOrder));

        PurchaseOrder queriedPurchaseOrder = purchaseOrderService.getPurchaseOrder(uuid);

        Assertions.assertEquals(purchaseOrder, queriedPurchaseOrder);
    }

    @Test
    public void throwExceptionWhenTryingToQueryNonexistentOrder() {
        UUID uuid = UUID.randomUUID();
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> purchaseOrderService.getPurchaseOrder(uuid));
        verify(purchaseOrderRepository).findById(uuid);
    }

    @Test
    public void throwExceptionWhenTryingToDeleteNonexistentOrder() {
        UUID uuid = UUID.randomUUID();
        given(purchaseOrderRepository.customDeleteById(uuid)).willReturn(0);

        assertThrows(OrderNotFoundException.class, () -> purchaseOrderService.deletePurchaseOrder(uuid));
        verify(purchaseOrderRepository).customDeleteById(uuid);
    }

    private PurchaseOrder createPurchaseOrderWithStatusAndUUID(OrderStatus orderStatus, UUID uuid) {
        return PurchaseOrder.builder()
                .identifier(uuid)
                .buyer(UUID.randomUUID())
                .seller(UUID.randomUUID())
                .orderStatus(orderStatus)
                .items(Set.of())
                .version(0)
                .build();
    }

    private PurchaseOrder createRandomPurchaseOrder() {
        return PurchaseOrder.builder()
                .identifier(UUID.randomUUID())
                .buyer(UUID.randomUUID())
                .seller(UUID.randomUUID())
                .orderStatus(OrderStatus.APPROVED)
                .items(Set.of())
                .version(0)
                .build();
    }
}