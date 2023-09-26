package org.example.persistence.collections;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * The structure of a document representing companies
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Company {
    @Id
    UUID companyIdentifier;

    String name;
}
