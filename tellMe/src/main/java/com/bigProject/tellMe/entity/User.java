package com.bigProject.tellMe.entity;

import com.bigProject.tellMe.enumClass.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity // 기본 생성자가 만들어짐
@Builder // entity어노테이션이 생성자를 만들어서 만들지 않음, 근데 builder는 모든 매개변수가 있는 생성자가 필요 그래서 오류남
@NoArgsConstructor //기본 생성자
@AllArgsConstructor //전체 생성자
@Getter
public class User {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(unique = true, nullable = false)
    private String user_id;

    @Column(nullable = false)
    private String user_name;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private Integer count;
}
