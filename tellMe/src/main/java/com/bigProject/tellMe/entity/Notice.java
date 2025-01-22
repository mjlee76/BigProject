package com.bigProject.tellMe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
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
}
