package com.bigProject.tellMe.dto;

import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.enumClass.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String userId;
    private String userName;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private String birthDate;
    private Role role;
    private LocalDateTime createDate;
    private Integer count;
}
