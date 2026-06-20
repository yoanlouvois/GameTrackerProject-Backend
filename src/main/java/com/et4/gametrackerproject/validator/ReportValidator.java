package com.et4.gametrackerproject.validator;

import com.et4.gametrackerproject.dto.ReportDto;
import com.et4.gametrackerproject.enums.ReportStatus;
import com.et4.gametrackerproject.enums.ReportType;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ReportValidator {

    public static List<String> validate(ReportDto report) {
        List<String> errors = new ArrayList<>();

        if (report == null) {
            errors.add("Reporter is required");
            errors.add("Reported user is required");
            errors.add("Report type is required");
            errors.add("Content ID is required");
            errors.add("Report reason is required");
            errors.add("Report status is required");
            return errors;
        }

        if (report.getReporter() == null) {
            errors.add("Reporter is required");
        } else {
            List<String> reporterErrors = UserValidator.validate(report.getReporter());
            if (!reporterErrors.isEmpty()) {
                errors.add("Reporter validation errors: " + String.join(", ", reporterErrors));
            }
        }

        if (report.getReported() == null) {
            errors.add("Reported user is required");
        } else {
            List<String> reportedErrors = UserValidator.validate(report.getReported());
            if (!reportedErrors.isEmpty()) {
                errors.add("Reported user validation errors: " + String.join(", ", reportedErrors));
            }
        }

        if (report.getType() == null) {
            errors.add("Report type is required");
        }

        if (report.getContentId() == null) {
            errors.add("Content ID is required");
        }

        if (!StringUtils.hasLength(report.getReason())) {
            errors.add("Report reason is required");
        } else if (report.getReason().length() < 10) { // Limite arbitraire
            errors.add("Report reason must be at least 10 characters");
        } else if (report.getReason().length() > 500) { // Limite arbitraire
            errors.add("Report reason cannot exceed 500 characters");
        }

        if (report.getStatus() == null) {
            errors.add("Report status is required");
        }

        if (report.getStatus() == ReportStatus.RESOLVED || report.getStatus() == ReportStatus.DISMISSED) {
            if (report.getResolver() == null) {
                errors.add("Resolver is required for resolved or rejected reports");
            } else {
                List<String> resolverErrors = UserValidator.validate(report.getResolver());
                if (!resolverErrors.isEmpty()) {
                    errors.add("Resolver validation errors: " + String.join(", ", resolverErrors));
                }
            }

            if (report.getResolvedAt() == null) {
                errors.add("Resolution date is required for resolved or rejected reports");
            } else if (report.getResolvedAt().isAfter(Instant.now())) {
                errors.add("Resolution date cannot be in the future");
            } else if (report.getCreationDate() != null &&
                    report.getResolvedAt().isBefore(report.getCreationDate())) {
                errors.add("Resolution date cannot be before creation date");
            }
        } else {
            if (report.getResolver() != null) {
                errors.add("Resolver should be null for non-resolved reports");
            }

            if (report.getResolvedAt() != null) {
                errors.add("Resolution date should be null for non-resolved reports");
            }
        }

        if (report.getReporter() != null &&
                report.getReported() != null &&
                report.getReporter().getId() != null &&
                report.getReporter().getId().equals(report.getReported().getId())) {
            errors.add("User cannot report themselves");
        }

        return errors;
    }
}
