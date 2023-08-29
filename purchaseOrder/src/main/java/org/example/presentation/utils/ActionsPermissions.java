package org.example.presentation.utils;

import org.example.utils.data.Roles;

import java.util.Map;
import java.util.Set;

public class ActionsPermissions {
    public static final Map<ResourceActionType, Set<Roles>> VALID_ROLES =
            Map.ofEntries(
                    Map.entry(ResourceActionType.GET, Set.of(Roles.BUYER_CUSTOMER, Roles.SUPPLIER_ACCOUNTING, Roles.SUPPLIER_MANAGEMENT)),
                    Map.entry(ResourceActionType.CREATE, Set.of(Roles.BUYER_CUSTOMER)),
                    Map.entry(ResourceActionType.UPDATE, Set.of(Roles.BUYER_CUSTOMER, Roles.SUPPLIER_MANAGEMENT)),
                    Map.entry(ResourceActionType.DELETE, Set.of(Roles.BUYER_CUSTOMER))
            );
}
