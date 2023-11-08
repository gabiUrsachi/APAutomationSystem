package org.example.business.services;

import org.example.SQSOps;
import org.example.business.discountStrategies.DiscountByAmountStrategy;
import org.example.business.discountStrategies.DiscountStrategy;
import org.example.business.discountStrategies.formulas.AmountBasedFormulaStrategy;
import org.example.business.utils.InvoiceStatusPrecedence;
import org.example.business.utils.InvoiceTaxationRate;
import org.example.customexceptions.InvalidResourceUpdateException;
import org.example.customexceptions.ResourceNotFoundException;
import org.example.persistence.collections.Invoice;
import org.example.persistence.repository.InvoiceRepository;
import org.example.persistence.utils.InvoiceStatus;
import org.example.persistence.utils.InvoiceStatusHistoryHelper;
import org.example.persistence.utils.data.InvoiceFilter;
import org.example.persistence.utils.data.InvoiceStatusHistoryObject;
import org.example.utils.ErrorMessages;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private InvoiceRepository invoiceRepository;

    private DiscountStrategy discountStrategy;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
        this.discountStrategy = new DiscountByAmountStrategy(this.invoiceRepository, new AmountBasedFormulaStrategy());
    }


    public Invoice createInvoice(@RequestBody Invoice invoiceEntity) {

        Invoice initializedInvoice = Invoice.builder()
                .identifier(UUID.randomUUID())
                .buyerId(invoiceEntity.getBuyerId())
                .sellerId(invoiceEntity.getSellerId())
                .items(invoiceEntity.getItems())
                .version(0)
                .statusHistory(InvoiceStatusHistoryHelper.initStatusHistory(InvoiceStatus.CREATED))
                .totalAmount(invoiceEntity.getTotalAmount())
                .build();
        initializedInvoice.setUri(initializedInvoice.getIdentifier() + "." + invoiceEntity.getUri());

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
        InvoiceStatus updatedInvoiceStatus = InvoiceStatusHistoryHelper.getMostRecentHistoryObject(invoice.getStatusHistory()).getStatus();
        InvoiceStatus requiredInvoiceStatus = InvoiceStatusPrecedence.PREDECESSORS.get(updatedInvoiceStatus);

        Invoice updatedInvoice = Invoice.builder()
                .identifier(invoice.getIdentifier())
                .buyerId(invoice.getBuyerId())
                .sellerId(invoice.getSellerId())
                .items(invoice.getItems())
                .statusHistory(invoice.getStatusHistory())
                .totalAmount(invoice.getTotalAmount())
                .version(currentVersion + 1)
                .uri(invoice.getUri())
                .build();

        Optional<Invoice> oldInvoice = invoiceRepository.findByIdentifier(identifier);

        if (oldInvoice.isPresent()) {
            if (InvoiceStatusHistoryHelper.getMostRecentHistoryObject(oldInvoice.get().getStatusHistory()).getStatus() != requiredInvoiceStatus) {
                throw new InvalidResourceUpdateException(ErrorMessages.INVALID_UPDATE, oldInvoice.get().getIdentifier());
            }
        }

        if (!isInEnum(String.valueOf(updatedInvoiceStatus))) {
            throw new InvalidResourceUpdateException(ErrorMessages.INVALID_UPDATE, invoice.getIdentifier());
        }

        if (updatedInvoiceStatus.equals(InvoiceStatus.SENT)) {
            updatedInvoice = applyDiscount(updatedInvoice);
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

            if (!InvoiceStatusHistoryHelper.getMostRecentHistoryObject(existingInvoice.get().getStatusHistory()).getStatus().equals(requiredInvoiceStatus)) {
                throw new InvalidResourceUpdateException(ErrorMessages.INVALID_UPDATE, existingInvoice.get().getIdentifier());
            }
        }

        if (updatedInvoiceStatus.equals(InvoiceStatus.SENT)) {
            // sellerCompany/documentId/buyerCompany
            SQSOps.sendMessage(updatedInvoice.getSellerId() + "/" + updatedInvoice.getIdentifier() + "/" + updatedInvoice.getBuyerId());
        }

        return updatedInvoice;
    }

    /**
     * This method is responsible for updating an invoice by adding a new field
     * related to the discount rate which should be applied to the total amount
     *
     * @param invoice document to be updated
     * @return updated document
     */
    private Invoice applyDiscount(Invoice invoice) {
        Invoice updatedInvoice = Invoice.builder()
                .identifier(invoice.getIdentifier())
                .buyerId(invoice.getBuyerId())
                .sellerId(invoice.getSellerId())
                .items(invoice.getItems())
                .statusHistory(invoice.getStatusHistory())
                .totalAmount(invoice.getTotalAmount())
                .version(invoice.getVersion())
                .uri(invoice.getUri())
                .build();

        Float discountRate = this.discountStrategy.computeDiscount(invoice.getBuyerId());
        updatedInvoice.setDiscountRate(discountRate);

        return updatedInvoice;
    }

    public static boolean isInEnum(String value) {
        return Arrays.stream(InvoiceStatus.values()).anyMatch(e -> e.name().equals(value));
    }

    public Float computeInvoiceTax(Integer month, Integer year, InvoiceFilter filter) {


        try {
            Date[] timestampsArray = generateMonthInterval(month, year);
            List<Invoice> filteredInvoices = invoiceRepository.findByBuyerUUIDAndDate(filter.getCompanyUUID(), timestampsArray[0], timestampsArray[1]);

            List<InvoiceStatus> statusList = filteredInvoices.stream()
                    .flatMap(purchaseOrder -> purchaseOrder.getStatusHistory().stream())
                    .map(InvoiceStatusHistoryObject::getStatus)
                    .collect(Collectors.toList());

            Map<InvoiceStatus, Long> monthlyStatusCounts = statusList.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Float taxAmount = 0.0f;
            for (Map.Entry<InvoiceStatus, Long> entry : monthlyStatusCounts.entrySet()) {

                Float statusTax = InvoiceTaxationRate.invoiceTaxRate.get(entry.getKey());
                Long statusChangesAmount = entry.getValue();
                taxAmount += statusTax * statusChangesAmount;
            }

            return taxAmount;

        } catch (Exception e) {
            throw new DateTimeException("Invalid date format");
        }

    }

    public Date[] generateMonthInterval(Integer month, Integer year) {

        ZonedDateTime firstDay = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.of("Z"));
        ZonedDateTime lastDay = firstDay.with(TemporalAdjusters.lastDayOfMonth());
        lastDay = lastDay.withHour(23).withMinute(59).withSecond(59).withNano(999000000);

        Date lowerTimestamp = Date.from(firstDay.toInstant());
        Date upperTimestamp = Date.from(lastDay.toInstant());

        return new Date[]{lowerTimestamp, upperTimestamp};

    }
}
