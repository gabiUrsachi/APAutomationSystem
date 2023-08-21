package org.example.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.Roles;
import org.example.business.services.FilterCreatorService;
import org.example.business.services.PurchaseOrderService;
import org.example.business.utils.Validator;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.utils.OrderStatus;
import org.example.presentation.utils.MapperService;
import org.example.persistence.utils.PurchaseOrderFilter;
import org.example.presentation.view.OrderRequestDTO;
import org.example.presentation.view.OrderResponseDTO;
import org.example.services.AuthorisationService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200/")
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;
    private final MapperService mapperService;
    private final AuthorisationService authorisationService;
    private final FilterCreatorService filterCreatorService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService, MapperService mapperService, AuthorisationService authorisationService, FilterCreatorService filterCreatorService) {
        this.purchaseOrderService = purchaseOrderService;
        this.mapperService = mapperService;
        this.authorisationService = authorisationService;
        this.filterCreatorService = filterCreatorService;
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
        Set<Roles> userRoles = (Set<Roles>) request.getAttribute("roles");
        UUID userCompanyUUID = (UUID) request.getAttribute("company");

        authorisationService.authorize(userRoles, Roles.BUYER_I);
        Validator.validateIdentifiersMatch(userCompanyUUID, orderRequestDTO.getBuyer());

        PurchaseOrder purchaseOrderRequest = mapperService.mapToEntity(orderRequestDTO);

        PurchaseOrder createdPurchaseOrder = purchaseOrderService.createPurchaseOrder(purchaseOrderRequest);

        return mapperService.mapToDTO(createdPurchaseOrder);
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
    public OrderResponseDTO getPurchaseOrder(@PathVariable UUID identifier, HttpServletRequest request) {
        Set<Roles> userRoles = (Set<Roles>) request.getAttribute("roles");
        UUID companyUUID = (UUID) request.getAttribute("company");

        Set<Roles> validRoles = authorisationService.authorize(userRoles, Roles.BUYER_I, Roles.SUPPLIER_I, Roles.SUPPLIER_II);

        List<PurchaseOrderFilter> queryFilters = filterCreatorService.createFilters(validRoles, companyUUID);
        PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrder(identifier, queryFilters);

        return mapperService.mapToDTO(purchaseOrder);
    }

    @Operation(summary = "get all purchase orders")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found purchase orders"),
                    @ApiResponse(responseCode = "401", description = "Invalid token"),
                    @ApiResponse(responseCode = "403", description = "Invalid role")
            })
    @GetMapping
    public List<OrderResponseDTO> getPurchaseOrders(HttpServletRequest request) {
        Set<Roles> userRoles = (Set<Roles>) request.getAttribute("roles");
        UUID companyUUID = (UUID) request.getAttribute("company");

        Set<Roles> validRoles = authorisationService.authorize(userRoles, Roles.BUYER_I, Roles.SUPPLIER_I, Roles.SUPPLIER_II);

        List<PurchaseOrderFilter> queryFilters = filterCreatorService.createFilters(validRoles, companyUUID);
        List<PurchaseOrder> purchaseOrders = purchaseOrderService.getPurchaseOrders(queryFilters);

        return mapperService.mapToDTO(purchaseOrders);
    }

    @Operation(summary = "updates an existing purchase order")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully updated order resource"),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
                    @ApiResponse(responseCode = "401", description = "Invalid token"),
                    @ApiResponse(responseCode = "403", description = "Invalid role"),
                    @ApiResponse(responseCode = "412", description = "Invalid resource version for update"),
                    @ApiResponse(responseCode = "422", description = "Invalid resource status for update")
            })
    @PutMapping("/{identifier}")
    public OrderResponseDTO updatePurchaseOrder(@PathVariable UUID identifier, @RequestBody OrderRequestDTO orderRequestDTO, HttpServletRequest request) {
        Set<Roles> userRoles = (Set<Roles>) request.getAttribute("roles");

        Set<Roles> validRoles = authorisationService.authorize(userRoles, Roles.BUYER_I, Roles.SUPPLIER_II);

        PurchaseOrder purchaseOrderRequest = mapperService.mapToEntity(orderRequestDTO);

        PurchaseOrder updatedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(purchaseOrderRequest);

        return mapperService.mapToDTO(updatedPurchaseOrder);
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
    public void removePurchaseOrder(@PathVariable UUID identifier, HttpServletRequest request) {
        purchaseOrderService.deletePurchaseOrder(identifier);

    }


    @GetMapping("/test")
    public List<PurchaseOrder> testEndpoint() {
        List<PurchaseOrderFilter> filters = List.of(
                new PurchaseOrderFilter(OrderStatus.CREATED, UUID.fromString("2c70891c-50b5-436d-9496-7c3722adcab0"), "buyer"),
                new PurchaseOrderFilter(OrderStatus.SAVED, UUID.fromString("2c70891c-50b5-436d-9496-7c3722adcab0"), "buyer")
        );

        List<PurchaseOrder> purchaseOrders = purchaseOrderService.getPurchaseOrders(filters);

        return purchaseOrders;
    }
}