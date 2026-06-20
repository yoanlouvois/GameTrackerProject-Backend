package com.et4.gametrackerproject.services.strategy;

import com.et4.gametrackerproject.dto.AvatarDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.services.AvatarService;
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
public class SaveAvatarPhotoTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private AvatarService avatarService;

    @InjectMocks
    private SaveAvatarPhoto saveAvatarPhoto;

    private InputStream photoStream;
    private final Integer avatarId = 1;
    private final String photoTitle = "test-avatar-photo";
    private final String cloudinaryUrl = "https://cloudinary.com/test-photo.jpg";
    private AvatarDto existingAvatar;
    private AvatarDto updatedAvatar;

    @BeforeEach
    void setUp() {
        // Créer un flux de données pour simuler une photo
        photoStream = new ByteArrayInputStream("test photo content".getBytes());

        // Créer un avatar existant
        existingAvatar = new AvatarDto();
        existingAvatar.setId(avatarId);
        existingAvatar.setPhoto("old-photo-url.jpg");

        // Créer l'avatar mis à jour
        updatedAvatar = new AvatarDto();
        updatedAvatar.setId(avatarId);
        updatedAvatar.setPhoto(cloudinaryUrl);
    }

    @Test
    void savePhoto_shouldUpdateAvatarWithNewPhotoUrl() throws IOException {
        // Arrange
        when(avatarService.getAvatarById(avatarId)).thenReturn(existingAvatar);
        when(cloudinaryService.savePhoto(any(InputStream.class), eq(photoTitle))).thenReturn(cloudinaryUrl);
        when(avatarService.updateAvatar(eq(avatarId), any(AvatarDto.class))).thenReturn(updatedAvatar);

        // Act
        AvatarDto result = saveAvatarPhoto.savePhoto(avatarId, photoStream, photoTitle);

        // Assert
        assertNotNull(result);
        assertEquals(avatarId, result.getId());
        assertEquals(cloudinaryUrl, result.getPhoto());

        // Verify interactions
        verify(avatarService).getAvatarById(avatarId);
        verify(cloudinaryService).savePhoto(any(InputStream.class), eq(photoTitle));
        verify(avatarService).updateAvatar(eq(avatarId), any(AvatarDto.class));
    }

    @Test
    void savePhoto_whenAvatarIdIsNull_shouldThrowInvalidOperationException() {
        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> saveAvatarPhoto.savePhoto(null, photoStream, photoTitle)
        );

        assertEquals("ID de l'avatar non fourni", exception.getMessage());
        assertEquals(ErrorCodes.INVALID_ID, exception.getErrorCode());

        // Verify no interactions occurred
        verifyNoInteractions(avatarService);
        verifyNoInteractions(cloudinaryService);
    }

    @Test
    void savePhoto_whenAvatarNotFound_shouldThrowEntityNotFoundException() {
        // Arrange
        when(avatarService.getAvatarById(avatarId)).thenThrow(new EntityNotFoundException("Avatar non trouvé"));

        // Act & Assert
        assertThrows(
                EntityNotFoundException.class,
                () -> saveAvatarPhoto.savePhoto(avatarId, photoStream, photoTitle)
        );

        // Verify
        verify(avatarService).getAvatarById(avatarId);
        verifyNoInteractions(cloudinaryService);
        verify(avatarService, never()).updateAvatar(anyInt(), any());
    }

    @Test
    void savePhoto_whenCloudinaryFails_shouldPropagateException() throws IOException {
        // Arrange
        when(avatarService.getAvatarById(avatarId)).thenReturn(existingAvatar);
        when(cloudinaryService.savePhoto(any(InputStream.class), anyString()))
                .thenThrow(new IOException("Cloudinary upload failed"));

        // Act & Assert
        IOException exception = assertThrows(
                IOException.class,
                () -> saveAvatarPhoto.savePhoto(avatarId, photoStream, photoTitle)
        );

        assertEquals("Cloudinary upload failed", exception.getMessage());

        // Verify
        verify(avatarService).getAvatarById(avatarId);
        verify(cloudinaryService).savePhoto(any(InputStream.class), eq(photoTitle));
        verify(avatarService, never()).updateAvatar(anyInt(), any());
    }

    @Test
    void savePhoto_whenCloudinaryReturnsEmptyUrl_shouldThrowInvalidOperationException() throws IOException {
        // Arrange
        when(avatarService.getAvatarById(avatarId)).thenReturn(existingAvatar);
        when(cloudinaryService.savePhoto(any(InputStream.class), eq(photoTitle))).thenReturn("");

        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> saveAvatarPhoto.savePhoto(avatarId, photoStream, photoTitle)
        );

        assertEquals("Erreur lors de l'enregistrement de la photo de l'avatar", exception.getMessage());
        assertEquals(ErrorCodes.UPDATE_PHOTO_EXCEPTION, exception.getErrorCode());

        // Verify
        verify(avatarService).getAvatarById(avatarId);
        verify(cloudinaryService).savePhoto(any(InputStream.class), eq(photoTitle));
        verify(avatarService, never()).updateAvatar(anyInt(), any());
    }

    @Test
    void savePhoto_whenCloudinaryReturnsNullUrl_shouldThrowInvalidOperationException() throws IOException {
        // Arrange
        when(avatarService.getAvatarById(avatarId)).thenReturn(existingAvatar);
        when(cloudinaryService.savePhoto(any(InputStream.class), eq(photoTitle))).thenReturn(null);

        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> saveAvatarPhoto.savePhoto(avatarId, photoStream, photoTitle)
        );

        assertEquals("Erreur lors de l'enregistrement de la photo de l'avatar", exception.getMessage());
        assertEquals(ErrorCodes.UPDATE_PHOTO_EXCEPTION, exception.getErrorCode());

        // Verify
        verify(avatarService).getAvatarById(avatarId);
        verify(cloudinaryService).savePhoto(any(InputStream.class), eq(photoTitle));
        verify(avatarService, never()).updateAvatar(anyInt(), any());
    }

    @Test
    void savePhoto_whenAvatarUpdateFails_shouldPropagateException() throws IOException {
        // Arrange
        when(avatarService.getAvatarById(avatarId)).thenReturn(existingAvatar);
        when(cloudinaryService.savePhoto(any(InputStream.class), eq(photoTitle))).thenReturn(cloudinaryUrl);
        when(avatarService.updateAvatar(eq(avatarId), any(AvatarDto.class)))
                .thenThrow(new RuntimeException("Avatar update failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> saveAvatarPhoto.savePhoto(avatarId, photoStream, photoTitle)
        );

        assertEquals("Avatar update failed", exception.getMessage());

        // Verify
        verify(avatarService).getAvatarById(avatarId);
        verify(cloudinaryService).savePhoto(any(InputStream.class), eq(photoTitle));
        verify(avatarService).updateAvatar(eq(avatarId), any(AvatarDto.class));
    }

    @Test
    void savePhoto_shouldSetCorrectPhotoUrlInAvatarDto() throws IOException {
        // Arrange
        when(avatarService.getAvatarById(avatarId)).thenReturn(existingAvatar);
        when(cloudinaryService.savePhoto(any(InputStream.class), eq(photoTitle))).thenReturn(cloudinaryUrl);

        // Capturer l'objet AvatarDto passé à updateAvatar
        doAnswer(invocation -> {
            AvatarDto passedAvatar = invocation.getArgument(1);
            assertEquals(cloudinaryUrl, passedAvatar.getPhoto());
            return updatedAvatar;
        }).when(avatarService).updateAvatar(eq(avatarId), any(AvatarDto.class));

        // Act
        saveAvatarPhoto.savePhoto(avatarId, photoStream, photoTitle);

        // La vérification est faite dans le doAnswer ci-dessus
    }
}
