package com.et4.gametrackerproject.model;

import com.et4.gametrackerproject.enums.OnlineStatus;
import com.et4.gametrackerproject.enums.PrivacySetting;
import com.et4.gametrackerproject.enums.ProfilRank;
import com.et4.gametrackerproject.enums.ScreenTheme;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {
        "favoriteGames", "friendShipsInitiated", "friendshipsReceived", "comments",
        "likes", "leaderboardsLines", "progressions", "ratings", "recommendationsSent",
        "recommendationsReceived", "messagesSent", "messagesReceived", "notifications",
        "reportsSent", "reportsAgainst", "reportsResolved", "achievementsEarned",
        "sanctionsReceived", "sanctionsDistributed", "winStreaks", "dailyGameSessions"
})
@Entity
@Table(name = "user")
public class User extends AbstractEntity{

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @Column(name = "country")
    private String country;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_admin")
    @Builder.Default
    private Boolean isAdmin = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_setting")
    @Builder.Default
    private PrivacySetting privacySetting = PrivacySetting.PUBLIC;

    @Column(name = "total_games_played")
    @Builder.Default
    private Integer totalGamesPlayed = 0;

    @Column(name = "total_play_time")
    @Builder.Default
    private Integer totalPlayTime = 0;

    @Column(name = "points")
    @Builder.Default
    private Integer points = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "userrank")
    private ProfilRank userRank;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme_preference")
    @Builder.Default
    private ScreenTheme themePreference = ScreenTheme.LIGHT;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "online_status")
    @Builder.Default
    private OnlineStatus onlineStatus = OnlineStatus.OFFLINE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<DailyGameSession> dailyGameSessions = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<FavoriteGame> favoriteGames = new HashSet<>();

    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Friendship> friendShipsInitiated = new HashSet<>();

    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Friendship> friendshipsReceived = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameComment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameCommentLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameLeaderboard> leaderboardsLines = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameProgress> progressions = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameRating> ratings = new HashSet<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameRecommendation> recommendationsSent = new HashSet<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameRecommendation> recommendationsReceived = new HashSet<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Message> messagesSent = new HashSet<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Message> messagesReceived = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Notification> notifications = new HashSet<>();

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Report> reportsSent = new HashSet<>();

    @OneToMany(mappedBy = "reported", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Report> reportsAgainst = new HashSet<>();

    @OneToMany(mappedBy = "resolver", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Report> reportsResolved = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UserAchievement> achievementsEarned = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UserSanction> sanctionsReceived = new HashSet<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UserSanction> sanctionsDistributed = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<WinStreak> winStreaks = new HashSet<>();
}
