package org.example.presentation.controllers.utils;

import org.example.utils.data.Roles;

import java.util.Map;
import java.util.Set;

public class InvoiceActionsPermissions {
    public static final Map<ResourceActionType, Set<Roles>> VALID_ROLES =
            Map.ofEntries(
                    Map.entry(ResourceActionType.GET, Set.of(Roles.BUYER_FINANCE,Roles.SUPPLIER_ACCOUNTING,Roles.SUPPLIER_MANAGEMENT)),
                    Map.entry(ResourceActionType.CREATE, Set.of(Roles.SUPPLIER_ACCOUNTING)),
                    Map.entry(ResourceActionType.CREATE_FROM_OR, Set.of(Roles.SUPPLIER_ACCOUNTING)),
                    Map.entry(ResourceActionType.UPDATE, Set.of(Roles.SUPPLIER_ACCOUNTING, Roles.BUYER_FINANCE)),
                    Map.entry(ResourceActionType.DELETE, Set.of(Roles.SUPPLIER_ACCOUNTING))
            );
}
