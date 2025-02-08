package com.bigProject.tellMe.config;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileUpLoadUtil {
    public static List<String> saveFiles(String uploadDir, List<MultipartFile> multipartFiles) throws IOException {
        List<String> savedFileNames = new ArrayList<>();
        Path uploadPath = Paths.get(uploadDir);

        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        for (MultipartFile multipartFile : multipartFiles) {
            if(multipartFile.isEmpty()) continue;
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);

            try(InputStream inputStream = multipartFile.getInputStream()){
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                savedFileNames.add(fileName);
            }catch(IOException ex) {
                throw new IOException("Could not save file : " + fileName, ex);
            }
        }
        return savedFileNames;
    }

    public static void cleanFile(String uploadDir) {
        Path dirPath = Paths.get(uploadDir);
        try {
            Files.list(dirPath).forEach(file ->{
                if(!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    }catch(IOException ex) {
                        System.out.println("Could not delete file:"+file);
                    }
                }
            });
        }catch(IOException ex) {
            System.out.println("Could not list directory:"+dirPath);
        }

    }
}
