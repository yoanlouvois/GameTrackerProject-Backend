package com.et4.gametrackerproject.controller.api;

import com.et4.gametrackerproject.dto.AchievementDto;
import com.et4.gametrackerproject.enums.AchievementRarity;
import com.et4.gametrackerproject.enums.AchievementType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.et4.gametrackerproject.utils.Constants.APP_ROOT;

/**
 * AchievementApi est une interface qui définit les opérations liées aux achievements.
 * Elle utilise Spring Web pour gérer les requêtes HTTP et Swagger pour la documentation de l'API.
 */
@Tag(name = "Achievements", description = "Operations related to achievements")
public interface AchievementApi {

    @PostMapping(value = APP_ROOT + "/achievements/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistre un achievement", description = "Enregistre un achievement")
    @ApiResponse(responseCode = "200", description = "L'achievement a été enregistré")
    AchievementDto createAchievement(@RequestBody AchievementDto achievementDto);

    @PutMapping(value = APP_ROOT + "/achievements/update/{idAchievement}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Modifie un achievement", description = "Modifie un achievement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'achievement a été modifié"),
            @ApiResponse(responseCode = "404", description = "Aucun achievement n'a été trouvé avec cet id")
    })
    AchievementDto updateAchievement(@PathVariable("idAchievement") Integer id, @RequestBody AchievementDto achievementDto);

    @DeleteMapping(value = APP_ROOT + "/achievements/delete/{idAchievement}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Supprime un achievement", description = "Supprime un achievement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'achievement a été supprimé"),
            @ApiResponse(responseCode = "404", description = "Aucun achievement n'a été trouvé avec cet id")
    })
    void deleteAchievement(@PathVariable("idAchievement") Integer id);

    @GetMapping( value = APP_ROOT+"/achievements/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des achievements", description = "Renvoie la liste des achievements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des achievements a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun achievement n'a été trouvé dans la BDD")
    })
    List<AchievementDto> getAllAchievements();

    @GetMapping(value = APP_ROOT + "/achievements/{idAchievement}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie un achievement par son ID", description = "Renvoie un achievement par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'achievement a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun achievement n'a été trouvé avec cet id")
    })
    AchievementDto getAchievementById(@PathVariable("idAchievement") Integer id);

    @GetMapping(value = APP_ROOT + "/achievements/type/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des achievements par type", description = "Renvoie la liste des achievements par type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Les achievements ont été trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun achievement n'a été trouvé avec ce type")
    })
    List<AchievementDto> getAchievementsByType(@PathVariable AchievementType type);

    @GetMapping(value = APP_ROOT + "/achievements/rarity/{rarity}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des achievements par rarété", description = "Renvoie la liste des achievements par rarété")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Les achievements ont été trouvés"),
            @ApiResponse(responseCode = "404", description = "Aucun achievement n'a été trouvé avec cette rarereté")
    })
    List<AchievementDto> getAchievementsByRarity(@PathVariable AchievementRarity rarity);

    @GetMapping(value = APP_ROOT + "/achievements/active", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des achievements actifs", description = "Renvoie la liste des achievements actifs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des achievements actifs a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun achievement actif n'a été trouvé dans la BDD")
    })
    List<AchievementDto> getActiveAchievements();

    @GetMapping(value = APP_ROOT + "/achievements/secrets", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des achievements secrets", description = "Renvoie la liste des achievements secrets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des achievements secrets a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun achievement secret n'a été trouvé dans la BDD")
    })
    List<AchievementDto> getSecretAchievements();

    @GetMapping(value = APP_ROOT + "/achievements/description/{keyword}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie la liste des achievements contenant un mot clé dans la description", description = "Renvoie la liste des achievements contenant un mot clé dans la description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des achievements contenant le mot clé a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun achievement n'a été trouvé avec ce mot clé")
    })
    List<AchievementDto> getAchievementsByDescriptionContaining(@PathVariable String keyword);

    @GetMapping(value = APP_ROOT + "/achievements/count", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Renvoie le nombre total d'achievements", description = "Renvoie le nombre total d'achievements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le nombre total d'achievements a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun achievement n'a été trouvé dans la BDD")
    })
    List<AchievementDto> countNumberAchievementsByType();
}
