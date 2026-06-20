package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.FriendshipDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.enums.FriendshipStatus;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Friendship;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.FriendshipRepository;
import com.et4.gametrackerproject.repository.UserRepository;
import com.et4.gametrackerproject.services.FriendshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    //======================== CREATE/DELETE/UPLOAD ===================
    private static final Logger log = LoggerFactory.getLogger(FriendshipServiceImpl.class);
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public FriendshipServiceImpl(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    @Override
    public FriendshipDto createFriendship(FriendshipDto friendshipDto) {
        if (friendshipDto == null) {
            log.error("Les données de la relation d'amitié sont null");
            throw new IllegalArgumentException("Les données de la relation d'amitié ne peuvent être null");
        }
        // Convert DTO to entity
        Friendship friendship = FriendshipDto.toEntity(friendshipDto);
        // Persist the new friendship
        friendship = friendshipRepository.save(friendship);
        log.info("Création d'une relation d'amitié avec l'ID {}", friendship.getId());
        return FriendshipDto.fromEntity(friendship);
    }

    @Override
    public FriendshipDto updateFriendshipStatus(Integer friendshipId, FriendshipStatus newStatus) {
        if (friendshipId == null) {
            log.error("L'ID de la relation d'amitié est null");
            throw new IllegalArgumentException("L'ID de la relation d'amitié ne peut être null");
        }
        if (newStatus == null) {
            log.error("Le nouveau statut de la relation d'amitié est null");
            throw new IllegalArgumentException("Le nouveau statut ne peut être null");
        }
        // Find existing friendship by ID
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Aucune relation d'amitié trouvée avec l'ID " + friendshipId));
        // Update the status and persist the changes
        friendship.setStatus(newStatus);
        Friendship updatedFriendship = friendshipRepository.save(friendship);
        log.info("Mise à jour du statut de la relation d'amitié {} vers {}", friendshipId, newStatus);
        return FriendshipDto.fromEntity(updatedFriendship);
    }

    @Override
    public void deleteFriendshipById(Integer friendshipId) {
        if (friendshipId == null) {
            log.error("L'ID de la relation d'amitié est null");
            throw new IllegalArgumentException("L'ID de la relation d'amitié ne peut être null");
        }
        // Retrieve the friendship to ensure it exists before deletion
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Aucune relation d'amitié trouvée avec l'ID " + friendshipId));
        friendshipRepository.delete(friendship);
        log.info("Relation d'amitié avec l'ID {} supprimée avec succès", friendshipId);

        //Verifie pour les 2 amis
        Optional<User> users = userRepository.findByFriendshipId(friendshipId);
        if (users.isPresent()) {
            log.error("Impossible de supprimer l'utilisateur car il a des jeux favoris");
            throw new InvalidOperationException("Cet utilisateur a des jeux favoris, impossible de le supprimer",
                    ErrorCodes.FRIENDSHIP_ALREADY_USED);
        }
    }

    //=========================================== GETTER ===================================

    @Override
    public FriendshipDto getFriendshipById(Integer friendshipId) {
        if (friendshipId == null) {
            log.error("L'ID de la relation d'amitié est null");
            throw new IllegalArgumentException("L'ID de la relation d'amitié ne peut être null");
        }
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Aucune relation d'amitié trouvée avec l'ID " + friendshipId));
        return FriendshipDto.fromEntity(friendship);
    }

    @Override
    public List<FriendshipDto> getAllFriendshipsForUser(Integer userId) {
        if (userId == null) {
            log.error("L'ID utilisateur est null");
            throw new IllegalArgumentException("L'ID utilisateur ne peut être null");
        }
        // Retrieve all friendships where the user is either user1 or user2
        List<Friendship> friendships = friendshipRepository.findByUser1IdOrUser2Id(userId, userId);
        return friendships.stream()
                .map(FriendshipDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendshipDto> getFriendshipsForUserByStatus(Integer userId, FriendshipStatus status) {
        if (userId == null) {
            log.error("L'ID utilisateur est null");
            throw new IllegalArgumentException("L'ID utilisateur ne peut être null");
        }
        if (status == null) {
            log.error("Le statut de la relation d'amitié est null");
            throw new IllegalArgumentException("Le statut de la relation d'amitié ne peut être null");
        }
        // Retrieve all friendships for the user (as user1 or user2) with the given status
        List<Friendship> friendships = friendshipRepository.findAllByUserAndStatus(userId, status);
        return friendships.stream()
                .map(FriendshipDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public FriendshipDto getFriendshipBetweenUsers(Integer user1Id, Integer user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("Les IDs des utilisateurs ne peuvent être null");
        }
        User user1 = User.builder().id(user1Id).build();
        User user2 = User.builder().id(user2Id).build();

        Optional<Friendship> friendshipOpt = friendshipRepository.findFriendship(user1, user2);
        return friendshipOpt
                .map(FriendshipDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Aucune relation d'amitié trouvée entre l'utilisateur "
                        + user1Id + " et l'utilisateur " + user2Id));
    }

    @Override
    public List<UserDto> getPendingRequests(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID utilisateur ne peut être null");
        }
        // Assuming pending requests are those where the user is the receiver (user2) and status is PENDING.
        List<Friendship> pendingFriendships = friendshipRepository.findPendingRequestsByReceiver(
                User.builder().id(userId).build());
        // Return the sender's details (user1) as the pending request initiators.
        return pendingFriendships.stream()
                .map(f -> UserDto.fromEntity(f.getUser1()))
                .collect(Collectors.toList());
    }

    @Override
    public int getFriendCount(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID utilisateur ne peut être null");
        }
        Long count = friendshipRepository.countFriendsByUser(User.builder().id(userId).build());
        return (count != null) ? count.intValue() : 0;
    }

    @Override
    public List<FriendshipDto> getAllFriendships() {
        List<Friendship> friendships = friendshipRepository.findAll();
        return friendships.stream()
                .map(FriendshipDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public FriendshipStatus getRelationshipStatus(Integer user1Id, Integer user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("Les IDs des utilisateurs ne peuvent être null");
        }
        User user1 = User.builder().id(user1Id).build();
        User user2 = User.builder().id(user2Id).build();

        // Retrieve the friendship between the two users (in any direction)
        Optional<Friendship> friendshipOpt = friendshipRepository.findFriendship(user1, user2);
        if (friendshipOpt.isPresent()) {
            return friendshipOpt.get().getStatus();
        } else {
            // Option 1: Throw an exception if no friendship exists
            throw new EntityNotFoundException("Aucune relation d'amitié trouvée entre l'utilisateur "
                    + user1Id + " et l'utilisateur " + user2Id);

        }
    }

    @Override
    public List<UserDto> getFriendsList(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID utilisateur ne peut être null");
        }
        // Retrieve accepted friendships (where status is 'ACCEPTED') for the given user
        List<User> friends = friendshipRepository.findAcceptedFriends(User.builder().id(userId).build());
        if (friends.isEmpty()) {
            throw new EntityNotFoundException("Aucun ami trouvé pour l'utilisateur " + userId);
        }
        return friends.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendshipDto> searchFriendshipsByUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut être null ou vide");
        }
        // Retrieve all friendships and filter those that involve a user whose username contains the search term.
        List<Friendship> allFriendships = friendshipRepository.findAll();
        List<Friendship> filtered = allFriendships.stream()
                .filter(f -> f.getUser1().getUsername().toLowerCase().contains(username.toLowerCase())
                        || f.getUser2().getUsername().toLowerCase().contains(username.toLowerCase()))
                .toList();
        return filtered.stream()
                .map(FriendshipDto::fromEntity)
                .collect(Collectors.toList());
    }

    //====================================== ACTIONS SUR LES AMITIES ===================================


    @Override
    public FriendshipDto sendFriendRequest(Integer senderId, Integer receiverId) {
        if (senderId == null || receiverId == null) {
            throw new IllegalArgumentException("Les IDs de l'expéditeur et du destinataire ne peuvent être null");
        }
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Un utilisateur ne peut pas s'envoyer une demande d'amitié à lui-même");
        }

        User sender = User.builder().id(senderId).build();
        User receiver = User.builder().id(receiverId).build();

        // Check if a friendship already exists (in either direction)
        Optional<Friendship> existing = friendshipRepository.findFriendship(sender, receiver);
        if (existing.isPresent()) {
            throw new IllegalStateException("Une relation d'amitié existe déjà entre ces utilisateurs");
        }

        // Create a new friendship with status PENDING and current creation date.
        Friendship friendship = Friendship.builder()
                .user1(sender)  // Assuming sender is always stored as user1
                .user2(receiver)
                .status(FriendshipStatus.PENDING)
                .creationDate(java.time.Instant.now())
                .build();

        Friendship saved = friendshipRepository.save(friendship);
        return FriendshipDto.fromEntity(saved);
    }

    @Override
    public FriendshipDto acceptFriendRequest(Integer friendshipId) {
        if (friendshipId == null) {
            throw new IllegalArgumentException("L'ID de la demande d'amitié ne peut être null");
        }

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Aucune demande d'amitié trouvée avec l'ID " + friendshipId));

        if (!FriendshipStatus.PENDING.equals(friendship.getStatus())) {
            throw new IllegalStateException("La demande d'amitié n'est pas en attente et ne peut pas être acceptée");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        Friendship updated = friendshipRepository.save(friendship);
        return FriendshipDto.fromEntity(updated);
    }

    @Override
    public FriendshipDto rejectFriendRequest(Integer friendshipId) {
        if (friendshipId == null) {
            throw new IllegalArgumentException("L'ID de la demande d'amitié ne peut être null");
        }

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Aucune demande d'amitié trouvée avec l'ID " + friendshipId));

        if (!FriendshipStatus.PENDING.equals(friendship.getStatus())) {
            throw new IllegalStateException("La demande d'amitié n'est pas en attente et ne peut pas être rejetée");
        }

        friendship.setStatus(FriendshipStatus.DECLINED);
        Friendship updated = friendshipRepository.save(friendship);
        return FriendshipDto.fromEntity(updated);
    }

    @Override
    public FriendshipDto cancelFriendship(Integer userId, Integer friendId) {
        if (userId == null || friendId == null) {
            throw new IllegalArgumentException("Les IDs utilisateur et ami ne peuvent être null");
        }

        User user1 = User.builder().id(userId).build();
        User user2 = User.builder().id(friendId).build();

        // Find the friendship in either direction
        Friendship friendship = friendshipRepository.findFriendship(user1, user2)
                .orElseThrow(() -> new EntityNotFoundException("Aucune relation d'amitié trouvée entre l'utilisateur "
                        + userId + " et l'utilisateur " + friendId));

        // Remove the friendship (cancel friendship means deletion)
        friendshipRepository.delete(friendship);

        // Optionally, return the DTO of the cancelled friendship
        return FriendshipDto.fromEntity(friendship);
    }

    //============================= UTILITAIRES ======================

    @Override
    public boolean friendshipExists(Integer user1Id, Integer user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("Les IDs des utilisateurs ne peuvent être null");
        }
        User user1 = User.builder().id(user1Id).build();
        User user2 = User.builder().id(user2Id).build();
        return friendshipRepository.existsFriendship(user1, user2);
    }

    @Override
    public boolean hasPendingRequestBetween(Integer user1Id, Integer user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("Les IDs des utilisateurs ne peuvent être null");
        }
        User user1 = User.builder().id(user1Id).build();
        User user2 = User.builder().id(user2Id).build();
        // Retrieve the friendship between the two users (in any order)
        return friendshipRepository.findFriendship(user1, user2)
                .map(f -> FriendshipStatus.PENDING.equals(f.getStatus()))
                .orElse(false);
    }

    //============================= AMIS EN COMMUNS + SUGGEST  ======================

    @Override
    public List<UserDto> getMutualFriends(Integer userId1, Integer userId2) {
        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("Les IDs des utilisateurs ne peuvent être null");
        }
        // Call the repository method to get mutual friends
        List<User> mutualFriends = friendshipRepository.findMutualFriends(userId1, userId2);
        if (mutualFriends.isEmpty()) {
            throw new EntityNotFoundException("Aucun ami en commun trouvé entre l'utilisateur "
                    + userId1 + " et l'utilisateur " + userId2);
        }
        // Convert each User to a UserDto
        return mutualFriends.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> suggestFriends(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID utilisateur ne peut être null");
        }
        // Call the repository method to get friend suggestions
        List<Object[]> suggestions = friendshipRepository.suggestFriends(userId);
        if (suggestions.isEmpty()) {
            throw new EntityNotFoundException("Aucune suggestion d'amis trouvée pour l'utilisateur " + userId);
        }
        // Each row contains [User, mutual_count]. We'll convert the User entity to UserDto.
        return suggestions.stream()
                .map(row -> UserDto.fromEntity((User) row[0]))
                .collect(Collectors.toList());
    }

}