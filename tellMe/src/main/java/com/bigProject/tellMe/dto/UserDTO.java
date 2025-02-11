package com.bigProject.tellMe.dto;

import com.bigProject.tellMe.enumClass.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
}
