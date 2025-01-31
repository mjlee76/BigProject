package com.bigProject.tellMe.controller.user;

import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    //태식 : 회원가입할 때 userId랑 phone, email이 중복되는지 체크
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

    //태식 : 아이디 찾기
    @PostMapping("/user/check_name/find_id")
    public String checkFindId(@RequestParam("userName") String userName, @RequestParam("phone") String phoneNum) {
        return userService.checkNameAndFindId(userName, phoneNum);
//        if(nameList.isEmpty()) {
//            return "noName";
//        }else {
//            List<User> userPhoneList = userService.findByUserNameAndPhone(userName, phoneNum);
//            if(userPhoneList.isEmpty()) {
//                return "noPhone";
//            }else {
//                return userPhoneList.get(0).getUserId();
//            }
//        }
    }

    //태식 : 비밀번호 찾기 전 : 아이디 체크
    @PostMapping("/user/check_id")
    public String checkId(@RequestParam("userId") String userId) {
        User user = userService.findByUserId(userId);
        if(user == null) {
            return "실패";
        }else {
            return "성공";
        }
    }

    //태식 : 비밀번호 찾기 : 아이디 체크 성공 : 임시비밀번호 발급
    @PostMapping("/user/check_name/find_password")
    public String findPassword(@RequestParam("userId") String userId, @RequestParam("userName") String userName, @RequestParam("phone") String phoneNum) {
        String checkUserId = userService.checkNameAndFindId(userName, phoneNum);

        if(userId.equals(checkUserId)) {
            return userService.FindPassword(userId);
        } else {
            return checkUserId;
        }
    }
}
