package org.example.business.services;

import org.example.business.errorhandling.ErrorMessages;
import org.example.business.errorhandling.customexceptions.OrderNotFoundException;
import org.example.business.exceptions.InvoiceNotFoundException;
import org.example.business.models.*;
import org.example.persistence.collections.Invoice;
import org.example.persistence.collections.PurchaseOrder;
import org.example.persistence.repository.InvoiceRepository;
import org.example.persistence.utils.InvoiceStatus;
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
            throw new InvoiceNotFoundException("Couldn't find invoice with identifier " + identifier);
        }
        return invoice.get();
    }

    public void deleteInvoice(UUID identifier) {
        invoiceRepository.deleteByIdentifier(identifier);
    }

    public Invoice initializeInvoice(Invoice invoice) {

        UUID identifier = UUID.randomUUID();
        invoice.setIdentifier(identifier);
        invoice.setInvoiceStatus(InvoiceStatus.CREATED);

        return invoice;

    }
    public void updateInvoice(UUID identifier, Invoice invoice) {

        invoice.setIdentifier(identifier);
        invoiceRepository.save(invoice);

    }

    public Invoice changeStatusToSaved(UUID identifier) {

        Invoice invoice = getInvoice(identifier);
        invoice.setIdentifier(identifier);
        return invoiceRepository.save(invoice);


    }
}
