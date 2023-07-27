package org.example.business.services;

import org.example.business.exceptions.InvoiceNotFoundException;
import org.example.business.models.OrderResponseDTO;
import org.example.business.models.InvoiceDTO;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoiceMapperService invoiceMapperService;

    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceMapperService invoiceMapperService) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapperService = invoiceMapperService;
    }

    public InvoiceDTO createInvoice(@RequestBody InvoiceDTO invoiceDTO) {

        Invoice invoice = invoiceMapperService.mapToEntity(invoiceDTO);
        Invoice responseInvoice = invoiceRepository.insert(invoice);

        return invoiceMapperService.mapToDTO(responseInvoice);
    }

    public List<InvoiceDTO> getInvoices() {

        return invoiceMapperService.mapToDTO(invoiceRepository.findAll());
    }

    public InvoiceDTO createInvoiceDTOFromPurchaseOrder(OrderResponseDTO orderResponseDTO) {

        return invoiceMapperService.mapToDTO(orderResponseDTO);
    }

    public InvoiceDTO getInvoice(UUID identifier) {

        Optional<Invoice> invoice;
        invoice = invoiceRepository.findByIdentifier(identifier);
        if (invoice.isEmpty()) {
            throw new InvoiceNotFoundException("Couldn't find invoice with identifier ");
        }
        return invoiceMapperService.mapToDTO(invoice.get());
    }

    public void deleteInvoice(UUID identifier) {
        invoiceRepository.deleteByIdentifier(identifier);
    }

}
