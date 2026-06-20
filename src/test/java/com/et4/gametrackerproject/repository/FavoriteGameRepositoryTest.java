package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.enums.GameCategory;
import com.et4.gametrackerproject.model.FavoriteGame;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
class FavoriteGameRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("db_pgt_test")
            .withUsername("test")
            .withPassword("test")
            // Configuration supplémentaire pour MySQL
            .withCommand("--character-set-server=utf8mb4",
                    "--collation-server=utf8mb4_unicode_ci",
                    "--default-authentication-plugin=mysql_native_password");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                mysql.getJdbcUrl() + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private FavoriteGameRepository favoriteGameRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;
    private User user2;
    private User user3;
    private Game game1;
    private Game game2;
    private Game game3;
    private Game game4;
    private FavoriteGame favoriteGame1;
    private FavoriteGame favoriteGame2;
    private FavoriteGame favoriteGame3;
    private FavoriteGame favoriteGame4;
    private FavoriteGame favoriteGame5;

    @BeforeEach
    void setup() {
        // Nettoyer les données existantes
        entityManager.clear();

        // Créer des utilisateurs de test
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1 = entityManager.persistFlushFind(user1);

        user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2 = entityManager.persistFlushFind(user2);

        user3 = new User();
        user3.setUsername("user3");
        user3.setEmail("user3@example.com");
        user3.setPassword("password3");
        user3 = entityManager.persistFlushFind(user3);

        // Créer des jeux de test
        game1 = new Game();
        game1.setName("Game 1");
        game1.setDescription("Description 1");
        game1.setCategory(GameCategory.ACTION);
        game1.setCreationDate(Instant.now().minusSeconds(60*60*24*365));
        game1.setUrl("http://example1.com");
        game1 = entityManager.persistFlushFind(game1);

        game2 = new Game();
        game2.setName("Game 2");
        game2.setDescription("Description 2");
        game2.setCategory(GameCategory.ADVENTURE);
        game2.setCreationDate(Instant.now().minusSeconds(60*60*24*365*2));
        game2.setUrl("http://example2.com");
        game2 = entityManager.persistFlushFind(game2);

        game3 = new Game();
        game3.setName("Game 3");
        game3.setDescription("Description 3");
        game3.setCategory(GameCategory.RPG);
        game3.setCreationDate(Instant.now().minusSeconds(60*60*24*365*3));
        game3.setUrl("http://example3.com");
        game3 = entityManager.persistFlushFind(game3);

        game4 = new Game();
        game4.setName("Game 4");
        game4.setDescription("Description 4");
        game4.setCategory(GameCategory.STRATEGY);
        game4.setCreationDate(Instant.now().minusSeconds(60*60*24*365*4));
        game4.setUrl("http://example4.com");
        game4 = entityManager.persistFlushFind(game4);

        // Créer des favoris
        // user1 aime game1, game2
        favoriteGame1 = new FavoriteGame();
        favoriteGame1.setUser(user1);
        favoriteGame1.setGame(game1);
        favoriteGame1.setCreationDate(Instant.now().minusSeconds(60*60*24*5));
        favoriteGame1 = entityManager.persistFlushFind(favoriteGame1);

        favoriteGame2 = new FavoriteGame();
        favoriteGame2.setUser(user1);
        favoriteGame2.setGame(game2);
        favoriteGame2.setCreationDate(Instant.now().minusSeconds(60*60*24*4));
        favoriteGame2 = entityManager.persistFlushFind(favoriteGame2);

        // user2 aime game1, game3
        favoriteGame3 = new FavoriteGame();
        favoriteGame3.setUser(user2);
        favoriteGame3.setGame(game1);
        favoriteGame3.setCreationDate(Instant.now().minusSeconds(60*60*24*3));
        favoriteGame3 = entityManager.persistFlushFind(favoriteGame3);

        favoriteGame4 = new FavoriteGame();
        favoriteGame4.setUser(user2);
        favoriteGame4.setGame(game3);
        favoriteGame4.setCreationDate(Instant.now().minusSeconds(60*60*24*2));
        favoriteGame4 = entityManager.persistFlushFind(favoriteGame4);

        // user3 aime game3
        favoriteGame5 = new FavoriteGame();
        favoriteGame5.setUser(user3);
        favoriteGame5.setGame(game3);
        favoriteGame5.setCreationDate(Instant.now().minusSeconds(60*60*24));
        favoriteGame5 = entityManager.persistFlushFind(favoriteGame5);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findByUser_shouldReturnUserFavorites() {
        // Act
        List<FavoriteGame> user1Favorites = favoriteGameRepository.findByUser(user1);
        List<FavoriteGame> user2Favorites = favoriteGameRepository.findByUser(user2);

        // Assert
        assertThat(user1Favorites).hasSize(2);
        assertThat(user1Favorites)
                .extracting(favorite -> favorite.getGame().getName())
                .containsExactlyInAnyOrder("Game 1", "Game 2");

        assertThat(user2Favorites).hasSize(2);
        assertThat(user2Favorites)
                .extracting(favorite -> favorite.getGame().getName())
                .containsExactlyInAnyOrder("Game 1", "Game 3");
    }

    @Test
    void findByGame_shouldReturnGameFavorites() {
        // Act
        List<FavoriteGame> game1Favorites = favoriteGameRepository.findByGame(game1);
        List<FavoriteGame> game3Favorites = favoriteGameRepository.findByGame(game3);
        List<FavoriteGame> game4Favorites = favoriteGameRepository.findByGame(game4);

        // Assert
        assertThat(game1Favorites).hasSize(2);
        assertThat(game1Favorites)
                .extracting(favorite -> favorite.getUser().getUsername())
                .containsExactlyInAnyOrder("user1", "user2");

        assertThat(game3Favorites).hasSize(2);
        assertThat(game3Favorites)
                .extracting(favorite -> favorite.getUser().getUsername())
                .containsExactlyInAnyOrder("user2", "user3");

        assertThat(game4Favorites).isEmpty();
    }

    @Test
    void findMostPopularGames_shouldReturnOrderedByPopularity() {
        // Act
        List<Object[]> popularGames = favoriteGameRepository.findMostPopularGames();

        // Assert
        assertThat(popularGames).hasSize(3); // 3 jeux ont des favoris

        // Le jeu le plus populaire (2 favoris)
        Object[] mostPopular1 = popularGames.getFirst();
        Game topGame1 = (Game) mostPopular1[0];
        Long count1 = (Long) mostPopular1[1];
        assertThat(topGame1.getName()).isIn("Game 1", "Game 3"); // Game 1 et Game 3 ont 2 favoris chacun
        assertThat(count1).isEqualTo(2L);

        // Le deuxième jeu le plus populaire (2 favoris aussi)
        Object[] mostPopular2 = popularGames.get(1);
        Game topGame2 = (Game) mostPopular2[0];
        Long count2 = (Long) mostPopular2[1];
        assertThat(topGame2.getName()).isIn("Game 1", "Game 3");
        assertThat(count2).isEqualTo(2L);

        // Le troisième jeu le plus populaire (1 favori)
        Object[] mostPopular3 = popularGames.get(2);
        Game topGame3 = (Game) mostPopular3[0];
        Long count3 = (Long) mostPopular3[1];
        assertThat(topGame3.getName()).isEqualTo("Game 2");
        assertThat(count3).isEqualTo(1L);
    }

    @Test
    void findByUserAndGame_shouldReturnCorrectFavorite() {
        // Act
        Optional<FavoriteGame> found1 = favoriteGameRepository.findByUserAndGame(user1, game1);
        Optional<FavoriteGame> found2 = favoriteGameRepository.findByUserAndGame(user1, game3);

        // Assert
        assertTrue(found1.isPresent());
        assertThat(found1.get().getUser().getUsername()).isEqualTo("user1");
        assertThat(found1.get().getGame().getName()).isEqualTo("Game 1");

        assertFalse(found2.isPresent()); // user1 n'a pas game3 en favori
    }

    @Test
    void findRecentlyAddedFavoritesByUser_shouldReturnOrderedByDate() {
        // Act
        List<FavoriteGame> recentFavoritesUser1 = favoriteGameRepository.findRecentlyAddedFavoritesByUser(user1);

        // Assert
        assertThat(recentFavoritesUser1).hasSize(2);

        // Vérifier que les favoris sont ordonnés par date de création (du plus récent au plus ancien)
        assertThat(recentFavoritesUser1.get(0).getCreationDate())
                .isAfterOrEqualTo(recentFavoritesUser1.get(1).getCreationDate());

        // Vérifier les jeux dans la liste
        assertThat(recentFavoritesUser1)
                .extracting(favorite -> favorite.getGame().getName())
                .containsExactly("Game 2", "Game 1"); // Ordre basé sur les dates définies dans setup
    }

    @Test
    void findCommonFavoriteGames_shouldReturnCommonGames() {
        // Act
        List<Game> commonGamesUser1User2 = favoriteGameRepository.findCommonFavoriteGames(user1.getId(), user2.getId());
        List<Game> commonGamesUser1User3 = favoriteGameRepository.findCommonFavoriteGames(user1.getId(), user3.getId());
        List<Game> commonGamesUser2User3 = favoriteGameRepository.findCommonFavoriteGames(user2.getId(), user3.getId());

        // Assert
        assertThat(commonGamesUser1User2).hasSize(1);
        assertThat(commonGamesUser1User2.getFirst().getName()).isEqualTo("Game 1");

        assertThat(commonGamesUser1User3).isEmpty(); // Pas de jeux en commun

        assertThat(commonGamesUser2User3).hasSize(1);
        assertThat(commonGamesUser2User3.getFirst().getName()).isEqualTo("Game 3");
    }

    @Test
    void countByGame_shouldReturnCorrectCount() {
        // Act
        Long game1Count = favoriteGameRepository.countByGame(game1);
        Long game2Count = favoriteGameRepository.countByGame(game2);
        Long game3Count = favoriteGameRepository.countByGame(game3);
        Long game4Count = favoriteGameRepository.countByGame(game4);

        // Assert
        assertThat(game1Count).isEqualTo(2); // user1 et user2
        assertThat(game2Count).isEqualTo(1); // user1
        assertThat(game3Count).isEqualTo(2); // user2 et user3
        assertThat(game4Count).isEqualTo(0); // aucun
    }

    @Test
    void countByUser_shouldReturnCorrectCount() {
        // Act
        Long user1Count = favoriteGameRepository.countByUser(user1);
        Long user2Count = favoriteGameRepository.countByUser(user2);
        Long user3Count = favoriteGameRepository.countByUser(user3);

        // Assert
        assertThat(user1Count).isEqualTo(2); // game1, game2
        assertThat(user2Count).isEqualTo(2); // game1, game3
        assertThat(user3Count).isEqualTo(1); // game3
    }

    @Test
    void existsByUserAndGame_shouldReturnCorrectResult() {
        // Act
        boolean exists1 = favoriteGameRepository.existsByUserAndGame(user1, game1);
        boolean exists2 = favoriteGameRepository.existsByUserAndGame(user1, game3);
        boolean exists3 = favoriteGameRepository.existsByUserAndGame(user3, game3);

        // Assert
        assertTrue(exists1);   // user1 a game1 en favori
        assertFalse(exists2);  // user1 n'a pas game3 en favori
        assertTrue(exists3);   // user3 a game3 en favori
    }

    @Test
    void deleteByUserAndGame_shouldRemoveFavorite() {
        // Verify initial state
        assertTrue(favoriteGameRepository.existsByUserAndGame(user1, game1));

        // Act
        favoriteGameRepository.deleteByUserAndGame(user1, game1);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertFalse(favoriteGameRepository.existsByUserAndGame(user1, game1));

        // Vérifier que les autres favoris ne sont pas affectés
        assertTrue(favoriteGameRepository.existsByUserAndGame(user1, game2));
        assertTrue(favoriteGameRepository.existsByUserAndGame(user2, game1));
    }

    @Test
    void save_shouldCreateNewFavorite() {
        // Arrange
        FavoriteGame newFavorite = new FavoriteGame();
        newFavorite.setUser(user3);
        newFavorite.setGame(game2);
        newFavorite.setCreationDate(Instant.now());

        // Vérifier qu'il n'existe pas déjà
        assertFalse(favoriteGameRepository.existsByUserAndGame(user3, game2));

        // Act
        FavoriteGame savedFavorite = favoriteGameRepository.save(newFavorite);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertTrue(favoriteGameRepository.existsByUserAndGame(user3, game2));

        Optional<FavoriteGame> retrieved = favoriteGameRepository.findByUserAndGame(user3, game2);
        assertTrue(retrieved.isPresent());
        assertThat(retrieved.get().getId()).isEqualTo(savedFavorite.getId());
    }

    @Test
    void findById_shouldReturnFavoriteGame_whenIdExists() {
        // When
        Optional<FavoriteGame> foundFavoriteGame = favoriteGameRepository.findById(favoriteGame1.getId());

        // Then
        assertThat(foundFavoriteGame).isPresent();
        assertThat(foundFavoriteGame.get().getId()).isEqualTo(favoriteGame1.getId());
        assertThat(foundFavoriteGame.get().getUser().getId()).isEqualTo(user1.getId());
        assertThat(foundFavoriteGame.get().getGame().getId()).isEqualTo(game1.getId());
    }

    @Test
    void findById_shouldReturnEmpty_whenIdDoesNotExist() {
        // Given
        Integer nonExistentId = 999;

        // When
        Optional<FavoriteGame> foundFavoriteGame = favoriteGameRepository.findById(nonExistentId);

        // Then
        assertThat(foundFavoriteGame).isEmpty();
    }

    @Test
    void save_shouldPersistNewFavoriteGame() {
        // Given
        FavoriteGame newFavoriteGame = new FavoriteGame();
        newFavoriteGame.setUser(user3);
        newFavoriteGame.setGame(game4);
        newFavoriteGame.setCreationDate(Instant.now());

        // When
        FavoriteGame savedFavoriteGame = favoriteGameRepository.save(newFavoriteGame);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<FavoriteGame> foundFavoriteGame = favoriteGameRepository.findById(savedFavoriteGame.getId());
        assertThat(foundFavoriteGame).isPresent();
        assertThat(foundFavoriteGame.get().getUser().getId()).isEqualTo(user3.getId());
        assertThat(foundFavoriteGame.get().getGame().getId()).isEqualTo(game4.getId());
    }

    @Test
    void save_shouldUpdateExistingFavoriteGame() {
        // Given
        FavoriteGame existingFavoriteGame = favoriteGameRepository.findById(favoriteGame1.getId()).orElseThrow();
        Instant newDate = Instant.now();
        existingFavoriteGame.setCreationDate(newDate);

        // When
        favoriteGameRepository.save(existingFavoriteGame);
        entityManager.flush();
        entityManager.clear();

        // Then
        FavoriteGame updatedFavoriteGame = favoriteGameRepository.findById(favoriteGame1.getId()).orElseThrow();

        // Utiliser isCloseTo pour comparer les instants avec une tolérance
        // OU utiliser truncatedTo pour comparer à une précision plus grossière
        assertThat(updatedFavoriteGame.getCreationDate())
                .isCloseTo(newDate, within(1, ChronoUnit.SECONDS));

        // Alternative :
        // assertThat(updatedFavoriteGame.getCreationDate().truncatedTo(ChronoUnit.SECONDS))
        //    .isEqualTo(newDate.truncatedTo(ChronoUnit.SECONDS));
    }


    @Test
    void deleteById_shouldRemoveFavoriteGame() {
        // When
        favoriteGameRepository.deleteById(favoriteGame1.getId());
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<FavoriteGame> deletedFavoriteGame = favoriteGameRepository.findById(favoriteGame1.getId());
        assertThat(deletedFavoriteGame).isEmpty();

        // Vérifier que seul cet élément a été supprimé
        assertThat(favoriteGameRepository.count()).isEqualTo(4); // 5 - 1
    }

    @Test
    void deleteAll_shouldRemoveAllFavoriteGames() {
        // When
        favoriteGameRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(favoriteGameRepository.count()).isEqualTo(0);
        assertThat(favoriteGameRepository.findAll()).isEmpty();
    }

}
