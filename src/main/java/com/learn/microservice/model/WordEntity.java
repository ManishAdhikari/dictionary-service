package com.learn.microservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "dictionary")
public class WordEntity {
    @Id
    private String id;
    private String word;
    private List<String> meanings;
    private List<String> synonyms;
    private List<String> antonyms;
}
