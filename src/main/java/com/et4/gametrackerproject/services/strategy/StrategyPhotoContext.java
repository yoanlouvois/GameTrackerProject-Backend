package com.et4.gametrackerproject.services.strategy;

import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * Classe de contexte qui implémente le pattern Strategy pour la gestion des photos/images.
 * Cette classe sélectionne et utilise la stratégie appropriée en fonction du contexte fourni
 * (avatar, achievement, etc.) pour sauvegarder différents types d'images.
 */
@Service
@Slf4j
public class StrategyPhotoContext {

    private final BeanFactory beanFactory;
    private Strategy strategy;

    /**
     * Contexte courant de la stratégie
     */
    @Setter
    private String context;

    /**
     * Constructeur avec injection du BeanFactory pour la résolution dynamique des stratégies
     *
     * @param beanFactory Factory Spring permettant de récupérer les beans de stratégie
     */
    @Autowired
    public StrategyPhotoContext(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Sauvegarde une photo selon la stratégie déterminée par le contexte
     *
     * @param context Contexte déterminant la stratégie à utiliser (ex: "avatar", "achievement")
     * @param id Identifiant de l'entité à laquelle la photo sera associée
     * @param photo Flux d'entrée contenant les données binaires de l'image
     * @param title Titre ou nom à donner à l'image
     * @return L'objet mis à jour avec l'URL de la nouvelle image
     * @throws IOException Si une erreur se produit pendant la lecture ou l'écriture du fichier
     * @throws InvalidOperationException Si le contexte spécifié est inconnu
     */
    public Object savePhoto(String context, Integer id, InputStream photo, String title) throws IOException {
        log.info("Demande de sauvegarde d'une photo dans le contexte: {}, pour l'ID: {}", context, id);

        // Vérifier la validité des paramètres
        if (context == null || context.trim().isEmpty()) {
            log.error("Contexte non spécifié pour la sauvegarde de photo");
            throw new InvalidOperationException("Contexte non spécifié", ErrorCodes.UNKNOWN_CONTEXT);
        }

        if (id == null) {
            log.error("ID non spécifié pour la sauvegarde de photo dans le contexte: {}", context);
            throw new InvalidOperationException("ID non spécifié", ErrorCodes.INVALID_ID);
        }

        if (photo == null) {
            log.error("Donnée photo non fournie pour l'ID: {} dans le contexte: {}", id, context);
            throw new InvalidOperationException("Aucune photo fournie", ErrorCodes.INVALID_FILE);
        }

        // Déterminer et configurer la stratégie appropriée
        determineContext(context);

        log.debug("Stratégie sélectionnée: {}", strategy.getClass().getSimpleName());

        // Exécuter la stratégie sélectionnée
        Object result = strategy.savePhoto(id, photo, title);

        log.info("Photo sauvegardée avec succès dans le contexte: {} pour l'ID: {}", context, id);
        return result;
    }

    /**
     * Détermine et initialise la stratégie appropriée en fonction du contexte fourni.
     *
     * @param context Le contexte qui détermine quelle stratégie utiliser
     * @throws InvalidOperationException Si le contexte spécifié n'est pas reconnu
     */
    private void determineContext(String context) {
        log.debug("Détermination de la stratégie pour le contexte: {}", context);

        final String beanName = context + "Strategy";

        try {
            switch (context) {
                case "achievement":
                    strategy = beanFactory.getBean(beanName, SaveAchievementPhoto.class);
                    break;
                case "avatar":
                    strategy = beanFactory.getBean(beanName, SaveAvatarPhoto.class);
                    break;
                case "game":
                    strategy = beanFactory.getBean(beanName, SaveGamePhoto.class);
                    break;
                default:
                    log.error("Contexte inconnu: {}", context);
                    throw new InvalidOperationException(
                            "Contexte inconnu pour l'enregistrement de la photo: " + context,
                            ErrorCodes.UNKNOWN_CONTEXT
                    );
            }
            log.debug("Stratégie choisie: {} pour le contexte: {}", strategy.getClass().getSimpleName(), context);
        } catch (Exception e) {
            if (!(e instanceof InvalidOperationException)) {
                log.error("Erreur lors de la récupération de la stratégie pour le contexte: {}", context, e);
                throw new InvalidOperationException(
                        "Erreur lors de la sélection de la stratégie: " + e.getMessage(),
                        ErrorCodes.UNKNOWN_CONTEXT
                );
            }
            throw e;
        }
    }
}
