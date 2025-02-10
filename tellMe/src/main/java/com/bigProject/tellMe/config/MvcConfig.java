package com.bigProject.tellMe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String noticeDir = "tellMe/tellMe-uploadFile/notice";
        String questionDir = "tellMe/tellMe-uploadFile/question";

        Path noticePath = Paths.get(noticeDir);
        Path questionPath = Paths.get(questionDir);

        registry.addResourceHandler("/" + noticeDir + "/**")
                .addResourceLocations("file:/" + noticePath.toFile().getAbsolutePath() + "/");

        registry.addResourceHandler("/" + questionDir + "/**") // 🔹 추가
                .addResourceLocations("file:/" + questionPath.toFile().getAbsolutePath() + "/");
    }
}
