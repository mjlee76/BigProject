package com.bigProject.tellMe.entity;

import com.bigProject.tellMe.enumClass.Category;
import com.bigProject.tellMe.enumClass.Reveal;
import com.bigProject.tellMe.enumClass.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor  // 기본생성자
@AllArgsConstructor // 전체생성자
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Question {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @OneToOne
    @JoinColumn(name = "filtered_id")
    private Filtered filtered;

    @CreatedDate
    private LocalDateTime createDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Reveal reveal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Integer views;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column
    private String file1;

    @Column
    private String file2;

    @Column
    private String file3;

    @PrePersist
    public void setDefaultValues() {
        if(this.category == null) {
            this.category = Category.정상;  // DB에 저장되기 전에 기본값 설정
        }
        if(this.views == null) {
            this.views = 0;  // DB에 저장되기 전에 기본값 설정
        }
        if(this.status == null) {
            this.status = Status.접수중;  // DB에 저장되기 전에 기본값 설정
        }
    }


    public void incrementViews() {
        this.views += 1;
    }


    public void changeStatusToProcessing() {
        this.status = Status.처리중;
    }


    public void markAsAnswered(Answer answer) {
        this.answer = answer;
        this.status = Status.답변완료;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }


}
