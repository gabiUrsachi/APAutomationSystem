package org.example.business.models;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.collections.Item;
import org.example.persistence.utils.InvoiceStatus;

import java.util.Set;
import java.util.UUID;

//DDO = Data Display Object
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDDO {

    private UUID identifier;

    private String buyerName;

    private String sellerName;

    private InvoiceStatus invoiceStatus;
}
