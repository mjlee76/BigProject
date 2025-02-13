package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.mapper.UserMapper;
import com.bigProject.tellMe.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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

<<<<<<< HEAD
    public String checkUnique(String userId, String phoneNum, String eMail) {
        List<User> listUsers = userRepository.findAll();

        for(User listUser : listUsers) {
            if(userRepository.findByUserId(userId) != null) {
                return "아이디중복";
            }else if(userRepository.findByPhone(phoneNum) != null) {
                return "핸드폰중복";
            }else if(userRepository.findByEmail(eMail) != null) {
                return "이메일중복";
            }
=======
//    public String checkUnique(String userId, String password, String phoneNum, String eMail) {
//        List<User> listUsers = userRepository.findAll();
//
//        for(User listUser : listUsers) {
//            if(userRepository.findByUserId(userId) != null) {
//                return "아이디중복";
//            }else if(passwordEncoder.matches(password, listUser.getPassword())) {
//                return "비밀번호중복";
//            }else if(userRepository.findByPhone(phoneNum) != null) {
//                return "핸드폰중복";
//            }else if(userRepository.findByEmail(eMail) != null) {
//                return "이메일중복";
//            }
//        }
//
//        return "중복없음";
//    }
public String checkUnique(String userId, String phoneNum, String eMail) {
    List<User> listUsers = userRepository.findAll();

    for(User listUser : listUsers) {
        if(userRepository.findByUserId(userId) != null) {
            return "아이디중복";
        }else if(userRepository.findByPhone(phoneNum) != null) {
            return "핸드폰중복";
        }else if(userRepository.findByEmail(eMail) != null) {
            return "이메일중복";
>>>>>>> 25389e745540d744ba6801f6b40f6f935e3230ac
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

    public boolean updateUserName(String userId, String currentPassword, String newName) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return false;  // 사용자 존재하지 않음
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;  // 비밀번호 불일치
        }

        // 이름 변경
        user.setUserName(newName);
        userRepository.save(user);
        return true;
    }


    public boolean updatePhone(String userId, String currentPassword, String newPhone) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return false;  // 사용자를 찾을 수 없음
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;  // 비밀번호 불일치
        }

        // 핸드폰 번호 변경
        user.setPhone(newPhone);
        userRepository.save(user);
        return true;
    }

    public boolean updateEmail(String userId, String currentPassword, String newEmail) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return false;  // 사용자를 찾을 수 없음
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;  // 비밀번호 불일치
        }

        // 이메일 변경
        user.setEmail(newEmail);
        userRepository.save(user);
        return true;
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.userToUserDTO(user);
    }

    public boolean updatePassword(String userId, String currentPassword, String newPassword) {
        // 1️⃣ 사용자 조회 (UserDTO 사용 X, User 엔티티 직접 수정)
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // 2️⃣ 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;  // 현재 비밀번호가 틀리면 false 반환
        }

        // 3️⃣ 새 비밀번호 암호화 후 저장 (DTO 변환 없이 User 엔티티 직접 수정)
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);  // ✅ User 엔티티 직접 저장

        return true;  // 비밀번호 변경 성공
    }

    public boolean updateAddress(String userId, String currentPassword, String newAddress) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return false;  // 사용자를 찾을 수 없음
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;  // 비밀번호 불일치
        }

        // 주소 변경
        user.setAddress(newAddress);
        userRepository.save(user);
        return true;
    }


    public List<Long> getAllUserIds() {
        return userRepository.findAll().stream().map(User::getId).collect(Collectors.toList());
    }
}
