package org.example.presentation.controllers.unit;


import org.example.business.services.FilteringService;
import org.example.business.services.PurchaseOrderService;
import org.example.business.services.ValidatorService;
import org.example.customexceptions.ForbiddenUpdateException;
import org.example.customexceptions.InvalidRoleException;
import org.example.customexceptions.OrderNotFoundException;
import org.example.filters.TokenValidationFilter;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.data.OrderStatus;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.example.presentation.controllers.PurchaseOrderController;
import org.example.presentation.utils.PurchaseOrderMapperService;
import org.example.presentation.view.OrderRequestDTO;
import org.example.services.AuthorisationService;
import org.example.utils.ErrorMessages;
import org.example.utils.Roles;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseOrderControllerShould {

    PurchaseOrderController purchaseOrderController;
    @Mock AuthorisationService authorisationService;
    @Mock FilteringService filteringService;
    @Mock PurchaseOrderService purchaseOrderService;
    @Mock PurchaseOrderMapperService purchaseOrderMapperService;

    @Mock HttpServletRequest request;

    @Before
    public void setUp() {
        ValidatorService validatorService = new ValidatorService();

        purchaseOrderController = new PurchaseOrderController(purchaseOrderService, purchaseOrderMapperService, authorisationService, filteringService, validatorService);
    }

    @Test
    public void returnNotFoundResponseWhenOrderDoesNotExist(){
        UUID searchedUUID = UUID.randomUUID();

        given(request.getAttribute("roles")).willReturn(List.of(Roles.BUYER_I));
        given(request.getAttribute("company")).willReturn(UUID.randomUUID());
        given(purchaseOrderService.getPurchaseOrder(eq(searchedUUID), any())).willAnswer((answer) -> { throw new OrderNotFoundException(ErrorMessages.ORDER_NOT_FOUND, searchedUUID); });

        assertThrows(OrderNotFoundException.class, () -> purchaseOrderController.getPurchaseOrder(searchedUUID, request));

        verify(request).getAttribute("roles");
        verify(request).getAttribute("company");
        verify(purchaseOrderService).getPurchaseOrder(eq(searchedUUID), any());
    }

    @Test
    public void returnForbiddenResponseForMismatchedRolesWhenOrdersQuerying(){
        UUID searchedUUID = UUID.randomUUID();
        Set<Roles> userRoles = Set.of(Roles.BUYER_II);

        given(request.getAttribute("roles")).willReturn(List.copyOf(userRoles));
        given(request.getAttribute("company")).willReturn(UUID.randomUUID());
        given(authorisationService.authorize(eq(userRoles), any())).willAnswer((answer) -> { throw new InvalidRoleException(); });

        assertThrows(InvalidRoleException.class, () -> purchaseOrderController.getPurchaseOrder(searchedUUID, request));

        verify(request).getAttribute("roles");
        verify(request).getAttribute("company");
        verify(authorisationService).authorize(eq(userRoles), any());
    }

    @Test
    public void returnForbiddenResponseForValidRolesWithoutSomePermissionsWhenUpdate(){
        Set<Roles> userRoles = Set.of(Roles.BUYER_I, Roles.SUPPLIER_II);
        UUID userCompanyUUID = UUID.randomUUID();
        OrderRequestDTO orderRequestDTO = createOrderRequestDTO(UUID.randomUUID(), UUID.randomUUID(), OrderStatus.SAVED);

        given(request.getAttribute("roles")).willReturn(List.copyOf(userRoles));
        given(request.getAttribute("company")).willReturn(userCompanyUUID);
        given(authorisationService.authorize(eq(userRoles), any())).willAnswer((answer) -> answer.getArgument(0));

        assertThrows(ForbiddenUpdateException.class, () -> purchaseOrderController.updatePurchaseOrder(UUID.randomUUID(), orderRequestDTO, request));

        verify(request).getAttribute("roles");
        verify(request).getAttribute("company");
        verify(authorisationService).authorize(eq(userRoles), any());
    }


    private PurchaseOrder createPurchaseOrderWithStatus(OrderStatus orderStatus) {
        return PurchaseOrder.builder()
                .identifier(UUID.randomUUID())
                .buyer(UUID.randomUUID())
                .seller(UUID.randomUUID())
                .orderStatus(orderStatus)
                .items(Set.of())
                .build();
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