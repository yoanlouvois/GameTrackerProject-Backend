package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.model.Message;
import com.et4.gametrackerproject.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message,Integer> {

    // Recherches de base
    List<Message> findBySender(User sender);

    List<Message> findByReceiver(User receiver);

    Page<Message> findBySender(User sender, Pageable pageable);

    Page<Message> findByReceiver(User receiver, Pageable pageable);

    // Rechercher les messages entre deux utilisateurs
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender = :user1 AND m.receiver = :user2) OR " +
            "(m.sender = :user2 AND m.receiver = :user1) " +
            "ORDER BY m.creationDate ASC")
    List<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2);

    // Même chose avec pagination
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender = :user1 AND m.receiver = :user2) OR " +
            "(m.sender = :user2 AND m.receiver = :user1) " +
            "ORDER BY m.creationDate ASC")
    Page<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);

    // Trouver les messages non lus
    List<Message> findByReceiverAndIsReadFalse(User receiver);

    Page<Message> findByReceiverAndIsReadFalse(User receiver, Pageable pageable);

    // Compter les messages non lus
    Long countByReceiverAndIsReadFalse(User receiver);

    // Marquer tous les messages comme lus pour une conversation
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.sender = :sender AND m.receiver = :receiver AND m.isRead = false")
    int markConversationAsRead(@Param("sender") User sender, @Param("receiver") User receiver);

    // Marquer un message spécifique comme lu
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.id = :messageId")
    int markMessageAsRead(@Param("messageId") Integer messageId);

    // Trouver les derniers messages de chaque conversation d'un utilisateur
    @Query(value =
            "WITH ranked_messages AS (" +
                    "    SELECT m.*, " +
                    "    ROW_NUMBER() OVER(PARTITION BY " +
                    "        CASE WHEN m.sender_id = :userId THEN m.receiver_id ELSE m.sender_id END " +
                    "        ORDER BY m.creation_date DESC" +
                    "    ) as rn " +
                    "    FROM message m " +
                    "    WHERE m.sender_id = :userId OR m.receiver_id = :userId" +
                    ") " +
                    "SELECT rm.* FROM ranked_messages rm WHERE rm.rn = 1 " +
                    "ORDER BY rm.creation_date DESC",
            nativeQuery = true)
    List<Message> findLatestMessagesFromUserConversations(@Param("userId") Integer userId);

    // Trouver les utilisateurs avec qui un utilisateur a des conversations
    @Query("SELECT DISTINCT " +
            "CASE WHEN m.sender = :user THEN m.receiver ELSE m.sender END " +
            "FROM Message m " +
            "WHERE m.sender = :user OR m.receiver = :user")
    List<User> findUserContacts(@Param("user") User user);

    // Trouver les utilisateurs avec qui un utilisateur a des messages non lus
    @Query("SELECT m.sender FROM Message m " +
            "WHERE m.receiver = :user AND m.isRead = false " +
            "GROUP BY m.sender")
    List<User> findUsersWithUnreadMessages(@Param("user") User user);

    // Compter le nombre de messages non lus par expéditeur
    @Query("SELECT m.sender.id, COUNT(m) FROM Message m " +
            "WHERE m.receiver = :user AND m.isRead = false " +
            "GROUP BY m.sender.id")
    List<Object[]> countUnreadMessagesBySender(@Param("user") User user);

    // Supprimer tous les messages d'une conversation
    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE " +
            "(m.sender = :user1 AND m.receiver = :user2) OR " +
            "(m.sender = :user2 AND m.receiver = :user1)")
    int deleteConversation(@Param("user1") User user1, @Param("user2") User user2);

    // Supprimer les messages plus anciens qu'une certaine date
    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.creationDate < :date")
    int deleteMessagesOlderThan(@Param("date") Instant date);

    // Trouver les messages non lus par date décroissante (les plus récents d'abord)
    @Query("SELECT m FROM Message m " +
            "WHERE m.receiver = :user AND m.isRead = false " +
            "ORDER BY m.creationDate DESC")
    Page<Message> findUnreadMessagesOrderedByDate(@Param("user") User user, Pageable pageable);

    Page<Message> findByReceiverAndCreationDateBetween(User user, Instant from, Instant to, Pageable pageable);

    Page<Message> findByReceiverAndContentContaining(User user, String query, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.receiver = :user AND m.creationDate > :date")
    List<Message> findRecentMessages(@Param("user") User user,@Param("date") Instant date);

    // Rechercher un message par son userId
    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    Optional<Message> findByUserId(Integer userId);
}
