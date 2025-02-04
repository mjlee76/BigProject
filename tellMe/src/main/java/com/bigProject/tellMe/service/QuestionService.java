package com.bigProject.tellMe.service;

import com.bigProject.tellMe.client.api.FastApiClient;
import com.bigProject.tellMe.client.dto.QuestionResponse;
import com.bigProject.tellMe.config.WebClientConfig;
import com.bigProject.tellMe.dto.QuestionDTO;
import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.mapper.QuestionMapper;
import com.bigProject.tellMe.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;

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

    public String checkApi(String title, String content) {
        Map<String, Map<String, String>> requestBody = fastApiClient.getFilter(title, content);

        if(requestBody!=null) {
            return "성공";
        }else {
            return "실패";
        }
    }
}
