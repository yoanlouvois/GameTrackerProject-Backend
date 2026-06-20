package com.et4.gametrackerproject.controller.api;

import com.et4.gametrackerproject.dto.FavoriteGameDto;
import com.et4.gametrackerproject.dto.GameDto;
import com.et4.gametrackerproject.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.et4.gametrackerproject.utils.Constants.APP_ROOT;

public interface FavoriteGameApi {

    //Opérations de base

    @PostMapping(value = APP_ROOT + "/favorites/add", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Ajouter un jeu aux favoris", description = "Ajouter un jeu aux favoris d'un utilisateur")
    @ApiResponse(responseCode = "200", description = "Le jeu a été ajouté aux favoris")
    FavoriteGameDto addToFavorites(@RequestBody FavoriteGameDto favoriteDto);

    @DeleteMapping(value = APP_ROOT + "/favorites/remove/{favoriteId}")
    @Operation(summary = "Supprimer un jeu des favoris", description = "Supprimer un jeu des favoris d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le jeu a été supprimé des favoris"),
            @ApiResponse(responseCode = "404", description = "Le jeu n'a pas été trouvé dans les favoris")
    })
    void removeFromFavorites(@PathVariable Integer favoriteId);

    //Récupération

    @GetMapping(value = APP_ROOT + "/favorites/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les jeux favoris d'un utilisateur", description = "Récupérer la liste des jeux favoris d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des jeux favoris a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu favori n'a été trouvé pour cet utilisateur")
    })
    List<GameDto> getFavoriteGamesForUser(@PathVariable Integer userId);

    @GetMapping(value = APP_ROOT + "/favorites/game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les utilisateurs qui ont favorisé un jeu", description = "Récupérer la liste des utilisateurs qui ont favorisé un jeu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des utilisateurs a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur n'a favorisé ce jeu")
    })
    List<UserDto> getUsersWhoFavoritedGame(@PathVariable Integer gameId);

    @GetMapping(value = APP_ROOT + "/favorites/exists/{userId}/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier si un jeu est favori pour un utilisateur", description = "Vérifier si un jeu est dans les favoris d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le statut du jeu favori a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu favori n'a été trouvé pour cet utilisateur")
    })
    boolean isGameFavoritedByUser(@PathVariable Integer userId,@PathVariable Integer gameId);

    @GetMapping(value = APP_ROOT + "/favorites/{favoriteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer un jeu favori par son ID", description = "Récupérer un jeu favori spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le jeu favori a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu favori n'a été trouvé avec cet ID")
    })
    FavoriteGameDto getFavoriteById(@PathVariable Integer favoriteId);


    //Statistiques

    @GetMapping(value = APP_ROOT + "/favorites/count/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Compter le nombre de favoris pour un jeu", description = "Compter le nombre total de favoris pour un jeu spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le nombre de favoris a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun favori n'a été trouvé pour ce jeu")
    })
    Long getTotalFavoritesCountForGame(@PathVariable Integer gameId);

    @GetMapping(value = APP_ROOT + "/favorites/mostFavorited/{limit}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les jeux les plus favoris", description = "Récupérer la liste des jeux les plus favoris")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des jeux favoris a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu favori n'a été trouvé")
    })
    Map<String, Long> getMostFavoritedGames(@PathVariable int limit);

    @GetMapping(value = APP_ROOT + "/favorites/count/category", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Compter le nombre de favoris par catégorie de jeu", description = "Compter le nombre total de favoris par catégorie de jeu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le nombre de favoris par catégorie a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun favori n'a été trouvé par catégorie")
    })
    Map<Integer, Long> getFavoriteCountByGameCategory();

    //Vérifier si un jeu est favori pour un utilisateur spécifique
    @GetMapping(value = APP_ROOT + "/favorites/find/{userId}/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier si un jeu est favori pour un utilisateur spécifique", description = "Vérifier si un jeu est dans les favoris d'un utilisateur spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le statut du jeu favori a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu favori n'a été trouvé pour cet utilisateur")
    })
    Optional<FavoriteGameDto> findFavoriteByUserAndGame(@PathVariable Integer userId, @PathVariable Integer gameId);

    //Récupérer les jeux favoris ajoutés récemment par un utilisateur
    @GetMapping(value = APP_ROOT + "/favorites/recent/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les jeux favoris ajoutés récemment par un utilisateur", description = "Récupérer la liste des jeux favoris ajoutés récemment par un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des jeux favoris a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu favori n'a été trouvé pour cet utilisateur")
    })
    List<FavoriteGameDto> getRecentlyAddedFavoritesForUser(@PathVariable Integer userId);

    //Trouver les favoris communs entre deux utilisateurs
    @GetMapping(value = APP_ROOT + "/favorites/common/{userId1}/{userId2}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Trouver les jeux favoris communs entre deux utilisateurs", description = "Récupérer la liste des jeux favoris communs entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des jeux favoris communs a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu favori commun n'a été trouvé")
    })
    List<GameDto> getCommonFavoriteGames(@PathVariable Integer userId1, @PathVariable Integer userId2);

    //Compter le nombre de jeux favoris pour un utilisateur
    @GetMapping(value = APP_ROOT + "/favorites/count/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Compter le nombre de jeux favoris pour un utilisateur", description = "Compter le nombre total de jeux favoris pour un utilisateur spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le nombre de jeux favoris a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun jeu favori n'a été trouvé pour cet utilisateur")
    })
    Long countFavoritesByUser(@PathVariable Integer userId);




}
