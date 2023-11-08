package org.example.business.utils;

import org.example.persistence.utils.InvoiceStatus;
import org.example.persistence.utils.data.OrderStatus;

import java.util.Map;

public class InvoiceTaxationRate {

    public static final Map<InvoiceStatus, Float> invoiceTaxRate = Map.of(
            InvoiceStatus.CREATED,0.0f,
            InvoiceStatus.SENT,5.0f,
            InvoiceStatus.PAID,20.0f
    );
}
