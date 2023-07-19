package org.example.business.models;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CompanyDTO {
    UUID companyIdentifier;

    String name;
}
