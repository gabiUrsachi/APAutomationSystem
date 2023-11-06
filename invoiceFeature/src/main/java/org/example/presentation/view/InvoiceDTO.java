package org.example.presentation.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.collections.Item;
import org.example.persistence.utils.InvoiceStatus;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {

    private UUID identifier;

    @NotNull
    private CompanyDTO buyer;

    @NotNull
    private CompanyDTO seller;

    @NotNull
    private Set<Item> items;

    private InvoiceStatus invoiceStatus;

    private Float totalAmount;

    private Float discountRate;

    private Float finalAmount;

    @NotNull
    private Integer version;

    private String uri;
}
