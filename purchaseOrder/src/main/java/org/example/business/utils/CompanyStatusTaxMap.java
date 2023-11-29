package org.example.business.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStatusTaxMap {
    private UUID companyUUID;
    private List<OrderStatusTaxPair> orderStatusTaxPairs;
}
