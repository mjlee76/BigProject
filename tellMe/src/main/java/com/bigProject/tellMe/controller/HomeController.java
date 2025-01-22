package com.bigProject.tellMe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("")
    public String mainPage() {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
