package com.bigProject.tellMe.entity;

import com.bigProject.tellMe.enumClass.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // 기본 생성자가 만들어짐
@Builder // entity어노테이션이 생성자를 만들어서 만들지 않음, 근데 builder는 모든 매개변수가 있는 생성자가 필요 그래서 오류남
@NoArgsConstructor //기본 생성자
@AllArgsConstructor //전체 생성자
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class) // TellMeApplication 에 EnableJpaAuditing 를 활용해서 Auditing 기능을 활성화 해줬지만 EntityListeners를 활용해서 이 클래스에서 활성화한다고 명시적으로 지정을 해줘야 한다.
public class User {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @CreatedDate
    private LocalDateTime createDate;

    @PrePersist
    public void setDefaultValues() {

        if(this.role == null) {
            this.role = Role.ROLE_USER;
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
