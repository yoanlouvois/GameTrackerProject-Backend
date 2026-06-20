package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.enums.NotifType;
import com.et4.gametrackerproject.model.Notification;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationDto {
    private Integer id;

    private Instant creationDate;

    @JsonIgnore
    private UserDto user;

    private NotifType type;

    private String content;

    @Builder.Default
    private Boolean isRead = false;

    public static NotificationDto fromEntity(Notification notification) {
        if(notification == null) {
            return null;
            // TODO: throw exception
        }

        return NotificationDto.builder()
                .id(notification.getId())
                .creationDate(notification.getCreationDate())
                .type(notification.getType())
                .content(notification.getContent())
                .isRead(notification.getIsRead())
                .build();
    }

    public static Notification toEntity(NotificationDto notificationDto) {
        if (notificationDto == null) {
            return null;
            // TODO: throw exception
        }

        return Notification.builder()
                .id(notificationDto.getId())
                .creationDate(notificationDto.getCreationDate())
                .type(notificationDto.getType())
                .content(notificationDto.getContent())
                .isRead(notificationDto.getIsRead())
                .build();
    }
}
