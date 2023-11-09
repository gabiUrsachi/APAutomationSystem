package org.example.unit.purchaseOrder.business.services;


import org.example.SQSOps;
import org.example.business.services.PurchaseOrderService;
import org.example.business.utils.PurchaseOrderHistoryHelper;
import org.example.business.utils.PurchaseOrderStatusPrecedence;
import org.example.customexceptions.InvalidResourceUpdateException;
import org.example.customexceptions.ResourceNotFoundException;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.example.persistence.utils.data.OrderHistoryObject;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.*;

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
    public void throwOrderNotFoundExceptionWhenTryingToUpdateNonexistentOrder() {
        UUID uuid = UUID.randomUUID();

        OrderStatus randomOrderStatus = createRandomStatus();
        OrderStatus requiredOrderStatus = PurchaseOrderStatusPrecedence.PREDECESSORS.get(randomOrderStatus);
        purchaseOrder = createPurchaseOrderWithStatusAndUUID(randomOrderStatus, uuid);

        given(purchaseOrderRepository.updateByIdentifierAndVersion(uuid, purchaseOrder.getVersion(), requiredOrderStatus, purchaseOrder)).willReturn(0);

        assertThrows(ResourceNotFoundException.class, () -> purchaseOrderService.updatePurchaseOrder(purchaseOrder));
        verify(purchaseOrderRepository).updateByIdentifierAndVersion(uuid, purchaseOrder.getVersion(), requiredOrderStatus, purchaseOrder);
    }

    @Test
    public void throwInvalidUpdateExceptionWhenTryingToUpdateASavedOrder() {
        UUID uuid = UUID.randomUUID();

        OrderStatus updatedOrderStatus = OrderStatus.CREATED;
        OrderStatus requiredOrderStatus = PurchaseOrderStatusPrecedence.PREDECESSORS.get(updatedOrderStatus);
        OrderStatus existingOrderStatus = OrderStatus.SAVED;

        PurchaseOrder existingPurchaseOrder = createPurchaseOrderWithStatusAndUUID(existingOrderStatus, uuid);
        PurchaseOrder updatedPurchaseOrder = createPurchaseOrderWithStatusAndUUID(updatedOrderStatus, uuid);

        given(purchaseOrderRepository.updateByIdentifierAndVersion(uuid, updatedPurchaseOrder.getVersion(), requiredOrderStatus, updatedPurchaseOrder)).willReturn(0);
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(existingPurchaseOrder));

        assertThrows(InvalidResourceUpdateException.class, () -> purchaseOrderService.updatePurchaseOrder(updatedPurchaseOrder));
        verify(purchaseOrderRepository).updateByIdentifierAndVersion(uuid, updatedPurchaseOrder.getVersion(), requiredOrderStatus, updatedPurchaseOrder);
        verify(purchaseOrderRepository).findById(uuid);
    }

    @Test
    public void throwOptimisticLockingFailureExceptionWhenTryingToUpdateAModifiedOrder() {
        UUID uuid = UUID.randomUUID();
        OrderStatus randomStatus = createRandomStatus();
        OrderStatus requiredOldStatus = PurchaseOrderStatusPrecedence.PREDECESSORS.get(randomStatus);

        PurchaseOrder existingPurchaseOrder = createPurchaseOrderWithStatusAndUUID(randomStatus, uuid);
        PurchaseOrder updatedPurchaseOrder = createPurchaseOrderWithStatusAndUUID(randomStatus, uuid);

        existingPurchaseOrder.setVersion(3);
        updatedPurchaseOrder.setVersion(existingPurchaseOrder.getVersion() - 1);

        given(purchaseOrderRepository.updateByIdentifierAndVersion(uuid, updatedPurchaseOrder.getVersion(), requiredOldStatus, updatedPurchaseOrder)).willReturn(0);
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(existingPurchaseOrder));

        assertThrows(OptimisticLockingFailureException.class, () -> purchaseOrderService.updatePurchaseOrder(updatedPurchaseOrder));
        verify(purchaseOrderRepository).updateByIdentifierAndVersion(uuid, updatedPurchaseOrder.getVersion(), requiredOldStatus, updatedPurchaseOrder);
        verify(purchaseOrderRepository).findById(uuid);
    }

    @Test
    public void successfullyUpdateAValidPurchaseOrder() {
        UUID uuid = UUID.randomUUID();

        OrderStatus updatedOrderStatus = OrderStatus.SAVED;
        OrderStatus requiredOrderStatus = PurchaseOrderStatusPrecedence.PREDECESSORS.get(updatedOrderStatus);

        PurchaseOrder updatedPurchaseOrder = createPurchaseOrderWithStatusAndUUID(updatedOrderStatus, uuid);

        try(MockedStatic<SQSOps> sqsOpsMockedStatic = Mockito.mockStatic(SQSOps.class)){
            given(purchaseOrderRepository.updateByIdentifierAndVersion(uuid, updatedPurchaseOrder.getVersion(), requiredOrderStatus, updatedPurchaseOrder)).willReturn(1);
            sqsOpsMockedStatic.when(()->SQSOps.sendMessage(any())).thenAnswer((Answer<Void>) invocation -> null);

            PurchaseOrder savedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(updatedPurchaseOrder);

            verify(purchaseOrderRepository).updateByIdentifierAndVersion(uuid, updatedPurchaseOrder.getVersion(), requiredOrderStatus, updatedPurchaseOrder);
            sqsOpsMockedStatic.verify(() -> SQSOps.sendMessage(any()));

            OrderStatus updatedPurchaseOrderStatus = PurchaseOrderHistoryHelper.getLatestOrderHistoryObject(updatedPurchaseOrder.getStatusHistory()).getStatus();
            OrderStatus savedPurchaseOrderStatus = PurchaseOrderHistoryHelper.getLatestOrderHistoryObject(savedPurchaseOrder.getStatusHistory()).getStatus();
            Assertions.assertEquals(uuid, savedPurchaseOrder.getIdentifier());
            Assertions.assertEquals(updatedPurchaseOrderStatus,savedPurchaseOrderStatus);
        }
    }

    @Test
    public void successfullyCreateNewPurchaseOrder() {
        PurchaseOrder newPurchaseOrder = createRandomPurchaseOrder();

        newPurchaseOrder.setIdentifier(null);

        given(purchaseOrderRepository.save(any())).willAnswer((answer) -> answer.getArgument(0));

        PurchaseOrder savedPurchaseOrder = purchaseOrderService.createPurchaseOrder(newPurchaseOrder);

        verify(purchaseOrderRepository).save(purchaseOrderCaptor.capture());

        PurchaseOrder capturedPurchaseOrder = purchaseOrderCaptor.getValue();

        OrderStatus capturedPurchaseOrderStatus = PurchaseOrderHistoryHelper.getLatestOrderHistoryObject(capturedPurchaseOrder.getStatusHistory()).getStatus();
        Assertions.assertEquals(OrderStatus.CREATED, capturedPurchaseOrderStatus);
        Assertions.assertNotNull(capturedPurchaseOrder.getIdentifier());
        OrderStatus savedPurchaseOrderStatus = PurchaseOrderHistoryHelper.getLatestOrderHistoryObject(savedPurchaseOrder.getStatusHistory()).getStatus();
        Assertions.assertEquals(OrderStatus.CREATED, savedPurchaseOrderStatus);
    }

    @Test
    public void returnExistentOrderById() {
        UUID uuid = UUID.randomUUID();
        purchaseOrder = createRandomPurchaseOrder();
        purchaseOrder.setIdentifier(uuid);

        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.of(purchaseOrder));

        PurchaseOrder queriedPurchaseOrder = purchaseOrderService.getPurchaseOrder(uuid);

        verify(purchaseOrderRepository).findById(uuid);
        Assertions.assertEquals(purchaseOrder, queriedPurchaseOrder);
    }

    @Test
    public void returnExistentOrderByIdAndFilters() {
        UUID uuid = UUID.randomUUID();
        purchaseOrder = createRandomPurchaseOrder();
        purchaseOrder.setIdentifier(uuid);
        List<PurchaseOrderFilter> queryFilters = createFilters();

        given(purchaseOrderRepository.findByUUIDAndFilters(uuid, queryFilters)).willReturn(purchaseOrder);

        PurchaseOrder queriedPurchaseOrder = purchaseOrderService.getPurchaseOrder(uuid, queryFilters);

        verify(purchaseOrderRepository).findByUUIDAndFilters(uuid, queryFilters);
        Assertions.assertEquals(purchaseOrder, queriedPurchaseOrder);
    }

    @Test
    public void throwExceptionWhenTryingToQueryNonexistentOrder() {
        UUID uuid = UUID.randomUUID();
        given(purchaseOrderRepository.findById(uuid)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> purchaseOrderService.getPurchaseOrder(uuid));
        verify(purchaseOrderRepository).findById(uuid);
    }

    @Test
    public void throwExceptionWhenTryingToDeleteNonexistentOrder() {
        UUID uuid = UUID.randomUUID();
        given(purchaseOrderRepository.customDeleteById(uuid)).willReturn(0);

        assertThrows(ResourceNotFoundException.class, () -> purchaseOrderService.deletePurchaseOrder(uuid));
        verify(purchaseOrderRepository).customDeleteById(uuid);
    }

    private PurchaseOrder createPurchaseOrderWithStatusAndUUID(OrderStatus orderStatus, UUID uuid) {
        return PurchaseOrder.builder()
                .identifier(uuid)
                .buyer(UUID.randomUUID())
                .seller(UUID.randomUUID())
                .statusHistory(PurchaseOrderHistoryHelper.generateOrderHistoryList(orderStatus))
                .items(Set.of())
                .version(0)
                .build();
    }

    private PurchaseOrder createRandomPurchaseOrder() {
        return PurchaseOrder.builder()
                .identifier(UUID.randomUUID())
                .buyer(UUID.randomUUID())
                .seller(UUID.randomUUID())
                .statusHistory(PurchaseOrderHistoryHelper.generateOrderHistoryList(OrderStatus.APPROVED))
                .items(Set.of())
                .version(0)
                .build();
    }

    private OrderStatus createRandomStatus() {
        return OrderStatus.values()[new Random().nextInt(OrderStatus.values().length)];
    }

    private List<PurchaseOrderFilter> createFilters() {
        return List.of(PurchaseOrderFilter.builder().build());
    }
}