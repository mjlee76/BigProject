package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.NoticeDTO;
import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.entity.Notice;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.mapper.QuestionMapper;
import com.bigProject.tellMe.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionMapper questionMapper;
    private final QuestionRepository questionRepository;

    public Question save(QuestionDTO questionDTO) {
        Question question = questionMapper.quDTOToQu(questionDTO);
        return questionRepository.save(question);
    }

    // 반복문을 통해 Entity를 DTO로 변환하고
    // 변환된 객체를 List에 담는다
    public List<QuestionDTO> findAll() {
        List<Question> questionList = questionRepository.findAll();
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question: questionList) {
            questionDTOList.add(QuestionDTO.toQuestionDTO(question));
        }
        return questionDTOList;
    }

    public QuestionDTO getQuestion(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);

        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();

            question.incrementViews(); // 조회수 증가 메서드 호출
            questionRepository.save(question); // 변경된 엔티티 저장

            // Question를 QuestionDTO로 변환
            return QuestionDTO.toQuestionDTO(question); // 문의 조회를 위한 DTO반환
        } else {
            throw new IllegalArgumentException("Question not found with ID: " + id);
        }
    }
}
