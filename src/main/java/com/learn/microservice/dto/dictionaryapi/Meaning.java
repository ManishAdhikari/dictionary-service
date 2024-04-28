package com.learn.microservice.dto.dictionaryapi;

import lombok.Data;

import java.util.List;

@Data
public class Meaning {
    public String partOfSpeech;
    public List<Definition> definitions;
    public List<String> synonyms;
    public List<String> antonyms;
}
