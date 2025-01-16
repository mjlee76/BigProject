package com.bigProject.tellMe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor  // 기본생성자
@AllArgsConstructor // 전체생성자
@Builder
public class Question {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private Boolean reveal;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false)
    private Integer views;

    @Column(nullable = false)
    private String file1;

    @Column(nullable = false)
    private String file2;

    @Column(nullable = false)
    private String file3;


}
