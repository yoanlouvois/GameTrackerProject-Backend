package com.et4.gametrackerproject.services.strategy;

import com.et4.gametrackerproject.dto.AchievementDto;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.services.AchievementService;
import com.et4.gametrackerproject.services.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implémentation de la stratégie de sauvegarde pour les icônes des succès (achievements).
 * Cette classe utilise Cloudinary comme service de stockage d'images et
 * met à jour les succès avec les URLs d'icônes correspondantes.
 */
@Service("achievementStrategy")
@Slf4j
public class SaveAchievementPhoto implements Strategy<AchievementDto> {

    private final CloudinaryService cloudinaryService;
    private final AchievementService achievementService;

    /**
     * Constructeur avec injection des dépendances nécessaires.
     *
     * @param cloudinaryService Service pour l'upload et la gestion des images sur Cloudinary
     * @param achievementService Service de gestion des succès dans l'application
     */
    @Autowired
    public SaveAchievementPhoto(CloudinaryService cloudinaryService, AchievementService achievementService) {
        this.cloudinaryService = cloudinaryService;
        this.achievementService = achievementService;
    }

    /**
     * Sauvegarde une icône pour un succès et associe son URL au succès correspondant.
     *
     * 1. Récupère le succès existant par son ID
     * 2. Télécharge l'icône vers Cloudinary
     * 3. Vérifie que l'URL retournée est valide
     * 4. Met à jour le succès avec l'URL de l'icône
     *
     * @param id Identifiant du succès à mettre à jour
     * @param photo Flux d'entrée contenant les données binaires de l'icône
     * @param title Titre ou nom à donner à l'icône (utilisé comme identifiant dans Cloudinary)
     * @return L'AchievementDto mis à jour avec l'URL de la nouvelle icône
     * @throws IOException Si une erreur se produit pendant la lecture ou l'écriture du fichier
     * @throws InvalidOperationException Si l'enregistrement de l'icône échoue
     * @throws EntityNotFoundException Si le succès avec l'ID spécifié n'existe pas
     */
    @Override
    public AchievementDto savePhoto(Integer id, InputStream photo, String title) throws IOException {
        log.info("Début de la sauvegarde de l'icône pour le succès avec l'ID: {}", id);

        // Vérifier si l'ID est valide
        if (id == null) {
            log.error("Impossible de sauvegarder l'icône: ID du succès est null");
            throw new InvalidOperationException("ID du succès non fourni", ErrorCodes.INVALID_ID);
        }

        // Récupérer le succès existant
        AchievementDto achievement = achievementService.getAchievementById(id);

        log.debug("Succès récupéré avec succès, upload de l'icône vers Cloudinary");

        // Sauvegarder l'icône sur Cloudinary et récupérer l'URL
        String urlPhoto = cloudinaryService.savePhoto(photo, title);

        // Vérifier que l'URL retournée est valide
        if (!StringUtils.hasLength(urlPhoto)) {
            log.error("Échec de l'enregistrement de l'icône sur Cloudinary pour le succès ID: {}", id);
            throw new InvalidOperationException(
                    "Erreur lors de l'enregistrement de la photo du succès",
                    ErrorCodes.UPDATE_PHOTO_EXCEPTION
            );
        }

        log.info("Icône enregistrée avec succès sur Cloudinary, URL: {}", urlPhoto);

        // Mettre à jour le succès avec l'URL de l'icône
        achievement.setIcon(urlPhoto);
        AchievementDto updatedAchievement = achievementService.updateAchievement(id, achievement);

        log.info("Succès mis à jour avec succès avec la nouvelle icône, ID: {}", id);
        return updatedAchievement;
    }
}
