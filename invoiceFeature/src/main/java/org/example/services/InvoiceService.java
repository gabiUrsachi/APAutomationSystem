package org.example.services;

import org.example.business.utils.InvoiceStatusPrecedence;
import org.example.customexceptions.OrderNotFoundException;
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

    public Invoice getInvoice(UUID identifier, List<InvoiceFilter> filters) {

        Invoice invoice = invoiceRepository.findByUUIDAndFilters(identifier, filters);
        if (invoice == null) {
            throw new InvoiceNotFoundException("Couldn't find invoice with identifier " + identifier);
        }
        return invoice;
    }

    public void deleteInvoice(UUID identifier) {
        int deletedRowsCount = invoiceRepository.deleteByIdentifier(identifier);

        if (deletedRowsCount == 0) {
            throw new InvoiceNotFoundException("Couldn't find invoice with identifier" + identifier);
        }
    }

    public Invoice initializeInvoice(Invoice invoice) {

        UUID identifier = UUID.randomUUID();
        invoice.setIdentifier(identifier);
        invoice.setVersion(0);
        invoice.setInvoiceStatus(InvoiceStatus.CREATED);

        return invoice;

    }

    public Invoice updateInvoice(UUID identifier, Invoice invoice) {

        int currentVersion = invoice.getVersion();
        InvoiceStatus requiredInvoiceStatus = InvoiceStatusPrecedence.PREDECESSORS.get(invoice.getInvoiceStatus());

        Invoice updatedInvoice = Invoice.builder()
                .identifier(invoice.getIdentifier())
                .buyerId(invoice.getBuyerId())
                .sellerId(invoice.getSellerId())
                .items(invoice.getItems())
                .invoiceStatus(invoice.getInvoiceStatus())
                .totalAmount(invoice.getTotalAmount())
                .version(currentVersion + 1)
                .build();


        Optional<Invoice> oldInvoice = invoiceRepository.findByIdentifier(identifier);

        if (oldInvoice.isPresent()) {
            if ( oldInvoice.get().getInvoiceStatus() != requiredInvoiceStatus) {
                throw new InvalidUpdateException(ErrorMessages.INVALID_UPDATE, oldInvoice.get().getIdentifier());

            }
        }
        if (!isInEnum(String.valueOf(invoice.getInvoiceStatus()))) {
            throw new InvalidUpdateException(ErrorMessages.INVALID_UPDATE, invoice.getIdentifier());
        }

        int updateCount = invoiceRepository.updateByIdentifierAndVersion(identifier, currentVersion, updatedInvoice);

        if (updateCount == 0) {
            Optional<Invoice> existingInvoice = invoiceRepository.findByIdentifier(identifier);

            if (existingInvoice.isEmpty()) {
                throw new InvoiceNotFoundException("Couldn't find invoice with identifier " + invoice.getIdentifier());
            }

            if (!Objects.equals(existingInvoice.get().getVersion(), invoice.getVersion())) {
                throw new OptimisticLockingFailureException(ErrorMessages.INVALID_VERSION);
            }

            if (!existingInvoice.get().getInvoiceStatus().equals(InvoiceStatus.CREATED)) {
                throw new InvalidUpdateException(ErrorMessages.INVALID_UPDATE, existingInvoice.get().getIdentifier());
            }

        }

        return updatedInvoice;
    }

    public Invoice changeInvoiceStatus(Invoice invoice, InvoiceStatus invoiceStatus) {

        invoice.setInvoiceStatus(invoiceStatus);
        return invoice;


    }

    public static boolean isInEnum(String value) {
        return Arrays.stream(InvoiceStatus.values()).anyMatch(e -> e.name().equals(value));
    }
}
