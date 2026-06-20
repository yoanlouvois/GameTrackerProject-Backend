package com.et4.gametrackerproject.controller.api;

import com.et4.gametrackerproject.dto.FriendshipDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.enums.FriendshipStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.et4.gametrackerproject.utils.Constants.APP_ROOT;

public interface FriendshipApi {

    //Opérations de base
    @PostMapping(value = APP_ROOT + "/friendships/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une amitié", description = "Créer une amitié entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'amitié a été créée"),
    })
    FriendshipDto createFriendship(@RequestBody FriendshipDto friendshipDto);

    @PutMapping(value = APP_ROOT + "/friendships/update/{friendshipId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour le statut d'une amitié", description = "Mettre à jour le statut d'une amitié entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le statut de l'amitié a été mis à jour"),
            @ApiResponse(responseCode = "404", description = "L'amitié n'a pas été trouvée")
    })
    FriendshipDto updateFriendshipStatus(@PathVariable Integer friendshipId, @RequestBody FriendshipStatus newStatus);

    @DeleteMapping(value = APP_ROOT + "/friendships/delete/{friendshipId}")
    @Operation(summary = "Supprimer une amitié", description = "Supprimer une amitié entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'amitié a été supprimée"),
            @ApiResponse(responseCode = "404", description = "L'amitié n'a pas été trouvée")
    })
    void deleteFriendship(@PathVariable Integer friendshipId);

    //Récupération

    @GetMapping(value = APP_ROOT + "/friendships/{friendshipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer une amitié par son ID", description = "Récupérer une amitié entre deux utilisateurs par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'amitié a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune amitié n'a été trouvée avec cet ID")
    })
    FriendshipDto getFriendshipById(@PathVariable Integer friendshipId);

    @GetMapping(value = APP_ROOT + "/friendships/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer toutes les amitiés d'un utilisateur", description = "Récupérer toutes les amitiés d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des amitiés a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune amitié n'a été trouvée pour cet utilisateur")
    })
    List<FriendshipDto> getAllFriendshipsForUser(@PathVariable Integer userId);

    @GetMapping(value = APP_ROOT + "/friendships/user/{user1Id}/user/{user2Id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer une amitié entre deux utilisateurs", description = "Récupérer une amitié entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'amitié a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune amitié n'a été trouvée entre ces deux utilisateurs")
    })
    FriendshipDto getFriendshipBetweenUsers(@PathVariable Integer user1Id,@PathVariable Integer user2Id);

    @PostMapping(value = APP_ROOT + "/friendships/send/{senderId}/to/{receiverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Envoyer une demande d'amitié", description = "Envoyer une demande d'amitié entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La demande d'amitié a été envoyée"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur n'a été trouvé")
    })
    FriendshipDto sendFriendRequest(@PathVariable Integer senderId,@PathVariable Integer receiverId);

    @PutMapping(value = APP_ROOT + "/friendships/accept/{friendshipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Accepter une demande d'amitié", description = "Accepter une demande d'amitié entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La demande d'amitié a été acceptée"),
            @ApiResponse(responseCode = "404", description = "Aucune demande d'amitié n'a été trouvée")
    })
    FriendshipDto acceptFriendRequest(@PathVariable Integer friendshipId);

    @PutMapping(value = APP_ROOT + "/friendships/reject/{friendshipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rejeter une demande d'amitié", description = "Rejeter une demande d'amitié entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La demande d'amitié a été rejetée"),
            @ApiResponse(responseCode = "404", description = "Aucune demande d'amitié n'a été trouvée")
    })
    FriendshipDto rejectFriendRequest(@PathVariable Integer friendshipId);

    @PutMapping(value = APP_ROOT + "/friendships/cancel/{userId}/friend/{friendId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Annuler une demande d'amitié", description = "Annuler une demande d'amitié entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La demande d'amitié a été annulée"),
            @ApiResponse(responseCode = "404", description = "Aucune demande d'amitié n'a été trouvée")
    })
    FriendshipDto cancelFriendship(@PathVariable Integer userId,@PathVariable Integer friendId);

    //Listes relationnelles

    @GetMapping(value = APP_ROOT + "/friendships/friends/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer la liste d'amis d'un utilisateur", description = "Récupérer la liste d'amis d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste d'amis a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun ami n'a été trouvé pour cet utilisateur")
    })
    List<UserDto> getFriendsList(@PathVariable Integer userId);

    @GetMapping(value = APP_ROOT + "/friendships/mutual/{user1Id}/user/{user2Id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer la liste d'amis communs entre deux utilisateurs", description = "Récupérer la liste d'amis communs entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste d'amis communs a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun ami commun n'a été trouvé entre ces deux utilisateurs")
    })
    List<UserDto> getMutualFriends(@PathVariable Integer user1Id,@PathVariable Integer user2Id);

    @GetMapping(value = APP_ROOT + "/friendships/pending/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer la liste des demandes d'amitié en attente", description = "Récupérer la liste des demandes d'amitié en attente pour un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des demandes d'amitié en attente a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune demande d'amitié en attente n'a été trouvée")
    })
    List<UserDto> getPendingRequests(@PathVariable Integer userId);

    // Vérifications

    @GetMapping(value = APP_ROOT + "/friendships/exists/{user1Id}/user/{user2Id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier si une amitié existe entre deux utilisateurs", description = "Vérifier si une amitié existe entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "L'amitié a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune amitié n'a été trouvée entre ces deux utilisateurs")
    })
    boolean friendshipExists(@PathVariable Integer user1Id,@PathVariable Integer user2Id);

    @GetMapping(value = APP_ROOT + "/friendships/pending/{user1Id}/user/{user2Id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier si une demande d'amitié est en attente", description = "Vérifier si une demande d'amitié est en attente entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La demande d'amitié a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune demande d'amitié n'a été trouvée entre ces deux utilisateurs")
    })
    boolean hasPendingRequestBetween(@PathVariable Integer user1Id,@PathVariable Integer user2Id);

    @GetMapping(value = APP_ROOT + "/friendships/status/{user1Id}/user/{user2Id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier le statut d'une amitié entre deux utilisateurs", description = "Vérifier le statut d'une amitié entre deux utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le statut de l'amitié a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucune amitié n'a été trouvée entre ces deux utilisateurs")
    })
    FriendshipStatus getRelationshipStatus(@PathVariable Integer user1Id,@PathVariable Integer user2Id);

    // Statistiques

    @GetMapping(value = APP_ROOT + "/friendships/count/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Compter le nombre d'amis d'un utilisateur", description = "Compter le nombre total d'amis d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Le nombre d'amis a été trouvé"),
            @ApiResponse(responseCode = "404", description = "Aucun ami n'a été trouvé pour cet utilisateur")
    })
    int getFriendCount(@PathVariable Integer userId);

    //Administration

    @GetMapping(value = APP_ROOT + "/friendships/admin/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer toutes les amitiés", description = "Récupérer toutes les amitiés de la base de données")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des amitiés a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune amitié n'a été trouvée dans la base de données")
    })
    List<FriendshipDto> getAllFriendships();

    @GetMapping(value = APP_ROOT + "/friendships/admin/search/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des amitiés par nom d'utilisateur", description = "Rechercher des amitiés par nom d'utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des amitiés a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune amitié n'a été trouvée pour cet utilisateur")
    })
    List<FriendshipDto> searchFriendshipsByUser(@PathVariable String username);

    @GetMapping(value = APP_ROOT + "/friendships/suggest/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Suggérer des amis à un utilisateur", description = "Suggérer des amis à un utilisateur en fonction de ses amis actuels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des amis suggérés a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucun ami suggéré n'a été trouvé")
    })
    List<UserDto> suggestFriends(@PathVariable Integer userId);

    @GetMapping(value = APP_ROOT + "/friendships/status/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer les amitiés d'un utilisateur par statut", description = "Récupérer les amitiés d'un utilisateur par statut")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La liste des amitiés a été trouvée"),
            @ApiResponse(responseCode = "404", description = "Aucune amitié n'a été trouvée pour cet utilisateur")
    })
    List<FriendshipDto> getFriendshipsForUserByStatus(@PathVariable Integer userId, FriendshipStatus status);

}
