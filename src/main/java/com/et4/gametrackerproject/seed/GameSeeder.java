package com.et4.gametrackerproject.seed;

import com.et4.gametrackerproject.dto.GameDto;
import com.et4.gametrackerproject.enums.DifficultyLevel;
import com.et4.gametrackerproject.enums.GameCategory;
import com.et4.gametrackerproject.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GameSeeder {

    private final GameService gameService;

    public void seedGames() {
        List<GameDto> games = List.of(
                GameDto.builder()
                        .name("Chess Grandmaster")
                        .imageUrl("https://res.cloudinary.com/dhaektb1h/image/upload/v1743654536/22326cb2-1ac9-4d30-90df-c3ab4af25c89.png")
                        .url("https://html5.gamedistribution.com/1b52d7c4cad04672891412279ebbbe5d/?")
                        .description("Mettez vos compétences d'échecs à l'épreuve contre les meilleurs joueurs virtuels ! Dans ce jeu d'échecs classique, utilisez vos connaissances des ouvertures, des tactiques et des stratégies pour battre vos adversaires.")
                        .category(GameCategory.BOARD)
                        .difficultyLevel(DifficultyLevel.MEDIUM)
                        .isActive(true)
                        .playCount(0)
                        .averageRating(0.0)
                        .minAge(3)
                        .build(),

                GameDto.builder()
                        .name("Vex X3M 3")
                        .imageUrl("/images/games/vex-x3m-3.png")
                        .url("https://html5.gamedistribution.com/ce1b2e5eabe44debb11bf77cd04c0f85/")
                        .description("Jeu d'action et de plateformes avec obstacles, vitesse et réflexes.")
                        .category(GameCategory.ACTION)
                        .difficultyLevel(DifficultyLevel.HARD)
                        .isActive(true)
                        .playCount(0)
                        .averageRating(0.0)
                        .minAge(7)
                        .build(),

                GameDto.builder()
                        .name("Office Solitaire")
                        .imageUrl("/images/games/office-solitaire.png")
                        .url("https://html5.gamedistribution.com/ab572c1e1dc04e4499da4db26e768286/")
                        .description("Jeu de cartes solitaire simple et relaxant dans une ambiance bureau.")
                        .category(GameCategory.CARD)
                        .difficultyLevel(DifficultyLevel.EASY)
                        .isActive(true)
                        .playCount(0)
                        .averageRating(0.0)
                        .minAge(3)
                        .build(),

                GameDto.builder()
                        .name("Grand Mahjong Connect")
                        .imageUrl("/images/games/grand-mahjong-connect.png")
                        .url("https://html5.gamedistribution.com/92211d47448849d189bbc32d0d4d8f6f/")
                        .description("Jeu de réflexion où il faut connecter les tuiles identiques pour vider le plateau.")
                        .category(GameCategory.BOARD)
                        .difficultyLevel(DifficultyLevel.MEDIUM)
                        .isActive(true)
                        .playCount(0)
                        .averageRating(0.0)
                        .minAge(3)
                        .build(),

                GameDto.builder()
                        .name("Jungle Match Adventures")
                        .imageUrl("/images/games/jungle-match-adventures.png")
                        .url("https://html5.gamedistribution.com/c6d324c4729f48ef9a3cd8882bdf078e/")
                        .description("Jeu d'association coloré dans un univers jungle avec niveaux progressifs.")
                        .category(GameCategory.ARCADE)
                        .difficultyLevel(DifficultyLevel.EASY)
                        .isActive(true)
                        .playCount(0)
                        .averageRating(0.0)
                        .minAge(3)
                        .build(),

                GameDto.builder()
                        .name("Fruit Match")
                        .imageUrl("/images/games/fruit-match.png")
                        .url("https://html5.gamedistribution.com/4254dba037de4b798541d2c97eae5016/")
                        .description("Jeu de match de fruits rapide et accessible avec combos et objectifs.")
                        .category(GameCategory.ARCADE)
                        .difficultyLevel(DifficultyLevel.EASY)
                        .isActive(true)
                        .playCount(0)
                        .averageRating(0.0)
                        .minAge(3)
                        .build(),

                GameDto.builder()
                        .name("Float for Brainrots")
                        .imageUrl("/images/games/float-for-brainrots.png")
                        .url("https://html5.gamedistribution.com/a2b83c1a317c4eb0bffcc4ffcece5ef9/")
                        .description("Jeu arcade décalé où il faut survivre et progresser en flottant.")
                        .category(GameCategory.ARCADE)
                        .difficultyLevel(DifficultyLevel.MEDIUM)
                        .isActive(true)
                        .playCount(0)
                        .averageRating(0.0)
                        .minAge(7)
                        .build(),

                GameDto.builder()
                        .name("Live 100 Days")
                        .imageUrl("/images/games/live-100-days.png")
                        .url("https://html5.gamedistribution.com/8a7fdc0a68ad4311a72557de8368b8cd/")
                        .description("Jeu de survie et de stratégie où l'objectif est de tenir le plus longtemps possible.")
                        .category(GameCategory.STRATEGY)
                        .difficultyLevel(DifficultyLevel.MEDIUM)
                        .isActive(true)
                        .playCount(0)
                        .averageRating(0.0)
                        .minAge(7)
                        .build(),

                GameDto.builder()
                        .name("Stickman Adventure")
                        .imageUrl("/images/games/stickman-adventure.png")
                        .url("https://html5.gamedistribution.com/fd5ae555f42e4dac872819ed9125616c/")
                        .description("Jeu d'aventure avec un stickman, des obstacles et des niveaux à compléter.")
                        .category(GameCategory.ADVENTURE)
                        .difficultyLevel(DifficultyLevel.MEDIUM)
                        .isActive(true)
                        .playCount(0)
                        .averageRating(0.0)
                        .minAge(7)
                        .build(),

                GameDto.builder()
                        .name("Juice Merge")
                        .imageUrl("/images/games/juice-merge.png")
                        .url("https://html5.gamedistribution.com/d4c11a64ed754d71b4671d699c66a9c7/")
                        .description("Jeu de fusion arcade où il faut combiner des éléments pour obtenir de meilleurs scores.")
                        .category(GameCategory.ARCADE)
                        .difficultyLevel(DifficultyLevel.EASY)
                        .isActive(true)
                        .playCount(0)
                        .averageRating(0.0)
                        .minAge(3)
                        .build()
        );

        games.forEach(game -> {
            if (gameService.getGameByUrl(game.getUrl()).isEmpty()) {
                gameService.createGame(game);
            }
        });
    }
}