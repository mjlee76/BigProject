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
import java.util.List;
import java.util.Map;
import java.util.Random;

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

        userRepository.save(user);
    }

    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId);
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

    public String checkNameAndFindId(String userName, String phoneNum) {
        List<User> nameList = userRepository.findByUserName(userName);
        if(nameList.isEmpty()) {
            return "noName";
        }else {
            List<User> userPhoneList = userRepository.findByUserNameAndPhone(userName, phoneNum);
            if(userPhoneList.isEmpty()) {
                return "noPhone";
            }else {
                return userPhoneList.get(0).getUserId();
            }
        }
    }

    public String FindPassword(String userId) {
        User user = userRepository.findByUserId(userId);
        UserDTO userDTO = userMapper.userToUserDTO(user);

        String temporaryPassword = makeTemporaryPassword();

        userDTO.setPassword(temporaryPassword);
        encodePassword(userDTO);
        userRepository.save(userMapper.userDTOToUser(userDTO));

        return temporaryPassword;
    }

    public String makeTemporaryPassword() {
        int length = 8;
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        int  charsLength = chars.length();
        Random random = new Random();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(charsLength)));
        }
        return sb.toString();
    }
}
