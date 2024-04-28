package com.learn.microservice.controller;

import com.learn.microservice.dto.WordDto;
import com.learn.microservice.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/dictionary")
@RequiredArgsConstructor
@Slf4j
public class DictionaryController {

    private final DictionaryService dictionaryService;
    @GetMapping("/meaning/{word}")
    public ResponseEntity<WordDto> getMeaning(@PathVariable("word") String word){
        log.info("Received request to get the meaning of [{}]", word);
        return ResponseEntity.ok(dictionaryService.getMeaning(word));
    }

    @GetMapping("/synonym/{word}")
    public ResponseEntity<WordDto> getSynonym(@PathVariable("word") String word){
        log.info("Received request to get the synonyms of [{}]", word);
        return ResponseEntity.ok(dictionaryService.getSynonym(word));
    }
}
