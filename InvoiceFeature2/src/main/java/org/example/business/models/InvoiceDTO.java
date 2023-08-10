package org.example.business.models;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.collections.Item;
import org.example.persistence.utils.InvoiceStatus;
import org.example.persistence.utils.OrderStatus;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {

    private UUID identifier;

    private CompanyDTO buyer;

    private CompanyDTO seller;

    private Set<Item> items;

    private InvoiceStatus invoiceStatus;
}
