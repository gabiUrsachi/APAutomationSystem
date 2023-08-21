package org.example.persistence.utils;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
@Builder
public class PurchaseOrderFilter {
    OrderStatus requiredStatus;
    UUID companyUUID;
    String companyType;
}
