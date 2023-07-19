package org.system.presentation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.system.business.models.OrderRequestDTO;
import org.system.business.models.OrderResponseDTO;
import org.system.business.services.PurchaseOrderOpsService;


@RestController
@RequestMapping("/api/orders")
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderOpsService purchaseOrderOpsService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createPurchaseOrder(@RequestBody OrderRequestDTO orderRequestDTO){
        OrderResponseDTO orderResponseDTO = purchaseOrderOpsService.createPurchaseOrder(orderRequestDTO);

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.CREATED);
    }
}