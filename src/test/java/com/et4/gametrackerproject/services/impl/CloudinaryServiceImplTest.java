package com.et4.gametrackerproject.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.Url;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CloudinaryServiceImplTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @Mock
    private Url url;

    @InjectMocks
    private CloudinaryServiceImpl cloudinaryService;

    private InputStream testPhoto;
    private final String testTitle = "test-photo-title";
    private final String testPublicId = "test-public-id";
    private final String expectedUrl = "https://res.cloudinary.com/test-cloud/image/upload/test-public-id.auto";

    @BeforeEach
    void setUp() {
        // Créer un InputStream de test
        testPhoto = new ByteArrayInputStream("test photo content".getBytes());

        // Configuration des mocks pour reproduire la chaîne d'appels de Cloudinary
        when(cloudinary.uploader()).thenReturn(uploader);
        when(cloudinary.url()).thenReturn(url);
    }

    @Test
    void savePhoto_ShouldUploadPhotoAndReturnUrl() throws IOException {
        // Arrange
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("public_id", testPublicId);

        when(uploader.upload(any(InputStream.class), anyMap())).thenReturn(uploadResult);

        when(url.secure(true)).thenReturn(url);
        when(url.publicId(testPublicId)).thenReturn(url);
        when(url.format("auto")).thenReturn(url);
        when(url.generate()).thenReturn(expectedUrl);

        // Act
        String resultUrl = cloudinaryService.savePhoto(testPhoto, testTitle);

        // Assert
        assertEquals(expectedUrl, resultUrl, "The returned URL should match the expected URL");

        // Verify correct metadata is being passed
        verify(uploader).upload(any(InputStream.class), any(Map.class));
    }

    @Test
    void savePhoto_ShouldPassCorrectMetadata() throws IOException {
        // Arrange
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("public_id", testPublicId);

        when(uploader.upload(any(InputStream.class), anyMap())).thenReturn(uploadResult);
        when(url.secure(true)).thenReturn(url);
        when(url.publicId(any())).thenReturn(url);
        when(url.format(any())).thenReturn(url);
        when(url.generate()).thenReturn(expectedUrl);

        // Act
        cloudinaryService.savePhoto(testPhoto, testTitle);

        // Assert — capturons les métadonnées pour vérifier leur contenu
        verify(uploader).upload(any(InputStream.class), eq(Map.of(
                "public_id", testTitle,
                "overwrite", true
        )));
    }

    @Test
    void savePhoto_WhenUploaderThrowsIOException_ShouldPropagateException() throws IOException {
        // Arrange
        when(uploader.upload(any(InputStream.class), anyMap())).thenThrow(new IOException("Upload failed"));

        // Act & Assert
        IOException exception = assertThrows(
                IOException.class,
                () -> cloudinaryService.savePhoto(testPhoto, testTitle),
                "Should throw IOException when uploader fails"
        );

        assertThat(exception.getMessage()).isEqualTo("Upload failed");
    }

    @Test
    void savePhoto_WhenNullInputStream_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> cloudinaryService.savePhoto(null, testTitle),
                "Should throw NullPointerException when photo is null"
        );
    }

    @Test
    void savePhoto_WhenEmptyTitle_ShouldStillWork() throws IOException {
        // Arrange
        String emptyTitle = "";
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("public_id", testPublicId);

        when(uploader.upload(any(InputStream.class), anyMap())).thenReturn(uploadResult);
        when(url.secure(true)).thenReturn(url);
        when(url.publicId(testPublicId)).thenReturn(url);
        when(url.format("auto")).thenReturn(url);
        when(url.generate()).thenReturn(expectedUrl);

        // Act
        String resultUrl = cloudinaryService.savePhoto(testPhoto, emptyTitle);

        // Assert
        assertEquals(expectedUrl, resultUrl, "Should work with empty title");

        // Vérifier que le title vide est bien passé dans les métadonnées
        verify(uploader).upload(any(InputStream.class), eq(Map.of(
                "public_id", emptyTitle,
                "overwrite", true
        )));
    }

    @Test
    void savePhoto_WhenPublicIdIsNull_ShouldHandleGracefully() throws IOException {
        // Arrange
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("public_id", null); // public_id est null

        when(uploader.upload(any(InputStream.class), anyMap())).thenReturn(uploadResult);
        when(url.secure(true)).thenReturn(url);
        when(url.publicId(null)).thenReturn(url); // S'attendre à un publicId null
        when(url.format("auto")).thenReturn(url);
        when(url.generate()).thenReturn(expectedUrl);

        // Act
        String resultUrl = cloudinaryService.savePhoto(testPhoto, testTitle);

        // Assert
        assertEquals(expectedUrl, resultUrl, "Should handle null publicId");
    }

    @Test
    void savePhoto_WhenUrlGenerationFails_ShouldThrowException() throws IOException {
        // Arrange
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("public_id", testPublicId);

        when(uploader.upload(any(InputStream.class), anyMap())).thenReturn(uploadResult);
        when(url.secure(true)).thenReturn(url);
        when(url.publicId(testPublicId)).thenReturn(url);
        when(url.format("auto")).thenReturn(url);
        when(url.generate()).thenThrow(new RuntimeException("URL generation failed"));

        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> cloudinaryService.savePhoto(testPhoto, testTitle),
                "Should propagate exceptions during URL generation"
        );
    }
}
