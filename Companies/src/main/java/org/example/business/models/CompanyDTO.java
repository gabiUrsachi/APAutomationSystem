package org.example.business.models;

import lombok.*;

import java.util.UUID;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {
    UUID companyIdentifier;

    String name;
}
