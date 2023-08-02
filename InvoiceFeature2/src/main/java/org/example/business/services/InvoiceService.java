package org.example.business.services;

import org.example.business.exceptions.InvoiceNotFoundException;
import org.example.business.models.InvoiceDDO;
import org.example.business.models.InvoiceDPO;
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

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }


    public Invoice createInvoice(@RequestBody Invoice invoiceEntity) {

        Invoice initializedInvoice = initializeInvoice(invoiceEntity);
        return invoiceRepository.insert(initializedInvoice);
    }

    public List<Invoice> getInvoices() {

        return invoiceRepository.findAll();
    }

    public Invoice getInvoice(UUID identifier) {

        Optional<Invoice> invoice;
        invoice = invoiceRepository.findByIdentifier(identifier);
        if (invoice.isEmpty()) {
            throw new InvoiceNotFoundException("Couldn't find invoice with identifier ");
        }
        return invoice.get();
    }

    public void deleteInvoice(UUID identifier) {
        invoiceRepository.deleteByIdentifier(identifier);
    }

    public Invoice initializeInvoice(Invoice invoice) {

        UUID identifier = UUID.randomUUID();
        invoice.setIdentifier(identifier);

        return invoice;

    }
}
