package com.bigProject.tellMe.client.controller;

import com.bigProject.tellMe.client.service.AzureBlobService;
import com.bigProject.tellMe.client.service.FastApiService;
import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.enumClass.ReportStatus;
import com.bigProject.tellMe.service.ReportService;
import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiRestController {
    private final UserService userService;
    private final ReportService reportService;

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

    @GetMapping("/download/report/{reportName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String reportName) {
        System.out.println("==================downloadFile : " + reportName);
        // 1) Azure Blob에서 파일 읽어오기
        byte[] fileData = azureBlobService.downloadFromBlob(reportName);

        // 2) HTTP 헤더 설정 (다운로드 시 파일명, Content-Type 등)
        HttpHeaders headers = new HttpHeaders();
        // RFC 5987 형식으로 UTF-8 인코딩된 파일명을 사용
        String encodedFilename = URLEncoder.encode(reportName, StandardCharsets.UTF_8);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);
        // 일반적인 바이너리 파일로 가정
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // 3) 파일 데이터를 담아서 응답
        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }
}
