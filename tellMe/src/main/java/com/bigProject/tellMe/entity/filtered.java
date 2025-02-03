package com.bigProject.tellMe.entity;

import com.bigProject.tellMe.enumClass.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor  // 기본생성자
@AllArgsConstructor // 전체생성자
@Builder
public class filtered {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filtered_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;
}
