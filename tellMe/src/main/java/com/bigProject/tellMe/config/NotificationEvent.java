package com.bigProject.tellMe.config;

import org.springframework.context.ApplicationEvent;

public class NotificationEvent extends ApplicationEvent {
    private final String userId;
    private final String message;

    public NotificationEvent(String userId, String message) {
        super(userId); // 이벤트 소스를 userId로 설정
        this.userId = userId;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}
