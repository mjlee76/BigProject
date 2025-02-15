package com.bigProject.tellMe.client.controller;

import com.bigProject.tellMe.client.service.AzureBlobService;
import com.bigProject.tellMe.client.service.FastApiService;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.service.QuestionService;
import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileRestController {
    private final UserService userService;
    private final FastApiService fastApiService;
    private final AzureBlobService azureBlobService;

    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, Authentication auth) {
        UserDTO userDTO = userService.findByUserId(auth.getName());
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어 있습니다.");
        }

        try {
            // (A) Azure Blob에 업로드
            String fileName = azureBlobService.uploadToBlob(file, String.valueOf(userDTO.getId()));
            // blobUrl 예: https://<스토리지계정이름>.blob.core.windows.net/my-container/4/파일이름.jpg

            // (B) (선택) 로컬에 저장도 하고 싶다면 기존 로직 유지
            //    String uploadDir = "/tellMe/apiCheck-uploadFile/" + userDTO.getId();
            //    FileUpLoadUtil.saveFiles(uploadDir, List.of(file));

            // (C) FastAPI 호출(파일 경로 대신 Blob URL을 넘길 수도 있음)
            String response = fastApiService.uploadFileApi(userDTO.getId(), fileName);
            // FastAPI 쪽에서 blobUrl(혹은 기존 로컬 경로)을 처리하도록 로직 조정

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            //e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: " + e.getMessage());
        }
    }
}
