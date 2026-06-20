package com.et4.gametrackerproject.services.strategy;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface générique qui définit la stratégie pour sauvegarder des photos.
 * Utilise le pattern Strategy pour permettre différentes implémentations
 * de stockage de photos (ex : stockage local, cloud, etc.)
 *
 * @param <T> Le type de retour après sauvegarde de la photo (pourrait être URL, File, etc.)
 */
public interface Strategy<T> {

    /**
     * Sauvegarde une photo selon la stratégie d'implémentation choisie.
     *
     * @param id Identifiant unique associé à la photo (ex : ID de l'entité liée)
     * @param photo Flux d'entrée contenant les données binaires de la photo
     * @param title Titre ou nom à donner à la photo
     * @return Un objet de type T représentant le résultat de la sauvegarde
     * @throws IOException Si une erreur se produit pendant la lecture ou l'écriture
     */
    T savePhoto(Integer id, InputStream photo, String title) throws IOException;

}
