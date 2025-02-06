package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.mapper.UserMapper;
import com.bigProject.tellMe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public UserDTO findByUserId(String userId) {
        User user = userRepository.findByUserId(userId);
        return userMapper.userToUserDTO(user);

    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public String checkUnique(String userId, String password, String phoneNum, String eMail) {
        List<User> listUsers = userRepository.findAll();

        for(User listUser : listUsers) {
            if(userRepository.findByUserId(userId) != null) {
                return "아이디중복";
            }else if(passwordEncoder.matches(password, listUser.getPassword())) {
                return "비밀번호중복";
            }else if(userRepository.findByPhone(phoneNum) != null) {
                return "핸드폰중복";
            }else if(userRepository.findByEmail(eMail) != null) {
                return "이메일중복";
            }
        }

        return "중복없음";
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

    public boolean checkPassword(String userId, String password) {
        User user = userRepository.findByUserId(userId);
        if (user != null) {
            // 저장된 암호화된 비밀번호와 입력된 비밀번호 비교
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    public boolean updateUserName(String userId, String newName) {
        User user = userRepository.findByUserId(userId);
        if(user != null) {
            UserDTO userDTO = userMapper.userToUserDTO(user);
            userDTO.setUserName(newName);
            user = userMapper.userDTOToUser(userDTO);
            userRepository.save(user);
            return true;
        }
        return false;
    }


    public boolean updatePhone(String userId, String newPhone) {
        return true;
    }

    public boolean updateEmail(String userId, String newEmail) {
        return true;
    }
}
