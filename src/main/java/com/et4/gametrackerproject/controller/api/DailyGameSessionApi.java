package com.et4.gametrackerproject.controller.api;

import com.et4.gametrackerproject.dto.DailyGameSessionDto;
import com.et4.gametrackerproject.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.et4.gametrackerproject.utils.Constants.APP_ROOT;

public interface DailyGameSessionApi {

    //Opérations de base

    @GetMapping(value = APP_ROOT + "/sessions/{idSession}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie une session de jeu par son ID", description = "Renvoie une session de jeu par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La session a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucune session n'a été trouvée avec cet id")
    })
    DailyGameSessionDto getSessionById(@PathVariable("idSession") Integer id);

    @PostMapping(value = APP_ROOT + "/sessions/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crée une nouvelle session de jeu", description = "Crée une nouvelle session de jeu")
    @ApiResponse(responseCode = "200", description = "La session a été créée")
    DailyGameSessionDto createSession(@RequestBody DailyGameSessionDto sessionDto);

    @PutMapping(value = APP_ROOT + "/sessions/update/{idSession}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Met à jour une session de jeu", description = "Met à jour une session de jeu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La session a été mise à jour"),
            @ApiResponse(responseCode = "404", description = "Aucune session n'a été trouvée avec cet id")
    })
    DailyGameSessionDto updateSession(@PathVariable("idSession") Integer id,@RequestBody DailyGameSessionDto sessionDto);

    @DeleteMapping(value = APP_ROOT + "/sessions/delete/{idSession}")
    @Operation(summary = "Supprime une session de jeu", description = "Supprime une session de jeu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La session a été supprimée"),
            @ApiResponse(responseCode = "404", description = "Aucune session n'a été trouvée avec cet id")
    })
    void deleteSession(@PathVariable("idSession") Integer id);

    @GetMapping(value = APP_ROOT + "/sessions/user/{userId}/date/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie une session de jeu par utilisateur et date", description = "Renvoie une session de jeu par utilisateur et date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La session a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune session n'a été trouvée avec cet utilisateur et cette date")
    })
    DailyGameSessionDto getSessionByUserAndDate(@PathVariable Integer userId, @PathVariable Instant date);

    @GetMapping(value = APP_ROOT + "/sessions/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie toutes les sessions de jeu d'un utilisateur", description = "Renvoie toutes les sessions de jeu d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Les sessions ont été trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune session n'a été trouvée pour cet utilisateur")
    })
    List<DailyGameSessionDto> getSessionsForUser(@PathVariable Integer userId);

    @GetMapping(value = APP_ROOT + "/sessions/user/{userId}/between/{start}/{end}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie toutes les sessions de jeu d'un utilisateur entre deux dates", description = "Renvoie toutes les sessions de jeu d'un utilisateur entre deux dates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Les sessions ont été trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune session n'a été trouvée pour cet utilisateur entre ces deux dates")
    })
    List<DailyGameSessionDto> getSessionsForUserBetweenDates(@PathVariable Integer userId,@PathVariable Instant start,@PathVariable Instant end);

    @GetMapping(value = APP_ROOT + "/sessions/user/{userId}/last", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la dernière session de jeu d'un utilisateur", description = "Renvoie la dernière session de jeu d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La session a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune session n'a été trouvée pour cet utilisateur")
    })
    Instant getLastPlayedDate(@PathVariable Integer userId);

    @GetMapping(value = APP_ROOT + "/sessions/date/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie toutes les sessions de jeu d'une date", description = "Renvoie toutes les sessions de jeu d'une date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Les sessions ont été trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune session n'a été trouvée pour cette date")
    })
    List<DailyGameSessionDto> getSessionByDate(@PathVariable Instant date);

    @GetMapping(value = APP_ROOT + "/sessions/most-active-users", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie les utilisateurs les plus actifs", description = "Renvoie les utilisateurs les plus actifs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Les utilisateurs les plus actifs ont été trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur n'a été trouvé dans la BDD")
    })
    Map<UserDto, Long> getMostActiveUsers();

    @GetMapping(value = APP_ROOT + "/sessions/total-playtime/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie le temps de jeu total d'un utilisateur", description = "Renvoie le temps de jeu total d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le temps de jeu total a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur n'a été trouvé avec cet id")
    })
    Integer calculateTotalPlaytimeByUser(@PathVariable Integer userId);

    @GetMapping(value = APP_ROOT + "/sessions/playtime/{userId}/between/{start}/{end}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie le temps de jeu d'un utilisateur entre deux dates", description = "Renvoie le temps de jeu d'un utilisateur entre deux dates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le temps de jeu a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur n'a été trouvé avec cet id")
    })
    Integer calculatePlaytimeByUserInPeriod(@PathVariable Integer userId, @PathVariable Instant start, @PathVariable Instant end);

    @GetMapping(value = APP_ROOT + "/sessions/count/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie le nombre de sessions d'un utilisateur", description = "Renvoie le nombre de sessions d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le nombre de sessions a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur n'a été trouvé avec cet id")
    })
    Long countSessionsByUser(@PathVariable Integer userId);

    @GetMapping(value = APP_ROOT + "/sessions/games-played/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie le nombre de jeux joués par un utilisateur", description = "Renvoie le nombre de jeux joués par un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le nombre de jeux joués a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur n'a été trouvé avec cet id")
    })
    Integer countGamesPlayedByUser(@PathVariable Integer userId);

    @GetMapping(value = APP_ROOT + "/sessions/longest/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la session de jeu la plus longue d'un utilisateur", description = "Renvoie la session de jeu la plus longue d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La session a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune session n'a été trouvée pour cet utilisateur")
    })
    DailyGameSessionDto getLongestSessionForUser(@PathVariable Integer userId);

    @GetMapping(value = APP_ROOT + "/sessions/recent/{userId}/{limit}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie les sessions de jeu récentes d'un utilisateur", description = "Renvoie les sessions de jeu récentes d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Les sessions ont été trouvées"),
            @ApiResponse(responseCode = "404", description = "Aucune session n'a été trouvée pour cet utilisateur")
    })
    List<DailyGameSessionDto> getRecentSessionsForUser(@PathVariable Integer userId, @PathVariable int limit);

    @GetMapping(value = APP_ROOT + "/sessions/average-playtime/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie le temps de jeu moyen d'un utilisateur", description = "Renvoie le temps de jeu moyen d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le temps de jeu moyen a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur n'a été trouvé avec cet id")
    })
    Double calculateAveragePlaytimeByUser(@PathVariable Integer userId);


}
