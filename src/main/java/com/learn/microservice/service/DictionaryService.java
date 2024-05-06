package com.learn.microservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.microservice.config.AppConfig;
import com.learn.microservice.dto.WordDto;
import com.learn.microservice.dto.dictionaryapi.DictionaryApiResponse;
import com.learn.microservice.mapper.WordMapper;
import com.learn.microservice.repository.DictionaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DictionaryService {

    private final AppConfig appConfig;
    private final DictionaryRepository dictionaryRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final WordMapper wordMapper;

    public WordDto getMeaning(String word) {
        var wordDtoBuilder = WordDto.builder().word(word);
        var wordDto = Optional.ofNullable(getDictionaryApiResponse(word))
                .map(r -> wordDtoBuilder
                        .meanings(r[0].getMeanings().stream()
                                .flatMap(m -> m.getDefinitions().stream()
                                        .map(d -> new AbstractMap.SimpleEntry<>(d.getDefinition(), m.getPartOfSpeech())))
                                .map(se -> String.format("%s | (%s)", se.getKey(), se.getValue()))
                                .toList())
                        .build())
                .orElse(wordDtoBuilder.meanings(List.of()).build());
        var entity = dictionaryRepository.save(wordMapper.toWordEntity(wordDto));
        log.info("Response saved in database with id: {}", entity.getId());
        return wordDto;
    }

    public WordDto getSynonym(String word){
        var wordDtoBuilder = WordDto.builder().word(word);
        var wordDto = Optional.ofNullable(getDictionaryApiResponse(word))
                .map(r -> wordDtoBuilder
                        .synonyms(r[0].getMeanings().stream()
                                .flatMap(m -> m.getSynonyms().stream())
                                .toList())
                        .build())
                .orElse(wordDtoBuilder.synonyms(List.of()).build());
        var entity = dictionaryRepository.save(wordMapper.toWordEntity(wordDto));
        log.info("Response saved in database with id: {}", entity.getId());
        return wordDto;
    }

    private DictionaryApiResponse[] getDictionaryApiResponse(String word) {
        log.info("Calling dictionary api to fetch the response for the word [{}]", word);
        var response = webClient.get()
                .uri(String.format("%s%s", appConfig.getDictionaryApiUrl(), word))
                .retrieve()
                .onStatus(e -> e.is4xxClientError() ||e.is5xxServerError(), res -> {
                    log.error("Unable to call dictionary api", res.bodyToMono(String.class));
                    return Mono.error(new RuntimeException("Unable to call dictionary api"));
                })
                .bodyToMono(String.class)
                .block();
        DictionaryApiResponse[] dictionaryApiResponse = null;
        try {
            dictionaryApiResponse = objectMapper.readValue(response, DictionaryApiResponse[].class);
            log.info("Received response from dictionary api. Response array size: {}", dictionaryApiResponse.length);
        } catch (JsonProcessingException e) {
            log.error("Error occurred while parsing the dictionary api response", e);
        }
        return dictionaryApiResponse;
    }
}
