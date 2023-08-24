package org.example.presentation.controllers.utils;

import org.example.utils.data.Roles;

import java.util.Map;
import java.util.Set;

public class InvoiceActionsPermissions {
    public static final Map<ResourceActionType, Set<Roles>> VALID_ROLES =
            Map.ofEntries(
                    Map.entry(ResourceActionType.GET, Set.of(Roles.BUYER_I,Roles.SUPPLIER_I,Roles.SUPPLIER_II)),
                    Map.entry(ResourceActionType.CREATE, Set.of(Roles.SUPPLIER_I)),
                    Map.entry(ResourceActionType.CREATE_FROM_OR, Set.of(Roles.SUPPLIER_II)),
                    Map.entry(ResourceActionType.UPDATE, Set.of(Roles.SUPPLIER_I, Roles.BUYER_II)),
                    Map.entry(ResourceActionType.DELETE, Set.of(Roles.SUPPLIER_I))
            );
}
