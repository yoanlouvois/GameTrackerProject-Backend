package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.NotificationDto;
import com.et4.gametrackerproject.enums.NotifType;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidEntityException;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.Notification;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.NotificationRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.NotificationService;
import com.et4.gametrackerproject.validator.NotificationValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public NotificationDto createNotification(NotificationDto notificationDto) {
        List<String> errors = NotificationValidator.validate(notificationDto);
        if (!errors.isEmpty()) {
            log.error("Invalid notification {}", notificationDto);
            throw new InvalidEntityException("La notification n'est pas valide", ErrorCodes.NOTIFICATION_NOT_VALID, errors);
        }

        log.info("Create notification : {}", notificationDto);

        return NotificationDto.fromEntity(
                notificationRepository.save(
                        NotificationDto.toEntity(notificationDto)
                )
        );
    }

    @Override
    public NotificationDto updateNotificationContent(Integer notificationId, String newContent) {
        if(notificationId == null || newContent == null){
            log.error("Invalid notification id or content");
            throw new InvalidEntityException("L'id de la notification ou le contenu est invalide", ErrorCodes.NOTIFICATION_NOT_VALID);
        }

        log.info("Update notification content : {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new EntityNotFoundException("Aucune notification avec l'id " + notificationId + " n'a été trouvée", ErrorCodes.NOTIFICATION_NOT_FOUND));

        notification.setContent(newContent);

        return NotificationDto.fromEntity(notificationRepository.save(notification));
    }

    @Override
    public void deleteNotification(Integer notificationId) {
        if(notificationId == null){
            log.error("Invalid notification id");
            throw new InvalidEntityException("L'id de la notification est invalide", ErrorCodes.NOTIFICATION_NOT_VALID);
        }

        log.info("Delete notification : {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new EntityNotFoundException("Aucune notification avec l'id " + notificationId + " n'a été trouvée", ErrorCodes.NOTIFICATION_NOT_FOUND));

        Optional<User> users = userRepository.findByNotificationId(notificationId);
        if (users.isPresent()) {
            log.error("Impossible de supprimer la notification avec l'ID {} car il est référencé par un user", notificationId);
            throw new InvalidOperationException("Impossible de supprimer la notification car il est référencé par un user",
                    ErrorCodes.NOTIFICATION_ALREADY_USED);
        }

        notificationRepository.delete(notification);
    }

    @Override
    public NotificationDto getNotificationById(Integer notificationId) {
        if(notificationId == null){
            log.error("Invalid notification id");
            throw new InvalidEntityException("L'id de la notification est invalide", ErrorCodes.NOTIFICATION_NOT_VALID);
        }

        log.info("Get notification by id : {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new EntityNotFoundException("Aucune notification avec l'id " + notificationId + " n'a été trouvée", ErrorCodes.NOTIFICATION_NOT_FOUND));

        return NotificationDto.fromEntity(notification);
    }

    @Override
    public Page<NotificationDto> getUserNotifications(Integer userId, Pageable pageable) {
        if(userId == null){
            log.error("Invalid user id");
            throw new InvalidEntityException("L'id de l'utilisateur est invalide", ErrorCodes.USER_NOT_VALID);
        }

        log.info("Get user notifications : {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur avec l'id " + userId + " n'a été trouvé", ErrorCodes.USER_NOT_FOUND));

        return notificationRepository.findByUser(user, pageable).map(NotificationDto::fromEntity);
    }

    @Override
    public Page<NotificationDto> getUnreadNotifications(Integer userId, Pageable pageable) {
        if(userId == null){
            log.error("Invalid user id");
            throw new InvalidEntityException("L'id de l'utilisateur est invalide", ErrorCodes.USER_NOT_VALID);
        }

        log.info("Get unread notifications : {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur avec l'id " + userId + " n'a été trouvé", ErrorCodes.USER_NOT_FOUND));

        return notificationRepository.findByUserAndIsReadFalse(user, pageable).map(NotificationDto::fromEntity);
    }

    @Override
    public Page<NotificationDto> getNotificationsByType(Integer userId, NotifType type, Pageable pageable) {
        if(userId == null || type == null){
            log.error("Invalid user id or notification type");
            throw new InvalidEntityException("L'id de l'utilisateur ou le type de notification est invalide", ErrorCodes.USER_NOT_VALID);
        }

        log.info("Get notifications by user {} and type : {}", userId, type);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur avec l'id " + userId + " n'a été trouvé", ErrorCodes.USER_NOT_FOUND));

        return notificationRepository.findByUserAndType(user, type, pageable).map(NotificationDto::fromEntity);
    }

    @Override
    public NotificationDto markAsRead(Integer notificationId) {
        if(notificationId == null){
            log.error("Invalid notification id");
            throw new InvalidEntityException("L'id de la notification est invalide", ErrorCodes.NOTIFICATION_NOT_VALID);
        }

        log.info("Mark notification as read : {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new EntityNotFoundException("Aucune notification avec l'id " + notificationId + " n'a été trouvée", ErrorCodes.NOTIFICATION_NOT_FOUND));

        notification.setIsRead(true);

        return NotificationDto.fromEntity(notificationRepository.save(notification));
    }

    @Override
    public void markAllAsRead(Integer userId) {
        if(userId == null){
            log.error("Invalid user id");
            throw new InvalidEntityException("L'id de l'utilisateur est invalide", ErrorCodes.USER_NOT_VALID);
        }

        log.info("Mark all notifications as read for user : {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur avec l'id " + userId + " n'a été trouvé", ErrorCodes.USER_NOT_FOUND));

        notificationRepository.markAllNotificationsAsRead(user);
    }

    @Override
    public Page<NotificationDto> getNotificationHistory(Integer userId, Instant startDate, Instant endDate, Pageable pageable) {
        if(userId == null || startDate == null || endDate == null){
            log.error("Invalid user id or date");
            throw new InvalidEntityException("L'id de l'utilisateur ou la date est invalide", ErrorCodes.NOTIFICATION_NOT_VALID);
        }

        log.info("Get notification history for user {} between {} and {}", userId, startDate, endDate);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur avec l'id " + userId + " n'a été trouvé", ErrorCodes.USER_NOT_FOUND));

        return notificationRepository.findByUserAndCreationDateBetween(user, startDate, endDate, pageable).map(NotificationDto::fromEntity);
    }

    @Override
    public Integer getUnreadCount(Integer userId) {
        if(userId == null){
            log.error("Invalid user id");
            throw new InvalidEntityException("L'id de l'utilisateur est invalide", ErrorCodes.USER_NOT_VALID);
        }

        log.info("Get unread count for user : {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur avec l'id " + userId + " n'a été trouvé", ErrorCodes.USER_NOT_FOUND));

        return notificationRepository.countByUserAndIsReadFalse(user).intValue();
    }
}
