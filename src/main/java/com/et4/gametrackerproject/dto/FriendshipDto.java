package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.enums.FriendshipStatus;
import com.et4.gametrackerproject.model.Friendship;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FriendshipDto {
    private Integer id;

    private Instant creationDate;

    private UserDto user1;

    private UserDto user2;

    @Builder.Default
    private FriendshipStatus status = FriendshipStatus.PENDING;

    public static FriendshipDto fromEntity(Friendship friendship) {
        if(friendship == null) {
            return null;
            // TODO: throw exception
        }

        return FriendshipDto.builder()
                .id(friendship.getId())
                .creationDate(friendship.getCreationDate())
                .user1(UserDto.fromEntity(friendship.getUser1()))
                .user2(UserDto.fromEntity(friendship.getUser2()))
                .status(friendship.getStatus())
                .build();
    }

    public static Friendship toEntity(FriendshipDto friendshipDto) {
        if (friendshipDto == null) {
            return null;
            // TODO: throw exception
        }

        return Friendship.builder()
                .id(friendshipDto.getId())
                .creationDate(friendshipDto.getCreationDate())
                .user1(UserDto.toEntity(friendshipDto.getUser1()))
                .user2(UserDto.toEntity(friendshipDto.getUser2()))
                .status(friendshipDto.getStatus())
                .build();
    }
}
