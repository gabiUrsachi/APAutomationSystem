package org.example.persistence.repository;

import org.example.persistence.collections.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompanyCustomRepositoryImpl implements CompanyCustomRepository{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Company> findAllByHasBucketOrNull(Boolean hasBucket) {
        Criteria searchCriteria = Criteria.where("hasBucket").is(hasBucket).orOperator(Criteria.where("hasBucket").isNull());
        Query searchQuery = new Query(searchCriteria);

        return mongoTemplate.find(searchQuery, Company.class);
    }
}
