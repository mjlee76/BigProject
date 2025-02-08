package com.bigProject.tellMe.service;

import com.bigProject.tellMe.client.api.FastApiClient;
import com.bigProject.tellMe.client.dto.QuestionApiDTO;
import com.bigProject.tellMe.dto.NoticeDTO;
import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.Answer;
import com.bigProject.tellMe.entity.Notice;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.enumClass.Reveal;
import com.bigProject.tellMe.enumClass.Status;
import com.bigProject.tellMe.mapper.QuestionMapper;
import com.bigProject.tellMe.repository.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final FastApiClient fastApiClient;

    private final QuestionMapper questionMapper;
    private final QuestionRepository questionRepository;

    public Question save(QuestionDTO questionDTO) {
        Question question = questionMapper.quDTOToQu(questionDTO);
        return questionRepository.save(question);
    }

    public Map<String, Object> spamCheck(Map<String, String> request) {
        Map<String, Object> postBody = new HashMap<>();
        String title = request.get("title");
        String content = request.get("content");
        postBody.put("title", title);
        postBody.put("content", content);

        Map<String, Object> questionBody = new HashMap<>();
        Long id = Long.parseLong(request.get("userId"));
        List<QuestionApiDTO> questionDTO = questionRepository.findByUserId(id)
                .stream()
                .map(questionMapper::quToQuDTO)
                .map(dto -> new QuestionApiDTO(dto.getId(), dto.getTitle(), dto.getContent()))
                .collect(Collectors.toList());
        questionBody.put("question", questionDTO);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("post_data", postBody);
        requestBody.put("question_data", questionBody);

        System.out.println("========================REQUESTBODY"+requestBody);

        Map<String, Object> responseBody = fastApiClient.getSpam(requestBody);
        System.out.println("========================responseBody"+responseBody);
        Map<String, Object> response = new HashMap<>();

        if (responseBody != null && Boolean.TRUE.equals(responseBody.get("valid"))) {
            response.put("valid", true);
            if(responseBody.get("spam")=="도배아님") {
                response.put("spam", "도배아님");
                response.put("message", "성공적으로 검증되었습니다.");
            }else {
                response.put("spam", "도배");
                response.put("message", "도배 감지!!! 게시글이 등록되지 않습니다.");
            }
        } else {
            response.put("valid", false);
            if(responseBody.get("message") != null) {
                response.put("message", responseBody.get("message"));
            }else {
                response.put("message", "검증 실패: 유효하지 않은 요청입니다.");
            }
        }

        return response;
    }

    public Map<String, Object> checkApi(Map<String, String> request, UserDTO userDTO) {
        String title = request.get("title");
        String content = request.get("content");

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> postBody = new HashMap<>();
        Map<String, Object> reportBody = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("user_id", userDTO.getUserId());
        userNode.put("user_name", userDTO.getUserName());
        userNode.put("gender", userDTO.getGender());
        userNode.put("role", userDTO.getRole().toString());
        userNode.put("birth_date", userDTO.getBirthDate());
        userNode.put("phone", userDTO.getPhone());
        userNode.put("address", userDTO.getAddress());
        userNode.put("email", userDTO.getEmail());
        userNode.put("create_date", userDTO.getCreateDate().toString());
        userNode.put("count", userDTO.getCount());

        //ObjectNode를 `Map<String, Object>`로 변환
        Map<String, Object> userMap = objectMapper.convertValue(userNode, Map.class);

        System.out.println("================" + title);
        System.out.println("================" + content);
        System.out.println("================" + userMap.toString());

        postBody.put("title", title);
        postBody.put("content", content);
        postBody.put("user", userMap);

        Map<String, Object> category = new HashMap<>();
        category.put("title", "");
        category.put("content", "");
        reportBody.put("category", category);
        reportBody.put("post_origin_data", "");
        reportBody.put("report_path", "");
        reportBody.put("create_date", "");

        requestBody.put("post_data", postBody);
        requestBody.put("report_req", reportBody);
        System.out.println("================" + requestBody);
        Map<String, Object> responseBody = fastApiClient.getFilter(requestBody);
        System.out.println("================" + responseBody);
        Map<String, Object> response = new HashMap<>();
        if (responseBody != null && Boolean.TRUE.equals(responseBody.get("valid"))) {
            response.put("valid", true);
            response.put("message", "성공적으로 검증되었습니다.");
        } else {
            response.put("valid", false);
            response.put("message", "검증 실패: 유효하지 않은 요청입니다.");
        }

        return response;
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
}
