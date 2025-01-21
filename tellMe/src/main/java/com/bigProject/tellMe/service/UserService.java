package com.bigProject.tellMe.service;

import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.mapper.UserMapper;
import com.bigProject.tellMe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
