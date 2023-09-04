package org.example.business.utils;


import org.example.persistence.utils.InvoiceStatus;

import java.util.Map;

/**
 * This class is used for maintaining valid invoice status transitions
 */
public class InvoiceStatusPrecedence {
    public static final Map<InvoiceStatus, InvoiceStatus> PREDECESSORS =
            Map.ofEntries(Map.entry(InvoiceStatus.CREATED, InvoiceStatus.CREATED),
                    Map.entry(InvoiceStatus.SENT, InvoiceStatus.CREATED),
                    Map.entry(InvoiceStatus.PAID, InvoiceStatus.SENT));
}
