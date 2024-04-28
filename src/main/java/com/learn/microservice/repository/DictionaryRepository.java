package com.learn.microservice.repository;

import com.learn.microservice.model.WordEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryRepository extends MongoRepository<WordEntity, String> {

}
