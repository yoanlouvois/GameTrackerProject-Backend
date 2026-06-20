package com.et4.gametrackerproject.controller.api;

import com.et4.gametrackerproject.dto.GameRatingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

import static com.et4.gametrackerproject.utils.Constants.APP_ROOT;

public interface GameRatingApi {

    //Opérations de base

    @PostMapping(value = APP_ROOT + "/rating/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Ajouter une évaluation", description = "Ajouter une évaluation")
    @ApiResponse(responseCode = "200", description = "Évaluation ajoutée")
    GameRatingDto submitRating(@RequestBody GameRatingDto ratingDto);

    @PutMapping(value = APP_ROOT + "/rating/{ratingId}/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour une évaluation", description = "Mettre à jour une évaluation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "évaluation mise à jour"),
            @ApiResponse(responseCode = "404", description = "évaluation non trouvée")
    })
    GameRatingDto updateRating(@PathVariable Integer ratingId,@RequestBody Integer newRating);

    @DeleteMapping(value = APP_ROOT + "/rating/{ratingId}")
    @Operation(summary = "Supprimer une évaluation", description = "Supprimer une évaluation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "évaluation supprimée"),
            @ApiResponse(responseCode = "404", description = "évaluation non trouvée")
    })
    void deleteRating(@PathVariable Integer ratingId);

    //Récupération des évaluations

    @GetMapping(value = APP_ROOT + "/rating/{ratingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer une évaluation par son ID", description = "Récupérer une évaluation par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "évaluation trouvée"),
            @ApiResponse(responseCode = "404", description = "évaluation non trouvée")
    })
    GameRatingDto getRatingById(@PathVariable Integer ratingId);

    @GetMapping(value = APP_ROOT + "/rating/user/{userId}/game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer l'évaluation d'un utilisateur pour un jeu", description = "Récupérer l'évaluation d'un utilisateur pour un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "évaluation trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune évaluation trouvée pour cet utilisateur et ce jeu")
    })
    GameRatingDto getUserRatingForGame(@PathVariable Integer userId,@PathVariable Integer gameId);

    @GetMapping(value = APP_ROOT + "/rating/game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les évaluations d'un jeu", description = "Récupérer les évaluations d'un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "évaluations trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune évaluation trouvée pour ce jeu")
    })
    Page<GameRatingDto> getRatingsForGame(@PathVariable Integer gameId, Pageable pageable);

    //Statistiques

    @GetMapping(value = APP_ROOT + "/rating/game/{gameId}/average", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Calculer la note moyenne d'un jeu", description = "Calculer la note moyenne d'un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "note moyenne calculée"),
            @ApiResponse(responseCode = "404", description = "Aucune évaluation trouvée pour ce jeu")
    })
    Double calculateAverageRatingForGame(@PathVariable Integer gameId);

    @GetMapping(value = APP_ROOT + "/rating/game/{gameId}/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Compter le nombre d'évaluations d'un jeu", description = "Compter le nombre d'évaluations d'un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "nombre d'évaluations compté"),
            @ApiResponse(responseCode = "404", description = "Aucune évaluation trouvée pour ce jeu")
    })
    Long countRatingsForGame(@PathVariable Integer gameId);

    @GetMapping(value = APP_ROOT + "/rating/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les évaluations récentes", description = "Récupérer les évaluations récentes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "évaluations récentes trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune évaluation trouvée")
    })
    Page<GameRatingDto> getRecentRatings(Pageable pageable);

    @GetMapping(value = APP_ROOT + "/rating/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des évaluations", description = "Rechercher des évaluations")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "évaluations trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune évaluation trouvée")
    })
    Page<GameRatingDto> searchRatings(@RequestBody String searchQuery, Pageable pageable);

    //Analyse

    @GetMapping(value = APP_ROOT + "/rating/top/{limit}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les jeux les mieux notés", description = "Récupérer les jeux les mieux notés")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "jeux les mieux notés trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    Map<Integer, Long> getTopRatedGames(@PathVariable int limit);

    @GetMapping(value = APP_ROOT + "/rating/date/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les évaluations par date", description = "Récupérer les évaluations par date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "évaluations trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune évaluation trouvée pour cette date")
    })
    GameRatingDto getRatingByDate(@PathVariable Instant date);
}
