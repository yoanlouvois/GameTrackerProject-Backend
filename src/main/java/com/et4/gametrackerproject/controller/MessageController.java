package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.MessageApi;
import com.et4.gametrackerproject.dto.MessageDto;
import com.et4.gametrackerproject.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
public class MessageController implements MessageApi {

    @Autowired
    private MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public MessageDto sendMessage(MessageDto messageDto) {
        return messageService.sendMessage(messageDto);
    }

    @Override
    public MessageDto updateMessageContent(Integer messageId, String newContent) {
        return messageService.updateMessageContent(messageId, newContent);
    }

    @Override
    public void deleteMessage(Integer messageId) {
        messageService.deleteMessageById(messageId);
    }

    @Override
    public MessageDto getMessageById(Integer messageId) {
        return messageService.getMessageById(messageId);
    }

    @Override
    public Page<MessageDto> getConversation(Integer user1Id, Integer user2Id, Pageable pageable) {
        return messageService.getConversation(user1Id, user2Id, pageable);
    }

    @Override
    public Page<MessageDto> getMessagesBySender(Integer senderId, Pageable pageable) {
        return messageService.getMessagesBySender(senderId, pageable);
    }

    @Override
    public Page<MessageDto> getMessagesByReceiver(Integer receiverId, Pageable pageable) {
        return messageService.getMessagesByReceiver(receiverId, pageable);
    }

    @Override
    public MessageDto markAsRead(Integer messageId) {
        return messageService.markAsRead(messageId);
    }

    @Override
    public void markConversationAsRead(Integer user1Id, Integer user2Id) {
        messageService.markConversationAsRead(user1Id, user2Id);
    }

    @Override
    public int countUnreadMessages(Integer userId) {
        return messageService.countUnreadMessages(userId);
    }

    @Override
    public Page<MessageDto> getMessageHistory(Integer userId, Instant from, Instant to, Pageable pageable) {
        return messageService.getMessageHistory(userId, from, to, pageable);
    }

    @Override
    public Page<MessageDto> searchMessages(Integer userId, String query, Pageable pageable) {
        return messageService.searchMessages(userId, query, pageable);
    }

    @Override
    public List<MessageDto> getRecentMessages(Integer userId, int hours) {
        return messageService.getRecentMessages(userId, hours);
    }

    @Override
    public void deleteConversation(Integer user1Id, Integer user2Id) {
        messageService.deleteConversation(user1Id, user2Id);
    }

    @Override
    public void encryptMessageContent(Integer messageId) {
        messageService.encryptMessageContent(messageId);
    }
}
