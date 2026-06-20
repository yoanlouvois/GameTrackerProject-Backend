package com.et4.gametrackerproject.services.strategy;

import com.et4.gametrackerproject.dto.AchievementDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.services.AchievementService;
import com.et4.gametrackerproject.services.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaveAchievementPhotoTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private SaveAchievementPhoto saveAchievementPhoto;

    private InputStream photoStream;
    private final Integer achievementId = 1;
    private final String photoTitle = "test-achievement-icon";
    private final String cloudinaryUrl = "https://cloudinary.com/test-icon.jpg";
    private AchievementDto existingAchievement;
    private AchievementDto updatedAchievement;

    @BeforeEach
    void setUp() {
        // Créer un flux de données pour simuler une icône
        photoStream = new ByteArrayInputStream("test icon content".getBytes());

        // Créer un achievement existant
        existingAchievement = new AchievementDto();
        existingAchievement.setId(achievementId);
        existingAchievement.setIcon("old-icon-url.jpg");

        // Créer l'achievement mis à jour
        updatedAchievement = new AchievementDto();
        updatedAchievement.setId(achievementId);
        updatedAchievement.setIcon(cloudinaryUrl);
    }

    @Test
    void savePhoto_shouldUpdateAchievementWithNewIconUrl() throws IOException {
        // Arrange
        when(achievementService.getAchievementById(achievementId)).thenReturn(existingAchievement);
        when(cloudinaryService.savePhoto(any(InputStream.class), eq(photoTitle))).thenReturn(cloudinaryUrl);
        when(achievementService.updateAchievement(eq(achievementId), any(AchievementDto.class))).thenReturn(updatedAchievement);

        // Act
        AchievementDto result = saveAchievementPhoto.savePhoto(achievementId, photoStream, photoTitle);

        // Assert
        assertNotNull(result);
        assertEquals(achievementId, result.getId());
        assertEquals(cloudinaryUrl, result.getIcon());

        // Verify interactions
        verify(achievementService).getAchievementById(achievementId);
        verify(cloudinaryService).savePhoto(any(InputStream.class), eq(photoTitle));
        verify(achievementService).updateAchievement(eq(achievementId), any(AchievementDto.class));
    }

    @Test
    void savePhoto_whenAchievementIdIsNull_shouldThrowInvalidOperationException() {
        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> saveAchievementPhoto.savePhoto(null, photoStream, photoTitle)
        );

        assertEquals("ID du succès non fourni", exception.getMessage());
        assertEquals(ErrorCodes.INVALID_ID, exception.getErrorCode());

        // Verify no interactions occurred
        verifyNoInteractions(achievementService);
        verifyNoInteractions(cloudinaryService);
    }

    @Test
    void savePhoto_whenAchievementNotFound_shouldThrowEntityNotFoundException() {
        // Arrange
        when(achievementService.getAchievementById(achievementId)).thenThrow(new EntityNotFoundException("Succès non trouvé"));

        // Act & Assert
        assertThrows(
                EntityNotFoundException.class,
                () -> saveAchievementPhoto.savePhoto(achievementId, photoStream, photoTitle)
        );

        // Verify
        verify(achievementService).getAchievementById(achievementId);
        verifyNoInteractions(cloudinaryService);
        verify(achievementService, never()).updateAchievement(anyInt(), any());
    }

    @Test
    void savePhoto_whenCloudinaryFails_shouldPropagateException() throws IOException {
        // Arrange
        when(achievementService.getAchievementById(achievementId)).thenReturn(existingAchievement);
        when(cloudinaryService.savePhoto(any(InputStream.class), anyString()))
                .thenThrow(new IOException("Cloudinary upload failed"));

        // Act & Assert
        IOException exception = assertThrows(
                IOException.class,
                () -> saveAchievementPhoto.savePhoto(achievementId, photoStream, photoTitle)
        );

        assertEquals("Cloudinary upload failed", exception.getMessage());

        // Verify
        verify(achievementService).getAchievementById(achievementId);
        verify(cloudinaryService).savePhoto(any(InputStream.class), eq(photoTitle));
        verify(achievementService, never()).updateAchievement(anyInt(), any());
    }

    @Test
    void savePhoto_whenCloudinaryReturnsEmptyUrl_shouldThrowInvalidOperationException() throws IOException {
        // Arrange
        when(achievementService.getAchievementById(achievementId)).thenReturn(existingAchievement);
        when(cloudinaryService.savePhoto(any(InputStream.class), eq(photoTitle))).thenReturn("");

        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> saveAchievementPhoto.savePhoto(achievementId, photoStream, photoTitle)
        );

        assertEquals("Erreur lors de l'enregistrement de la photo du succès", exception.getMessage());
        assertEquals(ErrorCodes.UPDATE_PHOTO_EXCEPTION, exception.getErrorCode());

        // Verify
        verify(achievementService).getAchievementById(achievementId);
        verify(cloudinaryService).savePhoto(any(InputStream.class), eq(photoTitle));
        verify(achievementService, never()).updateAchievement(anyInt(), any());
    }

    @Test
    void savePhoto_whenCloudinaryReturnsNullUrl_shouldThrowInvalidOperationException() throws IOException {
        // Arrange
        when(achievementService.getAchievementById(achievementId)).thenReturn(existingAchievement);
        when(cloudinaryService.savePhoto(any(InputStream.class), eq(photoTitle))).thenReturn(null);

        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> saveAchievementPhoto.savePhoto(achievementId, photoStream, photoTitle)
        );

        assertEquals("Erreur lors de l'enregistrement de la photo du succès", exception.getMessage());
        assertEquals(ErrorCodes.UPDATE_PHOTO_EXCEPTION, exception.getErrorCode());

        // Verify
        verify(achievementService).getAchievementById(achievementId);
        verify(cloudinaryService).savePhoto(any(InputStream.class), eq(photoTitle));
        verify(achievementService, never()).updateAchievement(anyInt(), any());
    }

    @Test
    void savePhoto_whenAchievementUpdateFails_shouldPropagateException() throws IOException {
        // Arrange
        when(achievementService.getAchievementById(achievementId)).thenReturn(existingAchievement);
        when(cloudinaryService.savePhoto(any(InputStream.class), eq(photoTitle))).thenReturn(cloudinaryUrl);
        when(achievementService.updateAchievement(eq(achievementId), any(AchievementDto.class)))
                .thenThrow(new RuntimeException("Achievement update failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> saveAchievementPhoto.savePhoto(achievementId, photoStream, photoTitle)
        );

        assertEquals("Achievement update failed", exception.getMessage());

        // Verify
        verify(achievementService).getAchievementById(achievementId);
        verify(cloudinaryService).savePhoto(any(InputStream.class), eq(photoTitle));
        verify(achievementService).updateAchievement(eq(achievementId), any(AchievementDto.class));
    }

    @Test
    void savePhoto_shouldSetCorrectIconUrlInAchievementDto() throws IOException {
        // Arrange
        when(achievementService.getAchievementById(achievementId)).thenReturn(existingAchievement);
        when(cloudinaryService.savePhoto(any(InputStream.class), eq(photoTitle))).thenReturn(cloudinaryUrl);

        // Capturer l'objet AchievementDto passé à updateAchievement
        doAnswer(invocation -> {
            AchievementDto passedAchievement = invocation.getArgument(1);
            assertEquals(cloudinaryUrl, passedAchievement.getIcon());
            return updatedAchievement;
        }).when(achievementService).updateAchievement(eq(achievementId), any(AchievementDto.class));

        // Act
        saveAchievementPhoto.savePhoto(achievementId, photoStream, photoTitle);

        // La vérification est faite dans le doAnswer ci-dessus
    }
}
