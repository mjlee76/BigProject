package com.bigProject.tellMe.config;

import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TellMeUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepo.findByUserId(userId); // 입력한 이메일이 db에 없으면 null

        if(user != null) { //db에 이메일 있으면 null아님
            return new TellMeUserDetails(user); //이메일을 통해서 뽑은 유저 정보를 넘겨줌
        }

        throw new UsernameNotFoundException("Could not find user with userId : " + userId); //null 이면 찾을 수 없음 경고메시지
    }
}
