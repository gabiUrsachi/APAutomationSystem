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
    public List<Company> findAllByHasBucketOrEmpty(Boolean hasBucket) {
        Criteria searchCriteria1 = Criteria.where("hasBucket").is(hasBucket);
        Criteria searchCriteria2 = Criteria.where("hasBucket").exists(false);

        Criteria finalCriteria = new Criteria().orOperator(searchCriteria1, searchCriteria2);
        Query finalSearchQuery = new Query(finalCriteria);

        return mongoTemplate.find(finalSearchQuery, Company.class);
    }
}
