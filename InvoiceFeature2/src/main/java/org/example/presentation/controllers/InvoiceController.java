package org.example.presentation.controllers;

import lombok.AllArgsConstructor;
import org.example.business.models.*;

import org.example.business.services.InvoiceMapperService;
import org.example.business.services.InvoiceService;
import org.example.persistence.collections.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.presentation.view.OrderResponseDTO;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:4200/")
public class InvoiceController {
    private final InvoiceService invoiceService;

    private final InvoiceMapperService invoiceMapperService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService, InvoiceMapperService invoiceMapperService) {
        this.invoiceService = invoiceService;
        this.invoiceMapperService = invoiceMapperService;
    }

    @PostMapping
    public InvoiceDTO createInvoice(@RequestBody InvoiceDPO invoiceDPO) {

        Invoice invoiceEntity = invoiceMapperService.mapToEntity(invoiceDPO);
        Invoice responseInvoice = invoiceService.createInvoice(invoiceEntity);
        return invoiceMapperService.mapToDTO(responseInvoice);

    }

    @GetMapping
    public List<InvoiceDDO> getInvoices() {
        return invoiceMapperService.mapToDDO(invoiceService.getInvoices());

    }

    @PostMapping("/fromOR")
    public InvoiceDTO createInvoiceFromPurchaseOrder(@RequestBody OrderResponseDTO orderResponseDTO) {
        InvoiceDPO invoiceDPO = invoiceMapperService.mapToDPO(orderResponseDTO);
        return createInvoice(invoiceDPO);

    }

    @GetMapping("/{identifier}")
    public InvoiceDTO getById(@PathVariable UUID identifier) {

        return invoiceMapperService.mapToDTO(invoiceService.getInvoice(identifier));

    }

    @PutMapping("/{identifier}")
    public  InvoiceDTO updateInvoice(@PathVariable UUID identifier, @RequestBody InvoiceDTO invoiceDTO) {
        Invoice invoice = invoiceMapperService.mapToEntity(invoiceDTO);
        invoiceService.updateInvoice(identifier,invoice);
        return invoiceMapperService.mapToDTO(invoice);

    }
    @DeleteMapping("/{identifier}")
    public void deleteById(@PathVariable UUID identifier) {

        invoiceService.deleteInvoice(identifier);

    }


}