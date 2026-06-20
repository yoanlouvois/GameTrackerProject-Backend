package com.et4.gametrackerproject.services.strategy;

import com.et4.gametrackerproject.dto.GameDto;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.services.CloudinaryService;
import com.et4.gametrackerproject.services.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

@Service("gameStrategy")
@Slf4j
public class SaveGamePhoto implements Strategy<GameDto>{

    private final CloudinaryService cloudinaryService;
    private final GameService gameService;

    @Autowired
    public SaveGamePhoto(CloudinaryService cloudinaryService, GameService gameService) {
        this.cloudinaryService = cloudinaryService;
        this.gameService = gameService;
    }


    @Override
    public GameDto savePhoto(Integer id, InputStream photo, String title) throws IOException {
        log.info("Début de la sauvegarde de la photo pour le jeu avec l'ID: {}", id);

        // Vérifier si l'ID est valide
        if (id == null) {
            log.error("Impossible de sauvegarder la photo: ID du jeu est null");
            throw new InvalidOperationException("ID du jeu non fourni", ErrorCodes.INVALID_ID);
        }

        // Récupérer l'avatar existant
        GameDto game = gameService.getGameById(id);

        log.debug("Jeu récupéré avec succès, upload de la photo vers Cloudinary");

        // Sauvegarder la photo sur Cloudinary et récupérer l'URL
        String urlPhoto = cloudinaryService.savePhoto(photo, title);

        // Vérifier que l'URL retournée est valide
        if (!StringUtils.hasLength(urlPhoto)) {
            log.error("Échec de l'enregistrement de la photo sur Cloudinary pour le jeu ID: {}", id);
            throw new InvalidOperationException(
                    "Erreur lors de l'enregistrement de la photo du jeu",
                    ErrorCodes.UPDATE_PHOTO_EXCEPTION
            );
        }

        log.info("Photo enregistrée avec succès sur Cloudinary, URL: {}", urlPhoto);

        // Mettre à jour l'avatar avec l'URL de la photo
        game.setImageUrl(urlPhoto);
        GameDto updatedGame = gameService.updateGame(id, game);

        log.info("Avatar mis à jour avec succès avec la nouvelle photo, ID: {}", id);
        return updatedGame;
    }
}
