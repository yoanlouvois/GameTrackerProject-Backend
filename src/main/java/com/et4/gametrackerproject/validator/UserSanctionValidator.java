package com.et4.gametrackerproject.validator;

import com.et4.gametrackerproject.dto.UserSanctionDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UserSanctionValidator {
    private static final int MIN_REASON_LENGTH = 5;
    private static final int MAX_REASON_LENGTH = 500;

    public static List<String> validate(UserSanctionDto userSanction) {
        List<String> errors = new ArrayList<>();

        if (userSanction == null) {
            errors.add("User is required");
            errors.add("Admin is required");
            errors.add("Type is required");
            errors.add("Reason is required");
            errors.add("Start date is required");
            errors.add("End date is required");
            return errors;
        }

        if (userSanction.getUser() == null){
            errors.add("User is required");
        } else {
            List<String> userErrors = UserValidator.validate(userSanction.getUser());
            if (!userErrors.isEmpty()) {
                errors.add("User validation errors: " + String.join(", ", userErrors));
            }
        }

        if (userSanction.getAdmin() == null){
            errors.add("Admin is required");
        } else {
            List<String> adminErrors = UserValidator.validate(userSanction.getAdmin());
            if (!adminErrors.isEmpty()) {
                errors.add("Admin validation errors: " + String.join(", ", adminErrors));
            }
        }

        if (userSanction.getType() == null) {
            errors.add("Type is required");
        }

        if (!StringUtils.hasLength(userSanction.getReason())) {
            errors.add("Reason is required");
        } else if (userSanction.getReason().length() < MIN_REASON_LENGTH) {
            errors.add("Reason must be at least " + MIN_REASON_LENGTH + " characters");
        } else if (userSanction.getReason().length() > MAX_REASON_LENGTH) {
            errors.add("Reason cannot exceed " + MAX_REASON_LENGTH + " characters");
        }

        if (userSanction.getStartDate() == null) {
            errors.add("Start date is required");
        }

        if (userSanction.getEndDate() == null) {
            errors.add("End date is required");
        }

        if (userSanction.getStartDate() != null && userSanction.getEndDate() != null) {
            if (userSanction.getStartDate().isAfter(userSanction.getEndDate())) {
                errors.add("Start date must be before end date");
            }
        }

        return errors;
    }
}
