package com.bigProject.tellMe.service;

import com.bigProject.tellMe.client.api.FastApiClient;
import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.enumClass.Reveal;
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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

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

        reportBody.put("category_title", "");
        reportBody.put("category_content", "");
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

    /**
     * 공지사항 목록을 페이지 단위로 조회하는 메서드
     *
     * @param pageable 페이지 정보(PageRequest) 객체 (페이지 번호, 정렬 방식 포함)
     * @return Page<NoticeDTO> 페이징된 공지사항 목록 (id, 제목, 작성일, 조회수 포함)
     */
    public Page<QuestionDTO> paging(Pageable pageable, String role) {

        // ✅ 현재 요청된 페이지 번호에서 1을 뺀 값 (Spring Data JPA는 페이지 인덱스를 0부터 시작함)
        int page = pageable.getPageNumber() - 1;

        // ✅ 한 페이지에서 보여줄 공지사항 개수
        int pageLimit = 5;

        // ✅ 공지사항을 ID 기준으로 내림차순 정렬하여 페이지네이션 적용된 데이터 조회
        // PageRequest.of(페이지 번호, 페이지당 개수, 정렬 기준)
        Page<Question> questions;
        if ("ROLE_MANAGER".equals(role)) {
            // ✅ 매니저는 모든 게시글을 조회
            questions = questionRepository.findAll(
                    PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id"))
            );
        } else {
            // ✅ 일반 사용자는 공개된 게시글만 조회
            questions = questionRepository.findByReveal(
                    Reveal.공개,
                    PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id"))
            );
        }

        // ✅ 조회된 데이터를 questionDTO 형태로 변환하여 반환
        // Question 엔티티의 id, title, createDate, views, userName, status만 매핑하여 DTO로 변환
        Page<QuestionDTO> questionDTOS = questions.map(question -> new QuestionDTO(
                question.getId(),
                question.getTitle(),
                question.getCreateDate(),
                question.getViews(),
                question.getUser().getUserName(),
                question.getStatus()
        ));
        return questionDTOS; // 변환된 공지사항 DTO 목록 반환
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
}
