package com.learn.microservice.dto.dictionaryapi;

import lombok.Data;

@Data
public class Phonetic {
    public String text;
    public String audio;
    public String sourceUrl;
}
