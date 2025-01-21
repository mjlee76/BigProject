package com.bigProject.tellMe.mapper;

import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.entity.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    Question quDTOToQu(QuestionDTO questionDTO);
}
