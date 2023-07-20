package org.example.business.models;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.collections.Item;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {
    @NonNull
    private CompanyDTO buyer;
    @NonNull
    private CompanyDTO seller;

    private Set<Item> items;

}
