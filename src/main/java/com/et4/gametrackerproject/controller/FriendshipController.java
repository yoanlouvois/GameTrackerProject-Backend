package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.FriendshipApi;
import com.et4.gametrackerproject.dto.FriendshipDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.enums.FriendshipStatus;
import com.et4.gametrackerproject.services.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FriendshipController implements FriendshipApi {

    @Autowired
    private FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipservice) {
        friendshipService = friendshipservice;
    }

    @Override
    public FriendshipDto createFriendship(FriendshipDto friendshipDto) {
        return friendshipService.createFriendship(friendshipDto);
    }

    @Override
    public FriendshipDto updateFriendshipStatus(Integer friendshipId, FriendshipStatus newStatus) {
        return friendshipService.updateFriendshipStatus(friendshipId, newStatus);
    }

    @Override
    public void deleteFriendship(Integer friendshipId) {
        friendshipService.deleteFriendshipById(friendshipId);
    }

    @Override
    public FriendshipDto getFriendshipById(Integer friendshipId) {
        return friendshipService.getFriendshipById(friendshipId);
    }

    @Override
    public List<FriendshipDto> getAllFriendshipsForUser(Integer userId) {
        return friendshipService.getAllFriendshipsForUser(userId);
    }

    @Override
    public FriendshipDto getFriendshipBetweenUsers(Integer user1Id, Integer user2Id) {
        return friendshipService.getFriendshipBetweenUsers(user1Id, user2Id);
    }

    @Override
    public FriendshipDto sendFriendRequest(Integer senderId, Integer receiverId) {
        return friendshipService.sendFriendRequest(senderId, receiverId);
    }

    @Override
    public FriendshipDto acceptFriendRequest(Integer friendshipId) {
        return friendshipService.acceptFriendRequest(friendshipId);
    }

    @Override
    public FriendshipDto rejectFriendRequest(Integer friendshipId) {
        return friendshipService.rejectFriendRequest(friendshipId);
    }

    @Override
    public FriendshipDto cancelFriendship(Integer userId, Integer friendId) {
        return friendshipService.cancelFriendship(userId, friendId);
    }

    @Override
    public List<UserDto> getFriendsList(Integer userId) {
        return friendshipService.getFriendsList(userId);
    }

    @Override
    public List<UserDto> getMutualFriends(Integer user1Id, Integer user2Id) {
        return friendshipService.getMutualFriends(user1Id, user2Id);
    }

    @Override
    public List<UserDto> getPendingRequests(Integer userId) {
        return friendshipService.getPendingRequests(userId);
    }

    @Override
    public boolean friendshipExists(Integer user1Id, Integer user2Id) {
        return friendshipService.friendshipExists(user1Id, user2Id);
    }

    @Override
    public boolean hasPendingRequestBetween(Integer user1Id, Integer user2Id) {
        return friendshipService.hasPendingRequestBetween(user1Id, user2Id);
    }

    @Override
    public FriendshipStatus getRelationshipStatus(Integer user1Id, Integer user2Id) {
        return friendshipService.getRelationshipStatus(user1Id, user2Id);
    }

    @Override
    public int getFriendCount(Integer userId) {
        return friendshipService.getFriendCount(userId);
    }

    @Override
    public List<FriendshipDto> getAllFriendships() {
        return friendshipService.getAllFriendships();
    }

    @Override
    public List<FriendshipDto> searchFriendshipsByUser(String username) {
        return friendshipService.searchFriendshipsByUser(username);
    }

    @Override
    public List<UserDto> suggestFriends(Integer userId) {
        return friendshipService.suggestFriends(userId);
    }

    @Override
    public List<FriendshipDto> getFriendshipsForUserByStatus(Integer userId, FriendshipStatus status) {
        return friendshipService.getFriendshipsForUserByStatus(userId, status);
    }
}
