package org.example.presentation.controllers;

import org.example.business.models.CompanyDTO;
import org.example.business.models.OrderRequestDTO;
import org.example.business.models.OrderResponseDTO;
import org.example.business.services.CompanyOpsService;
import org.example.business.services.PurchaseOrderOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/api/orders")
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderOpsService purchaseOrderOpsService;

    @Autowired
    private CompanyOpsService companyOpsService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createPurchaseOrder(@RequestBody OrderRequestDTO orderRequestDTO){
        OrderResponseDTO orderResponseDTO = purchaseOrderOpsService.createPurchaseOrder(orderRequestDTO);

        return new ResponseEntity<>(orderResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{identifier}")
    public ResponseEntity<Void> updatePurchaseOrder(@PathVariable UUID identifier, @RequestBody OrderRequestDTO orderRequestDTO){
        purchaseOrderOpsService.updatePurchaseOrder(identifier, orderRequestDTO);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<Set<OrderResponseDTO>> getPurchaseOrders(@RequestParam String buyerName){
        CompanyDTO buyerCompany = companyOpsService.getCompanyByName(buyerName);
        Set<OrderResponseDTO> orderResponseDTOSet = purchaseOrderOpsService.getPurchaseOrdersByBuyer(buyerCompany);

        return new ResponseEntity<>(orderResponseDTOSet, HttpStatus.OK);
    }
}