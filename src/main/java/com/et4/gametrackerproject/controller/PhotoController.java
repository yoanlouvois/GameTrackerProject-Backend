package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.PhotoApi;
import com.et4.gametrackerproject.services.strategy.StrategyPhotoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Contrôleur REST gérant les opérations liées aux photos dans l'application.
 * Ce contrôleur implémente l'interface PhotoApi et délègue le traitement
 * des photos au service StrategyPhotoContext en utilisant le pattern Strategy.
 */
@RestController
@Slf4j
public class PhotoController implements PhotoApi {

    private final StrategyPhotoContext strategyPhotoContext;

    /**
     * Constructeur avec injection de dépendance de la stratégie de gestion des photos.
     *
     * @param strategyPhotoContext Contexte de stratégie pour la gestion des photos
     */
    @Autowired
    public PhotoController(StrategyPhotoContext strategyPhotoContext) {
        this.strategyPhotoContext = strategyPhotoContext;
    }

    /**
     * Implémentation de la méthode permettant de sauvegarder une photo
     * en fonction du contexte spécifié.
     * Cette méthode délègue le traitement au contexte de stratégie qui sélectionnera
     * la stratégie appropriée en fonction du contexte (avatar, achievement, etc.).
     *
     * @param context Le contexte définissant le type d'entité (ex: "avatar", "achievement")
     * @param id L'identifiant de l'entité à laquelle la photo sera associée
     * @param photo Le fichier image à uploader
     * @param title Le titre ou nom à donner à la photo
     * @return L'entité mise à jour avec l'URL de la photo
     * @throws IOException Si une erreur se produit lors de la manipulation du fichier
     */
    @Override
    public Object savePhoto(String context, Integer id, MultipartFile photo, String title) throws IOException {
        log.info("Demande de sauvegarde d'une photo - Contexte: {}, ID: {}, Titre: {}", context, id, title);

        // Vérification des données fournies
        if (photo == null || photo.isEmpty()) {
            log.error("Le fichier photo est vide ou non fourni");
            throw new IllegalArgumentException("Le fichier photo est obligatoire");
        }

        log.debug("Taille du fichier: {} octets, Type de contenu: {}", photo.getSize(), photo.getContentType());

        // Déléguer le traitement au contexte de stratégie
        Object result = strategyPhotoContext.savePhoto(context, id, photo.getInputStream(), title);
        log.info("Photo sauvegardée avec succès. Contexte: {}, ID: {}", context, id);
        return result;
    }
}
