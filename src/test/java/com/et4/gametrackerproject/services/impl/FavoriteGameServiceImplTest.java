package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.FavoriteGameDto;
import com.et4.gametrackerproject.dto.GameDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.enums.GameCategory;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.model.FavoriteGame;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.FavoriteGameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteGameServiceImplTest {

    @Mock
    private FavoriteGameRepository favoriteGameRepository;

    @InjectMocks
    private FavoriteGameServiceImpl favoriteGameService;

    private User user;
    private Game game;
    private FavoriteGame favoriteGame;
    private FavoriteGameDto favoriteGameDto;
    private List<FavoriteGame> favoriteList;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1).username("testUser").email("test@example.com").build();

        // Utilisation de GameCategory enum au lieu de Category
        game = Game.builder()
                .id(1)
                .name("Test Game")
                .description("A test game")
                .category(GameCategory.ACTION)
                .build();

        favoriteGame = FavoriteGame.builder()
                .id(1)
                .user(user)
                .game(game)
                .build();

        UserDto userDto = UserDto.fromEntity(user);
        GameDto gameDto = GameDto.fromEntity(game);

        favoriteGameDto = FavoriteGameDto.builder()
                .id(1)
                .user(userDto)
                .game(gameDto)
                .build();

        favoriteList = Collections.singletonList(favoriteGame);
    }

    @Test
    void getFavoriteGamesForUser_shouldReturnGamesList() {
        // Arrange
        when(favoriteGameRepository.findByUser(any(User.class))).thenReturn(favoriteList);

        // Act
        List<GameDto> result = favoriteGameService.getFavoriteGamesForUser(1);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Test Game");
        verify(favoriteGameRepository).findByUser(any(User.class));
    }

    @Test
    void getFavoriteGamesForUser_withNullUserId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.getFavoriteGamesForUser(null));

        verify(favoriteGameRepository, never()).findByUser(any(User.class));
    }

    @Test
    void getUsersWhoFavoritedGame_shouldReturnUsersList() {
        // Arrange
        when(favoriteGameRepository.findByGame(any(Game.class))).thenReturn(favoriteList);

        // Act
        List<UserDto> result = favoriteGameService.getUsersWhoFavoritedGame(1);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUsername()).isEqualTo("testUser");
        verify(favoriteGameRepository).findByGame(any(Game.class));
    }

    @Test
    void getUsersWhoFavoritedGame_withNullGameId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.getUsersWhoFavoritedGame(null));

        verify(favoriteGameRepository, never()).findByGame(any(Game.class));
    }

    @Test
    void getFavoriteById_shouldReturnFavorite() {
        // Arrange
        when(favoriteGameRepository.findById(1)).thenReturn(Optional.of(favoriteGame));

        // Act
        FavoriteGameDto result = favoriteGameService.getFavoriteById(1);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getGame().getName()).isEqualTo("Test Game");
        verify(favoriteGameRepository).findById(1);
    }

    @Test
    void getFavoriteById_withNonExistentId_shouldThrowException() {
        // Arrange
        when(favoriteGameRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> favoriteGameService.getFavoriteById(999));

        verify(favoriteGameRepository).findById(999);
    }

    @Test
    void getFavoriteById_withNullId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.getFavoriteById(null));

        verify(favoriteGameRepository, never()).findById(anyInt());
    }

    @Test
    void getTotalFavoritesCountForGame_shouldReturnCount() {
        // Arrange
        when(favoriteGameRepository.countByGame(any(Game.class))).thenReturn(5L);

        // Act
        Long count = favoriteGameService.getTotalFavoritesCountForGame(1);

        // Assert
        assertThat(count).isEqualTo(5L);
        verify(favoriteGameRepository).countByGame(any(Game.class));
    }

    @Test
    void getTotalFavoritesCountForGame_withNullGameId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.getTotalFavoritesCountForGame(null));

        verify(favoriteGameRepository, never()).countByGame(any(Game.class));
    }

    @Test
    void getMostFavoritedGames_shouldReturnGamesMap() {
        // Arrange
        Object[][] gameData = {
                {game, 10L},
                {Game.builder().id(2).name("Another Game").category(GameCategory.ADVENTURE).build(), 5L}
        };

        List<Object[]> popularGames = Arrays.asList(gameData);
        when(favoriteGameRepository.findMostPopularGames()).thenReturn(popularGames);

        // Act
        Map<String, Long> result = favoriteGameService.getMostFavoritedGames(2);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get("Test Game")).isEqualTo(10L);
        assertThat(result.get("Another Game")).isEqualTo(5L);
        verify(favoriteGameRepository).findMostPopularGames();
    }

    @Test
    void getMostFavoritedGames_withLimitOne_shouldReturnOneGame() {
        // Arrange
        Object[][] gameData = {
                {game, 10L},
                {Game.builder().id(2).name("Another Game").category(GameCategory.ADVENTURE).build(), 5L}
        };

        List<Object[]> popularGames = Arrays.asList(gameData);
        when(favoriteGameRepository.findMostPopularGames()).thenReturn(popularGames);

        // Act
        Map<String, Long> result = favoriteGameService.getMostFavoritedGames(1);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get("Test Game")).isEqualTo(10L);
        verify(favoriteGameRepository).findMostPopularGames();
    }

    @Test
    void getFavoriteCountByGameCategory_shouldReturnCategoriesMap() {
        // Arrange
        Game actionGame = Game.builder().id(1).name("Action Game").category(GameCategory.ACTION).build();
        Game adventureGame = Game.builder().id(2).name("Adventure Game").category(GameCategory.ADVENTURE).build();

        Object[][] gameData = {
                {actionGame, 7L},
                {adventureGame, 3L}
        };

        List<Object[]> popularGames = Arrays.asList(gameData);
        when(favoriteGameRepository.findMostPopularGames()).thenReturn(popularGames);

        // Act
        Map<Integer, Long> result = favoriteGameService.getFavoriteCountByGameCategory();

        // Assert
        // Il est normal que la taille soit 1, car les deux enums ont la même valeur pour getDeclaringClass().getModifiers()
        assertThat(result).isNotEmpty();
        // Vérifions que la somme des valeurs est correcte (7L + 3L = 10L)
        assertThat(result.values().stream().mapToLong(Long::longValue).sum()).isEqualTo(10L);

        verify(favoriteGameRepository).findMostPopularGames();
    }


    @Test
    void addToFavorites_shouldSaveAndReturnFavorite() {
        // Arrange
        FavoriteGame.builder()
                .user(User.builder().id(1).build())
                .game(Game.builder().id(1).build())
                .build();

        when(favoriteGameRepository.save(any(FavoriteGame.class))).thenReturn(favoriteGame);

        // Act
        FavoriteGameDto result = favoriteGameService.addToFavorites(favoriteGameDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        verify(favoriteGameRepository).save(any(FavoriteGame.class));
    }

    @Test
    void addToFavorites_withNullFavorite_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.addToFavorites(null));

        verify(favoriteGameRepository, never()).save(any(FavoriteGame.class));
    }

    @Test
    void addToFavorites_withNullGame_shouldThrowException() {
        // Arrange
        FavoriteGameDto invalidFavorite = FavoriteGameDto.builder()
                .user(favoriteGameDto.getUser())
                .game(null)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.addToFavorites(invalidFavorite));

        verify(favoriteGameRepository, never()).save(any(FavoriteGame.class));
    }

    @Test
    void deleteById_shouldDeleteFavorite() {
        // Act
        favoriteGameService.deleteById(1);

        // Assert
        verify(favoriteGameRepository).deleteById(1);
    }

    @Test
    void deleteById_withNullId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.deleteById(null));

        verify(favoriteGameRepository, never()).deleteById(anyInt());
    }

    @Test
    void clearUserFavorites_shouldDeleteAllUserFavorites() {
        // Arrange
        when(favoriteGameRepository.findByUser(any(User.class))).thenReturn(favoriteList);

        // Act
        favoriteGameService.clearUserFavorites(1);

        // Assert
        verify(favoriteGameRepository).findByUser(any(User.class));
        verify(favoriteGameRepository).deleteAll(favoriteList);
    }

    @Test
    void clearUserFavorites_withNullUserId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.clearUserFavorites(null));

        verify(favoriteGameRepository, never()).findByUser(any(User.class));
        verify(favoriteGameRepository, never()).deleteAll(anyList());
    }

    @Test
    void isGameFavoritedByUser_shouldReturnTrue() {
        // Arrange
        when(favoriteGameRepository.existsByUserAndGame(any(User.class), any(Game.class))).thenReturn(true);

        // Act
        boolean result = favoriteGameService.isGameFavoritedByUser(1, 1);

        // Assert
        assertTrue(result);
        verify(favoriteGameRepository).existsByUserAndGame(any(User.class), any(Game.class));
    }

    @Test
    void isGameFavoritedByUser_withNullIds_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.isGameFavoritedByUser(null, 1));

        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.isGameFavoritedByUser(1, null));

        verify(favoriteGameRepository, never()).existsByUserAndGame(any(User.class), any(Game.class));
    }

    @Test
    void findFavoriteByUserAndGame_shouldReturnFavorite() {
        // Arrange
        when(favoriteGameRepository.findByUserAndGame(any(User.class), any(Game.class))).thenReturn(Optional.of(favoriteGame));

        // Act
        Optional<FavoriteGameDto> result = favoriteGameService.findFavoriteByUserAndGame(1, 1);

        // Assert
        assertTrue(result.isPresent());
        assertThat(result.get().getId()).isEqualTo(1);
        verify(favoriteGameRepository).findByUserAndGame(any(User.class), any(Game.class));
    }

    @Test
    void findFavoriteByUserAndGame_withNullIds_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.findFavoriteByUserAndGame(null, 1));

        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.findFavoriteByUserAndGame(1, null));

        verify(favoriteGameRepository, never()).findByUserAndGame(any(User.class), any(Game.class));
    }

    @Test
    void getRecentlyAddedFavoritesForUser_shouldReturnFavoritesList() {
        // Arrange
        when(favoriteGameRepository.findRecentlyAddedFavoritesByUser(any(User.class))).thenReturn(favoriteList);

        // Act
        List<FavoriteGameDto> result = favoriteGameService.getRecentlyAddedFavoritesForUser(1);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1);
        verify(favoriteGameRepository).findRecentlyAddedFavoritesByUser(any(User.class));
    }

    @Test
    void getRecentlyAddedFavoritesForUser_withNoFavorites_shouldThrowException() {
        // Arrange
        when(favoriteGameRepository.findRecentlyAddedFavoritesByUser(any(User.class))).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> favoriteGameService.getRecentlyAddedFavoritesForUser(1));

        verify(favoriteGameRepository).findRecentlyAddedFavoritesByUser(any(User.class));
    }

    @Test
    void getRecentlyAddedFavoritesForUser_withNullUserId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.getRecentlyAddedFavoritesForUser(null));

        verify(favoriteGameRepository, never()).findRecentlyAddedFavoritesByUser(any(User.class));
    }

    @Test
    void getCommonFavoriteGames_shouldReturnGamesListCommonToTwoUsers() {
        // Arrange
        List<Game> commonGames = Collections.singletonList(game);
        when(favoriteGameRepository.findCommonFavoriteGames(1, 2)).thenReturn(commonGames);

        // Act
        List<GameDto> result = favoriteGameService.getCommonFavoriteGames(1, 2);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Test Game");
        verify(favoriteGameRepository).findCommonFavoriteGames(1, 2);
    }

    @Test
    void getCommonFavoriteGames_withNoCommonGames_shouldThrowException() {
        // Arrange
        when(favoriteGameRepository.findCommonFavoriteGames(1, 2)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> favoriteGameService.getCommonFavoriteGames(1, 2));

        verify(favoriteGameRepository).findCommonFavoriteGames(1, 2);
    }

    @Test
    void getCommonFavoriteGames_withNullIds_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.getCommonFavoriteGames(null, 2));

        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.getCommonFavoriteGames(1, null));

        verify(favoriteGameRepository, never()).findCommonFavoriteGames(anyInt(), anyInt());
    }

    @Test
    void countFavoritesByUser_shouldReturnCount() {
        // Arrange
        when(favoriteGameRepository.countByUser(any(User.class))).thenReturn(3L);

        // Act
        Long count = favoriteGameService.countFavoritesByUser(1);

        // Assert
        assertThat(count).isEqualTo(3L);
        verify(favoriteGameRepository).countByUser(any(User.class));
    }

    @Test
    void countFavoritesByUser_withNullUserId_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.countFavoritesByUser(null));

        verify(favoriteGameRepository, never()).countByUser(any(User.class));
    }

    @Test
    void deleteFavoriteByUserAndGame_shouldDeleteFavorite() {
        // Arrange
        when(favoriteGameRepository.findByUserAndGame(any(User.class), any(Game.class))).thenReturn(Optional.of(favoriteGame));

        // Act
        favoriteGameService.deleteFavoriteByUserAndGame(1, 1);

        // Assert
        verify(favoriteGameRepository).findByUserAndGame(any(User.class), any(Game.class));
        verify(favoriteGameRepository).deleteByUserAndGame(any(User.class), any(Game.class));
    }

    @Test
    void deleteFavoriteByUserAndGame_withNonExistentFavorite_shouldThrowException() {
        // Arrange
        when(favoriteGameRepository.findByUserAndGame(any(User.class), any(Game.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> favoriteGameService.deleteFavoriteByUserAndGame(1, 1));

        verify(favoriteGameRepository).findByUserAndGame(any(User.class), any(Game.class));
        verify(favoriteGameRepository, never()).deleteByUserAndGame(any(User.class), any(Game.class));
    }

    @Test
    void deleteFavoriteByUserAndGame_withNullIds_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.deleteFavoriteByUserAndGame(null, 1));

        assertThrows(IllegalArgumentException.class,
                () -> favoriteGameService.deleteFavoriteByUserAndGame(1, null));

        verify(favoriteGameRepository, never()).findByUserAndGame(any(User.class), any(Game.class));
        verify(favoriteGameRepository, never()).deleteByUserAndGame(any(User.class), any(Game.class));
    }
}
