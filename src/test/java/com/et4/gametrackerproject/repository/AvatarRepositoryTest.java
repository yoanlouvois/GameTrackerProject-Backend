package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.model.Avatar;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class AvatarRepositoryTest {

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
    private AvatarRepository avatarRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Avatar avatar1;
    private Avatar avatar2;
    private Avatar avatar3;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setup() {
        // Nettoyer les données existantes
        entityManager.clear();

        // Créer les avatars de test
        avatar1 = new Avatar();
        avatar1.setPhoto("photo1.jpg");

        avatar2 = new Avatar();
        avatar2.setPhoto("photo2.jpg");

        avatar3 = new Avatar();
        avatar3.setPhoto("photo3.jpg");

        // Persister les avatars
        avatar1 = entityManager.persistFlushFind(avatar1);
        avatar2 = entityManager.persistFlushFind(avatar2);
        avatar3 = entityManager.persistFlushFind(avatar3);

        // Créer des utilisateurs de test
        user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setAvatar(avatar1);

        user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setAvatar(avatar1); // Même avatar qu'user1

        user3 = new User();
        user3.setUsername("user3");
        user3.setEmail("user3@example.com");
        user3.setPassword("password3");
        user3.setAvatar(avatar2);

        // Persister les utilisateurs
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        // S'assurer que tout est enregistré en base
        entityManager.flush();
    }

    @Test
    void findUnusedAvatars_shouldReturnOnlyUnusedAvatars() {
        // Act
        List<Avatar> unusedAvatars = avatarRepository.findUnusedAvatars();

        // Assert
        assertThat(unusedAvatars).hasSize(1);
        assertThat(unusedAvatars).extracting(Avatar::getId).contains(avatar3.getId());
        assertThat(unusedAvatars).extracting(Avatar::getPhoto).contains("photo3.jpg");
    }

    @Test
    void countUsersByAvatarId_shouldReturnCorrectCount() {
        // Act
        Long countForAvatar1 = avatarRepository.countUsersByAvatarId(avatar1.getId());
        Long countForAvatar2 = avatarRepository.countUsersByAvatarId(avatar2.getId());
        Long countForAvatar3 = avatarRepository.countUsersByAvatarId(avatar3.getId());

        // Assert
        assertThat(countForAvatar1).isEqualTo(2L);
        assertThat(countForAvatar2).isEqualTo(1L);
        assertThat(countForAvatar3).isEqualTo(0L);
    }

    @Test
    void findAvatarsByPopularity_shouldReturnAvatarsOrderedByUserCount() {
        // Act
        List<Object[]> results = avatarRepository.findAvatarsByPopularity();

        // Assert
        assertThat(results).hasSize(3); // Tous les avatars, même ceux sans utilisateurs

        // Premier résultat devrait être avatar1 (2 utilisateurs)
        Avatar mostPopularAvatar = (Avatar) results.getFirst()[0];
        Long mostPopularCount = (Long) results.getFirst()[1];
        assertThat(mostPopularAvatar.getId()).isEqualTo(avatar1.getId());
        assertThat(mostPopularCount).isEqualTo(2L);

        // Deuxième résultat devrait être avatar2 (1 utilisateur)
        Avatar secondAvatar = (Avatar) results.get(1)[0];
        Long secondCount = (Long) results.get(1)[1];
        assertThat(secondAvatar.getId()).isEqualTo(avatar2.getId());
        assertThat(secondCount).isEqualTo(1L);

        // Troisième résultat devrait être avatar3 (0 utilisateur)
        Avatar thirdAvatar = (Avatar) results.get(2)[0];
        Long thirdCount = (Long) results.get(2)[1];
        assertThat(thirdAvatar.getId()).isEqualTo(avatar3.getId());
        assertThat(thirdCount).isEqualTo(0L);
    }

    @Test
    void findAll_shouldReturnAllAvatars() {
        // Act
        List<Avatar> allAvatars = avatarRepository.findAll();

        // Assert
        assertThat(allAvatars).hasSize(3);
        assertThat(allAvatars).extracting(Avatar::getId)
                .containsExactlyInAnyOrder(avatar1.getId(), avatar2.getId(), avatar3.getId());
    }

    @Test
    void findById_shouldReturnCorrectAvatar() {
        // Act
        Avatar foundAvatar = avatarRepository.findById(avatar1.getId()).orElse(null);

        // Assert
        assertThat(foundAvatar).isNotNull();
        assertThat(foundAvatar.getId()).isEqualTo(avatar1.getId());
        assertThat(foundAvatar.getPhoto()).isEqualTo("photo1.jpg");
    }

    @Test
    void save_shouldCreateNewAvatar() {
        // Arrange
        Avatar newAvatar = new Avatar();
        newAvatar.setPhoto("new_photo.jpg");

        // Act
        Avatar savedAvatar = avatarRepository.save(newAvatar);
        entityManager.flush();  // Ne supprimez pas cette ligne critique

        entityManager.clear();
        Avatar retrievedAvatar = entityManager.find(Avatar.class, savedAvatar.getId());

        // Assert
        assertThat(retrievedAvatar).isNotNull();
        assertThat(retrievedAvatar.getId()).isNotNull();
        assertThat(retrievedAvatar.getPhoto()).isEqualTo("new_photo.jpg");
    }


    @Test
    void update_shouldUpdateExistingAvatar() {
        // Arrange
        avatar1.setPhoto("updated_photo.jpg");

        // Act
        Avatar updatedAvatar = avatarRepository.save(avatar1);

        // Force refresh from database
        entityManager.flush();
        entityManager.clear();
        Avatar retrievedAvatar = entityManager.find(Avatar.class, avatar1.getId());

        // Assert
        assertThat(retrievedAvatar).isNotNull();
        assertThat(retrievedAvatar.getId()).isEqualTo(avatar1.getId());
        assertThat(retrievedAvatar.getPhoto()).isEqualTo("updated_photo.jpg");
    }

    @Test
    void delete_shouldRemoveAvatar() {
        // Arrange — créer un avatar sans utilisateurs pour le supprimer
        Avatar avatarToDelete = new Avatar();
        avatarToDelete.setPhoto("delete_me.jpg");
        avatarToDelete = entityManager.persistFlushFind(avatarToDelete);

        // Act
        avatarRepository.delete(avatarToDelete);
        entityManager.flush();

        // Assert
        Avatar retrievedAvatar = entityManager.find(Avatar.class, avatarToDelete.getId());
        assertThat(retrievedAvatar).isNull();
    }
}
