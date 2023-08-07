package org.example.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.business.services.OrderOperationsService;
import org.example.persistence.collections.PurchaseOrder;
import org.example.presentation.utils.MapperService;
import org.example.presentation.view.OrderRequestDTO;
import org.example.presentation.view.OrderResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<OrderResponseDTO> getPurchaseOrder(@PathVariable UUID identifier){
        PurchaseOrder purchaseOrder = orderOperationsService.getPurchaseOrder(identifier);

        OrderResponseDTO orderResponseDTO = mapperService.mapToDTO(purchaseOrder);

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.OK);
    }

    @Operation(summary = "get all purchase orders")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found purchase orders")
            })
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getPurchaseOrders(){
        List<PurchaseOrder> purchaseOrders = orderOperationsService.getPurchaseOrders();

        List<OrderResponseDTO> orderResponseDTOs = mapperService.mapToDTO(purchaseOrders);

        return new ResponseEntity<>(orderResponseDTOs, HttpStatus.OK);
    }

    @Operation(summary = "creates new purchase order")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "201", description = "Successfully added  new order resource")
            })
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createPurchaseOrder(@RequestBody OrderRequestDTO orderRequestDTO){
        PurchaseOrder purchaseOrderRequest = mapperService.mapToEntity(orderRequestDTO);

        PurchaseOrder createdPurchaseOrder = orderOperationsService.createPurchaseOrder(purchaseOrderRequest);

        OrderResponseDTO orderResponseDTO = mapperService.mapToDTO(createdPurchaseOrder);

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "updates an existing purchase order")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully updated order resource"),
                    @ApiResponse(responseCode = "404", description = "Order not found"),
                    @ApiResponse(responseCode = "422", description = "Unsatisfied conditions for update"),
            })
    @PutMapping("/{identifier}")
    public ResponseEntity<OrderResponseDTO> updatePurchaseOrder(@PathVariable UUID identifier, @RequestBody OrderRequestDTO orderRequestDTO){
        PurchaseOrder purchaseOrderRequest = mapperService.mapToEntity(orderRequestDTO);

        PurchaseOrder updatedPurchaseOrder = orderOperationsService.updatePurchaseOrder(identifier, purchaseOrderRequest);

        OrderResponseDTO orderResponseDTO = mapperService.mapToDTO(updatedPurchaseOrder);

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.OK);
    }

    @Operation(summary = "changes state of an existing purchase order")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully updated order state"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    @PatchMapping("/{identifier}")
    public ResponseEntity<OrderResponseDTO> savePurchaseOrder(@PathVariable UUID identifier){
        PurchaseOrder purchaseOrder = orderOperationsService.savePurchaseOrder(identifier);

        OrderResponseDTO orderResponseDTO = mapperService.mapToDTO(purchaseOrder);

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.OK);
    }

    @Operation(summary = "remove purchase order by id")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully removed purchase order"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    @DeleteMapping("/{identifier}")
    public ResponseEntity<Void> removePurchaseOrder(@PathVariable UUID identifier){
        orderOperationsService.deletePurchaseOrder(identifier);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}