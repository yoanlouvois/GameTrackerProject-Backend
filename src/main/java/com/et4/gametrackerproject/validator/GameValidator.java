package com.et4.gametrackerproject.validator;

import com.et4.gametrackerproject.dto.*;
import com.et4.gametrackerproject.enums.DifficultyLevel;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameValidator {

    public static List<String> validate(GameDto gameDto) {
        List<String> errors = new ArrayList<>();

        if (gameDto == null) {
            errors.add("Name is required");
            errors.add("URL is required");
            errors.add("Category is required");
            errors.add("Image Url is required");
            return errors;
        }

        if (gameDto.getName() == null || gameDto.getName().isBlank()) {
            errors.add("Name is required");
        } else if (gameDto.getName().length() > 100) { // Limite arbitraire
            errors.add("Name cannot exceed 100 characters");
        }

        if(!StringUtils.hasLength(gameDto.getUrl())) {
            errors.add("URL is required");
        }
        else {
            // TODO : Ajouter une validation de l'URL
            if (!gameDto.getUrl().startsWith("http://") && !gameDto.getUrl().startsWith("https://")) {
                errors.add("URL must start with http:// or https://");
            }
            if (gameDto.getUrl().length() > 255) { // Limite à vérifier
                errors.add("URL cannot exceed 255 characters");
            }
        }

        if(gameDto.getImageUrl() == null || gameDto.getImageUrl().isBlank()) {
            errors.add("Image Url is required");
        }

        if(gameDto.getImageUrl() != null && !gameDto.getImageUrl().isBlank()) {
            if (!gameDto.getImageUrl().startsWith("http://") && !gameDto.getImageUrl().startsWith("https://")) {
                errors.add("Image URL must start with http:// or https://");
            }
            if (gameDto.getImageUrl().length() > 255) { // Limite à vérifier
                errors.add("Image URL cannot exceed 255 characters");
            }
        }

        if (gameDto.getDescription() != null && gameDto.getDescription().length() > 1000) { // Limite arbitraire
            errors.add("Description cannot exceed 1000 characters");
        }
        if (gameDto.getCategory() == null) {
            errors.add("Category is required");
        }

        if (gameDto.getDifficultyLevel() == null) {
            gameDto.setDifficultyLevel(DifficultyLevel.MEDIUM);
        }

        if (gameDto.getMinAge() != null && gameDto.getMinAge() < 0) {
            errors.add("Minimum age cannot be negative");
        }

        if (gameDto.getAverageRating() != null) {
            if (gameDto.getAverageRating() < 0 || gameDto.getAverageRating() > 5) {
                errors.add("Average rating must be between 0 and 5");
            }
        }

        if(gameDto.getPlayCount() == null){
            gameDto.setPlayCount(0);
        }
        else if (gameDto.getPlayCount() < 0) {
            errors.add("Play count cannot be negative");
        }

        if (gameDto.getIsActive() == null) {
            gameDto.setIsActive(true);
        }

        if (gameDto.getFavoriteGames() == null) {
            gameDto.setFavoriteGames(Set.of());
        } else {
            for (FavoriteGameDto favorite : gameDto.getFavoriteGames()) {
                List<String> favoriteErrors = FavoriteGameValidator.validate(favorite);
                if (!favoriteErrors.isEmpty()) {
                    errors.add("Favorite game validation errors: " + String.join(", ", favoriteErrors));
                }
            }
        }

        if (gameDto.getComments() == null) {
            gameDto.setComments(Set.of());
        } else {
            for (GameCommentDto comment : gameDto.getComments()) {
                List<String> commentErrors = GameCommentValidator.validate(comment);
                if (!commentErrors.isEmpty()) {
                    errors.add("Comment validation errors: " + String.join(", ", commentErrors));
                }
            }
        }

        if (gameDto.getLeaderboardEntries() == null) {
            gameDto.setLeaderboardEntries(Set.of());
        } else {
            for (GameLeaderboardDto entry : gameDto.getLeaderboardEntries()) {
                List<String> leaderboardErrors = GameLeaderboardValidator.validate(entry);
                if (!leaderboardErrors.isEmpty()) {
                    errors.add("Leaderboard entry validation errors: " + String.join(", ", leaderboardErrors));
                }
            }
        }

        if (gameDto.getProgressions() == null) {
            gameDto.setProgressions(Set.of());
        } else {
            for (GameProgressDto progress : gameDto.getProgressions()) {
                List<String> progressErrors = GameProgressValidator.validate(progress);
                if (!progressErrors.isEmpty()) {
                    errors.add("Game progress validation errors: " + String.join(", ", progressErrors));
                }
            }
        }

        if (gameDto.getRatings() == null) {
            gameDto.setRatings(Set.of());
        } else {
            for (GameRatingDto rating : gameDto.getRatings()) {
                List<String> ratingErrors = GameRatingValidator.validate(rating);
                if (!ratingErrors.isEmpty()) {
                    errors.add("Game rating validation errors: " + String.join(", ", ratingErrors));
                }
            }
        }

        if (gameDto.getRecommendations() == null) {
            gameDto.setRecommendations(Set.of());
        } else {
            for (GameRecommendationDto recommendation : gameDto.getRecommendations()) {
                List<String> recommendationErrors = GameRecommendationValidator.validate(recommendation);
                if (!recommendationErrors.isEmpty()) {
                    errors.add("Game recommendation validation errors: " + String.join(", ", recommendationErrors));
                }
            }
        }

        if (gameDto.getTags() == null) {
            gameDto.setTags(Set.of());
        } else {
            for (GameTagDto tag : gameDto.getTags()) {
                List<String> tagErrors = GameTagValidator.validate(tag);
                if (!tagErrors.isEmpty()) {
                    errors.add("Game tag validation errors: " + String.join(", ", tagErrors));
                }
            }
        }

        if (gameDto.getWinStreaks() == null) {
            gameDto.setWinStreaks(Set.of());
        } else {
            for (WinStreakDto streak : gameDto.getWinStreaks()) {
                List<String> streakErrors = WinStreakValidator.validate(streak);
                if (!streakErrors.isEmpty()) {
                    errors.add("Win streak validation errors: " + String.join(", ", streakErrors));
                }
            }
        }

        return errors;
    }
}
