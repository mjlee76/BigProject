package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.mapper.UserMapper;
import com.bigProject.tellMe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public void encodePassword(UserDTO userDTO) {
        String rawPassword = userDTO.getPassword();
        String encodePassword = passwordEncoder.encode(rawPassword);
        userDTO.setPassword(encodePassword);
    }

    public void save(UserDTO userDTO) {
        encodePassword(userDTO);
        User user = userMapper.userDTOToUser(userDTO);
        System.out.println("==============================");
        System.out.println(user.toString());

        userRepository.save(user);
    }

    public User findByUserId(String name) {
        return userRepository.findByUserId(name);
    }


    public boolean isuserIdUnique(Long id, String userId) {
        User user = userRepository.findByUserId(userId);
        if(user == null) {
            return true;
        }

        boolean isCreatingNew = (id == null);

        if(isCreatingNew) {
            if (user != null) {
                return false;
            }
        }else {
            if(user.getId() != id) {
                return false;
            }
        }

        return true;
    }

    public boolean isphoneUnique(Long id, String phoneNum) {
        User user = userRepository.findByPhone(phoneNum);
        if(user == null) {
            return true;
        }

        boolean isCreatingNew = (id == null);

        if(isCreatingNew) {
            if (user != null) {
                return false;
            }
        }else {
            if(user.getId() != id) {
                return false;
            }
        }

        return true;
    }

    public boolean isemailUnique(Long id, String eMail) {
        User user = userRepository.findByEmail(eMail);
        if(user == null) {
            return true;
        }

        boolean isCreatingNew = (id == null);

        if(isCreatingNew) {
            if (user != null) {
                return false;
            }
        }else {
            if(user.getId() != id) {
                return false;
            }
        }

        return true;
    }
}
