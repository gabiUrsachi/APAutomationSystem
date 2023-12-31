package org.example.unit.purchaseOrder.presentation.controllers;


import org.example.business.services.PurchaseOrderFilteringService;
import org.example.business.services.PurchaseOrderService;
import org.example.business.services.PurchaseOrderValidatorService;
import org.example.customexceptions.ForbiddenActionException;
import org.example.customexceptions.ResourceNotFoundException;
import org.example.persistence.utils.data.OrderStatus;
import org.example.presentation.controllers.PurchaseOrderController;
import org.example.presentation.utils.PurchaseOrderMapperService;
import org.example.presentation.view.OrderRequestDTO;
import org.example.services.AuthorisationService;
import org.example.utils.ErrorMessages;
import org.example.utils.data.Roles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseOrderControllerShould {

    PurchaseOrderController purchaseOrderController;
    @Mock AuthorisationService authorisationService;
    @Mock
    PurchaseOrderFilteringService purchaseOrderFilteringService;
    @Mock PurchaseOrderService purchaseOrderService;
    @Mock PurchaseOrderMapperService purchaseOrderMapperService;

    @Mock HttpServletRequest request;

    @Before
    public void setUp() {
        PurchaseOrderValidatorService purchaseOrderValidatorService = new PurchaseOrderValidatorService();

        purchaseOrderController = new PurchaseOrderController(purchaseOrderService, purchaseOrderMapperService, authorisationService, purchaseOrderFilteringService, purchaseOrderValidatorService);
    }

    @Test
    public void returnNotFoundResponseWhenOrderDoesNotExist(){
        UUID searchedUUID = UUID.randomUUID();

        given(request.getAttribute("roles")).willReturn(List.of(Roles.BUYER_CUSTOMER));
        given(request.getAttribute("company")).willReturn(UUID.randomUUID());
        given(purchaseOrderService.getPurchaseOrder(eq(searchedUUID), any())).willAnswer((answer) -> { throw new ResourceNotFoundException(ErrorMessages.ORDER_NOT_FOUND, searchedUUID.toString()); });

        assertThrows(ResourceNotFoundException.class, () -> purchaseOrderController.getPurchaseOrder(searchedUUID, request));

        verify(request).getAttribute("roles");
        verify(request).getAttribute("company");
        verify(purchaseOrderService).getPurchaseOrder(eq(searchedUUID), any());
    }

    @Test
    public void returnForbiddenResponseForMismatchedRolesWhenOrdersQuerying(){
        UUID searchedUUID = UUID.randomUUID();
        Set<Roles> userRoles = Set.of(Roles.BUYER_FINANCE);

        given(request.getAttribute("roles")).willReturn(List.copyOf(userRoles));
        given(request.getAttribute("company")).willReturn(UUID.randomUUID());
        given(authorisationService.authorize(eq(userRoles), any())).willAnswer((answer) -> { throw new ForbiddenActionException(); });

        assertThrows(ForbiddenActionException.class, () -> purchaseOrderController.getPurchaseOrder(searchedUUID, request));

        verify(request).getAttribute("roles");
        verify(request).getAttribute("company");
        verify(authorisationService).authorize(eq(userRoles), any());
    }

    @Test
    public void returnForbiddenResponseForValidRolesWithoutSomePermissionsWhenUpdate(){
        Set<Roles> userRoles = Set.of(Roles.BUYER_CUSTOMER, Roles.SUPPLIER_MANAGEMENT);
        UUID userCompanyUUID = UUID.randomUUID();
        OrderRequestDTO orderRequestDTO = createOrderRequestDTO(UUID.randomUUID(), UUID.randomUUID(), OrderStatus.SAVED);

        given(request.getAttribute("roles")).willReturn(List.copyOf(userRoles));
        given(request.getAttribute("company")).willReturn(userCompanyUUID);
        given(authorisationService.authorize(eq(userRoles), any())).willAnswer((answer) -> answer.getArgument(0));

        assertThrows(ForbiddenActionException.class, () -> purchaseOrderController.updatePurchaseOrder(UUID.randomUUID(), orderRequestDTO, request));

        verify(request).getAttribute("roles");
        verify(request).getAttribute("company");
        verify(authorisationService).authorize(eq(userRoles), any());
    }

    private OrderRequestDTO createOrderRequestDTO(UUID buyer, UUID seller, OrderStatus orderStatus){
        return OrderRequestDTO.builder()
                .identifier(UUID.randomUUID())
                .buyer(buyer)
                .seller(seller)
                .orderStatus(orderStatus)
                .items(Set.of())
                .version(0)
                .build();
    }
}