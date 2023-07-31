package org.example.presentation.controllers;

import org.example.business.models.InvoiceDDO;
import org.example.business.models.InvoiceDPO;
import org.example.business.models.OrderResponseDTO;
import org.example.business.models.InvoiceDTO;
import org.example.business.services.InvoiceMapperService;
import org.example.business.services.InvoiceService;
import org.example.persistence.collections.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:4200/")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceMapperService invoiceMapperService;

    @PostMapping
    public InvoiceDPO createInvoice(@RequestBody InvoiceDPO invoiceDPO) {
        InvoiceDPO initializedInvoice = initializeInvoice(invoiceDPO);
        Invoice invoiceEntity = invoiceMapperService.mapToEntity(initializedInvoice);

        Invoice responseInvoice = invoiceService.createInvoice(invoiceEntity);
        return invoiceMapperService.mapToDPO(responseInvoice);

    }

    @GetMapping
    public List<InvoiceDDO> getInvoices() {
        return invoiceMapperService.mapToDDO(invoiceService.getInvoices());

    }

    @PostMapping("/fromOR")
    public InvoiceDTO createInvoiceFromPurchaseOrder(@RequestBody OrderResponseDTO orderResponseDTO) {
        return invoiceMapperService.mapToDTO(orderResponseDTO);
    }

    @GetMapping("/{identifier}")
    public InvoiceDTO getById(@PathVariable UUID identifier) {

        return invoiceMapperService.mapToDTO(invoiceService.getInvoice(identifier));

    }

    @DeleteMapping("/{identifier}")
    public void deleteById(@PathVariable UUID identifier) {

        invoiceService.deleteInvoice(identifier);

    }

    public InvoiceDPO initializeInvoice(InvoiceDPO invoiceDPO) {

        UUID identifier = UUID.randomUUID();
        invoiceDPO.setIdentifier(identifier);

        return invoiceDPO;

    }
}