package com.bigProject.tellMe.dto;

import com.bigProject.tellMe.enumClass.Role;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
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
