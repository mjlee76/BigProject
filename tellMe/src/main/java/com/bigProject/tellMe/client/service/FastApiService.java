package com.bigProject.tellMe.client.service;

import com.bigProject.tellMe.client.api.FastApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FastApiService {
    private final FastApiClient fastApiClient;

    public String uploadFileApi(Long userId, String fileName) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("file_name", fileName);
        requestBody.put("user_id", userId);
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
}
