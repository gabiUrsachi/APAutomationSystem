package org.example.persistence.repository;

import org.example.persistence.collections.Company;

import java.util.List;

public interface CompanyCustomRepository {
    List<Company> findAllByHasBucketOrNull(Boolean hasBucket);
}
