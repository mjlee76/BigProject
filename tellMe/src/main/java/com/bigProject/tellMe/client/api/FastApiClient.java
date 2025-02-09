package com.bigProject.tellMe.client.api;

import com.bigProject.tellMe.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "fastApiClient", url = "${api.url}", configuration = FeignConfig.class)
public interface FastApiClient {
    @PostMapping(value = "/check_spam", consumes = "application/json", produces = "application/json")
    Map<String, Object> getSpam(@RequestBody Map<String, Object> requestBody);

    @PostMapping(value = "/filtered_module", consumes = "application/json", produces = "application/json")
    Map<String, Object> getFilter(@RequestBody Map<String, Object> requestBody);

    @PostMapping(value = "/make_report", consumes = "application/json", produces = "application/json")
    Map<String, Object> getReport(@RequestBody Map<String, Object> requestBody);
}
