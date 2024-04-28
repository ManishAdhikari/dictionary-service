package com.learn.microservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WordDto {
    private String word;
    private List<String> meanings;
    private List<String> synonyms;
    private List<String> antonyms;
}
