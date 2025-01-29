package com.bigProject.tellMe.entity;

import com.bigProject.tellMe.dto.NoticeDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder // notice
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notice {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="notice_id")
    private Long id;

    @Column(nullable=false)
    private String title;

    @Column(nullable=false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable=false)
    private Integer views;

    @Column
    private String file;

    public void incrementViews() {
        this.views += 1;
    }

}

