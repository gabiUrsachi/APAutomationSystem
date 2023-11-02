//package org.example.unit.purchaseOrder.business.services;
//
//import org.example.business.services.PurchaseOrderValidatorService;
//import org.example.customexceptions.ForbiddenActionException;
//import org.example.persistence.utils.data.OrderHistoryObject;
//import org.example.persistence.utils.data.OrderStatus;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.jupiter.api.Assertions;
//import org.junit.runner.RunWith;
//import org.mockito.internal.matchers.Or;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.time.LocalDateTime;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.UUID;
//
//@RunWith(MockitoJUnitRunner.class)
//public class PurchaseOrderValidatorServiceShould {
//
//    PurchaseOrderValidatorService purchaseOrderValidatorService;
//
//    @Before
//    public void setUp() {
//        purchaseOrderValidatorService = new PurchaseOrderValidatorService();
//    }
//
//    @Test
//    public void throwIdentifiersMismatchExceptionForDifferentIdentifiers() {
//        UUID uuid1 = UUID.randomUUID();
//        UUID uuid2 = UUID.randomUUID();
//
//        Assertions.assertThrows(ForbiddenActionException.class, () -> purchaseOrderValidatorService.verifyIdentifiersMatch(uuid1, uuid2));
//    }
//
//    @Test
//    public void notThrowExceptionForEqualIdentifiers() {
//        UUID uuid1 = UUID.randomUUID();
//        UUID uuid2 = UUID.fromString(uuid1.toString());
//
//        purchaseOrderValidatorService.verifyIdentifiersMatch(uuid1, uuid2);
//    }
//
//    @Test
//    public void throwForbiddenActionExceptionForBuyerTryingToApproveOrder() {
//        OrderHistoryObject newOrderHistoryObject = new OrderHistoryObject(LocalDateTime.now(), OrderStatus.CREATED);
//        Set<OrderHistoryObject> orderHistory = new HashSet<>();
//        orderHistory.add(newOrderHistoryObject);
//
//        UUID userCompanyUUID = UUID.randomUUID();
//        UUID sellerCompanyUUID = UUID.randomUUID();
//
//        Assertions.assertThrows(ForbiddenActionException.class, () -> purchaseOrderValidatorService.verifyUpdatePermission(orderHistory, userCompanyUUID, userCompanyUUID, sellerCompanyUUID));
//    }
//
//    @Test
//    public void throwForbiddenActionExceptionForBuyerTryingToSaveAnotherBuyersOrder() {
//
//        OrderHistoryObject firstOrderHistoryObject = new OrderHistoryObject(LocalDateTime.now().minusMinutes(10), OrderStatus.CREATED);
//        OrderHistoryObject secondOrderHistoryObject = new OrderHistoryObject(LocalDateTime.now(), OrderStatus.SAVED);
//        Set<OrderHistoryObject> orderHistory = new HashSet<>();
//        orderHistory.add(firstOrderHistoryObject);
//        orderHistory.add(secondOrderHistoryObject);
//
//        UUID userCompanyUUID = UUID.randomUUID();
//        UUID buyerCompanyUUID = UUID.randomUUID();
//        UUID sellerCompanyUUID = UUID.randomUUID();
//
//        Assertions.assertThrows(ForbiddenActionException.class, () -> purchaseOrderValidatorService.verifyUpdatePermission(orderHistory, userCompanyUUID, buyerCompanyUUID, sellerCompanyUUID));
//    }
//
//    @Test
//    public void throwForbiddenActionExceptionForSellerTryingToSaveOrder() {
//
//        OrderHistoryObject firstOrderHistoryObject = new OrderHistoryObject(LocalDateTime.now().minusMinutes(10), OrderStatus.CREATED);
//        OrderHistoryObject secondOrderHistoryObject = new OrderHistoryObject(LocalDateTime.now(), OrderStatus.SAVED);
//        Set<OrderHistoryObject> orderHistory = new HashSet<>();
//        orderHistory.add(firstOrderHistoryObject);
//        orderHistory.add(secondOrderHistoryObject);
//
//
//
//        UUID userCompanyUUID = UUID.randomUUID();
//        UUID buyerCompanyUUID = UUID.randomUUID();
//
//        Assertions.assertThrows(ForbiddenActionException.class, () -> purchaseOrderValidatorService.verifyUpdatePermission(orderHistory, userCompanyUUID, buyerCompanyUUID, userCompanyUUID));
//    }
//
//    @Test
//    public void throwForbiddenActionExceptionForSellerTryingToApproveAnotherSellersOrder() {
//        OrderStatus newOrderStatus = OrderStatus.APPROVED;
//
//        OrderHistoryObject firstOrderHistoryObject = new OrderHistoryObject(LocalDateTime.now().minusMinutes(15), OrderStatus.CREATED);
//        OrderHistoryObject secondOrderHistoryObject = new OrderHistoryObject(LocalDateTime.now().minusMinutes(10), OrderStatus.SAVED);
//        OrderHistoryObject thirdOrderHistoryObject = new OrderHistoryObject(LocalDateTime.now(), OrderStatus.SAVED);
//        Set<OrderHistoryObject> orderHistory = new HashSet<>();
//        orderHistory.add(firstOrderHistoryObject);
//        orderHistory.add(secondOrderHistoryObject);
//        orderHistory.add(thirdOrderHistoryObject);
//
//        UUID userCompanyUUID = UUID.randomUUID();
//        UUID buyerCompanyUUID = UUID.randomUUID();
//        UUID sellerCompanyUUID = UUID.randomUUID();
//
//        Assertions.assertThrows(ForbiddenActionException.class, () -> purchaseOrderValidatorService.verifyUpdatePermission(orderHistory, userCompanyUUID, buyerCompanyUUID, sellerCompanyUUID));
//    }
//
//}