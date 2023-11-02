package org.example.presentation.utils;

import org.example.utils.data.Roles;

import java.util.Map;
import java.util.Set;

public class InvoiceActionsPermissions {
    public static final Map<InvoiceResourceActionType, Set<Roles>> VALID_ROLES =
            Map.ofEntries(
                    Map.entry(InvoiceResourceActionType.GET, Set.of(Roles.BUYER_FINANCE,Roles.SUPPLIER_ACCOUNTING,Roles.SUPPLIER_MANAGEMENT)),
                    Map.entry(InvoiceResourceActionType.CREATE, Set.of(Roles.SUPPLIER_ACCOUNTING)),
                    Map.entry(InvoiceResourceActionType.CREATE_FROM_OR, Set.of(Roles.SUPPLIER_ACCOUNTING)),
                    Map.entry(InvoiceResourceActionType.UPDATE, Set.of(Roles.SUPPLIER_ACCOUNTING, Roles.BUYER_FINANCE)),
                    Map.entry(InvoiceResourceActionType.DELETE, Set.of(Roles.SUPPLIER_ACCOUNTING))
            );
}
