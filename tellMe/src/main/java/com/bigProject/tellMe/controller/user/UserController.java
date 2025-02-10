package com.bigProject.tellMe.controller.user;

import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.enumClass.Role;
import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/signUp")
    public String signUpUser(Model model) {
        UserDTO userDTO = new UserDTO();
        model.addAttribute("user", userDTO);

        return "sign_up_user";
    }

    @PostMapping("/save")
    public String saveUser(UserDTO userDTO, Model model) {
        //userDTO.setRole(Role.ROLE_ADMIN);
        //userDTO.setRole(Role.ROLE_MANAGER);
        //userDTO.setRole(Role.ROLE_COUNSELOR);
<<<<<<< HEAD
<<<<<<< HEAD
//        userDTO.setRole(Role.ROLE_USER);
        System.out.println("======================================");
        System.out.println(userDTO.toString());
=======
        //userDTO.setRole(Role.ROLE_USER);
=======
        userDTO.setRole(Role.ROLE_USER);
>>>>>>> a18053f (내 페이지 수정.)
//        userDTO.setCount(0);
//        userDTO.setCreateDate(LocalDateTime.now());
//        System.out.println("======================================");
        //System.out.println(userDTO.toString());
>>>>>>> e1cf1c9 (내 페이지 수정.)
        userService.save(userDTO);
        return "login";
    }

    @GetMapping("/find/id")
    public String findId() {
        return "find_id";
    }

    @GetMapping("/find/pw")
    public String findPw() {
        return "find_pw";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }
}
