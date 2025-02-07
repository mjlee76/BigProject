package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.NoticeDTO;
import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.Answer;
import com.bigProject.tellMe.entity.Notice;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.enumClass.Category;
import com.bigProject.tellMe.enumClass.Reveal;
import com.bigProject.tellMe.enumClass.Status;
import com.bigProject.tellMe.mapper.QuestionMapper;
import com.bigProject.tellMe.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;


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

    public Page<QuestionDTO> searchAndFilter(String query, Status status, String category, String role, Pageable pageable) {
        // 동적 쿼리를 위한 조건 생성
        Specification<Question> spec = Specification.where(null);

        // 역할에 따른 공개 여부 필터링
        if (!role.equals("ROLE_MANAGER") && !role.equals("ROLE_ADMIN")) {
            spec = spec.and((root, cq, criteriaBuilder) -> criteriaBuilder.equal(root.get("reveal"), Reveal.공개));
        }

        // 상태 필터링
        if (status != null) {
            spec = spec.and((root, cq, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status));
        }

        // 검색어 필터링
        if (query != null && !query.isEmpty()) {
            if (category == null || "all".equals(category)) {
                // 전체 검색 (제목, 작성자, 내용)
                spec = spec.and((root, cq, criteriaBuilder) -> criteriaBuilder.or(
                        criteriaBuilder.like(root.get("title"), "%" + query + "%"),
                        criteriaBuilder.like(root.get("user").get("userName"), "%" + query + "%"),
                        criteriaBuilder.like(root.get("content"), "%" + query + "%")
                ));
            } else {
                // 카테고리별 검색
                switch (category) {
                    case "title":
                        spec = spec.and((root, cq, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + query + "%"));
                        break;
                    case "author":
                        spec = spec.and((root, cq, criteriaBuilder) -> criteriaBuilder.like(root.get("user").get("userName"), "%" + query + "%"));
                        break;
                    case "content":
                        spec = spec.and((root, cq, criteriaBuilder) -> criteriaBuilder.like(root.get("content"), "%" + query + "%"));
                        break;
                }
            }
        }

        // 페이징 처리
        Page<Question> questions = questionRepository.findAll(spec, pageable);

        // Question 엔티티를 QuestionDTO로 변환
        return questions.map(question -> new QuestionDTO(
                question.getId(),
                question.getTitle(),
                question.getCreateDate(),
                question.getViews(),
                question.getUser().getUserName(),
                question.getStatus()
        ));
    }

    // MyPage 내 민원 조회 - 카테고리별 검색 로직 추가
    public Page<QuestionDTO> searchUserQuestions(User user, String query, Status status, String category, Pageable pageable) {
        Specification<Question> spec = Specification.where((root, cq, cb) ->
                cb.equal(root.get("user"), user)
        );

        // 상태 필터링
        if (status != null) {
            spec = spec.and((root, cq, cb) -> cb.equal(root.get("status"), status));
        }

        // 검색어 및 카테고리 필터링
        if (query != null && !query.isEmpty()) {
            switch (category) {
                case "title":
                    spec = spec.and((root, cq, cb) ->
                            cb.like(root.get("title"), "%" + query + "%"));
                    break;
                case "content":
                    spec = spec.and((root, cq, cb) ->
                            cb.like(root.get("content"), "%" + query + "%"));
                    break;
                default: // 전체 검색
                    spec = spec.and((root, cq, cb) -> cb.or(
                            cb.like(root.get("title"), "%" + query + "%"),
                            cb.like(root.get("content"), "%" + query + "%")));
            }
        }

        Page<Question> questions = questionRepository.findAll(spec, pageable);
        return questions.map(QuestionDTO::toQuestionDTO);
    }

    // 접수중을 처리중으로 변경
    public void updateStatusToProcessing(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);

        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            question.changeStatusToProcessing();  // 상태 변경 메서드 호출
            questionRepository.save(question);   // 변경된 엔티티 저장
        } else {
            throw new IllegalArgumentException("해당 ID의 질문을 찾을 수 없습니다.");
        }
    }


    public List<QuestionDTO> findQuestionsByUser(User user) {
        List<Question> questions = questionRepository.findByUser(user);
        return questions.stream()
                .map(QuestionDTO::toQuestionDTO)
                .collect(Collectors.toList());
    }

    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    public void updateQuestion(QuestionDTO dto) {
        Question question = questionRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID"));

        dto.updateEntity(question);
        questionRepository.save(question);
    }

    // 민원 상태별 카운트
    public long countByStatus(Status status) {
        return questionRepository.countByStatus(status);
    }

    // QuestionService에 오늘의 민원 수와 악성 민원 수를 조회하는 메서드 추가
    public long countTodayQuestions() {
        // 오늘 날짜에 해당하는 민원 수를 조회하는 쿼리 작성
        LocalDate today = LocalDate.now();
        return questionRepository.countByCreateDateBetween(
                today.atStartOfDay(), today.atTime(23, 59, 59)
        );
    }

    public long countTodayCategoryNotNormal() {
        LocalDate today = LocalDate.now();
        return questionRepository.countByCategoryNotAndCreateDateBetween(
                Category.정상, today.atStartOfDay(), today.atTime(23, 59, 59)
        );
    }

    public Map<String, List<Long>> countQuestionsAndMaliciousByHour(LocalDate today) {
        List<Long> normalCounts = new ArrayList<>();
        List<Long> maliciousCounts = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime startOfHour = today.atTime(hour, 0);
            LocalDateTime endOfHour = today.atTime(hour, 59, 59);


            // 일반 민원 수 조회 (category가 정상인 경우)
            long normalCount = questionRepository.countByCategoryAndCreateDateBetween(Category.정상, startOfHour, endOfHour);
            normalCounts.add(normalCount);

            // 악성 민원 수 조회 (정상이 아닌 카테고리)
            long maliciousCount = questionRepository.countByCategoryNotAndCreateDateBetween(Category.정상, startOfHour, endOfHour);
            maliciousCounts.add(maliciousCount);
        }

        // 결과를 Map으로 반환하여 일반 민원과 악성 민원의 시간대별 데이터를 한 번에 반환
        Map<String, List<Long>> result = new HashMap<>();
        result.put("normal", normalCounts);
        result.put("malicious", maliciousCounts);

        return result;
    }




}
