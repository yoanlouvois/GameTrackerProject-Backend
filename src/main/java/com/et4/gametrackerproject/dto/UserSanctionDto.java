package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.enums.SanctionType;
import com.et4.gametrackerproject.model.UserSanction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserSanctionDto {
    private Integer id;

    private Instant creationDate;

    private Instant lastModifiedDate;

    @JsonIgnore
    private UserDto user;

    private UserDto admin;

    private SanctionType type;

    private String reason;

    private Instant startDate;

    private Instant endDate;

    public static UserSanctionDto fromEntity(UserSanction userSanction) {
        if(userSanction == null) {
            return null;
            //TODO: throw exception
        }

        return UserSanctionDto.builder()
                .id(userSanction.getId())
                .creationDate(userSanction.getCreationDate())
                .lastModifiedDate(userSanction.getLastModifiedDate())
                .admin(UserDto.fromEntity(userSanction.getAdmin()))
                .type(userSanction.getType())
                .reason(userSanction.getReason())
                .startDate(userSanction.getStartDate())
                .endDate(userSanction.getEndDate())
                .build();
    }

    public static UserSanction toEntity(UserSanctionDto userSanctionDto) {
        if (userSanctionDto == null) {
            return null;
            //TODO: throw exception
        }

        return UserSanction.builder()
                .id(userSanctionDto.getId())
                .creationDate(userSanctionDto.getCreationDate())
                .lastModifiedDate(userSanctionDto.getLastModifiedDate())
                .admin(UserDto.toEntity(userSanctionDto.getAdmin()))
                .type(userSanctionDto.getType())
                .reason(userSanctionDto.getReason())
                .startDate(userSanctionDto.getStartDate())
                .endDate(userSanctionDto.getEndDate())
                .build();
    }
}
