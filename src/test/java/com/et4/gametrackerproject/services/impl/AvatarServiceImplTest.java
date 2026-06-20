package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.AvatarDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.model.Avatar;
import com.et4.gametrackerproject.repository.AvatarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvatarServiceImplTest {

    @Mock
    private AvatarRepository avatarRepository;

    @InjectMocks
    private AvatarServiceImpl avatarService;

    private Avatar sampleAvatar;
    private AvatarDto sampleAvatarDto;

    @BeforeEach
    void setUp() {
        sampleAvatar = new Avatar();
        sampleAvatar.setId(1);
        sampleAvatar.setPhoto("base64encodedphoto");

        sampleAvatarDto = new AvatarDto();
        sampleAvatarDto.setId(1);
        sampleAvatarDto.setPhoto("base64encodedphoto");
    }

    @Nested
    @DisplayName("Tests pour getAvatarById")
    class GetAvatarByIdTests {

        @Test
        @DisplayName("Devrait retourner l'avatar quand il existe")
        void shouldReturnAvatarWhenExists() {
            // Arrange
            when(avatarRepository.findById(1)).thenReturn(Optional.of(sampleAvatar));

            // Act
            AvatarDto result = avatarService.getAvatarById(1);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getPhoto()).isEqualTo("base64encodedphoto");
            verify(avatarRepository).findById(1);
        }

        @Test
        @DisplayName("Devrait lancer EntityNotFoundException quand l'avatar n'existe pas")
        void shouldThrowExceptionWhenAvatarNotFound() {
            // Arrange
            when(avatarRepository.findById(999)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> avatarService.getAvatarById(999))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Aucun avatar trouvé avec l'ID 999");

            verify(avatarRepository).findById(999);
        }

        @Test
        @DisplayName("Devrait retourner null quand l'ID est null")
        void shouldReturnNullWhenIdIsNull() {
            // Act
            AvatarDto result = avatarService.getAvatarById(null);

            // Assert
            assertThat(result).isNull();
            verify(avatarRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("Tests pour getAllAvatars")
    class GetAllAvatarsTests {

        @Test
        @DisplayName("Devrait retourner tous les avatars")
        void shouldReturnAllAvatars() {
            // Arrange
            List<Avatar> avatars = Arrays.asList(
                    sampleAvatar,
                    createAvatar(2, "anotherphoto")
            );
            when(avatarRepository.findAll()).thenReturn(avatars);

            // Act
            List<AvatarDto> result = avatarService.getAllAvatars();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1);
            assertThat(result.get(1).getId()).isEqualTo(2);
            verify(avatarRepository).findAll();
        }

        @Test
        @DisplayName("Devrait lancer EntityNotFoundException quand aucun avatar trouvé")
        void shouldThrowExceptionWhenNoAvatarsFound() {
            // Arrange
            when(avatarRepository.findAll()).thenReturn(Collections.emptyList());

            // Act & Assert
            assertThatThrownBy(() -> avatarService.getAllAvatars())
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Aucun avatar trouvé");

            verify(avatarRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Tests pour uploadAvatar")
    class UploadAvatarTests {

        @Test
        @DisplayName("Devrait télécharger un avatar avec succès")
        void shouldUploadAvatarSuccessfully() {
            // Arrange
            MockMultipartFile file = new MockMultipartFile(
                    "avatar", "avatar.jpg", "image/jpeg", "test image content".getBytes()
            );

            when(avatarRepository.save(any(Avatar.class))).thenAnswer(invocation -> {
                Avatar savedAvatar = invocation.getArgument(0);
                savedAvatar.setId(1);
                return savedAvatar;
            });

            // Act
            AvatarDto result = avatarService.uploadAvatar(file);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getPhoto()).isNotEmpty(); // Base64 encoded content
            verify(avatarRepository).save(any(Avatar.class));
        }

        @Test
        @DisplayName("Devrait lancer IllegalArgumentException quand le fichier est null")
        void shouldThrowExceptionWhenFileIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> avatarService.uploadAvatar(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Le fichier ne peut être null ou vide");

            verify(avatarRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait lancer IllegalArgumentException quand le fichier est vide")
        void shouldThrowExceptionWhenFileIsEmpty() {
            // Arrange
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "avatar", "avatar.jpg", "image/jpeg", new byte[0]
            );

            // Act & Assert
            assertThatThrownBy(() -> avatarService.uploadAvatar(emptyFile))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Le fichier ne peut être null ou vide");

            verify(avatarRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests pour updateAvatar")
    class UpdateAvatarTests {

        @Test
        @DisplayName("Devrait mettre à jour l'avatar avec succès")
        void shouldUpdateAvatarSuccessfully() {
            // Arrange
            Integer id = 1;
            AvatarDto updatedDto = new AvatarDto();
            updatedDto.setId(id);
            updatedDto.setPhoto("updatedPhoto");

            Avatar existingAvatar = new Avatar();
            existingAvatar.setId(id);
            existingAvatar.setPhoto("oldPhoto");

            Avatar updatedAvatar = new Avatar();
            updatedAvatar.setId(id);
            updatedAvatar.setPhoto("updatedPhoto");

            when(avatarRepository.findById(id)).thenReturn(Optional.of(existingAvatar));
            when(avatarRepository.save(any(Avatar.class))).thenReturn(updatedAvatar);

            // Act
            AvatarDto result = avatarService.updateAvatar(id, updatedDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getPhoto()).isEqualTo("updatedPhoto");
            verify(avatarRepository).findById(id);
            verify(avatarRepository).save(any(Avatar.class));
        }

        @Test
        @DisplayName("Devrait lancer EntityNotFoundException quand l'avatar n'existe pas")
        void shouldThrowExceptionWhenAvatarNotFound() {
            // Arrange
            Integer id = 999;
            when(avatarRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> avatarService.updateAvatar(id, sampleAvatarDto))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Aucun avatar trouvé avec l'ID 999");

            verify(avatarRepository).findById(id);
            verify(avatarRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait lancer IllegalArgumentException quand l'ID est null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> avatarService.updateAvatar(null, sampleAvatarDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("L'ID de l'avatar ne peut être null");

            verify(avatarRepository, never()).findById(any());
            verify(avatarRepository, never()).save(any());
        }

        @Test
        @DisplayName("Devrait lancer IllegalArgumentException quand AvatarDto est null")
        void shouldThrowExceptionWhenDtoIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> avatarService.updateAvatar(1, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Les données de mise à jour de l'avatar ne peuvent être null");

            verify(avatarRepository, never()).findById(any());
            verify(avatarRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests pour deleteAvatar")
    class DeleteAvatarTests {

        @Test
        @DisplayName("Devrait supprimer l'avatar avec succès")
        void shouldDeleteAvatarSuccessfully() {
            // Arrange
            Integer id = 1;
            when(avatarRepository.findById(id)).thenReturn(Optional.of(sampleAvatar));
            doNothing().when(avatarRepository).delete(sampleAvatar);

            // Act
            avatarService.deleteAvatar(id);

            // Assert
            verify(avatarRepository).findById(id);
            verify(avatarRepository).delete(sampleAvatar);
        }

        @Test
        @DisplayName("Devrait lancer EntityNotFoundException quand l'avatar n'existe pas")
        void shouldThrowExceptionWhenAvatarNotFound() {
            // Arrange
            Integer id = 999;
            when(avatarRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> avatarService.deleteAvatar(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Aucun avatar trouvé avec l'ID 999");

            verify(avatarRepository).findById(id);
            verify(avatarRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Devrait lancer IllegalArgumentException quand l'ID est null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> avatarService.deleteAvatar(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("L'ID de l'avatar ne peut être null");

            verify(avatarRepository, never()).findById(any());
            verify(avatarRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Tests pour avatarExists")
    class AvatarExistsTests {

        @Test
        @DisplayName("Devrait retourner true quand l'avatar existe")
        void shouldReturnTrueWhenAvatarExists() {
            // Arrange
            Integer id = 1;
            when(avatarRepository.existsById(id)).thenReturn(true);

            // Act
            boolean result = avatarService.avatarExists(id);

            // Assert
            assertThat(result).isTrue();
            verify(avatarRepository).existsById(id);
        }

        @Test
        @DisplayName("Devrait retourner false quand l'avatar n'existe pas")
        void shouldReturnFalseWhenAvatarDoesNotExist() {
            // Arrange
            Integer id = 999;
            when(avatarRepository.existsById(id)).thenReturn(false);

            // Act
            boolean result = avatarService.avatarExists(id);

            // Assert
            assertThat(result).isFalse();
            verify(avatarRepository).existsById(id);
        }

        @Test
        @DisplayName("Devrait lancer IllegalArgumentException quand l'ID est null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> avatarService.avatarExists(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("L'ID de l'avatar ne peut être null");

            verify(avatarRepository, never()).existsById(any());
        }
    }

    @Nested
    @DisplayName("Tests pour getAllDefaultAvatars")
    class GetAllDefaultAvatarsTests {

        @Test
        @DisplayName("Devrait retourner tous les avatars par défaut")
        void shouldReturnAllDefaultAvatars() {
            // Arrange
            List<Avatar> allAvatars = Arrays.asList(
                    createAvatar(0, "default0"),
                    createAvatar(1, "default1"),
                    createAvatar(2, "default2"),
                    createAvatar(3, "default3"),
                    createAvatar(4, "notDefault")
            );
            when(avatarRepository.findAll()).thenReturn(allAvatars);

            // Act
            List<AvatarDto> result = avatarService.getAllDefaultAvatars();

            // Assert
            assertThat(result).hasSize(4);
            assertThat(result.get(0).getId()).isEqualTo(0);
            assertThat(result.get(1).getId()).isEqualTo(1);
            assertThat(result.get(2).getId()).isEqualTo(2);
            assertThat(result.get(3).getId()).isEqualTo(3);
            verify(avatarRepository).findAll();
        }

        @Test
        @DisplayName("Devrait lancer EntityNotFoundException quand aucun avatar par défaut trouvé")
        void shouldThrowExceptionWhenNoDefaultAvatarsFound() {
            // Arrange
            List<Avatar> allAvatars = Arrays.asList(
                    createAvatar(4, "notDefault"),
                    createAvatar(5, "notDefault2")
            );
            when(avatarRepository.findAll()).thenReturn(allAvatars);

            // Act & Assert
            assertThatThrownBy(() -> avatarService.getAllDefaultAvatars())
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Aucun avatar par défaut trouvé");

            verify(avatarRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Tests pour getUnusedAvatars")
    class GetUnusedAvatarsTests {

        @Test
        @DisplayName("Devrait retourner tous les avatars inutilisés")
        void shouldReturnAllUnusedAvatars() {
            // Arrange
            List<Avatar> unusedAvatars = Arrays.asList(
                    createAvatar(1, "unused1"),
                    createAvatar(2, "unused2")
            );
            when(avatarRepository.findUnusedAvatars()).thenReturn(unusedAvatars);

            // Act
            List<AvatarDto> result = avatarService.getUnusedAvatars();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1);
            assertThat(result.get(1).getId()).isEqualTo(2);
            verify(avatarRepository).findUnusedAvatars();
        }

        @Test
        @DisplayName("Devrait lancer EntityNotFoundException quand aucun avatar inutilisé trouvé")
        void shouldThrowExceptionWhenNoUnusedAvatarsFound() {
            // Arrange
            when(avatarRepository.findUnusedAvatars()).thenReturn(Collections.emptyList());

            // Act & Assert
            assertThatThrownBy(() -> avatarService.getUnusedAvatars())
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Aucun avatar inutilisé trouvé");

            verify(avatarRepository).findUnusedAvatars();
        }
    }

    @Nested
    @DisplayName("Tests pour getUserCountByAvatarId")
    class GetUserCountByAvatarIdTests {

        @Test
        @DisplayName("Devrait retourner le nombre d'utilisateurs pour un avatar")
        void shouldReturnUserCountForAvatar() {
            // Arrange
            Integer id = 1;
            when(avatarRepository.countUsersByAvatarId(id)).thenReturn(5L);

            // Act
            Long result = avatarService.getUserCountByAvatarId(id);

            // Assert
            assertThat(result).isEqualTo(5L);
            verify(avatarRepository).countUsersByAvatarId(id);
        }

        @Test
        @DisplayName("Devrait lancer IllegalArgumentException quand l'ID est null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> avatarService.getUserCountByAvatarId(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("L'ID de l'avatar ne peut être null");

            verify(avatarRepository, never()).countUsersByAvatarId(any());
        }
    }

    @Nested
    @DisplayName("Tests pour getMostPopularAvatars")
    class GetMostPopularAvatarsTests {

        @Test
        @DisplayName("Devrait retourner les avatars les plus populaires")
        void shouldReturnMostPopularAvatars() {
            // Arrange
            Avatar avatar1 = createAvatar(1, "popular1");
            Avatar avatar2 = createAvatar(2, "popular2");

            List<Object[]> popularResults = Arrays.asList(
                    new Object[]{avatar1, 10L},
                    new Object[]{avatar2, 5L}
            );
            when(avatarRepository.findAvatarsByPopularity()).thenReturn(popularResults);

            // Act
            List<AvatarDto> result = avatarService.getMostPopularAvatars();

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1);
            assertThat(result.get(1).getId()).isEqualTo(2);
            verify(avatarRepository).findAvatarsByPopularity();
        }

        @Test
        @DisplayName("Devrait lancer EntityNotFoundException quand aucun avatar populaire trouvé")
        void shouldThrowExceptionWhenNoPopularAvatarsFound() {
            // Arrange
            when(avatarRepository.findAvatarsByPopularity()).thenReturn(Collections.emptyList());

            // Act & Assert
            assertThatThrownBy(() -> avatarService.getMostPopularAvatars())
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Aucun avatar populaire trouvé");

            verify(avatarRepository).findAvatarsByPopularity();
        }
    }

    // Méthode utilitaire pour créer des avatars de test
    private Avatar createAvatar(Integer id, String photo) {
        Avatar avatar = new Avatar();
        avatar.setId(id);
        avatar.setPhoto(photo);
        return avatar;
    }
}
