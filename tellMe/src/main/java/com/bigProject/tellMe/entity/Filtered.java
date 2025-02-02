package com.bigProject.tellMe.entity;

import com.bigProject.tellMe.enumClass.Category;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor  // 기본생성자
@AllArgsConstructor // 전체생성자
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Filtered {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filtered_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @CreatedDate
    private LocalDateTime createDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;
}
