package com.bigProject.tellMe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {
    @GetMapping("")
    public String mainPage() {
        return "main";
    }
}
