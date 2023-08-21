package org.example.persistence.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.example.persistence.utils.OrderStatus;

import java.util.UUID;

@Data
@Getter
@AllArgsConstructor
public class PurchaseOrderFilter {
    OrderStatus orderStatus;
    UUID companyUUID;
    String companyType;
}
