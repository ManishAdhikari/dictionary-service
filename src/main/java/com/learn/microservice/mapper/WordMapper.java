package com.learn.microservice.mapper;

import com.learn.microservice.dto.WordDto;
import com.learn.microservice.model.WordEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WordMapper {

    WordEntity toWordEntity(WordDto wordDto);
}
