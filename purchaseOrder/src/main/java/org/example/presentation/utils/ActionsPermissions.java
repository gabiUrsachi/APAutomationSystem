package org.example.presentation.utils;

import org.example.utils.data.Roles;

import java.util.Map;
import java.util.Set;

public class ActionsPermissions {
    public static final Map<PurchaseOrderResourceActionType, Set<Roles>> VALID_ROLES =
            Map.ofEntries(
                    Map.entry(PurchaseOrderResourceActionType.GET, Set.of(Roles.BUYER_CUSTOMER, Roles.SUPPLIER_ACCOUNTING, Roles.SUPPLIER_MANAGEMENT)),
                    Map.entry(PurchaseOrderResourceActionType.CREATE, Set.of(Roles.BUYER_CUSTOMER)),
                    Map.entry(PurchaseOrderResourceActionType.UPDATE, Set.of(Roles.BUYER_CUSTOMER, Roles.SUPPLIER_MANAGEMENT)),
                    Map.entry(PurchaseOrderResourceActionType.DELETE, Set.of(Roles.BUYER_CUSTOMER))
            );
}
