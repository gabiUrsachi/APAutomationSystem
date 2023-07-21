package org.example.presentation.controllers;

import org.example.business.models.OrderResponseDTO;
import org.example.business.services.CompanyOpsService;
import org.example.business.models.InvoiceDTO;
import org.example.business.services.InvoiceService;
import org.example.persistence.collections.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private CompanyOpsService companyOpsService;

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody InvoiceDTO invoiceDTO){
        InvoiceDTO responseInvoice = invoiceService.createInvoice(invoiceDTO);
        return new ResponseEntity<>(responseInvoice, HttpStatus.CREATED);

    }
    @GetMapping
    public ResponseEntity<List<Invoice>> getInvoices() {
        return new ResponseEntity<>(invoiceService.getInvoices(), HttpStatus.OK);
    }

    @PostMapping("/fromOR")
    public ResponseEntity<InvoiceDTO> createInvoiceFromPurchaseOrder(@RequestBody OrderResponseDTO orderResponseDTO ){
        InvoiceDTO responseInvoice = invoiceService.createInvoiceFromPurchaseOrder(orderResponseDTO);
        return new ResponseEntity<>(responseInvoice, HttpStatus.CREATED);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<Invoice> getById(@PathVariable UUID identifier){
        Optional<Invoice> invoice = invoiceService.getInvoice(identifier);
        System.out.println(identifier);

        return new ResponseEntity<>(invoice.get(),HttpStatus.OK);
    }
    @DeleteMapping("/{identifier}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID identifier){

        invoiceService.deleteInvoice(identifier);

        return new ResponseEntity<>(HttpStatus.OK); }
}