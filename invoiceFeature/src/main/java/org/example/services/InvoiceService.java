package org.example.services;

import org.example.persistence.utils.data.InvoiceFilter;
import org.example.utils.ErrorMessages;
import org.example.customexceptions.InvalidUpdateException;
import org.example.customexceptions.InvoiceNotFoundException;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.example.persistence.utils.InvoiceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
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

    public List<Invoice> getInvoices(List<InvoiceFilter> filters) {

        return invoiceRepository.findByFilters(filters);
    }

    public Invoice getInvoice(UUID identifier,List<InvoiceFilter> filters) {

        Invoice invoice = invoiceRepository.findByUUIDAndFilters(identifier,filters);
        if (invoice == null) {
            throw new InvoiceNotFoundException("Couldn't find invoice with identifier " + identifier);
        }
        return invoice;
    }

    public void deleteInvoice(UUID identifier) {
        invoiceRepository.deleteByIdentifier(identifier);
    }

    public Invoice initializeInvoice(Invoice invoice) {

        UUID identifier = UUID.randomUUID();
        invoice.setIdentifier(identifier);
        invoice.setVersion(0);
        invoice.setInvoiceStatus(InvoiceStatus.CREATED);

        return invoice;

    }
    public void updateInvoice(UUID identifier, Invoice invoice) {

        int currentVersion=invoice.getVersion();


        Invoice updatedInvoice = Invoice.builder()
                .identifier(invoice.getIdentifier())
                .buyerId(invoice.getBuyerId())
                .sellerId(invoice.getSellerId())
                .items(invoice.getItems())
                .invoiceStatus(invoice.getInvoiceStatus())
                .version(currentVersion+1)
                .build();

        int updateCount = invoiceRepository.updateByIdentifierAndVersion(identifier, currentVersion,updatedInvoice);

        if (updateCount == 0) {
            Optional<Invoice> existingInvoice = invoiceRepository.findByIdentifier(identifier);

            if(existingInvoice.isEmpty()){
                throw new InvoiceNotFoundException("Couldn't find invoice with identifier "+ invoice.getIdentifier());
            }

            if(!Objects.equals(existingInvoice.get().getVersion(),invoice.getVersion())){
                throw new OptimisticLockingFailureException(ErrorMessages.INVALID_VERSION);
            }

            if (!existingInvoice.get().getInvoiceStatus().equals(InvoiceStatus.CREATED)) {
                throw new InvalidUpdateException(ErrorMessages.INVALID_UPDATE,existingInvoice.get().getIdentifier());
            }
        }

        invoice = changeInvoiceStatus(invoice,InvoiceStatus.PAID);

        invoiceRepository.save(invoice);

    }

    public Invoice changeInvoiceStatus(Invoice invoice, InvoiceStatus invoiceStatus) {

        invoice.setInvoiceStatus(invoiceStatus);
        return invoice;


    }
}