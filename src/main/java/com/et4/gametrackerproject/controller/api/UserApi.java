package com.et4.gametrackerproject.controller.api;

import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.enums.OnlineStatus;
import com.et4.gametrackerproject.enums.PrivacySetting;
import com.et4.gametrackerproject.enums.ScreenTheme;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.et4.gametrackerproject.utils.Constants.APP_ROOT;

public interface UserApi {

    // Gestion du cycle de vie

    @PostMapping(value = APP_ROOT + "/users/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un nouvel utilisateur", description = "Créer un nouvel utilisateur")
    @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès")
    UserDto createUser(@RequestBody UserDto userDto);

    @PutMapping(value = APP_ROOT + "/users/update/{userId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un utilisateur", description = "Mettre à jour un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto updateUser(@PathVariable Integer userId,@RequestBody UserDto userDto);

    @DeleteMapping(value = APP_ROOT + "/users/delete/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Supprimer un utilisateur", description = "Supprimer un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    void deleteUser(@PathVariable Integer userId);

    // Récupération

    @GetMapping(value = APP_ROOT + "/users/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer un utilisateur par son ID", description = "Récupérer un utilisateur par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto getUserById(@PathVariable Integer userId);

    @GetMapping(value = APP_ROOT + "/users/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer un utilisateur par son nom d'utilisateur", description = "Récupérer un utilisateur par son nom d'utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto getUserByUsername(@PathVariable String username);

    @GetMapping(value = APP_ROOT + "/users/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer un utilisateur par son adresse e-mail", description = "Récupérer un utilisateur par son adresse e-mail")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto getUserByEmail(@PathVariable String email);

    @GetMapping(value = APP_ROOT + "/users/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer tous les utilisateurs", description = "Récupérer tous les utilisateurs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé")
    })
    Page<UserDto> getAllUsers(Pageable pageable);

    // Authentification et sécurité

    @PutMapping(value = APP_ROOT + "/users/reset-password/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Réinitialiser le mot de passe d'un utilisateur", description = "Réinitialiser le mot de passe d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    void resetPassword(@PathVariable Integer userId,@RequestBody String newPassword);

    @PostMapping(value = APP_ROOT + "/users/request-password-reset", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Demander la réinitialisation du mot de passe", description = "Demander la réinitialisation du mot de passe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Demande de réinitialisation du mot de passe envoyée"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    void requestPasswordReset(@RequestBody String email);

    // Préférences utilisateur

    @PutMapping(value = APP_ROOT + "/users/update-privacy/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour les paramètres de confidentialité d'un utilisateur", description = "Mettre à jour les paramètres de confidentialité d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paramètres de confidentialité mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto updatePrivacySettings(@PathVariable Integer userId,@RequestBody PrivacySetting privacy);

    @PutMapping(value = APP_ROOT + "/users/update-theme/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour la préférence de thème d'un utilisateur", description = "Mettre à jour la préférence de thème d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Préférence de thème mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto updateThemePreference(@PathVariable Integer userId,@RequestBody ScreenTheme theme);

    @PutMapping(value = APP_ROOT + "/users/update-avatar/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour l'avatar d'un utilisateur", description = "Mettre à jour l'avatar d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto updateAvatar(@PathVariable Integer userId,@RequestBody Integer avatarId);

    // Gestion des relations

    @GetMapping(value = APP_ROOT + "/users/search/{query}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des utilisateurs", description = "Rechercher des utilisateurs par nom d'utilisateur ou adresse e-mail")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé")
    })
    Page<UserDto> searchUsers(@PathVariable String query, Pageable pageable);

    @GetMapping(value = APP_ROOT + "/users/friends/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer la liste des amis d'un utilisateur", description = "Récupérer la liste des amis d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des amis trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun ami trouvé")
    })
    Page<UserDto> getFriendsList(@PathVariable Integer userId, Pageable pageable);

    // Statistiques et progression

    @PutMapping(value = APP_ROOT + "/users/update-stats/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour les statistiques de jeu d'un utilisateur", description = "Mettre à jour les statistiques de jeu d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistiques mises à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto updatePlayStats(@PathVariable Integer userId,@RequestBody Integer gameTime,@RequestBody Integer gamesPlayed);

    @PutMapping(value = APP_ROOT + "/users/add-points/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Ajouter des points à un utilisateur", description = "Ajouter des points à un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Points ajoutés avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto addPoints(@PathVariable Integer userId,@RequestBody Integer points);

    // Administration

    @GetMapping(value = APP_ROOT + "/users/status/{isActive}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les utilisateurs par statut", description = "Récupérer les utilisateurs par statut (actif/inactif)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé")
    })
    Page<UserDto> getUsersByStatus(@PathVariable boolean isActive, Pageable pageable);

    // Gestion des sessions

    @PutMapping(value = APP_ROOT + "/users/update-online-status/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour le statut en ligne d'un utilisateur", description = "Mettre à jour le statut en ligne d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statut en ligne mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto updateOnlineStatus(@PathVariable Integer userId,@RequestBody OnlineStatus status);

    @PutMapping(value = APP_ROOT + "/users/record-login/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer la connexion d'un utilisateur", description = "Enregistrer la connexion d'un utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion enregistrée avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto recordLogin(@PathVariable Integer userId);

    // Validation

    @GetMapping(value = APP_ROOT + "/users/check-username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier la disponibilité du nom d'utilisateur", description = "Vérifier la disponibilité du nom d'utilisateur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nom d'utilisateur disponible"),
            @ApiResponse(responseCode = "409", description = "Nom d'utilisateur déjà pris")
    })
    boolean isUsernameAvailable(@PathVariable String username);

    @GetMapping(value = APP_ROOT + "/users/check-email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier si l'adresse e-mail est enregistrée", description = "Vérifier si l'adresse e-mail est enregistrée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Adresse e-mail enregistrée"),
            @ApiResponse(responseCode = "404", description = "Adresse e-mail non trouvée")
    })
    boolean isEmailRegistered(@PathVariable String email);

    @GetMapping(value = APP_ROOT + "/users/check-adult/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier si l'utilisateur est majeur", description = "Vérifier si l'utilisateur est majeur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur majeur"),
            @ApiResponse(responseCode = "403", description = "Utilisateur mineur")
    })
    boolean isAdultUser(@PathVariable Integer userId);

    // Intégration sociale

    @PostMapping(value = APP_ROOT + "/users/share/{userId}/{platform}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Partager le profil d'un utilisateur sur une plateforme sociale", description = "Partager le profil d'un utilisateur sur une plateforme sociale")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil partagé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto shareProfile(@PathVariable Integer userId,@PathVariable String platform);

    // Gestion des données

    @PostMapping(value = APP_ROOT + "/users/import/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Importer les données d'un utilisateur", description = "Importer les données d'un utilisateur à partir d'un fichier JSON")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Données importées avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    UserDto importUserData(@PathVariable Integer userId,@RequestBody String jsonData);

    @GetMapping(value = APP_ROOT + "/users/export/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Exporter les données d'un utilisateur", description = "Exporter les données d'un utilisateur au format JSON")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Données exportées avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    String exportUserData(@PathVariable Integer userId);
}
