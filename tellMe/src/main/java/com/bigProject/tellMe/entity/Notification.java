package com.bigProject.tellMe.entity;

import com.bigProject.tellMe.enumClass.Status;
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
public class Notification {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @CreatedDate
    private LocalDateTime createDate;
}
