package org.example.presentation.view;

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
