package org.example.persistence.utils.data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.persistence.utils.CompanyRole;
import org.example.persistence.utils.InvoiceStatus;

import java.util.UUID;

@Data
@Getter
@Setter
@Builder
public class InvoiceFilter {
    InvoiceStatus requiredStatus;
    UUID companyUUID;
    CompanyRole companyType;
}
