package org.example.services;

import org.example.SQSOps;
import org.example.business.utils.InvoiceStatusPrecedence;
import org.example.customexceptions.InvalidResourceUpdateException;
import org.example.customexceptions.ResourceNotFoundException;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.example.persistence.utils.InvoiceStatus;
import org.example.persistence.utils.data.InvoiceFilter;
import org.example.utils.ErrorMessages;
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

        Invoice initializedInvoice = Invoice.builder()
                .identifier(UUID.randomUUID())
                .buyerId(invoiceEntity.getBuyerId())
                .sellerId(invoiceEntity.getSellerId())
                .items(invoiceEntity.getItems())
                .version(0)
                .invoiceStatus(InvoiceStatus.CREATED)
                .build();
        initializedInvoice.setUri(initializedInvoice.getIdentifier()+"."+invoiceEntity.getUri());

        return invoiceRepository.insert(initializedInvoice);
    }

    public List<Invoice> getInvoices(List<InvoiceFilter> filters) {

        return invoiceRepository.findByFilters(filters);
    }

    public Invoice getInvoice(UUID identifier, List<InvoiceFilter> filters) {

        Invoice invoice = invoiceRepository.findByUUIDAndFilters(identifier, filters);
        if (invoice == null) {
            throw new ResourceNotFoundException(ErrorMessages.INVOICE_NOT_FOUND, identifier.toString());
        }
        return invoice;
    }

    public void deleteInvoice(UUID identifier) {
        int deletedRowsCount = invoiceRepository.deleteByIdentifier(identifier);

        if (deletedRowsCount == 0) {
            throw new ResourceNotFoundException(ErrorMessages.INVOICE_NOT_FOUND, identifier.toString());
        }
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
                .uri(invoice.getUri())
                .build();


        Optional<Invoice> oldInvoice = invoiceRepository.findByIdentifier(identifier);

        if (oldInvoice.isPresent()) {
            if ( oldInvoice.get().getInvoiceStatus() != requiredInvoiceStatus) {
                throw new InvalidResourceUpdateException(ErrorMessages.INVALID_UPDATE, oldInvoice.get().getIdentifier());

            }
        }
        if (!isInEnum(String.valueOf(invoice.getInvoiceStatus()))) {
            throw new InvalidResourceUpdateException(ErrorMessages.INVALID_UPDATE, invoice.getIdentifier());
        }

        int updateCount = invoiceRepository.updateByIdentifierAndVersion(identifier, currentVersion, updatedInvoice);

        if (updateCount == 0) {
            Optional<Invoice> existingInvoice = invoiceRepository.findByIdentifier(identifier);

            if (existingInvoice.isEmpty()) {
                throw new ResourceNotFoundException(ErrorMessages.INVOICE_NOT_FOUND, invoice.getIdentifier().toString());
            }

            if (!Objects.equals(existingInvoice.get().getVersion(), invoice.getVersion())) {
                throw new OptimisticLockingFailureException(ErrorMessages.INVALID_VERSION);
            }

            if (!existingInvoice.get().getInvoiceStatus().equals(InvoiceStatus.CREATED)) {
                throw new InvalidResourceUpdateException(ErrorMessages.INVALID_UPDATE, existingInvoice.get().getIdentifier());
            }

        }

        if (updatedInvoice.getInvoiceStatus().equals(InvoiceStatus.SENT)) {
            // sellerCompany/documentId/buyerCompany
            SQSOps.sendMessage(updatedInvoice.getSellerId() + "/" + updatedInvoice.getIdentifier() + "/" + updatedInvoice.getBuyerId());
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
