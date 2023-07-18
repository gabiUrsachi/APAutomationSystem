package com.system.automation.ap.purchaseOrder.controller;

import com.system.automation.ap.purchaseOrder.persistence.collections.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class POController {

    @GetMapping
    public ResponseEntity<List<Document>> getDocuments(){
         return null;
    }
}
