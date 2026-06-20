package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.ChangerMdpUserDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.enums.FriendshipStatus;
import com.et4.gametrackerproject.enums.OnlineStatus;
import com.et4.gametrackerproject.enums.PrivacySetting;
import com.et4.gametrackerproject.enums.ScreenTheme;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidEntityException;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.*;
import com.et4.gametrackerproject.repository.*;
import com.et4.gametrackerproject.services.UserService;
import com.et4.gametrackerproject.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AvatarRepository avatarRepository;
    private final FriendshipRepository friendshipRepository;
    private final FavoriteGameRepository favoriteGameRepository;
    private final UserSanctionRepository userSanctionRepository;
    private final GameRecommendationRepository gameRecommendationRepository;
    private final MessageRepository messageRepository;
    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, AvatarRepository avatarRepository, FriendshipRepository friendshipRepository, FavoriteGameRepository favoriteGameRepository, UserSanctionRepository userSanctionRepository, GameRecommendationRepository gameRecommendationRepository, MessageRepository messageRepository, ReportRepository reportRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
        this.friendshipRepository = friendshipRepository;
        this.favoriteGameRepository = favoriteGameRepository;
        this.userSanctionRepository = userSanctionRepository;
        this.gameRecommendationRepository = gameRecommendationRepository;
        this.messageRepository = messageRepository;
        this.reportRepository = reportRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        List<String> errors = UserValidator.validate(userDto);
        if (!errors.isEmpty()) {
            log.error("User invalide: {}", errors);
            throw new InvalidEntityException("User is not valid", ErrorCodes.USER_NOT_VALID,errors);
        }

        log.info("Create User {}", userDto);

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);

        return UserDto.fromEntity(userRepository.save(UserDto.toEntity(userDto)));
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        List<String> errors = UserValidator.validate(userDto);
        if (!errors.isEmpty()) {
            log.error("User is not valid: {}", errors);
            throw new InvalidEntityException("User is not valid", ErrorCodes.USER_NOT_VALID, errors);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User non trouvé avec l'id {}", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }
        if(!userId.equals(userDto.getId())) {
            log.error("User id {} is not the same as userDto id {}", userId, userDto.getId());
            throw new EntityNotFoundException("User id " + userId + " is not the same as userDto id " + userDto.getId(), ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Update User {}", userDto);

        UserDto user = userRepository.findById(userId)
                .map(UserDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND));

        // Ne faites rien si le champ du mot de passe est vide lors de la mise à jour
        if (StringUtils.hasText(userDto.getPassword())) {
            // Un nouveau mot de passe a été fourni, hachez-le
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(encodedPassword);
        } else {
            // Aucun nouveau mot de passe fourni, conservez l'ancien
            userDto.setPassword(user.getPassword());
        }

        return UserDto.fromEntity(userRepository.save(UserDto.toEntity(userDto)));
    }

    @Override
    public void deleteUser(Integer userId) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        Optional<FavoriteGame> favorites = favoriteGameRepository.findFavoriteGameByUserId(userId);
        if (favorites.isPresent()) {
            log.error("Cet utilisateur a des jeux favoris, impossible de le supprimer");
            throw new InvalidOperationException("Cet utilisateur a des jeux favoris, impossible de le supprimer",
                    ErrorCodes.USER_ALREADY_USED);
        }

        Optional<UserSanction> userSanctions = userSanctionRepository.findByUserId(userId);
        if (userSanctions.isPresent()) {
            log.error("Cet utilisateur a des sanctions, impossible de le supprimer");
            throw new InvalidOperationException("Cet utilisateur a des sanctions, impossible de le supprimer",
                    ErrorCodes.USER_ALREADY_USED);
        }

        Optional<GameRecommendation> gameRecommendations = gameRecommendationRepository.findByUserId(userId);
        if (gameRecommendations.isPresent()) {
            log.error("Cet utilisateur a des recommandations de jeux, impossible de le supprimer");
            throw new InvalidOperationException("Cet utilisateur a des recommandations de jeux, impossible de le supprimer",
                    ErrorCodes.USER_ALREADY_USED);
        }

        Optional<Message> messages = messageRepository.findByUserId(userId);
        if (messages.isPresent()) {
            log.error("Cet utilisateur a des messages, impossible de le supprimer");
            throw new InvalidOperationException("Cet utilisateur a des messages, impossible de le supprimer",
                    ErrorCodes.USER_ALREADY_USED);
        }

        Optional<Report> reports = reportRepository.findByUserId(userId);
        if (reports.isPresent()) {
            log.error("Cet utilisateur a des rapports, impossible de le supprimer");
            throw new InvalidOperationException("Cet utilisateur a des rapports, impossible de le supprimer",
                    ErrorCodes.USER_ALREADY_USED);
        }

        Optional<Friendship> friendships = friendshipRepository.findByUserId(userId);
        if (friendships.isPresent()) {
            log.error("Cet utilisateur a des amitiés, impossible de le supprimer");
            throw new InvalidOperationException("Cet utilisateur a des amitiés, impossible de le supprimer",
                    ErrorCodes.USER_ALREADY_USED);
        }
        userRepository.deleteById(userId);
    }



    @Override
    public UserDto getUserById(Integer userId) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Get User with id {}", userId);

        return userRepository.findById(userId)
                .map(UserDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND));
    }

    @Override
    public UserDto getUserByUsername(String username) {
        if(username == null) {
            log.error("Username is null");
            throw new EntityNotFoundException("Username is null", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Get User with username {}", username);

        return userRepository.findByUsername(username)
                .map(UserDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("User with username " + username + " not found", ErrorCodes.USER_NOT_FOUND));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        if(email == null) {
            log.error("Email is null");
            throw new EntityNotFoundException("Email is null", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Get User with email {}", email);

        return userRepository.findByEmail(email)
                .map(UserDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found", ErrorCodes.USER_NOT_FOUND));
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        log.info("Get all Users");

        return userRepository.findAll(pageable).map(UserDto::fromEntity);
    }

    @Override
    public void resetPassword(Integer userId, String newPassword) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(newPassword == null) {
            log.error("New password is null");
            throw new InvalidEntityException("New password is null", ErrorCodes.USER_NOT_VALID);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND));

        log.info("Reset password for User with id {}", userId);

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        userRepository.save(user);
        log.info("Password reset successfully for User with id {}", userId);
    }

    @Override
    public void requestPasswordReset(String email) {
        if(email == null) {
            log.error("Email is null");
            throw new InvalidEntityException("Email is null", ErrorCodes.USER_NOT_VALID);
        }
        if(!userRepository.existsByEmail(email)) {
            log.error("User with email {} not found", email);
            throw new EntityNotFoundException("User with email " + email + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Request password reset for User with email {}", email);

        // TODO : Envoyer un email de réinitialisation de mot de passe
    }

    @Override
    public UserDto updatePrivacySettings(Integer userId, PrivacySetting privacy) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(privacy == null) {
            log.error("Privacy setting is null");
            throw new InvalidEntityException("Privacy setting is null", ErrorCodes.USER_NOT_VALID);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Update privacy settings for User with id {}", userId);

        userRepository.updateUserPrivacySetting(userId, privacy);
        return getUserById(userId);
    }

    @Override
    public UserDto updateThemePreference(Integer userId, ScreenTheme theme) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(theme == null) {
            log.error("Theme preference is null");
            throw new InvalidEntityException("Theme preference is null", ErrorCodes.USER_NOT_VALID);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Update theme preference for User with id {}", userId);

        userRepository.updateUserTheme(userId, theme);
        return getUserById(userId);
    }

    @Override
    public UserDto updateAvatar(Integer userId, Integer avatarId) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(avatarId == null) {
            log.error("Avatar id is null");
            throw new InvalidEntityException("Avatar id is null", ErrorCodes.USER_NOT_VALID);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }
        if(!avatarRepository.existsById(avatarId)) {
            log.error("Avatar with id {} not found", avatarId);
            throw new EntityNotFoundException("Avatar with id " + avatarId + " not found", ErrorCodes.AVATAR_NOT_FOUND);
        }

        log.info("Update avatar for User with id {}", userId);

        userRepository.updateUserAvatar(userId, avatarId);
        return getUserById(userId);
    }

    @Override
    public Page<UserDto> searchUsers(String query, Pageable pageable) {
        if(query == null) {
            log.error("Query is null");
            throw new InvalidEntityException("Query is null", ErrorCodes.USER_NOT_VALID);
        }

        log.info("Search Users with query {}", query);

        return userRepository.findByUsernameContainingIgnoreCase(query, pageable).map(UserDto::fromEntity);
    }

    @Override
    public Page<UserDto> getFriendsList(Integer userId, Pageable pageable) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Get Friends List for User with id {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND));

        return friendshipRepository.findAllUsersByUserAndStatus(user, FriendshipStatus.ACCEPTED, pageable).map(UserDto::fromEntity);
    }

    @Override
    public UserDto updatePlayStats(Integer userId, Integer gameTime, Integer gamesPlayed) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(gameTime == null) {
            log.error("Game time is null");
            throw new InvalidEntityException("Game time is null", ErrorCodes.USER_NOT_VALID);
        }
        if(gamesPlayed == null) {
            log.error("Games played is null");
            throw new InvalidEntityException("Games played is null", ErrorCodes.USER_NOT_VALID);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Update play stats for User with id {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND));
        user.setTotalPlayTime(user.getTotalPlayTime() + gameTime);
        user.setTotalGamesPlayed(user.getTotalGamesPlayed() + gamesPlayed);
        return UserDto.fromEntity(userRepository.save(user));
    }

    @Override
    public UserDto addPoints(Integer userId, Integer points) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(points == null) {
            log.error("Points is null");
            throw new InvalidEntityException("Points is null", ErrorCodes.USER_NOT_VALID);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Add points for User with id {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND));
        user.setPoints(user.getPoints() + points);
        return UserDto.fromEntity(userRepository.save(user));
    }

    @Override
    public Page<UserDto> getUsersByStatus(boolean isActive, Pageable pageable) {
        log.info("Get Users by status {}", isActive);

        return userRepository.findByIsActive(isActive, pageable).map(UserDto::fromEntity);
    }

    @Override
    public UserDto updateOnlineStatus(Integer userId, OnlineStatus status) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(status == null) {
            log.error("Online status is null");
            throw new InvalidEntityException("Online status is null", ErrorCodes.USER_NOT_VALID);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Update online status for User with id {}", userId);

        userRepository.updateUserStatus(userId, status);
        return getUserById(userId);
    }

    @Override
    public UserDto recordLogin(Integer userId) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Record login for User with id {}", userId);

        userRepository.updateUserStatus(userId, OnlineStatus.ONLINE);
        return getUserById(userId);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        if(username == null) {
            log.error("Username is null");
            throw new InvalidEntityException("Username is null", ErrorCodes.USER_NOT_VALID);
        }

        log.info("Check if username {} is available", username);

        return !userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailRegistered(String email) {
        if(email == null) {
            log.error("Email is null");
            throw new InvalidEntityException("Email is null", ErrorCodes.USER_NOT_VALID);
        }

        log.info("Check if email {} is registered", email);

        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isAdultUser(Integer userId) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Check if User with id {} is adult", userId);

        return userRepository.findById(userId)
                .map(user -> user.getBirthDate().isBefore(user.getBirthDate().plusYears(18)))
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND));
    }

    @Override
    public UserDto shareProfile(Integer userId, String platform) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(platform == null) {
            log.error("Platform is null");
            throw new InvalidEntityException("Platform is null", ErrorCodes.USER_NOT_VALID);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Share profile for User with id {} on platform {}", userId, platform);

        // TODO : Partager le profil de l'utilisateur sur la plateforme choisie
        return getUserById(userId);
    }

    @Override
    public UserDto importUserData(Integer userId, String jsonData) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(jsonData == null) {
            log.error("Json data is null");
            throw new InvalidEntityException("Json data is null", ErrorCodes.USER_NOT_VALID);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Import user data for User with id {}", userId);

        // TODO : Importer les données de l'utilisateur depuis le fichier JSON
        return getUserById(userId);
    }

    @Override
    public String exportUserData(Integer userId) {
        if(userId == null) {
            log.error("User id is null");
            throw new EntityNotFoundException("User id is null", ErrorCodes.USER_NOT_FOUND);
        }
        if(!userRepository.existsById(userId)) {
            log.error("User with id {} not found", userId);
            throw new EntityNotFoundException("User with id " + userId + " not found", ErrorCodes.USER_NOT_FOUND);
        }

        log.info("Export user data for User with id {}", userId);

        // TODO : Exporter les données de l'utilisateur dans un fichier JSON
        return "";
    }

    @Override
    public UserDto changerMdp(ChangerMdpUserDto dto) {
        validate(dto);//verification entre le mot de passe et la confirmation
        Optional<User> userOptional = userRepository.findById(dto.getId()) ;
        if (userOptional.isEmpty()){
            log.warn("Aucun user trouvé avec l'ID {}", dto.getId());
            throw new EntityNotFoundException("Aucun user trouvé avec l'ID "+dto.getId(), ErrorCodes.USER_NOT_FOUND);
        }
        User user = userOptional.get();//recupération de l'utilisateur
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(encodedPassword);//modification du mot de passe

        return UserDto.fromEntity(userRepository.save(user));
    }

    private void validate(ChangerMdpUserDto dto) {
        if(dto == null) {
            log.warn("Impossible de modifier le mot de passe : ChangerMdpUserDto est null");
            throw new InvalidEntityException("Aucune info n'a été fournie pour changer de mdp",
                    ErrorCodes.USER_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
        if(dto.getId() == null) {
            log.warn("Impossible de modifier le mot de passe : L'ID de l'utilisateur est null");
            throw new InvalidEntityException("L'ID de l'utilisateur est null",
                    ErrorCodes.USER_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
        if(!StringUtils.hasLength(dto.getPassword()) || !StringUtils.hasLength(dto.getConfirmPassword())) {
            log.warn("Impossible de modifier le mot de passe : Le mot de passe ou la confirmation est null");
            throw new InvalidEntityException("Le mot de passe ou la confirmation est null",
                    ErrorCodes.USER_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
        if (!dto.getPassword().equals((dto.getConfirmPassword()))) {
            log.warn("Impossible de modifier le mot de passe : Le mot de passe et la confirmation ne correspondent pas");
            throw new InvalidEntityException("Le mot de passe et la confirmation ne correspondent pas",
                    ErrorCodes.USER_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
    }
}
