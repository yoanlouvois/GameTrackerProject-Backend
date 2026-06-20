package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.model.Message;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MessageDto {
    private Integer id;

    private Instant creationDate;

    private Instant lastModifiedDate;

    private UserDto sender;

    private UserDto receiver;

    private String content;

    @Builder.Default
    private Boolean isRead = false;

    public static MessageDto fromEntity(Message message) {
        if(message == null) {
            return null;
            // TODO: throw exception
        }

        return MessageDto.builder()
                .id(message.getId())
                .creationDate(message.getCreationDate())
                .lastModifiedDate(message.getLastModifiedDate())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .build();
    }

    public static Message toEntity(MessageDto messageDto) {
        if (messageDto == null) {
            return null;
            // TODO: throw exception
        }

        return Message.builder()
                .id(messageDto.getId())
                .creationDate(messageDto.getCreationDate())
                .lastModifiedDate(messageDto.getLastModifiedDate())
                .content(messageDto.getContent())
                .isRead(messageDto.getIsRead())
                .build();
    }
}
