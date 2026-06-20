package com.et4.gametrackerproject.validator;

import com.et4.gametrackerproject.dto.NotificationDto;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class NotificationValidator {
    public static List<String> validate(NotificationDto notification) {
        List<String> errors = new ArrayList<>();

        if (notification == null) {
            errors.add("User is required");
            errors.add("Notification type is required");
            errors.add("Notification content is required");
            return errors;
        }

        if (notification.getUser() == null) {
            errors.add("User is required");
        } else {
            List<String> userErrors = UserValidator.validate(notification.getUser());
            if (!userErrors.isEmpty()) {
                errors.add("User validation errors: " + String.join(", ", userErrors));
            }
        }

        if (notification.getType() == null) {
            errors.add("Notification type is required");
        }

        if (!StringUtils.hasLength(notification.getContent())) {
            errors.add("Notification content is required");
        } else if (notification.getContent().length() > 500) { // Limite arbitraire
            errors.add("Notification content cannot exceed 500 characters");
        }

        if(notification.getIsRead() == null){
            notification.setIsRead(false);
        }

        return errors;
    }
}
