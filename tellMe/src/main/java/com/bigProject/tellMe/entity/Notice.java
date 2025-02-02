package com.bigProject.tellMe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder // notice
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Notice {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="notice_id")
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false)
    private String content;

    @CreatedDate
    private LocalDateTime createDate;

    @Column(nullable=false)
    private Integer views;

    @Column
    private String file;

    @PrePersist
    public void setDefaultValues() {
        if(this.views == null) {
            this.views = 0;  // DB에 저장되기 전에 기본값 설정
        }
    }

    public void incrementViews() {
        this.views += 1;
    }
}
