package org.example.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.presentation.utils.ActionsPermissions;
import org.example.presentation.utils.ResourceActionType;
import org.example.utils.Roles;
import org.example.business.services.PurchaseOrderFilteringService;
import org.example.business.services.PurchaseOrderService;
import org.example.business.services.PurchaseOrderValidatorService;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.data.PurchaseOrderFilter;
import org.example.presentation.utils.PurchaseOrderMapperService;
import org.example.presentation.view.OrderRequestDTO;
import org.example.presentation.view.OrderResponseDTO;
import org.example.services.AuthorisationService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200/")
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderMapperService purchaseOrderMapperService;
    private final AuthorisationService authorisationService;
    private final PurchaseOrderFilteringService purchaseOrderFilteringService;
    private final PurchaseOrderValidatorService purchaseOrderValidatorService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService, PurchaseOrderMapperService purchaseOrderMapperService, AuthorisationService authorisationService, PurchaseOrderFilteringService purchaseOrderFilteringService, PurchaseOrderValidatorService purchaseOrderValidatorService) {
        this.purchaseOrderService = purchaseOrderService;
        this.purchaseOrderMapperService = purchaseOrderMapperService;
        this.authorisationService = authorisationService;
        this.purchaseOrderFilteringService = purchaseOrderFilteringService;
        this.purchaseOrderValidatorService = purchaseOrderValidatorService;
    }

    @Operation(summary = "creates new purchase order")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully added  new order resource"),
                    @ApiResponse(responseCode = "401", description = "Invalid token"),
                    @ApiResponse(responseCode = "403", description = "Invalid role or company identifier mismatch")
            })
    @PostMapping
    @SuppressWarnings("unchecked cast")
    public OrderResponseDTO createPurchaseOrder(@RequestBody OrderRequestDTO orderRequestDTO, HttpServletRequest request) {
        UUID companyUUID = (UUID) request.getAttribute("company");
        Set<Roles> userRoles = new HashSet<>((List <Roles>) request.getAttribute("roles"));
        Set<Roles> validRoles = ActionsPermissions.VALID_ROLES.get(ResourceActionType.CREATE);

        authorisationService.authorize(userRoles, validRoles.toArray(new Roles[0]));
        purchaseOrderValidatorService.verifyIdentifiersMatch(companyUUID, orderRequestDTO.getBuyer());

        PurchaseOrder purchaseOrderRequest = purchaseOrderMapperService.mapToEntity(orderRequestDTO);

        PurchaseOrder createdPurchaseOrder = purchaseOrderService.createPurchaseOrder(purchaseOrderRequest);

        return purchaseOrderMapperService.mapToDTO(createdPurchaseOrder);
    }

    @Operation(summary = "get purchase order by identifier")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found purchase order"),
                    @ApiResponse(responseCode = "401", description = "Invalid token"),
                    @ApiResponse(responseCode = "403", description = "Invalid role"),
                    @ApiResponse(responseCode = "404", description = "No purchase order matching the identifier given for the logged in user was found")
            })
    @GetMapping("/{identifier}")
    @SuppressWarnings("unchecked cast")
    public OrderResponseDTO getPurchaseOrder(@PathVariable UUID identifier, HttpServletRequest request) {
        UUID companyUUID = (UUID) request.getAttribute("company");
        Set<Roles> userRoles = new HashSet<>((List <Roles>) request.getAttribute("roles"));
        Set<Roles> validRoles = ActionsPermissions.VALID_ROLES.get(ResourceActionType.GET);

        Set<Roles> matchingRoles = authorisationService.authorize(userRoles, validRoles.toArray(new Roles[0]));

        List<PurchaseOrderFilter> queryFilters = purchaseOrderFilteringService.createQueryFilters(matchingRoles, companyUUID);
        PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrder(identifier, queryFilters);

        return purchaseOrderMapperService.mapToDTO(purchaseOrder);
    }

    @Operation(summary = "get all purchase orders")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found purchase orders"),
                    @ApiResponse(responseCode = "401", description = "Invalid token"),
                    @ApiResponse(responseCode = "403", description = "Invalid role")
            })
    @GetMapping
    @SuppressWarnings("unchecked cast")
    public List<OrderResponseDTO> getPurchaseOrders(HttpServletRequest request) {
        UUID companyUUID = (UUID) request.getAttribute("company");
        Set<Roles> userRoles = new HashSet<>((List <Roles>) request.getAttribute("roles"));
        Set<Roles> validRoles = ActionsPermissions.VALID_ROLES.get(ResourceActionType.GET);

        Set<Roles> matchingRoles = authorisationService.authorize(userRoles, validRoles.toArray(new Roles[0]));

        List<PurchaseOrderFilter> queryFilters = purchaseOrderFilteringService.createQueryFilters(matchingRoles, companyUUID);
        List<PurchaseOrder> purchaseOrders = purchaseOrderService.getPurchaseOrders(queryFilters);

        return purchaseOrderMapperService.mapToDTO(purchaseOrders);
    }

    @Operation(summary = "updates an existing purchase order")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully updated order resource"),
                    @ApiResponse(responseCode = "401", description = "Invalid token"),
                    @ApiResponse(responseCode = "403", description = "Invalid role or lack of permission for a valid role"),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
                    @ApiResponse(responseCode = "412", description = "Invalid resource version for update"),
                    @ApiResponse(responseCode = "422", description = "Invalid resource status for update")
            })
    @PutMapping("/{identifier}")
    @SuppressWarnings("unchecked cast")
    public OrderResponseDTO updatePurchaseOrder(@PathVariable UUID identifier, @RequestBody OrderRequestDTO orderRequestDTO, HttpServletRequest request) {
        UUID companyUUID = (UUID) request.getAttribute("company");
        Set<Roles> userRoles = new HashSet<>((List <Roles>) request.getAttribute("roles"));
        Set<Roles> validRoles = ActionsPermissions.VALID_ROLES.get(ResourceActionType.UPDATE);

        authorisationService.authorize(userRoles, validRoles.toArray(new Roles[0]));
        purchaseOrderValidatorService.verifyUpdatePermission(orderRequestDTO.getOrderStatus(), companyUUID, orderRequestDTO.getBuyer(), orderRequestDTO.getSeller());

        PurchaseOrder purchaseOrderRequest = purchaseOrderMapperService.mapToEntity(orderRequestDTO);

        PurchaseOrder updatedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(purchaseOrderRequest);

        return purchaseOrderMapperService.mapToDTO(updatedPurchaseOrder);
    }

    @Operation(summary = "remove purchase order by id")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully removed purchase order"),
                    @ApiResponse(responseCode = "401", description = "Invalid token"),
                    @ApiResponse(responseCode = "403", description = "Invalid role"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    @DeleteMapping("/{identifier}")
    @SuppressWarnings("unchecked cast")
    public void removePurchaseOrder(@PathVariable UUID identifier, HttpServletRequest request) {
        Set<Roles> userRoles = new HashSet<>((List <Roles>) request.getAttribute("roles"));
        Set<Roles> validRoles = ActionsPermissions.VALID_ROLES.get(ResourceActionType.DELETE);

        authorisationService.authorize(userRoles, validRoles.toArray(new Roles[0]));

        purchaseOrderService.deletePurchaseOrder(identifier);
    }
}