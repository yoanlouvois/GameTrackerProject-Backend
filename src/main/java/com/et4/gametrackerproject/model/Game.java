package com.et4.gametrackerproject.model;

import com.et4.gametrackerproject.enums.DifficultyLevel;
import com.et4.gametrackerproject.enums.GameCategory;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {
        "favoriteGames", "comments", "leaderboardEntries", "progressions",
        "ratings", "recommendations", "tags", "winStreaks"
})
@Entity
@Table(name = "game")
public class Game extends AbstractEntity{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private GameCategory category;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "play_count")
    @Builder.Default
    private Integer playCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    @Builder.Default
    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<FavoriteGame> favoriteGames = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameComment> comments = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameLeaderboard> leaderboardEntries = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameProgress> progressions = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameRating> ratings = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameRecommendation> recommendations = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<GameTag> tags = new HashSet<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<WinStreak> winStreaks = new HashSet<>();
}
