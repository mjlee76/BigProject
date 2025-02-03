package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.AnswerDTO;
import com.bigProject.tellMe.entity.Answer;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.enumClass.Status;
import com.bigProject.tellMe.repository.AnswerRepository;
import com.bigProject.tellMe.repository.QuestionRepository;
import com.bigProject.tellMe.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveAnswer(AnswerDTO answerDTO,
                                  Authentication auth) {
        // 현재 로그인한 관리자 정보 조회
        User user = userRepository.findByUserId(auth.getName());

        // 관련 질문 조회(?)
        Question question = questionRepository.findById(answerDTO.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        // Answer 엔티티 생성 및 저장
        Answer answer = Answer.builder()
                .content(answerDTO.getContent())
                .user(user)
                .createDate(LocalDateTime.now())
                .file(answerDTO.getFile1())
                .build();

        Answer savedAnswer = answerRepository.save(answer);

        //Question에 Answer 연결 및 상태 변경
        question.markAsAnswered(savedAnswer);

        questionRepository.save(question);
    }
}
