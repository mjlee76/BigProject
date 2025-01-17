package com.bigProject.tellMe.controller.user;

import com.bigProject.tellMe.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @PostMapping("/user/signUp")
    public String joinUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);

        return "signUpUser";
    }

}
