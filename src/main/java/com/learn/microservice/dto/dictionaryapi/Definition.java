package com.learn.microservice.dto.dictionaryapi;

import lombok.Data;

import java.util.List;

@Data
public class Definition {
    public String definition;
    public List<Object> synonyms;
    public List<Object> antonyms;
    public String example;
}
