package com.bigProject.tellMe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor  // 기본생성자
@AllArgsConstructor // 전체생성자
@Builder
public class Origin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "origin_id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
