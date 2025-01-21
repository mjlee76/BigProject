package com.bigProject.tellMe.controller.user;

import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.enumClass.Role;
import com.bigProject.tellMe.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        super();
        this.userService = userService;
    }

    @GetMapping("/signUp")
    public String signUpUser(Model model) {
        UserDTO userDTO = new UserDTO();
        model.addAttribute("user", userDTO);

        return "signUpUser";
    }

    @PostMapping("/save")
    public String saveUser(UserDTO userDTO, Model model) {
        userDTO.setRole(Role.ROLE_USER);
        userDTO.setCount(0);
        userDTO.setCreateDate(LocalDateTime.now());
        System.out.println("======================================");
        System.out.println(userDTO.toString());
        userService.save(userDTO);
        return "login";
    }

}
