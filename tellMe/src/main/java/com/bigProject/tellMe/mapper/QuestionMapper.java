package com.bigProject.tellMe.mapper;

import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(target = "userId", source = "question.user.id")
    QuestionDTO quToQuDTO(Question question);

    @Mapping(target = "user", source = "questionDTO.userId") // user 필드 매핑
    Question quDTOToQu(QuestionDTO questionDTO);

    default User mapUser(Long userId) {
        return User.builder()
                .id(userId)  // 빌더를 통해 User 객체를 생성
                .build();
    }
}
