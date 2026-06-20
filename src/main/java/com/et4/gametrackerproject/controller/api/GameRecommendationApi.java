package com.et4.gametrackerproject.controller.api;

import com.et4.gametrackerproject.dto.GameRecommendationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.et4.gametrackerproject.utils.Constants.APP_ROOT;

public interface GameRecommendationApi {

    //Opérations de base

    @PostMapping(value = APP_ROOT + "/recommendation/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une recommendation", description = "Créer une recommendation")
    @ApiResponse(responseCode = "200", description = "Recommendation créée")
    GameRecommendationDto createRecommendation(@RequestBody Integer senderId,@RequestBody Integer receiverId,@RequestBody Integer gameId,@RequestBody String message);

    @PutMapping(value = APP_ROOT + "/recommendation/{recommendationId}/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour une recommendation", description = "Mettre à jour une recommendation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "recommendation mise à jour"),
            @ApiResponse(responseCode = "404", description = "recommendation non trouvée")
    })
    GameRecommendationDto updateRecommendationMessage(@PathVariable Integer recommendationId,@RequestBody String newMessage);

    @DeleteMapping(value = APP_ROOT + "/recommendation/{recommendationId}")
    @Operation(summary = "Supprimer une recommendation", description = "Supprimer une recommendation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "recommendation supprimée"),
            @ApiResponse(responseCode = "404", description = "recommendation non trouvée")
    })
    void deleteRecommendation(@PathVariable Integer recommendationId);

    @GetMapping(value = APP_ROOT + "/recommendation/{recommendationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer une recommendation par son ID", description = "Récupérer une recommendation par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "recommendation trouvée"),
            @ApiResponse(responseCode = "404", description = "recommendation non trouvée")
    })
    GameRecommendationDto getRecommendationById(@PathVariable Integer recommendationId);

    @GetMapping(value = APP_ROOT + "/recommendation/sender/{senderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les recommendations envoyées par un utilisateur", description = "Récupérer les recommendations envoyées par un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "recommendations trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune recommendation trouvée pour cet utilisateur")
    })
    Page<GameRecommendationDto> getRecommendationsBySender(@PathVariable Integer senderId, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/recommendation/receiver/{receiverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les recommendations reçues par un utilisateur", description = "Récupérer les recommendations reçues par un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "recommendations trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune recommendation trouvée pour cet utilisateur")
    })
    Page<GameRecommendationDto> getRecommendationsByReceiver(@PathVariable Integer receiverId, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/recommendation/users/{user1Id}/{user2Id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les recommendations entre deux utilisateurs", description = "Récupérer les recommendations entre deux utilisateurs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "recommendations trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune recommendation trouvée entre ces deux utilisateurs")
    })
    Page<GameRecommendationDto> getRecommendationsBetweenUsers(@PathVariable Integer user1Id,@PathVariable Integer user2Id, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/recommendation/game/{gameId}/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Compter le nombre de recommendations pour un jeu", description = "Compter le nombre de recommendations pour un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "nombre de recommendations trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucune recommendation trouvée pour ce jeu")
    })
    Long countRecommendationsForGame(@PathVariable Integer gameId);

    @GetMapping(value = APP_ROOT + "/recommendation/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer toutes les recommendations", description = "Récupérer toutes les recommendations")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "recommendations trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune recommendation trouvée")
    })
    Page<GameRecommendationDto> getAllRecommendations(Pageable pageable);

    @GetMapping(value = APP_ROOT + "/recommendation/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des recommendations", description = "Rechercher des recommendations")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "recommendations trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune recommendation trouvée")
    })
    Page<GameRecommendationDto> searchRecommendations(@RequestBody String searchQuery, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/recommendation/mostRecommended", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les jeux les plus recommandés", description = "Récupérer les jeux les plus recommandés")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "jeux les plus recommandés trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu trouvé")
    })
    Map<Integer, Long> getMostRecommendedGames(Pageable pageable);

    @GetMapping(value = APP_ROOT + "/recommendation/received/{receiverId}/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Compter le nombre de recommendations reçues par un utilisateur", description = "Compter le nombre de recommendations reçues par un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "nombre de recommendations compté"),
            @ApiResponse(responseCode = "404", description = "Aucune recommendation trouvée pour cet utilisateur")
    })
    Long countRecommendationsReceivedByUser(@PathVariable Integer receiverId);

    @GetMapping(value = APP_ROOT + "/recommendation/sent/{senderId}/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Compter le nombre de recommendations envoyées par un utilisateur", description = "Compter le nombre de recommendations envoyées par un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "nombre de recommendations compté"),
            @ApiResponse(responseCode = "404", description = "Aucune recommendation trouvée pour cet utilisateur")
    })
    Long countRecommendationsSentByUser(@PathVariable Integer senderId);



}
