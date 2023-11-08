package org.example.persistence.utils.data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.persistence.utils.InvoiceStatus;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
public class InvoiceStatusHistoryObject {
    LocalDateTime date;
    InvoiceStatus status;
}
