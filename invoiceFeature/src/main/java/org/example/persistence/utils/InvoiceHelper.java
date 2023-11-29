package org.example.persistence.utils;

import org.example.persistence.collections.Invoice;
import org.example.persistence.utils.data.InvoiceStatusHistoryObject;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class InvoiceHelper {
    public static InvoiceStatusHistoryObject getMostRecentHistoryObject(List<InvoiceStatusHistoryObject> invoiceStatusHistoryObjects) {

        return invoiceStatusHistoryObjects.stream()
                .max(Comparator.comparing(InvoiceStatusHistoryObject::getDate))
                .orElse(null);
    }

    public static List<InvoiceStatusHistoryObject> initStatusHistory(InvoiceStatus invoiceStatus){
        return List.of(
                InvoiceStatusHistoryObject
                        .builder()
                        .date(LocalDateTime.now())
                        .status(invoiceStatus)
                        .build()
        );
    }

    public static Float computeTotalDiscountedAmount(List<Invoice> invoices){

        return invoices.stream()
                .map(invoice -> invoice.getTotalAmount() * invoice.getDiscountRate())
                .reduce(Float::sum)
                .orElse(0f);
    }

    public static Integer computeTotalNumberOfItems(List<Invoice> invoices){

        return invoices.stream()
                .map(invoice -> invoice.getItems().size())
                .reduce(Integer::sum)
                .orElse(0);
    }
}
