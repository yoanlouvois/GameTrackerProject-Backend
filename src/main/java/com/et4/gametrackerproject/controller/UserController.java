package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.UserApi;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.enums.OnlineStatus;
import com.et4.gametrackerproject.enums.PrivacySetting;
import com.et4.gametrackerproject.enums.ScreenTheme;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController implements UserApi {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return userService.createUser(userDto);
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @Override
    public void deleteUser(Integer userId) {
        userService.deleteUser(userId);
    }

    @Override
    public UserDto getUserById(Integer userId) {
        return userService.getUserById(userId);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        return userService.getUserByUsername(username);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Override
    public void resetPassword(Integer userId, String newPassword) {
        userService.resetPassword(userId, newPassword);
    }

    @Override
    public void requestPasswordReset(String email) {
        userService.requestPasswordReset(email);
    }

    @Override
    public UserDto updatePrivacySettings(Integer userId, PrivacySetting privacy) {
        return userService.updatePrivacySettings(userId, privacy);
    }

    @Override
    public UserDto updateThemePreference(Integer userId, ScreenTheme theme) {
        return userService.updateThemePreference(userId, theme);
    }

    @Override
    public UserDto updateAvatar(Integer userId, Integer avatarId) {
        return userService.updateAvatar(userId, avatarId);
    }

    @Override
    public Page<UserDto> searchUsers(String query, Pageable pageable) {
        return userService.searchUsers(query, pageable);
    }

    @Override
    public Page<UserDto> getFriendsList(Integer userId, Pageable pageable) {
        return userService.getFriendsList(userId, pageable);
    }

    @Override
    public UserDto updatePlayStats(Integer userId, Integer gameTime, Integer gamesPlayed) {
        return userService.updatePlayStats(userId, gameTime, gamesPlayed);
    }

    @Override
    public UserDto addPoints(Integer userId, Integer points) {
        return userService.addPoints(userId, points);
    }

    @Override
    public Page<UserDto> getUsersByStatus(boolean isActive, Pageable pageable) {
        return userService.getUsersByStatus(isActive, pageable);
    }

    @Override
    public UserDto updateOnlineStatus(Integer userId, OnlineStatus status) {
        return userService.updateOnlineStatus(userId, status);
    }

    @Override
    public UserDto recordLogin(Integer userId) {
        return userService.recordLogin(userId);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return userService.isUsernameAvailable(username);
    }

    @Override
    public boolean isEmailRegistered(String email) {
        return userService.isEmailRegistered(email);
    }

    @Override
    public boolean isAdultUser(Integer userId) {
        return userService.isAdultUser(userId);
    }

    @Override
    public UserDto shareProfile(Integer userId, String platform) {
        return userService.shareProfile(userId, platform);
    }

    @Override
    public UserDto importUserData(Integer userId, String jsonData) {
        return userService.importUserData(userId, jsonData);
    }

    @Override
    public String exportUserData(Integer userId) {
        return userService.exportUserData(userId);
    }
}
