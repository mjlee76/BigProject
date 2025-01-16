package com.bigProject.tellMe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Role {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="role_id")
    private Integer id;

    @Column(nullable=false)
    private String name;

}
