package com.et4.gametrackerproject.validator;

import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.enums.OnlineStatus;
import com.et4.gametrackerproject.enums.PrivacySetting;
import com.et4.gametrackerproject.enums.ScreenTheme;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.regex.Pattern;

public class UserValidator {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 30;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 100;
    private static final int MIN_AGE_YEARS = 13;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_-]+$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    public static List<String> validate(UserDto user) {
        List<String> errors = new ArrayList<>();

        if (user == null) {
            errors.add("Username is required");
            errors.add("Email is required");
            return errors;
        }

        if (!StringUtils.hasLength(user.getUsername())) {
            errors.add("Username is required");
        } else if (user.getUsername().length() < MIN_USERNAME_LENGTH) {
            errors.add("Username must be at least " + MIN_USERNAME_LENGTH + " characters");
        } else if (user.getUsername().length() > MAX_USERNAME_LENGTH) {
            errors.add("Username cannot exceed " + MAX_USERNAME_LENGTH + " characters");
        } else if (!USERNAME_PATTERN.matcher(user.getUsername()).matches()) {
            errors.add("Username can only contain letters, numbers, underscores and hyphens");
        }

        if (!StringUtils.hasLength(user.getEmail())) {
            errors.add("Email is required");
        } else if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            errors.add("Email format is invalid");
        }

        if (user.getId() == null && user.getPassword() == null) {
            errors.add("Password is required");
        } else if (user.getPassword() != null) {
            if (!StringUtils.hasLength(user.getPassword())) {
                errors.add("Password cannot be empty");
            } else if (user.getPassword().length() < MIN_PASSWORD_LENGTH) {
                errors.add("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
            } else if (user.getPassword().length() > MAX_PASSWORD_LENGTH) {
                errors.add("Password cannot exceed " + MAX_PASSWORD_LENGTH + " characters");
            } else if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
                errors.add("Password must contain at least one digit, one lowercase letter, " +
                        "one uppercase letter, one special character, and no whitespace");
            }
        }

        if (user.getBirthDate() != null) {
            LocalDate now = LocalDate.now();
            if (user.getBirthDate().isAfter(now)) {
                errors.add("Birth date cannot be in the future");
            } else {
                Period age = Period.between(user.getBirthDate(), now);
                if (age.getYears() < MIN_AGE_YEARS) {
                    errors.add("User must be at least " + MIN_AGE_YEARS + " years old");
                }
            }
        }

        if (user.getAvatar() != null) {
            List<String> avatarErrors = AvatarValidator.validate(user.getAvatar());
            if (!avatarErrors.isEmpty()) {
                errors.add("Avatar validation errors: " + String.join(", ", avatarErrors));
            }
        }

        if (user.getCountry() != null && !user.getCountry().trim().isEmpty()) {
            // TODO : Ajouter une validation du pays
        }

        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        if (user.getIsAdmin() == null) {
            user.setIsAdmin(false);
        }
        if (user.getPrivacySetting() == null) {
            user.setPrivacySetting(PrivacySetting.PUBLIC);
        }
        if (user.getTotalGamesPlayed() == null) {
            user.setTotalGamesPlayed(0);
        } else if (user.getTotalGamesPlayed() < 0) {
            errors.add("Total games played cannot be negative");
        }
        if (user.getTotalPlayTime() == null) {
            user.setTotalPlayTime(0);
        } else if (user.getTotalPlayTime() < 0) {
            errors.add("Total play time cannot be negative");
        }
        if (user.getPoints() == null) {
            user.setPoints(0);
        } else if (user.getPoints() < 0) {
            errors.add("Points cannot be negative");
        }
        if (user.getThemePreference() == null) {
            user.setThemePreference(ScreenTheme.LIGHT);
        }


        if (user.getLastLogin() != null) {
            if (user.getLastLogin().isAfter(Instant.now())) {
                errors.add("Last login date cannot be in the future");
            }

            if (user.getCreationDate() != null &&
                    user.getLastLogin().isBefore(user.getCreationDate())) {
                errors.add("Last login date cannot be before creation date");
            }
        }

        if (user.getOnlineStatus() == null) {
            user.setOnlineStatus(OnlineStatus.OFFLINE);
        } else if (user.getOnlineStatus() == OnlineStatus.ONLINE &&
                (user.getLastLogin() == null ||
                        Duration.between(user.getLastLogin(), Instant.now()).toHours() > 24)) {
            errors.add("Online status and last login date are inconsistent");
        }

        if (user.getUserRank() == null) {
            // TODO : Ajouter logique de ranking
        }

        if (user.getDailyGameSessions() == null) {
            user.setDailyGameSessions(new HashSet<>());
        }
        if (user.getFavoriteGames() == null){
            user.setFavoriteGames(new HashSet<>());
        }
        if (user.getFriendshipsInitiated() == null) {
            user.setFriendshipsInitiated(new HashSet<>());
        }
        if (user.getFriendshipsReceived() == null) {
            user.setFriendshipsReceived(new HashSet<>());
        }
        if (user.getComments() == null){
            user.setComments(new HashSet<>());
        }
        if (user.getLikes() == null){
            user.setLikes(new HashSet<>());
        }
        if (user.getLeaderboardsLines() == null){
            user.setLeaderboardsLines(new HashSet<>());
        }
        if (user.getProgressions() == null){
            user.setProgressions(new HashSet<>());
        }
        if (user.getRatings() == null) {
            user.setRatings(new HashSet<>());
        }
        if (user.getRecommendationsSent() == null) {
            user.setRecommendationsSent(new HashSet<>());
        }
        if (user.getRecommendationsReceived() == null) {
            user.setRecommendationsReceived(new HashSet<>());
        }
        if (user.getMessagesSent() == null) {
            user.setMessagesSent(new HashSet<>());
        }
        if (user.getMessagesReceived() == null) {
            user.setMessagesReceived(new HashSet<>());
        }
        if (user.getNotifications() == null) {
            user.setNotifications(new HashSet<>());
        }
        if (user.getReportsSent() == null) {
            user.setReportsSent(new HashSet<>());
        }
        if (user.getReportsAgainst() == null) {
            user.setReportsAgainst(new HashSet<>());
        }
        if (user.getReportsResolved() == null) {
            user.setReportsResolved(new HashSet<>());
        }
        if (user.getAchievementsEarned() == null) {
            user.setAchievementsEarned(new HashSet<>());
        }
        if (user.getSanctionsReceived() == null) {
            user.setSanctionsReceived(new HashSet<>());
        }
        if (user.getSanctionsDistributed() == null) {
            user.setSanctionsDistributed(new HashSet<>());
        }
        if (user.getWinStreaks() == null) {
            user.setWinStreaks(new HashSet<>());
        }

        return errors;
    }
}
