package com.bigProject.tellMe.controller.user;

import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @PostMapping("/user/check/unique")
    public String checkIdDuplicate(@Param("id") Long id, @RequestParam("userId") String userId,
                                   @RequestParam("phone") String phoneNum, @RequestParam("email") String eMail) {
        boolean userid = userService.isuserIdUnique(id, userId);
        boolean phone = userService.isphoneUnique(id, phoneNum);
        boolean email = userService.isemailUnique(id, eMail);

        if(userid == true && phone == true && email == true) {
            return "OK";
        } else if(userid == false && phone == true && email == true) {
            return "IdDuplicated";
        } else if(userid == true && phone == false && email == true) {
            return "PhoneDuplicated";
        } else if(userid == true && phone == true && email == false) {
            return "EmailDuplicated";
        } else {
            return "Duplicated";
        }
    }
}
