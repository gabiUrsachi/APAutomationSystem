package org.example.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.business.services.OrderOperationsService;
import org.example.persistence.collections.PurchaseOrder;
import org.example.presentation.utils.MapperService;
import org.example.presentation.view.OrderRequestDTO;
import org.example.presentation.view.OrderResponseDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200/")
public class PurchaseOrderController {
    private final OrderOperationsService orderOperationsService;
    private final MapperService mapperService;

    public PurchaseOrderController(OrderOperationsService orderOperationsService, MapperService mapperService) {
        this.orderOperationsService = orderOperationsService;
        this.mapperService = mapperService;
    }

    @Operation(summary = "get purchase order by identifier")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found purchase order"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    @GetMapping("/{identifier}")
    public OrderResponseDTO getPurchaseOrder(@PathVariable UUID identifier){
        PurchaseOrder purchaseOrder = orderOperationsService.getPurchaseOrder(identifier);

        return mapperService.mapToDTO(purchaseOrder);
    }

    @Operation(summary = "get all purchase orders")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found purchase orders")
            })
    @GetMapping
    public List<OrderResponseDTO> getPurchaseOrders(){
        List<PurchaseOrder> purchaseOrders = orderOperationsService.getPurchaseOrders();

        return mapperService.mapToDTO(purchaseOrders);
    }

    @Operation(summary = "creates new purchase order")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully added  new order resource")
            })
    @PostMapping
    public OrderResponseDTO createPurchaseOrder(@RequestBody OrderRequestDTO orderRequestDTO){
        PurchaseOrder purchaseOrderRequest = mapperService.mapToEntity(orderRequestDTO);

        PurchaseOrder createdPurchaseOrder = orderOperationsService.createPurchaseOrder(purchaseOrderRequest);

        return mapperService.mapToDTO(createdPurchaseOrder);
    }

    @Operation(summary = "updates an existing purchase order")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully updated order resource"),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
                    @ApiResponse(responseCode = "422", description = "Unsatisfied conditions for update"),
            })
    @PutMapping("/{identifier}")
    public OrderResponseDTO updatePurchaseOrder(@PathVariable UUID identifier, @RequestBody OrderRequestDTO orderRequestDTO){
        PurchaseOrder purchaseOrderRequest = mapperService.mapToEntity(orderRequestDTO);

        PurchaseOrder updatedPurchaseOrder = orderOperationsService.updatePurchaseOrder(identifier, purchaseOrderRequest);

        return mapperService.mapToDTO(updatedPurchaseOrder);
    }

    @Operation(summary = "changes state of an existing purchase order")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully updated order state"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    @PatchMapping("/{identifier}")
    public OrderResponseDTO savePurchaseOrder(@PathVariable UUID identifier){
        PurchaseOrder purchaseOrder = orderOperationsService.savePurchaseOrder(identifier);

        return mapperService.mapToDTO(purchaseOrder);
    }

    @Operation(summary = "remove purchase order by id")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully removed purchase order"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    @DeleteMapping("/{identifier}")
    public void removePurchaseOrder(@PathVariable UUID identifier){
        orderOperationsService.deletePurchaseOrder(identifier);

    }
}