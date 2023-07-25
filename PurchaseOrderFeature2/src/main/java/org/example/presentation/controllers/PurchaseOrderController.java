package org.example.presentation.controllers;

import org.example.business.models.OrderRequestDTO;
import org.example.business.models.OrderResponseDTO;
import org.example.business.services.OrderOperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200/")
public class PurchaseOrderController {
    @Autowired
    private OrderOperationsService orderOperationsService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createPurchaseOrder(@RequestBody OrderRequestDTO orderRequestDTO){
        OrderResponseDTO orderResponseDTO = orderOperationsService.createPurchaseOrder(orderRequestDTO);

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{identifier}")
    public ResponseEntity<Void> updatePurchaseOrder(@PathVariable UUID identifier, @RequestBody OrderRequestDTO orderRequestDTO){
        orderOperationsService.updatePurchaseOrder(identifier, orderRequestDTO);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{identifier}")
    public ResponseEntity<OrderResponseDTO> savePurchaseOrder(@PathVariable UUID identifier){
        OrderResponseDTO orderResponseDTO = orderOperationsService.savePurchaseOrder(identifier);

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<OrderResponseDTO> getPurchaseOrder(@PathVariable UUID identifier){
        OrderResponseDTO orderResponseDTO = orderOperationsService.getPurchaseOrder(identifier);

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getPurchaseOrders(){
        List<OrderResponseDTO> orderResponseDTOs = orderOperationsService.getPurchaseOrders();

        return new ResponseEntity<>(orderResponseDTOs, HttpStatus.OK);
    }


}