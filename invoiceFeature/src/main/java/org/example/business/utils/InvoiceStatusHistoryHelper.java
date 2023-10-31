package org.example.business.utils;

import org.example.persistence.utils.InvoiceStatus;
import org.example.persistence.utils.data.InvoiceStatusHistoryObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;

public class InvoiceStatusHistoryHelper {
    public static InvoiceStatusHistoryObject getMostRecentHistoryObject(Set<InvoiceStatusHistoryObject> invoiceStatusHistoryObjects) {

        return invoiceStatusHistoryObjects.stream()
                .max(Comparator.comparing(InvoiceStatusHistoryObject::getDate))
                .orElse(null);
    }

    public static Set<InvoiceStatusHistoryObject> initStatusHistory(InvoiceStatus invoiceStatus){
        return Set.of(
                InvoiceStatusHistoryObject
                        .builder()
                        .date(LocalDateTime.now())
                        .invoiceStatus(invoiceStatus)
                        .build()
        );
    }
}
