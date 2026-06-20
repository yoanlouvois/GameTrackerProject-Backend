package com.et4.gametrackerproject.services.strategy;

import com.et4.gametrackerproject.dto.AchievementDto;
import com.et4.gametrackerproject.dto.AvatarDto;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StrategyPhotoContextTest {

    @Mock
    private BeanFactory beanFactory;

    @Mock
    private SaveAvatarPhoto avatarStrategy;

    @Mock
    private SaveAchievementPhoto achievementStrategy;

    @InjectMocks
    private StrategyPhotoContext strategyPhotoContext;

    private final Integer validId = 1;
    private final String validTitle = "test-photo";
    private InputStream validPhoto;
    private final AvatarDto expectedAvatarResult = new AvatarDto();
    private final AchievementDto expectedAchievementResult = new AchievementDto();

    @BeforeEach
    void setUp() {
        validPhoto = new ByteArrayInputStream("test photo content".getBytes());

        // Initialiser les résultats attendus
        expectedAvatarResult.setId(validId);
        expectedAvatarResult.setPhoto("avatar-photo-url.jpg");

        expectedAchievementResult.setId(validId);
        expectedAchievementResult.setIcon("achievement-icon-url.jpg");
    }

    @Test
    void savePhoto_WithAvatarContext_ShouldUseAvatarStrategy() throws IOException {
        // Arrange
        when(beanFactory.getBean("avatarStrategy", SaveAvatarPhoto.class)).thenReturn(avatarStrategy);
        when(avatarStrategy.savePhoto(validId, validPhoto, validTitle)).thenReturn(expectedAvatarResult);

        // Act
        Object result = strategyPhotoContext.savePhoto("avatar", validId, validPhoto, validTitle);

        // Assert
        assertThat(result).isNotNull().isEqualTo(expectedAvatarResult);

        // Verify
        verify(beanFactory).getBean("avatarStrategy", SaveAvatarPhoto.class);
        verify(avatarStrategy).savePhoto(validId, validPhoto, validTitle);
        verifyNoInteractions(achievementStrategy);
    }

    @Test
    void savePhoto_WithAchievementContext_ShouldUseAchievementStrategy() throws IOException {
        // Arrange
        when(beanFactory.getBean("achievementStrategy", SaveAchievementPhoto.class)).thenReturn(achievementStrategy);
        when(achievementStrategy.savePhoto(validId, validPhoto, validTitle)).thenReturn(expectedAchievementResult);

        // Act
        Object result = strategyPhotoContext.savePhoto("achievement", validId, validPhoto, validTitle);

        // Assert
        assertThat(result).isNotNull().isEqualTo(expectedAchievementResult);

        // Verify
        verify(beanFactory).getBean("achievementStrategy", SaveAchievementPhoto.class);
        verify(achievementStrategy).savePhoto(validId, validPhoto, validTitle);
        verifyNoInteractions(avatarStrategy);
    }

    @Test
    void savePhoto_WithUnknownContext_ShouldThrowInvalidOperationException() {
        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> strategyPhotoContext.savePhoto("unknown", validId, validPhoto, validTitle)
        );

        assertEquals("Contexte inconnu pour l'enregistrement de la photo: unknown", exception.getMessage());
        assertEquals(ErrorCodes.UNKNOWN_CONTEXT, exception.getErrorCode());

        // Verify
        verifyNoInteractions(avatarStrategy);
        verifyNoInteractions(achievementStrategy);
    }

    @Test
    void savePhoto_WithNullContext_ShouldThrowInvalidOperationException() {
        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> strategyPhotoContext.savePhoto(null, validId, validPhoto, validTitle)
        );

        assertEquals("Contexte non spécifié", exception.getMessage());
        assertEquals(ErrorCodes.UNKNOWN_CONTEXT, exception.getErrorCode());

        // Verify
        verifyNoInteractions(avatarStrategy);
        verifyNoInteractions(achievementStrategy);
    }

    @Test
    void savePhoto_WithEmptyContext_ShouldThrowInvalidOperationException() {
        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> strategyPhotoContext.savePhoto("", validId, validPhoto, validTitle)
        );

        assertEquals("Contexte non spécifié", exception.getMessage());
        assertEquals(ErrorCodes.UNKNOWN_CONTEXT, exception.getErrorCode());

        // Verify
        verifyNoInteractions(avatarStrategy);
        verifyNoInteractions(achievementStrategy);
    }

    @Test
    void savePhoto_WithNullId_ShouldThrowInvalidOperationException() {
        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> strategyPhotoContext.savePhoto("avatar", null, validPhoto, validTitle)
        );

        assertEquals("ID non spécifié", exception.getMessage());
        assertEquals(ErrorCodes.INVALID_ID, exception.getErrorCode());

        // Verify
        verifyNoInteractions(avatarStrategy);
    }

    @Test
    void savePhoto_WithNullPhoto_ShouldThrowInvalidOperationException() {
        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> strategyPhotoContext.savePhoto("avatar", validId, null, validTitle)
        );

        assertEquals("Aucune photo fournie", exception.getMessage());
        assertEquals(ErrorCodes.INVALID_FILE, exception.getErrorCode());

        // Verify
        verifyNoInteractions(avatarStrategy);
    }

    @Test
    void savePhoto_WhenStrategyBeanNotFound_ShouldThrowInvalidOperationException() {
        // Arrange
        when(beanFactory.getBean("avatarStrategy", SaveAvatarPhoto.class))
                .thenThrow(new NoSuchBeanDefinitionException("avatarStrategy"));

        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> strategyPhotoContext.savePhoto("avatar", validId, validPhoto, validTitle)
        );

        assertTrue(exception.getMessage().startsWith("Erreur lors de la sélection de la stratégie:"));
        assertEquals(ErrorCodes.UNKNOWN_CONTEXT, exception.getErrorCode());

        // Verify
        verify(beanFactory).getBean("avatarStrategy", SaveAvatarPhoto.class);
        verifyNoInteractions(avatarStrategy);
    }

    @Test
    void savePhoto_WhenStrategyThrowsException_ShouldPropagateException() throws IOException {
        // Arrange
        when(beanFactory.getBean("avatarStrategy", SaveAvatarPhoto.class)).thenReturn(avatarStrategy);
        when(avatarStrategy.savePhoto(validId, validPhoto, validTitle))
                .thenThrow(new IOException("Strategy execution failed"));

        // Act & Assert
        IOException exception = assertThrows(
                IOException.class,
                () -> strategyPhotoContext.savePhoto("avatar", validId, validPhoto, validTitle)
        );

        assertEquals("Strategy execution failed", exception.getMessage());

        // Verify
        verify(beanFactory).getBean("avatarStrategy", SaveAvatarPhoto.class);
        verify(avatarStrategy).savePhoto(validId, validPhoto, validTitle);
    }

    @Test
    void savePhoto_WithWhitespaceContext_ShouldThrowInvalidOperationException() {
        // Act & Assert
        InvalidOperationException exception = assertThrows(
                InvalidOperationException.class,
                () -> strategyPhotoContext.savePhoto("   ", validId, validPhoto, validTitle)
        );

        assertEquals("Contexte non spécifié", exception.getMessage());
        assertEquals(ErrorCodes.UNKNOWN_CONTEXT, exception.getErrorCode());
    }
}
