package com.et4.gametrackerproject.controller.api;

import com.et4.gametrackerproject.dto.GameDto;
import com.et4.gametrackerproject.enums.DifficultyLevel;
import com.et4.gametrackerproject.enums.GameCategory;
import com.et4.gametrackerproject.model.Game;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.et4.gametrackerproject.utils.Constants.APP_ROOT;

public interface GameApi {

    // Opérations de base

    @PostMapping(value = APP_ROOT + "/game/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un jeu",description = "Créer un jeu")
    @ApiResponse(responseCode = "200", description = "Jeu créé")
    GameDto createGame(@RequestBody GameDto gameDto);

    @PutMapping(value = APP_ROOT + "/game/{gameId}/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un jeu",description = "Mettre à jour un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jeu mis à jour"),
            @ApiResponse(responseCode = "404", description = "Jeu non trouvé")
    })
    GameDto updateGame(@PathVariable("gameId") Integer id,@RequestBody GameDto gameDto);

    @DeleteMapping(value = APP_ROOT + "/game/delete/{gameId}")
    @Operation(summary = "Supprimer un jeu",description = "Supprimer un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jeu supprimé"),
            @ApiResponse(responseCode = "404", description = "Jeu non trouvé")
    })
    void deleteGame(@PathVariable("gameId") Integer id);

    @GetMapping(value = APP_ROOT + "/game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer un jeu par son ID",description = "Récupérer un jeu par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jeu trouvé"),
            @ApiResponse(responseCode = "404", description = "Jeu non trouvé")
    })
    GameDto getGameById(@PathVariable("gameId") Integer id);

    @GetMapping(value = APP_ROOT + "/game/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer tous les jeux",description = "Récupérer tous les jeux")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    Page<GameDto> getAllGames(Pageable pageable);

    @GetMapping(value = APP_ROOT + "/game/search/{query}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des jeux par nom",description = "Rechercher des jeux par nom")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    Page<GameDto> searchGames(@PathVariable String query, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/game/{category}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Filtrer les jeux par catégorie",description = "Filtrer les jeux par catégorie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    Page<GameDto> filterByCategory( @PathVariable GameCategory category, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/game/{difficulty}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Filtrer les jeux par niveau de difficulté",description = "Filtrer les jeux par niveau de difficulté")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    Page<GameDto> filterByDifficulty( @PathVariable DifficultyLevel difficulty, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/game/{minAge}-{maxAge} ans", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Filtrer les jeux par tranche d'âge",description = "Filtrer les jeux par tranche d'âge")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    Page<GameDto> filterByAgeRange( @PathVariable Integer minAge,@PathVariable Integer maxAge, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/game/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Filtrer les jeux par tags",description = "Filtrer les jeux par tags")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    Page<GameDto> filterByTags(@RequestBody Set<String> tags, Pageable pageable);


    // Recherche d'un jeu par son URL
    @GetMapping(value = APP_ROOT + "/game/url/{url}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer un jeu par son URL",description = "Récupérer un jeu par son URL")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jeu trouvé"),
            @ApiResponse(responseCode = "404", description = "Jeu non trouvé")
    })
    Optional<Game> getGameByUrl(@PathVariable String url);

    @GetMapping(value = APP_ROOT + "/game/image/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer l'URL de l'image d'un jeu par son ID",description = "Récupérer l'URL de l'image d'un jeu par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "URL de l'image trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune image trouvée")
    })
    String getImageUrlByID(@PathVariable Integer id);

    // Recherche des jeux par nom exact
    @GetMapping(value = APP_ROOT + "/game/name/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer des jeux par nom",description = "Récupérer des jeux par nom")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    List<Game> getGamesByName(@PathVariable String name);

    // Recherche des jeux actifs avec pagination
    @GetMapping(value = APP_ROOT + "/game/active", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer des jeux actifs",description = "Récupérer des jeux actifs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux actifs trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu actif trouvé")
    })
    Page<Game> getGamesByIsActive(Boolean isActive, Pageable pageable);

    // Recherche des jeux par catégorie et niveau de difficulté
    @GetMapping(value = APP_ROOT + "/game/category/difficulty", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer des jeux par catégorie et niveau de difficulté",description = "Récupérer des jeux par catégorie et niveau de difficulté")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    List<Game> getGamesByCategoryAndDifficulty(GameCategory category, DifficultyLevel difficultyLevel);

    // Recherche des jeux ayant une note supérieure ou égale à minRating
    @GetMapping(value = APP_ROOT + "/game/rating", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer des jeux par note",description = "Récupérer des jeux par note")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    List<Game> getHighlyRatedGames(Double minRating);

    // Recherche des jeux les plus populaires (triés par playCount décroissant)
    @GetMapping(value = APP_ROOT + "/game/popular", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les jeux les plus populaires",description = "Récupérer les jeux les plus populaires")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux populaires trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu populaire trouvé")
    })
    List<Game> getMostPopularGames(Pageable pageable);

    // Recherche des jeux accessibles pour un âge donné (minAge <= age)
    @GetMapping(value = APP_ROOT + "/game/<={age} ans", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer des jeux accessibles pour un âge donné",description = "Récupérer des jeux accessibles pour un âge donné")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    List<Game> getGamesByMinAgeLessThanEqual(@PathVariable Integer age);

    // Recherche combinée avec plusieurs filtres
    @GetMapping(value = APP_ROOT + "/game/filters", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer des jeux avec plusieurs filtres",description = "Récupérer des jeux avec plusieurs filtres")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    Page<Game> getGamesWithFilters(String name, GameCategory category, DifficultyLevel difficulty,
                                   Double minRating, Integer minAge, Pageable pageable);

    // Recherche des jeux les plus récents
    @GetMapping(value = APP_ROOT + "/game/newest", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les jeux les plus récents",description = "Récupérer les jeux les plus récents")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux récents trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu récent trouvé")
    })
    List<Game> getNewestGames(Pageable pageable);

    // Recherche des jeux populaires par catégorie
    @GetMapping(value = APP_ROOT + "/game/popular/category", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les jeux populaires par catégorie",description = "Récupérer les jeux populaires par catégorie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des jeux populaires par catégorie trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu populaire par catégorie trouvé")
    })
    List<Game> getMostPopularGamesByCategory(GameCategory category, Pageable pageable);

}
