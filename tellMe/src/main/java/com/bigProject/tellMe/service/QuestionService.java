package com.bigProject.tellMe.service;

import com.bigProject.tellMe.client.api.FastApiClient;
import com.bigProject.tellMe.client.dto.QuestionApiDTO;
import com.bigProject.tellMe.controller.complaint.ComplaintRestController;
import com.bigProject.tellMe.dto.FilteredDTO;
import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.dto.ReportDTO;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.Filtered;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.enumClass.Reveal;
import com.bigProject.tellMe.enumClass.Status;
import com.bigProject.tellMe.mapper.FilteredMapper;
import com.bigProject.tellMe.mapper.QuestionMapper;
import com.bigProject.tellMe.mapper.ReportMapper;
import com.bigProject.tellMe.repository.FilteredRepository;
import com.bigProject.tellMe.repository.QuestionRepository;
import com.bigProject.tellMe.repository.ReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class QuestionService {
    private final FastApiClient fastApiClient;

    @Lazy
    @Autowired
    private ComplaintRestController complaintRestController;

    private final QuestionMapper questionMapper;
    private final FilteredMapper filteredMapper;
    private final ReportMapper reportMapper;
    private final QuestionRepository questionRepository;
    private final FilteredRepository filteredRepository;
    private final ReportRepository reportRepository;

    private final UserService userService;
    private final NotificationService notificationService;

    public Question save(QuestionDTO questionDTO) {
        Question question = questionMapper.quDTOToQu(questionDTO);
        return questionRepository.save(question);
    }

    public String uploadFileApi(String uploadDir) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("file_path", uploadDir);
        System.out.println("===============uploadFileApi:requestBody"+requestBody);
        Map<String, Object> responseBody = fastApiClient.getUploadFile(requestBody);
        System.out.println("===============uploadFileApi:responseBody"+responseBody);
        String response = "";

        if (responseBody != null && Boolean.TRUE.equals(responseBody.get("valid"))) {
            if("악성".equals(responseBody.get("message"))) {
                response = "악성";
            }else {
                response = "정상";
            }
        }else {
            if(responseBody.get("message") != null) {
                response = (String) responseBody.get("message");
            }else {
                response = "검증 실패: 유효하지 않은 요청입니다.";
            }
        }
        return response;
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

        Map<String, Object> responseBody = fastApiClient.getSpam(requestBody);
        Map<String, Object> response = new HashMap<>();

        if (responseBody != null && Boolean.TRUE.equals(responseBody.get("valid"))) {
            response.put("valid", true);
            if("도배아님".equals(responseBody.get("spam"))) {
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

    @Async
    @Transactional
    public CompletableFuture<Void> filterApi(QuestionDTO questionDTO) {
        try {
            System.out.println("================"+questionDTO.toString());
            String title = questionDTO.getTitle();
            String content = questionDTO.getContent();
            Long userId = questionDTO.getUserId();
            UserDTO userDTO = userService.findById(userId);

            Map<String, Object> requestBody = new HashMap<>();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ObjectNode userNode = objectMapper.createObjectNode();
            userNode.put("user_name", userDTO.getUserName());
            userNode.put("phone", userDTO.getPhone());
            //userNode.put("count", userDTO.getCount());
            //ObjectNode를 `Map<String, Object>`로 변환
            Map<String, Object> userMap = objectMapper.convertValue(userNode, Map.class);

            Map<String, Object> postBody = new HashMap<>();
            postBody.put("title", title);
            postBody.put("content", content);
            postBody.put("user", userMap);

            Map<String, Object> reportBody = new HashMap<>();
            reportBody.put("category", new ArrayList<>());
            reportBody.put("post_origin_data", new HashMap<>());
            reportBody.put("report_path", "");
            reportBody.put("create_date", "");

            requestBody.put("post_data", postBody);
            requestBody.put("report_req", reportBody);
            System.out.println("================requestBody : " + requestBody);
            Map<String, Object> responseBody = fastApiClient.getFilter(requestBody);
            System.out.println("================responseBody : " + responseBody);
            String categoryString = "";

            if(responseBody != null && Boolean.TRUE.equals(responseBody.get("valid"))) {
                if("악성".equals(responseBody.get("message"))) {
                    Map<String, Object> post_data = (Map<String, Object>) responseBody.get("post_data");
                    Map<String, Object> reportReq = (Map<String, Object>) responseBody.get("report_req");

                    String filteredTitle = (String) post_data.get("title");
                    String filteredContent = (String) post_data.get("content");
                    System.out.println("===========filteredTitle : "+filteredTitle);
                    FilteredDTO filteredDTO = new FilteredDTO();
                    filteredDTO.setTitle(filteredTitle);
                    filteredDTO.setContent(filteredContent);
                    Filtered filtered = filteredMapper.filToFilDTO(filteredDTO);
                    filtered = filteredRepository.save(filtered);

                    Long filter_id = filtered.getId();
                    System.out.println("============filter_id : "+filter_id);
                    questionDTO.setFilteredId(filter_id);

                    List<String> responseCategories = (List<String>) reportReq.get("category");
                    categoryString = String.join(",", responseCategories);
                    //가져오는 법 : Arrays.asList(question.getCategory().split(","))
                    questionDTO.setCategory(categoryString);
                    System.out.println("========SSE TEST============"+userDTO.getUserId());
                    System.out.println("========SSE TEST============"+categoryString);
                    post_data.put("question_id", questionDTO.getId());
                    responseBody.put("post_data", post_data);
                    CompletableFuture.runAsync(() -> reportApi(responseBody));
                }
                questionDTO.setStatus(Status.접수중);
                questionRepository.save(questionMapper.quDTOToQu(questionDTO));

                System.out.println("SSE TEST - userId: " + questionDTO.getUserId());
                // ✅ 만약 category가 '악성'이면, 작성자에게 알림 전송
                if ("악성".equals(responseBody.get("message"))) {
                    String notifiMessage = "게시글 "+ questionDTO.getId() + "번의 사유 : [" + categoryString + "] 악성민원이 감지되어 수정됐습니다.";
                    notificationService.createNotification(questionDTO.getUserId(),  notifiMessage);
                    complaintRestController.triggerEvent(questionDTO.getUserId(), "notification", notifiMessage);
                }

                // ✅ 모든 사용자에게 새로고침 이벤트 전송
                List<Long> allUserIds = userService.getAllUserIds(); // 전체 사용자 ID 조회
                for (Long userIds : allUserIds) {
                    complaintRestController.triggerEvent(userIds, "refresh", null);
                }
            }
            return CompletableFuture.completedFuture(null);
        }catch (Exception e) {
            System.err.println("🚨 비동기 API 실행 중 오류 발생: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    private void reportApi(Map<String, Object> requestBody) {
        System.out.println("==============reportApi : requestBody : "+requestBody);
        Map<String, Object> responseBody = fastApiClient.getReport(requestBody);
        System.out.println("==============reportApi : responseBody : "+responseBody);
        ReportDTO reportDTO = new ReportDTO();
        if(responseBody != null && Boolean.TRUE.equals(responseBody.get("valid"))) {
            System.out.println("==============reportApi : responseBody : "+responseBody);
            Map<String, Object> reportReq = (Map<String, Object>) responseBody.get("report_req");
            System.out.println("==============reportApi : reportReq : "+reportReq);
            List<String> responseCategories = (List<String>) reportReq.get("category");
            String categoryString = String.join(",", responseCategories);
            System.out.println("==============reportApi : categoryString : "+categoryString);

            reportDTO.setReport((String)reportReq.get("report_path"));
            String createDateStr = (String) reportReq.get("create_date"); // 🔹 FastAPI 응답에서 가져오기
            // 🔹 변환을 위한 포맷 정의
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd_HHmmss");
            // 🔹 `LocalDateTime`으로 변환
            LocalDateTime createDate = LocalDateTime.parse(createDateStr, formatter);
            reportDTO.setCreateDate(createDate);
            reportDTO.setCategory(categoryString);
            System.out.println("==============reportApi : reportDTO : "+reportDTO);
            reportRepository.save(reportMapper.repoDTOTORepo(reportDTO));
        }
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
        System.out.println("=============1");
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        System.out.println("=============2");
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();

            question.incrementViews(); // 조회수 증가 메서드 호출
            questionRepository.save(question); // 변경된 엔티티 저장
            System.out.println("=============3");
            // Question를 QuestionDTO로 변환
            QuestionDTO questionDTO = QuestionDTO.toQuestionDTO(question);
            if(question.getFiltered() != null) {
                questionDTO.setFilterTitle(question.getFiltered().getTitle());
                questionDTO.setFilterContent(question.getFiltered().getContent());
            }
            System.out.println("=============4");
            return questionDTO; // 문의 조회를 위한 DTO반환
        } else {
            throw new IllegalArgumentException("Question not found with ID: " + id);
        }
    }

    public Page<QuestionDTO> searchAndFilter(String query, Status status, String category, String role, Long userId, Pageable pageable) {
        Specification<Question> spec = Specification.where(null);

        if (!role.equals("ROLE_MANAGER") && !role.equals("ROLE_ADMIN")) {
            spec = spec.and((root, cq, cb) -> cb.equal(root.get("reveal"), Reveal.공개));
        }

        if (status != null) {
            if (status == Status.필터링중) {
                spec = spec.and((root, cq, cb) ->
                        cb.or(
                                cb.equal(root.get("status"), status),
                                cb.equal(root.get("user").get("id"), userId)
                        )
                );
            } else {
                spec = spec.and((root, cq, cb) -> cb.equal(root.get("status"), status));
            }
        }

        if (query != null && !query.isEmpty()) {
            if (category == null || "all".equals(category)) {
                spec = spec.and((root, cq, cb) -> {
                    cq.distinct(true); // 중복 제거
                    Join<Question, Filtered> filteredJoin = root.join("filtered", JoinType.LEFT);
                    return cb.or(
                            cb.like(root.get("title"), "%" + query + "%"),
                            cb.like(root.get("user").get("userName"), "%" + query + "%"),
                            cb.like(root.get("content"), "%" + query + "%"),
                            cb.like(filteredJoin.get("title"), "%" + query + "%"),
                            cb.like(filteredJoin.get("content"), "%" + query + "%")
                    );
                });
            } else {
                switch (category) {
                    case "title":
                        spec = spec.and((root, cq, cb) -> {
                            cq.distinct(true);
                            Join<Question, Filtered> filteredJoin = root.join("filtered", JoinType.LEFT);
                            return cb.or(
                                    cb.like(root.get("title"), "%" + query + "%"),
                                    cb.like(filteredJoin.get("title"), "%" + query + "%")
                            );
                        });
                        break;
                    case "author":
                        spec = spec.and((root, cq, cb) ->
                                cb.like(root.get("user").get("userName"), "%" + query + "%"));
                        break;
                    case "content":
                        spec = spec.and((root, cq, cb) -> {
                            cq.distinct(true);
                            Join<Question, Filtered> filteredJoin = root.join("filtered", JoinType.LEFT);
                            return cb.or(
                                    cb.like(root.get("content"), "%" + query + "%"),
                                    cb.like(filteredJoin.get("content"), "%" + query + "%")
                            );
                        });
                        break;
                }
            }
        }

        Page<Question> questions = questionRepository.findAll(spec, pageable);

        return questions.map(question -> new QuestionDTO(
                question.getId(),
                question.getTitle(),
                question.getCreateDate(),
                question.getViews(),
                question.getUser().getUserName(),
                question.getStatus(),
                question.getFiltered() != null ? question.getFiltered().getTitle() : null,
                question.getFiltered() != null ? question.getFiltered().getContent() : null
        ));
    }

    // MyPage 내 민원 조회 - 카테고리별 검색 로직 추가
    public Page<QuestionDTO> searchUserQuestions(User user, String query, Status status, String category, Pageable pageable) {
        // 기본 조건: 현재 사용자의 문의만 조회
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
                    spec = spec.and((root, cq, cb) -> {
                        cq.distinct(true); // 중복 제거
                        Join<Question, Filtered> filteredJoin = root.join("filtered", JoinType.LEFT);
                        return cb.or(
                                cb.like(root.get("title"), "%" + query + "%"),
                                cb.like(filteredJoin.get("title"), "%" + query + "%")
                        );
                    });
                    break;
                case "content":
                    spec = spec.and((root, cq, cb) -> {
                        cq.distinct(true); // 중복 제거
                        Join<Question, Filtered> filteredJoin = root.join("filtered", JoinType.LEFT);
                        return cb.or(
                                cb.like(root.get("content"), "%" + query + "%"),
                                cb.like(filteredJoin.get("content"), "%" + query + "%")
                        );
                    });
                    break;
                default: // 전체 검색
                    spec = spec.and((root, cq, cb) -> {
                        cq.distinct(true); // 중복 제거
                        Join<Question, Filtered> filteredJoin = root.join("filtered", JoinType.LEFT);
                        return cb.or(
                                cb.like(root.get("title"), "%" + query + "%"),
                                cb.like(root.get("content"), "%" + query + "%"),
                                cb.like(filteredJoin.get("title"), "%" + query + "%"),
                                cb.like(filteredJoin.get("content"), "%" + query + "%")
                        );
                    });
                    break;
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
                question.getStatus(),
                question.getFiltered() != null ? question.getFiltered().getTitle() : null,
                question.getFiltered() != null ? question.getFiltered().getContent() : null
        ));
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
        return questionRepository.countByCategoryNotAndCreateDateBetween("정상", today.atStartOfDay(), today.atTime(23, 59, 59));
    }
    // 어제 민원 조회
    public long countYesterdayQuestions() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return questionRepository.countByCreateDateBetween(
                yesterday.atStartOfDay(),
                yesterday.atTime(23, 59, 59)
        );
    }



    public Map<String, List<Long>> countQuestionsAndMaliciousByHour(LocalDate today) {
        List<Long> normalCounts = new ArrayList<>();
        List<Long> maliciousCounts = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime startOfHour = today.atTime(hour, 0);
            LocalDateTime endOfHour = today.atTime(hour, 59, 59);


            // 일반 민원 수 조회 (category가 정상인 경우)
            long normalCount = questionRepository.countByCategoryAndCreateDateBetween("정상", startOfHour, endOfHour);
            normalCounts.add(normalCount);

            // 악성 민원 수 조회 (정상이 아닌 카테고리)
            long maliciousCount = questionRepository.countByCategoryNotAndCreateDateBetween("정상", startOfHour, endOfHour);
            maliciousCounts.add(maliciousCount);
        }

        // 결과를 Map으로 반환하여 일반 민원과 악성 민원의 시간대별 데이터를 한 번에 반환
        Map<String, List<Long>> result = new HashMap<>();
        result.put("normal", normalCounts);
        result.put("malicious", maliciousCounts);

        return result;
    }





//    // 민원 악성 카테고리별 카운트
//    public long countByCategory(Category category) {
//        return questionRepository.countByCategory(category);
//    }
}
