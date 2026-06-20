package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.FriendshipDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.enums.FriendshipStatus;

import java.util.List;

public interface FriendshipService {

    //Opérations de base
    FriendshipDto createFriendship(FriendshipDto friendshipDto);
    FriendshipDto updateFriendshipStatus(Integer friendshipId, FriendshipStatus newStatus);
    void deleteFriendshipById(Integer friendshipId);

    //Récupération
    FriendshipDto getFriendshipById(Integer friendshipId);
    List<FriendshipDto> getAllFriendshipsForUser(Integer userId);



    FriendshipDto getFriendshipBetweenUsers(Integer user1Id, Integer user2Id);

    //Gestion des demandes
    FriendshipDto sendFriendRequest(Integer senderId, Integer receiverId);
    FriendshipDto acceptFriendRequest(Integer friendshipId);
    FriendshipDto rejectFriendRequest(Integer friendshipId);
    FriendshipDto cancelFriendship(Integer userId, Integer friendId);

    //Listes relationnelles
    List<UserDto> getFriendsList(Integer userId);
    List<UserDto> getMutualFriends(Integer user1Id, Integer user2Id);
    List<UserDto> getPendingRequests(Integer userId);

    // Vérifications
    boolean friendshipExists(Integer user1Id, Integer user2Id);
    boolean hasPendingRequestBetween(Integer user1Id, Integer user2Id);
    FriendshipStatus getRelationshipStatus(Integer user1Id, Integer user2Id);

    // Statistiques
    int getFriendCount(Integer userId);

    //Administration
    List<FriendshipDto> getAllFriendships();
    List<FriendshipDto> searchFriendshipsByUser(String username);

    List<UserDto> suggestFriends(Integer userId);
    List<FriendshipDto> getFriendshipsForUserByStatus(Integer userId, FriendshipStatus status);

}