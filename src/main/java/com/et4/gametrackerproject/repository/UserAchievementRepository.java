package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.model.Achievement;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.model.UserAchievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserAchievementRepository extends JpaRepository<UserAchievement,Integer> {

    // Requêtes de base
    List<UserAchievement> findByUser(User user);

    Page<UserAchievement> findByUser(User user, Pageable pageable);

    List<UserAchievement> findByUserOrderByUnlockedAtDesc(User user);

    Page<UserAchievement> findByUserOrderByUnlockedAtDesc(User user, Pageable pageable);

    List<UserAchievement> findByAchievement(Achievement achievement);

    Page<UserAchievement> findByAchievement(Achievement achievement, Pageable pageable);

    Optional<UserAchievement> findByUserAndAchievement(User user, Achievement achievement);

    boolean existsByUserAndAchievement(User user, Achievement achievement);

    // Les derniers succès déverrouillés
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user = :user ORDER BY ua.unlockedAt DESC")
    List<UserAchievement> findRecentlyUnlockedAchievements(@Param("user") User user, Pageable pageable);

    // Les succès les plus rares que l'utilisateur a débloqués
    @Query("SELECT ua FROM UserAchievement ua " +
            "WHERE ua.user = :user " +
            "ORDER BY (SELECT COUNT(ua2) FROM UserAchievement ua2 WHERE ua2.achievement = ua.achievement) ASC")
    List<UserAchievement> findRarestAchievementsUnlocked(@Param("user") User user, Pageable pageable);

    // Trouver les succès débloqués par période
    List<UserAchievement> findByUserAndUnlockedAtBetween(User user, Instant start, Instant end);

    // Compter le nombre de succès débloqués par utilisateur
    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user = :user")
    Long countAchievementsUnlockedByUser(@Param("user") User user);

    // Trouver les utilisateurs ayant débloqué un succès spécifique
    @Query("SELECT ua.user FROM UserAchievement ua WHERE ua.achievement = :achievement")
    List<User> findUsersWhoUnlockedAchievement(@Param("achievement") Achievement achievement, Pageable pageable);

    // Calculer le taux de déblocage global d'un succès
    @Query("SELECT (COUNT(ua) * 1.0 / COUNT(u) * 100) FROM UserAchievement ua, User u " +
            "WHERE ua.achievement = :achievement")
    Double calculateGlobalUnlockRate(@Param("achievement") Achievement achievement);

    // Trouver les succès débloqués par ordre chronologique
    @Query("SELECT ua FROM UserAchievement ua " +
            "WHERE ua.user = :user " +
            "ORDER BY ua.unlockedAt ASC")
    List<UserAchievement> findAchievementProgressionTimeline(@Param("user") User user);

    UserAchievement user(User user);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.id = :achievementId")
    Optional<UserAchievement> findAllByAchievementId(Integer achievementId);


}
