package org.example.persistence.collections;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Document
public class Company {
    @Id
    UUID companyIdentifier;

    String name;
}
