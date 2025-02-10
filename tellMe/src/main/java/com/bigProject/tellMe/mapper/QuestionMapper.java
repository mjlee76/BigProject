package com.bigProject.tellMe.mapper;

import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.entity.Filtered;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(target = "userId", source = "question.user.id")
    @Mapping(target = "filteredId", source = "question.filtered.id")
    QuestionDTO quToQuDTO(Question question);

    @Mapping(target = "user", source = "questionDTO.userId") // user 필드 매핑
    @Mapping(target = "filtered", source = "questionDTO.filteredId")
    Question quDTOToQu(QuestionDTO questionDTO);

    default User mapUser(Long userId) {
        return User.builder()
                .id(userId)  // 빌더를 통해 User 객체를 생성
                .build();
    }

    default Filtered mapFiltered(Long filteredId) {
        if (filteredId == null) {
            return null; // 🔹 null이면 객체를 생성하지 않음
        }
        return Filtered.builder()
                .id(filteredId)  // 빌더를 통해 User 객체를 생성
                .build();
    }
}
