package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.MessageDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidEntityException;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.Message;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.MessageRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.MessageService;
import com.et4.gametrackerproject.validator.MessageValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MessageDto sendMessage(MessageDto messageDto) {
        List<String> errors = MessageValidator.validate(messageDto);
        if (!errors.isEmpty()) {
            log.error("Message is not valid: {}", messageDto);
            throw new InvalidEntityException("Message is not valid", ErrorCodes.MESSAGE_NOT_VALID, errors);
        }

        log.info("Create Message : {}", messageDto);

        return MessageDto.fromEntity(
                messageRepository.save(
                        MessageDto.toEntity(messageDto)
                )
        );
    }

    @Override
    public MessageDto updateMessageContent(Integer messageId, String newContent) {
        if(messageId == null || newContent == null){
            log.error("Message ID or new content is null");
            throw new InvalidEntityException("Message ID or new content is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Update Message {} Content : {}", messageId, newContent);

        Message message = messageRepository.findById(messageId).orElseThrow(() -> new EntityNotFoundException("Message not found", ErrorCodes.MESSAGE_NOT_FOUND));

        message.setContent(newContent);

        return MessageDto.fromEntity(
                messageRepository.save(message)
        );
    }

    @Override
    public void deleteMessageById(Integer messageId) {
        if(messageId == null){
            log.error("Message ID is null");
            throw new InvalidEntityException("Message ID is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Delete Message {}", messageId);

        Message message = messageRepository.findById(messageId).orElseThrow(() -> new EntityNotFoundException("Message not found", ErrorCodes.MESSAGE_NOT_FOUND));

        Optional<User> users = userRepository.findByMessageId(messageId);
        if (users.isPresent()) {
            log.error("Impossible de supprimer le message avec l'ID {} car il est référencé par un receiver ou un sender", messageId);
            throw new InvalidOperationException("Impossible de supprimer le message car il est référencé par un receiver ou un sender",
                    ErrorCodes.MESSAGE_ALREADY_USED);
        }



        messageRepository.delete(message);
    }

    @Override
    public MessageDto getMessageById(Integer messageId) {
        if(messageId == null){
            log.error("Message ID is null");
            throw new InvalidEntityException("Message ID is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Get Message {}", messageId);

        Message message = messageRepository.findById(messageId).orElseThrow(() -> new EntityNotFoundException("Message not found", ErrorCodes.MESSAGE_NOT_FOUND));

        return MessageDto.fromEntity(message);
    }

    @Override
    public Page<MessageDto> getConversation(Integer user1Id, Integer user2Id, Pageable pageable) {
        if(user1Id == null || user2Id == null){
            log.error("User ID is null");
            throw new InvalidEntityException("User ID is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Get Conversation between {} and {}", user1Id, user2Id);

        User user1 = userRepository.findById(user1Id).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));
        User user2 = userRepository.findById(user2Id).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));

        return messageRepository.findConversation(user1, user2, pageable).map(MessageDto::fromEntity);
    }

    @Override
    public Page<MessageDto> getMessagesBySender(Integer senderId, Pageable pageable) {
        if(senderId == null){
            log.error("Sender ID is null");
            throw new InvalidEntityException("Sender ID is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Get Messages by Sender {}", senderId);

        User sender = userRepository.findById(senderId).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));

        return messageRepository.findBySender(sender, pageable).map(MessageDto::fromEntity);
    }

    @Override
    public Page<MessageDto> getMessagesByReceiver(Integer receiverId, Pageable pageable) {
        if(receiverId == null){
            log.error("Receiver ID is null");
            throw new InvalidEntityException("Receiver ID is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Get Messages by Receiver {}", receiverId);

        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));

        return messageRepository.findByReceiver(receiver, pageable).map(MessageDto::fromEntity);
    }

    @Override
    public MessageDto markAsRead(Integer messageId) {
        if(messageId == null){
            log.error("Message ID is null");
            throw new InvalidEntityException("Message ID is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Mark Message {} as Read", messageId);

        Message message = messageRepository.findById(messageId).orElseThrow(() -> new EntityNotFoundException("Message not found", ErrorCodes.MESSAGE_NOT_FOUND));

        message.setIsRead(true);

        return MessageDto.fromEntity(
                messageRepository.save(message)
        );
    }

    @Override
    public void markConversationAsRead(Integer user1Id, Integer user2Id) {
        if(user1Id == null || user2Id == null){
            log.error("User ID is null");
            throw new InvalidEntityException("User ID is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Mark Conversation between {} and {} as Read", user1Id, user2Id);

        User user1 = userRepository.findById(user1Id).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));
        User user2 = userRepository.findById(user2Id).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));

        messageRepository.markConversationAsRead(user1, user2);
    }

    @Override
    public int countUnreadMessages(Integer userId) {
        if(userId == null){
            log.error("User ID is null");
            throw new InvalidEntityException("User ID is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Count Unread Messages for User {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));

        return messageRepository.countByReceiverAndIsReadFalse(user).intValue();
    }

    @Override
    public Page<MessageDto> getMessageHistory(Integer userId, Instant from, Instant to, Pageable pageable) {
        if(userId == null || from == null || to == null){
            log.error("User ID, From or To is null");
            throw new InvalidEntityException("User ID, From or To is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Get Message History for User {} from {} to {}", userId, from, to);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));

        return messageRepository.findByReceiverAndCreationDateBetween(user, from, to, pageable).map(MessageDto::fromEntity);
    }

    @Override
    public Page<MessageDto> searchMessages(Integer userId, String query, Pageable pageable) {
        if(userId == null || query == null){
            log.error("User ID or Query is null");
            throw new InvalidEntityException("User ID or Query is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Search Messages for User {} with Query {}", userId, query);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));

        return messageRepository.findByReceiverAndContentContaining(user, query, pageable).map(MessageDto::fromEntity);
    }

    @Override
    public List<MessageDto> getRecentMessages(Integer userId, int hours) {
        if(userId == null){
            log.error("User ID is null");
            throw new InvalidEntityException("User ID is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Get Recent Messages for User {} in the last {} hours", userId, hours);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));

        List<Message> messages = messageRepository.findRecentMessages(user, Instant.now().minusSeconds(hours * 3600L));
        List<MessageDto> messageDtos = new ArrayList<>();

        for(Message message : messages){
            messageDtos.add(MessageDto.fromEntity(message));
        }

        return messageDtos;
    }

    @Override
    public void deleteConversation(Integer user1Id, Integer user2Id) {
        if(user1Id == null || user2Id == null){
            log.error("User ID is null");
            throw new InvalidEntityException("User ID is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Delete Conversation between {} and {}", user1Id, user2Id);

        User user1 = userRepository.findById(user1Id).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));
        User user2 = userRepository.findById(user2Id).orElseThrow(() -> new EntityNotFoundException("User not found", ErrorCodes.USER_NOT_FOUND));

        // TODO : Supprimer les relations avec les autres entités

        messageRepository.deleteConversation(user1, user2);
    }

    @Override
    public void encryptMessageContent(Integer messageId) {
        if(messageId == null){
            log.error("Message ID is null");
            throw new InvalidEntityException("Message ID is null", ErrorCodes.MESSAGE_NOT_VALID);
        }

        log.info("Encrypt Message {} Content", messageId);

        // TODO : Encrypt message content
    }
}
