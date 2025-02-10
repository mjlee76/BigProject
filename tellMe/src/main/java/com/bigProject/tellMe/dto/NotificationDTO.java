package com.bigProject.tellMe.dto;

import com.bigProject.tellMe.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String message;
    private boolean isRead;
    private LocalDateTime createDate;
}
