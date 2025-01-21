package com.bigProject.tellMe.controller.customer;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @GetMapping("/service")
    public String customerService() {
        return "customer/service";
    }

    @GetMapping("/notice")
    public String customerNotice() {
        return "customer/notice";
    }
}
