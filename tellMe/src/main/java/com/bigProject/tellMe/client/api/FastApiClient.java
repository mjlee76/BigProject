package com.bigProject.tellMe.client.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "fastApiClient", url = "${api.url}")
public interface FastApiClient {
    @GetMapping("/filter_by_module")
    public Map<String, Map<String, String>> getFilter(@RequestParam("title") String title, @RequestParam("content") String content);
}
