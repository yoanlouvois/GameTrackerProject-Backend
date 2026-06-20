package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public interface MessageService {

    //Opérations de base
    MessageDto sendMessage(MessageDto messageDto);
    MessageDto updateMessageContent(Integer messageId, String newContent);
    void deleteMessageById(Integer messageId);

    //Récupération
    MessageDto getMessageById(Integer messageId);
    Page<MessageDto> getConversation(Integer user1Id, Integer user2Id, Pageable pageable);
    Page<MessageDto> getMessagesBySender(Integer senderId, Pageable pageable);
    Page<MessageDto> getMessagesByReceiver(Integer receiverId, Pageable pageable);

    //Gestion des états
    MessageDto markAsRead(Integer messageId);
    void markConversationAsRead(Integer user1Id, Integer user2Id);
    int countUnreadMessages(Integer userId);

    //Historique
    Page<MessageDto> getMessageHistory(Integer userId, Instant from, Instant to, Pageable pageable);
    Page<MessageDto> searchMessages(Integer userId, String query, Pageable pageable);
    List<MessageDto> getRecentMessages(Integer userId, int hours);

    // Modération
    void deleteConversation(Integer user1Id, Integer user2Id);

    // Sécurité
    void encryptMessageContent(Integer messageId);
}