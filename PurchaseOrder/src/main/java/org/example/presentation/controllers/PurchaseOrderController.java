package org.example.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.Roles;
import org.example.business.services.PurchaseOrderService;
import org.example.persistence.collections.PurchaseOrder;
import org.example.presentation.utils.MapperService;
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
    private final MapperService mapperService;
    private final AuthorisationService authorisationService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService, MapperService mapperService, AuthorisationService authorisationService) {
        this.purchaseOrderService = purchaseOrderService;
        this.mapperService = mapperService;
        this.authorisationService = authorisationService;
    }

    @Operation(summary = "creates new purchase order")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully added  new order resource")
            })
    @PostMapping
    @SuppressWarnings("unchecked cast")
    public OrderResponseDTO createPurchaseOrder(@RequestBody OrderRequestDTO orderRequestDTO, HttpServletRequest request) {
        Set<Roles> userRoles = (Set<Roles>) request.getAttribute("roles");
        authorisationService.authorize(userRoles, Roles.BUYER_I);

        PurchaseOrder purchaseOrderRequest = mapperService.mapToEntity(orderRequestDTO);

        PurchaseOrder createdPurchaseOrder = purchaseOrderService.createPurchaseOrder(purchaseOrderRequest);

        return mapperService.mapToDTO(createdPurchaseOrder);
    }

    @Operation(summary = "get purchase order by identifier")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found purchase order"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    @GetMapping("/{identifier}")
    public OrderResponseDTO getPurchaseOrder(@PathVariable UUID identifier, HttpServletRequest request) {
        PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrder(identifier);

        return mapperService.mapToDTO(purchaseOrder);
    }

    @Operation(summary = "get all purchase orders")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found purchase orders")
            })
    @GetMapping
    public List<OrderResponseDTO> getPurchaseOrders(HttpServletRequest request) {
        List<PurchaseOrder> purchaseOrders = purchaseOrderService.getPurchaseOrders();

        return mapperService.mapToDTO(purchaseOrders);
    }

    @Operation(summary = "updates an existing purchase order")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully updated order resource"),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
                    @ApiResponse(responseCode = "412", description = "Invalid resource version for update"),
                    @ApiResponse(responseCode = "422", description = "Invalid resource status for update"),
            })
    @PutMapping("/{identifier}")
    public OrderResponseDTO updatePurchaseOrder(@PathVariable UUID identifier, @RequestBody OrderRequestDTO orderRequestDTO, HttpServletRequest request) {
        PurchaseOrder purchaseOrderRequest = mapperService.mapToEntity(orderRequestDTO);

        PurchaseOrder updatedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(purchaseOrderRequest);

        return mapperService.mapToDTO(updatedPurchaseOrder);
    }

    @Operation(summary = "remove purchase order by id")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully removed purchase order"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    @DeleteMapping("/{identifier}")
    public void removePurchaseOrder(@PathVariable UUID identifier, HttpServletRequest request) {
        purchaseOrderService.deletePurchaseOrder(identifier);

    }
}