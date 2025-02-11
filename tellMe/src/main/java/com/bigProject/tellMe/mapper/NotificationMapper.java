package com.bigProject.tellMe.mapper;

import com.bigProject.tellMe.dto.NotificationDTO;
import com.bigProject.tellMe.entity.Notification;
import com.bigProject.tellMe.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "user", source = "notificationDTO.userId")
    Notification nocaDTOToNoca(NotificationDTO notificationDTO);

    @Mapping(target = "userId", source = "notification.user.id")
    NotificationDTO nocaToNocaDTO(Notification notification);

    default User mapUser(Long userId) {
        return User.builder()
                .id(userId)  // 빌더를 통해 User 객체를 생성
                .build();
    }
}
