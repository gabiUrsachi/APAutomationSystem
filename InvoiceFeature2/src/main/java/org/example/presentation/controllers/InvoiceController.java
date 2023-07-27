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
    public InvoiceDTO createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.createInvoice(invoiceDTO);

    }

    @GetMapping
    public List<InvoiceDTO> getInvoices() {
        return invoiceService.getInvoices();
    }

    @PostMapping("/fromOR")
    public InvoiceDTO createInvoiceFromPurchaseOrder(@RequestBody OrderResponseDTO orderResponseDTO) {
        return invoiceService.createInvoiceDTOFromPurchaseOrder(orderResponseDTO);
    }

    @GetMapping("/{identifier}")
    public InvoiceDTO getById(@PathVariable UUID identifier) {
        return invoiceService.getInvoice(identifier);

    }

    @DeleteMapping("/{identifier}")
    public void deleteById(@PathVariable UUID identifier) {

        invoiceService.deleteInvoice(identifier);

    }
}