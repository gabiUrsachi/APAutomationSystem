package org.example.business.services;

import org.example.business.exceptions.InvoiceNotFoundException;
import org.example.business.models.OrderResponseDTO;
import org.example.business.models.InvoiceDTO;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.example.persistence.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private MapperService mapperService;

    public InvoiceService(InvoiceRepository invoiceRepository, MapperService mapperService) {
        this.invoiceRepository = invoiceRepository;
        this.mapperService = mapperService;
    }

    public InvoiceDTO createInvoice(@RequestBody InvoiceDTO invoiceDTO) {

        Invoice invoice = mapperService.mapToEntity(invoiceDTO);
        Invoice responseInvoice = invoiceRepository.insert(invoice);

        return mapperService.mapToDTO(responseInvoice);
    }

    public List<Invoice> getInvoices() {
        return invoiceRepository.findAll();
    }

    public InvoiceDTO createInvoiceDTOFromPurchaseOrder(OrderResponseDTO orderResponseDTO) {

        return mapperService.mapToDTO(orderResponseDTO);
    }

    public Optional<Invoice> getInvoice(UUID identifier) {

        Optional<Invoice> invoice;
        invoice = invoiceRepository.findByIdentifier(identifier);
        if (invoice.isEmpty()) {
            throw new InvoiceNotFoundException("Couldn't find invoice with identifier ");
        }
        return invoice;
    }

    public void deleteInvoice(UUID identifier) {
        invoiceRepository.deleteByIdentifier(identifier);
    }

}
