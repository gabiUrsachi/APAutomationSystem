package org.example.unit.purchaseOrder.business.services;

import org.example.business.services.PurchaseOrderValidatorService;
import org.example.customexceptions.ForbiddenActionException;
import org.example.persistence.utils.data.OrderStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseOrderValidatorServiceShould {

    PurchaseOrderValidatorService purchaseOrderValidatorService;

    @Before
    public void setUp() {
        purchaseOrderValidatorService = new PurchaseOrderValidatorService();
    }

    @Test
    public void throwIdentifiersMismatchExceptionForDifferentIdentifiers() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        Assertions.assertThrows(ForbiddenActionException.class, () -> purchaseOrderValidatorService.verifyIdentifiersMatch(uuid1, uuid2));
    }

    @Test
    public void notThrowExceptionForEqualIdentifiers() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.fromString(uuid1.toString());

        purchaseOrderValidatorService.verifyIdentifiersMatch(uuid1, uuid2);
    }

    @Test
    public void throwForbiddenActionExceptionForBuyerTryingToApproveOrder() {
        OrderStatus newOrderStatus = OrderStatus.APPROVED;
        UUID userCompanyUUID = UUID.randomUUID();
        UUID sellerCompanyUUID = UUID.randomUUID();

        Assertions.assertThrows(ForbiddenActionException.class, () -> purchaseOrderValidatorService.verifyUpdatePermission(newOrderStatus, userCompanyUUID, userCompanyUUID, sellerCompanyUUID));
    }

    @Test
    public void throwForbiddenActionExceptionForBuyerTryingToSaveAnotherBuyersOrder() {
        OrderStatus newOrderStatus = OrderStatus.SAVED;
        UUID userCompanyUUID = UUID.randomUUID();
        UUID buyerCompanyUUID = UUID.randomUUID();
        UUID sellerCompanyUUID = UUID.randomUUID();

        Assertions.assertThrows(ForbiddenActionException.class, () -> purchaseOrderValidatorService.verifyUpdatePermission(newOrderStatus, userCompanyUUID, buyerCompanyUUID, sellerCompanyUUID));
    }

    @Test
    public void throwForbiddenActionExceptionForSellerTryingToSaveOrder() {
        OrderStatus newOrderStatus = OrderStatus.SAVED;
        UUID userCompanyUUID = UUID.randomUUID();
        UUID buyerCompanyUUID = UUID.randomUUID();

        Assertions.assertThrows(ForbiddenActionException.class, () -> purchaseOrderValidatorService.verifyUpdatePermission(newOrderStatus, userCompanyUUID, buyerCompanyUUID, userCompanyUUID));
    }

    @Test
    public void throwForbiddenActionExceptionForSellerTryingToApproveAnotherSellersOrder() {
        OrderStatus newOrderStatus = OrderStatus.APPROVED;
        UUID userCompanyUUID = UUID.randomUUID();
        UUID buyerCompanyUUID = UUID.randomUUID();
        UUID sellerCompanyUUID = UUID.randomUUID();

        Assertions.assertThrows(ForbiddenActionException.class, () -> purchaseOrderValidatorService.verifyUpdatePermission(newOrderStatus, userCompanyUUID, buyerCompanyUUID, sellerCompanyUUID));
    }

}