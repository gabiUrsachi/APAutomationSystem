package org.example.persistence.utils;

import org.example.persistence.utils.data.InvoiceStatusHistoryObject;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class InvoiceStatusHistoryHelper {
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
                        .invoiceStatus(invoiceStatus)
                        .build()
        );
    }
}
