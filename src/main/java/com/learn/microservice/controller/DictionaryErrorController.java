package com.learn.microservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DictionaryErrorController implements ErrorController {

    @GetMapping("/error")
    public ResponseEntity<String> errorMessage(){
        log.error("Technical error occurred");
        return ResponseEntity.ok("Sorry! It appears that some problem occurred. Please try after sometime");
    }
}
