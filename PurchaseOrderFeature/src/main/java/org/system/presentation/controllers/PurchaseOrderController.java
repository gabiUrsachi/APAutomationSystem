package org.system.presentation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.system.business.services.PurchaseOrderService;
import org.example.persistence.collections.Company;

import java.util.Set;


@RestController
@RequestMapping("/api/po")
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderService purchaseOrderService;


    @GetMapping(value = "/companies")
    public ResponseEntity<Set<Company>> getCompanies(@RequestParam String name){
        return new ResponseEntity<>(purchaseOrderService.getCompaniesByName(name), HttpStatus.OK);
    }
}