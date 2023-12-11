package org.example.persistence.utils.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyOrderStatusChangeMap {

    private UUID _id; //companyUUID
    private List<OrderStatusOccurrencePair> statusCounts;
}
