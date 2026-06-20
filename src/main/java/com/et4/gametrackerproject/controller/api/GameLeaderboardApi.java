package com.et4.gametrackerproject.controller.api;

import com.et4.gametrackerproject.dto.GameLeaderboardDto;
import com.et4.gametrackerproject.enums.LeaderboardPeriod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.et4.gametrackerproject.utils.Constants.APP_ROOT;

public interface GameLeaderboardApi {

    //Opérations de base

    @PostMapping(value = APP_ROOT + "/leaderboard/submit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Ajouter un score",description = "Ajouter un score")
    @ApiResponse(responseCode = "200", description = "Score ajouté")
    GameLeaderboardDto submitScore(@RequestBody GameLeaderboardDto scoreEntry);

    @PutMapping(value = APP_ROOT + "/leaderboard/update/{entryId}/{newScore}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un score",description = "Mettre à jour un score")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Score mis à jour"),
            @ApiResponse(responseCode = "404", description = "Score non trouvé")
    })
    GameLeaderboardDto updateScore(@PathVariable Integer entryId,@PathVariable Integer newScore);

    @DeleteMapping(value = APP_ROOT + "/leaderboard/delete/{entryId}")
    @Operation(summary = "Supprimer un score",description = "Supprimer un score")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Score supprimé"),
            @ApiResponse(responseCode = "404", description = "Score non trouvé")
    })
    void deleteScoreEntry(@PathVariable Integer entryId);

    @GetMapping(value = APP_ROOT + "/leaderboard/game/{gameId}/{period}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer le leaderboard d'un jeu",description = "Récupérer le leaderboard d'un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leaderboard trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun leaderboard trouvé pour ce jeu")
    })
    Page<GameLeaderboardDto> getLeaderboardForGame(@PathVariable Integer gameId,@PathVariable LeaderboardPeriod period, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/leaderboard/period/{period}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer le leaderboard par période",description = "Récupérer le leaderboard par période")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leaderboard trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun leaderboard trouvé pour cette période")
    })
    Page<GameLeaderboardDto> getLeaderboardByPeriod(@PathVariable LeaderboardPeriod period, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/leaderboard/game/{gameId}/user/{userId}/period/{period}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer le leaderboard d'un jeu pour un utilisateur",description = "Récupérer le leaderboard d'un jeu pour un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leaderboard trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun leaderboard trouvé pour ce jeu et cet utilisateur")
    })
    Optional<GameLeaderboardDto> getLeaderBoardByGameUserPeriod(@PathVariable Integer gameId, @PathVariable Integer userId, @PathVariable LeaderboardPeriod period);

    @GetMapping(value = APP_ROOT + "/leaderboard/game/{gameId}/period/{period}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer le leaderboard d'un jeu par période",description = "Récupérer le leaderboard d'un jeu par période")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leaderboard trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun leaderboard trouvé pour ce jeu et cette période")
    })
    List<GameLeaderboardDto> getLeaderboardByGamePeriodScore(@PathVariable Integer gameId, @PathVariable LeaderboardPeriod period);

    @GetMapping(value = APP_ROOT + "/leaderboard/game/{gameId}/period/{period}/page", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer le leaderboard d'un jeu par période avec pagination",description = "Récupérer le leaderboard d'un jeu par période avec pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leaderboard trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun leaderboard trouvé pour ce jeu et cette période")
    })
    Page<GameLeaderboardDto> getLeaderboardPageByRank(@PathVariable Integer gameId, @PathVariable LeaderboardPeriod period, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/leaderboard/game/{gameId}/period/{period}/limit/{limit}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les meilleurs joueurs d'un jeu par période",description = "Récupérer les meilleurs joueurs d'un jeu par période")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meilleurs joueurs trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun joueur trouvé pour ce jeu et cette période")
    })
    List<GameLeaderboardDto> getTopRankedPlayersByGamePeriod(@PathVariable Integer gameId, @PathVariable LeaderboardPeriod period, @PathVariable int limit);

    @GetMapping(value = APP_ROOT + "/leaderboard/user/{userId}/game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer le leaderboard d'un utilisateur pour un jeu",description = "Récupérer le leaderboard d'un utilisateur pour un jeu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leaderboard trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun leaderboard trouvé pour cet utilisateur et ce jeu")
    })
    List<GameLeaderboardDto> getLeaderboardEntriesForUserAndGame(@PathVariable Integer userId, @PathVariable Integer gameId);

    @GetMapping(value = APP_ROOT + "/leaderboard/date/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer le leaderboard par date",description = "Récupérer le leaderboard par date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leaderboard trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun leaderboard trouvé pour cette date")
    })
    List<GameLeaderboardDto> getLeaderboardEntriesByDate(@PathVariable Instant date);


}
