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
public class Origin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "origin_id")
    private Integer id;

<<<<<<< HEAD
    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

=======
>>>>>>> taesik
    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createDate;

<<<<<<< HEAD
    @ManyToOne
    @JoinColumn(name = "category_id")
=======
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
>>>>>>> taesik
    private Category category;
}
