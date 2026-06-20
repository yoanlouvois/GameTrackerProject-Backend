package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.enums.SanctionType;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.model.UserSanction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserSanctionRepository extends JpaRepository<UserSanction,Integer> {

    // Récupérer toutes les sanctions d'un utilisateur
    @Query("SELECT us FROM UserSanction us WHERE us.user = :user")
    Optional<UserSanction> findByUserId(Integer userId);

    Page<UserSanction> findByUserId(Integer userId, Pageable pageable);

    Page<UserSanction> findByUserIdOrderByStartDateDesc(Integer userId, Pageable pageable);

    List<UserSanction> findByAdmin(User admin);

    Page<UserSanction> findByAdmin(User admin, Pageable pageable);

    List<UserSanction> findByType(SanctionType type);

    Page<UserSanction> findByType(SanctionType type, Pageable pageable);

    // Requêtes combinées
    List<UserSanction> findByUserAndType(User user, SanctionType type);

    List<UserSanction> findByAdminAndType(User admin, SanctionType type);

    // Requêtes temporelles
    List<UserSanction> findByStartDateAfter(Instant date);

    List<UserSanction> findByEndDateBefore(Instant date);

    List<UserSanction> findByEndDateAfter(Instant date);

    List<UserSanction> findByStartDateBetween(Instant startDate, Instant endDate);

    List<UserSanction> findByEndDateBetween(Instant startDate, Instant endDate);

    // Recherche de sanctions actives
    @Query("SELECT us FROM UserSanction us WHERE us.user = :user AND (us.endDate IS NULL OR us.endDate > CURRENT_TIMESTAMP)")
    List<UserSanction> findActiveByUser(@Param("user") User user);

    @Query("SELECT us FROM UserSanction us WHERE us.user.id = :userId AND (us.endDate IS NULL OR us.endDate > CURRENT_TIMESTAMP)")
    List<UserSanction> findActiveByUserId(@Param("userId") Integer userId);

    @Query("SELECT us FROM UserSanction us WHERE us.type = :type AND (us.endDate IS NULL OR us.endDate > CURRENT_TIMESTAMP)")
    List<UserSanction> findActiveByType(@Param("type") SanctionType type);

    // Vérification de sanctions actives
    @Query("SELECT COUNT(us) > 0 FROM UserSanction us WHERE us.user = :user AND us.type = :type AND (us.endDate IS NULL OR us.endDate > CURRENT_TIMESTAMP)")
    boolean hasActiveSanctionByType(@Param("user") User user, @Param("type") SanctionType type);

    @Query("SELECT COUNT(us) > 0 FROM UserSanction us WHERE us.user.id = :userId AND us.type = :type AND (us.endDate IS NULL OR us.endDate > CURRENT_TIMESTAMP)")
    boolean hasActiveSanctionByTypeAndUserId(@Param("userId") Integer userId, @Param("type") SanctionType type);

    // Recherche de sanctions expirées
    @Query("SELECT us FROM UserSanction us WHERE us.endDate < CURRENT_TIMESTAMP")
    List<UserSanction> findExpiredSanctions();

    @Query("SELECT us FROM UserSanction us WHERE us.user = :user AND us.endDate < CURRENT_TIMESTAMP")
    List<UserSanction> findExpiredSanctionsByUser(@Param("user") User user);

    // Sanctions les plus récentes
    @Query("SELECT us FROM UserSanction us WHERE us.user = :user ORDER BY us.startDate DESC")
    List<UserSanction> findMostRecentByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT us FROM UserSanction us WHERE us.user.id = :userId ORDER BY us.startDate DESC")
    List<UserSanction> findMostRecentByUserId(@Param("userId") Integer userId, Pageable pageable);

    // Trouver la dernière sanction d'un utilisateur par type
    @Query("SELECT us FROM UserSanction us WHERE us.user = :user AND us.type = :type ORDER BY us.startDate DESC")
    Optional<UserSanction> findLatestByUserAndType(@Param("user") User user, @Param("type") SanctionType type);

    @Query("SELECT us FROM UserSanction us WHERE us.user.id = :userId AND us.type = :type ORDER BY us.startDate DESC")
    Optional<UserSanction> findLatestByUserIdAndType(@Param("userId") Integer userId, @Param("type") SanctionType type);

    // Statistiques sur les sanctions
    @Query("SELECT us.type, COUNT(us) FROM UserSanction us GROUP BY us.type")
    List<Object[]> countSanctionsByType();

    @Query("SELECT COUNT(DISTINCT us.user) FROM UserSanction us")
    Long countDistinctSanctionedUsers();

    @Query("SELECT us.admin, COUNT(us) FROM UserSanction us GROUP BY us.admin ORDER BY COUNT(us) DESC")
    List<Object[]> countSanctionsByAdmin(Pageable pageable);
}
