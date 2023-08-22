package org.example.business.services;

import org.example.customexceptions.ForbiddenUpdateException;
import org.example.customexceptions.IdentifiersMismatchException;
import org.example.persistence.utils.data.OrderStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class ValidatorServiceShould {

    ValidatorService validatorService;

    @Before
    public void setUp() {
        validatorService = new ValidatorService();
    }

    @Test
    public void throwIdentifiersMismatchExceptionForDifferentIdentifiers() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        Assertions.assertThrows(IdentifiersMismatchException.class, () -> validatorService.verifyIdentifiersMatch(uuid1, uuid2));
    }

    @Test
    public void notThrowExceptionForEqualIdentifiers() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.fromString(uuid1.toString());

        validatorService.verifyIdentifiersMatch(uuid1, uuid2);
    }

    @Test
    public void throwForbiddenUpdateExceptionForBuyerTryingToApproveOrder() {
        OrderStatus newOrderStatus = OrderStatus.APPROVED;
        UUID userCompanyUUID = UUID.randomUUID();
        UUID sellerCompanyUUID = UUID.randomUUID();

        Assertions.assertThrows(ForbiddenUpdateException.class, () -> validatorService.verifyUpdatePermission(newOrderStatus, userCompanyUUID, userCompanyUUID, sellerCompanyUUID));
    }

    @Test
    public void throwForbiddenUpdateExceptionForBuyerTryingToSaveAnotherBuyersOrder() {
        OrderStatus newOrderStatus = OrderStatus.SAVED;
        UUID userCompanyUUID = UUID.randomUUID();
        UUID buyerCompanyUUID = UUID.randomUUID();
        UUID sellerCompanyUUID = UUID.randomUUID();

        Assertions.assertThrows(ForbiddenUpdateException.class, () -> validatorService.verifyUpdatePermission(newOrderStatus, userCompanyUUID, buyerCompanyUUID, sellerCompanyUUID));
    }

    @Test
    public void throwForbiddenUpdateExceptionForSellerTryingToSaveOrder() {
        OrderStatus newOrderStatus = OrderStatus.SAVED;
        UUID userCompanyUUID = UUID.randomUUID();
        UUID buyerCompanyUUID = UUID.randomUUID();

        Assertions.assertThrows(ForbiddenUpdateException.class, () -> validatorService.verifyUpdatePermission(newOrderStatus, userCompanyUUID, buyerCompanyUUID, userCompanyUUID));
    }

    @Test
    public void throwForbiddenUpdateExceptionForSellerTryingToApproveAnotherSellersOrder() {
        OrderStatus newOrderStatus = OrderStatus.APPROVED;
        UUID userCompanyUUID = UUID.randomUUID();
        UUID buyerCompanyUUID = UUID.randomUUID();
        UUID sellerCompanyUUID = UUID.randomUUID();

        Assertions.assertThrows(ForbiddenUpdateException.class, () -> validatorService.verifyUpdatePermission(newOrderStatus, userCompanyUUID, buyerCompanyUUID, sellerCompanyUUID));
    }

}