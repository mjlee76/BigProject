package com.bigProject.tellMe.client.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.bigProject.tellMe.config.AzureStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AzureBlobService {
    private final AzureStorageProperties azureStorageProperties;

    // Azure Storage 연결 문자열(또는 SAS URL)을 환경 변수나 설정 파일에서 불러옴
//    @Value("${azure.storage.connection-string}")
//    private String connectionString;

    // 업로드할 컨테이너 이름
    private final String uploadContainerName = "fastapi";
    private final String reportContainerName = "report";

    public String uploadToBlob(MultipartFile file, String userId) throws IOException {
        // 1) BlobServiceClient 생성
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureStorageProperties.getConnectionString())
                .buildClient();

        // 2) 컨테이너 클라이언트 얻기
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(uploadContainerName);
        // 컨테이너가 없다면 생성
        if (!containerClient.exists()) {
            containerClient.create();
        }

        // 3) 업로드할 Blob의 이름 설정(유저ID 폴더 구조를 흉내내고 싶다면 경로처럼 구성)
        // 예: "userId/파일명"
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        //String safeFilename = originalFilename.replaceAll(" ", "_"); // 공백을 _로 변경
        String blobName = userId + "/" + originalFilename;
        //String blobName = userId + "/" + StringUtils.cleanPath(file.getOriginalFilename());

        // 4) BlobClient 생성
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        // 5) MultipartFile -> 업로드
        try (InputStream inputStream = file.getInputStream()) {
            blobClient.upload(inputStream, file.getSize(), true);
        }

        // 6) 업로드된 Blob의 URL 반환
        return originalFilename;
    }

    public byte[] downloadFromBlob(String reportName) {
        // Blob 이름을 UTF-8로 URL 인코딩 (업로드 시에도 동일하게 인코딩했어야 합니다)
        String encodedReportName = URLEncoder.encode(reportName, StandardCharsets.UTF_8);
        // 1. BlobServiceClient 생성
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(azureStorageProperties.getConnectionString())
                .buildClient();
        // 2. 특정 컨테이너 객체를 가져옴
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(reportContainerName);
        // 3. 컨테이너 내의 특정 Blob을 가져옴
        BlobClient blobClient = containerClient.getBlobClient(encodedReportName);
        // 4. Blob이 존재하는지 체크 (선택)
        if (!blobClient.exists()) {
            throw new RuntimeException("Blob not found: " + reportName);
        }
        // 5. Blob 다운로드 (바이트 배열에 쓰기)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blobClient.download(outputStream);

        return outputStream.toByteArray();
    }
}
