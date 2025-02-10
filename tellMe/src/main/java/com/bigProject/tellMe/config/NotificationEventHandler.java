package com.bigProject.tellMe.config;

import com.bigProject.tellMe.controller.complaint.ComplaintRestController;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {
    private final ComplaintRestController complaintRestController;

    @EventListener
    public void handleNotification(NotificationEvent event) {
        complaintRestController.sendNotification(event.getUserId(), event.getMessage());
    }
}
