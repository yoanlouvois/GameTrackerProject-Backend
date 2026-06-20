package com.et4.gametrackerproject.dto;

import com.et4.gametrackerproject.enums.OnlineStatus;
import com.et4.gametrackerproject.enums.PrivacySetting;
import com.et4.gametrackerproject.enums.ProfilRank;
import com.et4.gametrackerproject.enums.ScreenTheme;
import com.et4.gametrackerproject.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private Integer id;

    private Instant creationDate;

    private Instant lastModifiedDate;

    private String username;

    private String email;

    private String password;

    private LocalDate birthDate;

    private AvatarDto avatar;

    private String country;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isAdmin = false;

    @Builder.Default
    private PrivacySetting privacySetting = PrivacySetting.PUBLIC;

    @Builder.Default
    private Integer totalGamesPlayed = 0;

    @Builder.Default
    private Integer totalPlayTime = 0;

    @Builder.Default
    private Integer points = 0;

    private ProfilRank userRank;

    @Builder.Default
    private ScreenTheme themePreference = ScreenTheme.LIGHT;

    private Instant lastLogin;

    @Builder.Default
    private OnlineStatus onlineStatus = OnlineStatus.OFFLINE;

    @Builder.Default
    private Set<DailyGameSessionDto> dailyGameSessions = new HashSet<>();

    @Builder.Default
    private Set<FavoriteGameDto> favoriteGames = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<FriendshipDto> friendshipsInitiated = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<FriendshipDto> friendshipsReceived = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<GameCommentDto> comments = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<GameCommentLikeDto> likes = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<GameLeaderboardDto> leaderboardsLines = new HashSet<>();

    @Builder.Default
    private Set<GameProgressDto> progressions = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<GameRatingDto> ratings = new HashSet<>();

    @Builder.Default
    private Set<GameRecommendationDto> recommendationsSent = new HashSet<>();

    @Builder.Default
    private Set<GameRecommendationDto> recommendationsReceived = new HashSet<>();

    @Builder.Default
    private Set<MessageDto> messagesSent = new HashSet<>();

    @Builder.Default
    private Set<MessageDto> messagesReceived = new HashSet<>();

    @Builder.Default
    private Set<NotificationDto> notifications = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<ReportDto> reportsSent = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<ReportDto> reportsAgainst = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<ReportDto> reportsResolved = new HashSet<>();

    @Builder.Default
    private Set<UserAchievementDto> achievementsEarned = new HashSet<>();

    @Builder.Default
    private Set<UserSanctionDto> sanctionsReceived = new HashSet<>();

    @JsonIgnore
    @Builder.Default
    private Set<UserSanctionDto> sanctionsDistributed = new HashSet<>();

    @Builder.Default
    private Set<WinStreakDto> winStreaks = new HashSet<>();

    public static UserDto fromEntity(User user) {
        if (user == null) {
            return null;
            //TODO: throw exception
        }

        return UserDto.builder()
                .id(user.getId())
                .creationDate(user.getCreationDate())
                .lastModifiedDate(user.getLastModifiedDate())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .birthDate(user.getBirthDate())
                .avatar(AvatarDto.fromEntity(user.getAvatar()))
                .country(user.getCountry())
                .isActive(user.getIsActive())
                .isAdmin(user.getIsAdmin())
                .privacySetting(user.getPrivacySetting())
                .totalGamesPlayed(user.getTotalGamesPlayed())
                .totalPlayTime(user.getTotalPlayTime())
                .points(user.getPoints())
                .userRank(user.getUserRank())
                .themePreference(user.getThemePreference())
                .lastLogin(user.getLastLogin())
                .onlineStatus(user.getOnlineStatus())
                .dailyGameSessions(user.getDailyGameSessions().stream().map(DailyGameSessionDto::fromEntity).collect(Collectors.toSet()))
                .favoriteGames(user.getFavoriteGames().stream().map(FavoriteGameDto::fromEntity).collect(Collectors.toSet()))
                .progressions(user.getProgressions().stream().map(GameProgressDto::fromEntity).collect(Collectors.toSet()))
                .recommendationsSent(user.getRecommendationsSent().stream().map(GameRecommendationDto::fromEntity).collect(Collectors.toSet()))
                .recommendationsReceived(user.getRecommendationsReceived().stream().map(GameRecommendationDto::fromEntity).collect(Collectors.toSet()))
                .messagesSent(user.getMessagesSent().stream().map(MessageDto::fromEntity).collect(Collectors.toSet()))
                .messagesReceived(user.getMessagesReceived().stream().map(MessageDto::fromEntity).collect(Collectors.toSet()))
                .notifications(user.getNotifications().stream().map(NotificationDto::fromEntity).collect(Collectors.toSet()))
                .achievementsEarned(user.getAchievementsEarned().stream().map(UserAchievementDto::fromEntity).collect(Collectors.toSet()))
                .sanctionsReceived(user.getSanctionsReceived().stream().map(UserSanctionDto::fromEntity).collect(Collectors.toSet()))
                .winStreaks(user.getWinStreaks().stream().map(WinStreakDto::fromEntity).collect(Collectors.toSet()))
                .build();
    }

    public static User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
            //TODO throw exception
        }

        return User.builder()
                .id(userDto.getId())
                .creationDate(userDto.getCreationDate())
                .lastModifiedDate(userDto.getLastModifiedDate())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .birthDate(userDto.getBirthDate())
                .avatar(AvatarDto.toEntity(userDto.getAvatar()))
                .country(userDto.getCountry())
                .isActive(userDto.getIsActive())
                .isAdmin(userDto.getIsAdmin())
                .privacySetting(userDto.getPrivacySetting())
                .totalGamesPlayed(userDto.getTotalGamesPlayed())
                .totalPlayTime(userDto.getTotalPlayTime())
                .points(userDto.getPoints())
                .userRank(userDto.getUserRank())
                .themePreference(userDto.getThemePreference())
                .lastLogin(userDto.getLastLogin())
                .onlineStatus(userDto.getOnlineStatus())
                .dailyGameSessions(userDto.getDailyGameSessions().stream().map(DailyGameSessionDto::toEntity).collect(Collectors.toSet()))
                .favoriteGames(userDto.getFavoriteGames().stream().map(FavoriteGameDto::toEntity).collect(Collectors.toSet()))
                .progressions(userDto.getProgressions().stream().map(GameProgressDto::toEntity).collect(Collectors.toSet()))
                .recommendationsSent(userDto.getRecommendationsSent().stream().map(GameRecommendationDto::toEntity).collect(Collectors.toSet()))
                .recommendationsReceived(userDto.getRecommendationsReceived().stream().map(GameRecommendationDto::toEntity).collect(Collectors.toSet()))
                .messagesSent(userDto.getMessagesSent().stream().map(MessageDto::toEntity).collect(Collectors.toSet()))
                .messagesReceived(userDto.getMessagesReceived().stream().map(MessageDto::toEntity).collect(Collectors.toSet()))
                .notifications(userDto.getNotifications().stream().map(NotificationDto::toEntity).collect(Collectors.toSet()))
                .achievementsEarned(userDto.getAchievementsEarned().stream().map(UserAchievementDto::toEntity).collect(Collectors.toSet()))
                .sanctionsReceived(userDto.getSanctionsReceived().stream().map(UserSanctionDto::toEntity).collect(Collectors.toSet()))
                .winStreaks(userDto.getWinStreaks().stream().map(WinStreakDto::toEntity).collect(Collectors.toSet()))
                .build();
    }
}
