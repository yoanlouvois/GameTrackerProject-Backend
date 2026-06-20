package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.enums.FriendshipStatus;
import com.et4.gametrackerproject.model.Friendship;
import com.et4.gametrackerproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship,Integer> {

    @Query("SELECT f FROM Friendship f WHERE f.user1.id = :userId1 OR f.user2.id = :userId2")
    List<Friendship> findByUser1IdOrUser2Id(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

    // Trouver une amitié spécifique entre deux utilisateurs (dans n'importe quel sens)
    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user1 AND f.user2 = :user2) OR (f.user1 = :user2 AND f.user2 = :user1)")
    Optional<Friendship> findFriendship(@Param("user1") User user1, @Param("user2") User user2);

    // Vérifier si une amitié existe entre deux utilisateurs
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE (f.user1 = :user1 AND f.user2 = :user2) OR (f.user1 = :user2 AND f.user2 = :user1)")
    boolean existsFriendship(@Param("user1") User user1, @Param("user2") User user2);

    // Récupérer toutes les amitiés d'un utilisateur avec un statut spécifique
    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user OR f.user2 = :user) AND f.status = :status")
    List<Friendship> findAllByUserAndStatus(@Param("user") Integer userId, @Param("status") FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user OR f.user2 = :user) AND f.status = :status")
    Page<User> findAllUsersByUserAndStatus(User user, FriendshipStatus status, Pageable pageable);

    // Récupérer les demandes d'amitié reçues par un utilisateur (où il est user2 et le statut est PENDING)
    @Query("SELECT f FROM Friendship f WHERE f.user2 = :user AND f.status = 'PENDING'")
    List<Friendship> findPendingRequestsByReceiver(@Param("user") User user);


    // Récupérer les amis confirmés d'un utilisateur
    @Query("SELECT f.user2 FROM Friendship f WHERE f.user1 = :user AND f.status = 'ACCEPTED' " +
            "UNION " +
            "SELECT f.user1 FROM Friendship f WHERE f.user2 = :user AND f.status = 'ACCEPTED'")
    List<User> findAcceptedFriends(@Param("user") User user);

    // Compter le nombre d'amis pour un utilisateur
    @Query("SELECT COUNT(f) FROM Friendship f WHERE (f.user1 = :user OR f.user2 = :user) AND f.status = 'ACCEPTED'")
    Long countFriendsByUser(@Param("user") User user);


    // Trouver les amis en commun entre deux utilisateurs
    @Query(value =
            "SELECT u.* FROM user u " +
                    "JOIN friendship f1 ON (u.id = f1.user_id_1 OR u.id = f1.user_id_2) " +
                    "JOIN friendship f2 ON (u.id = f2.user_id_1 OR u.id = f2.user_id_2) " +
                    "WHERE f1.status = 'ACCEPTED' AND f2.status = 'ACCEPTED' " +
                    "AND ((f1.user_id_1 = :userId1 OR f1.user_id_2 = :userId1) " +
                    "AND (f2.user_id_1 = :userId2 OR f2.user_id_2 = :userId2)) " +
                    "AND u.id != :userId1 AND u.id != :userId2",
            nativeQuery = true)
    List<User> findMutualFriends(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

    // Suggérer des amis basés sur des amitiés communes (amis d'amis)
    @Query(value =
            "SELECT u.*, COUNT(*) as mutual_count FROM user u " +
                    "JOIN friendship f1 ON (u.id = f1.user_id_1 OR u.id = f1.user_id_2) " +
                    "JOIN friendship f2 ON " +
                    "  ((f1.user_id_1 = f2.user_id_1 OR f1.user_id_1 = f2.user_id_2 OR " +
                    "    f1.user_id_2 = f2.user_id_1 OR f1.user_id_2 = f2.user_id_2) " +
                    "   AND f1.user_id_1 != f2.user_id_1 AND f1.user_id_1 != f2.user_id_2 " +
                    "   AND f1.user_id_2 != f2.user_id_1 AND f1.user_id_2 != f2.user_id_2) " +
                    "WHERE f1.status = 'ACCEPTED' AND f2.status = 'ACCEPTED' " +
                    "AND (f2.user_id_1 = :userId OR f2.user_id_2 = :userId) " +
                    "AND u.id != :userId " +
                    "AND NOT EXISTS (SELECT 1 FROM friendship f3 " +
                    "               WHERE ((f3.user_id_1 = :userId AND f3.user_id_2 = u.id) " +
                    "                  OR (f3.user_id_1 = u.id AND f3.user_id_2 = :userId))) " +
                    "GROUP BY u.id, u.username " +
                    "ORDER BY mutual_count DESC",
            nativeQuery = true)
    List<Object[]> suggestFriends(@Param("userId") Integer userId);

    @Query("SELECT f FROM Friendship f WHERE f.user1.id = :userId OR f.user2.id = :userId")
    List<Friendship> findAllByUserId(@Param("userId") Integer id);

    // Trouver une amitié par ID d'utilisateur
    @Query("SELECT f FROM Friendship f WHERE f.user1.id = :userId OR f.user2.id = :userId")
    Optional<Friendship> findByUserId(Integer userId);
}
