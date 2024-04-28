package com.learn.microservice.dto.dictionaryapi;

import lombok.Data;

import java.util.List;

@Data
public class DictionaryApiResponse {
    public String word;
    public String phonetic;
    public List<Phonetic> phonetics;
    public List<Meaning> meanings;
    public License license;
    public List<String> sourceUrls;
}
