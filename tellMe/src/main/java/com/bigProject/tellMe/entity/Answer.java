package com.bigProject.tellMe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor  // 기본생성자
@AllArgsConstructor // 전체생성자
@Builder
public class Answer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private String file1;
}
