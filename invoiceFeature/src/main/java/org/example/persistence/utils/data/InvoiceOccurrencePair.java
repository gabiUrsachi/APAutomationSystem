package org.example.persistence.utils.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.persistence.utils.InvoiceStatus;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceOccurrencePair {

    private InvoiceStatus status;
    private Integer count;
}
