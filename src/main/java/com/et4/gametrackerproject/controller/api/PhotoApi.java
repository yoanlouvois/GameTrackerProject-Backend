package com.et4.gametrackerproject.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.et4.gametrackerproject.utils.Constants.APP_ROOT;

/**
 * PhotoApi est une interface qui définit les opérations liées à l'upload et à la gestion
 * des photos dans différents contextes de l'application (avatars, achievements, etc.).
 * Elle utilise Spring Web pour gérer les requêtes HTTP et OpenAPI 3 pour la documentation de l'API.
 */
@Tag(name = "Photos", description = "Opérations liées à la gestion des photos")
public interface PhotoApi {

    /**
     * Endpoint pour sauvegarder une photo et l'associer à une entité spécifique
     * selon le contexte fourni.
     *
     * @param context Le contexte définissant le type d'entité (ex : "avatar", "achievement")
     * @param id L'identifiant de l'entité à laquelle la photo sera associée
     * @param photo Le fichier image à uploader
     * @param title Le titre ou nom à donner à la photo
     * @return L'entité mise à jour avec l'URL de la photo
     * @throws IOException Si une erreur se produit lors de la manipulation du fichier
     */
    @PostMapping(
            value = APP_ROOT + "/photos/{id}/{title}/{context}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Sauvegarde une photo et l'associe à une entité",
            description = "Télécharge une photo et l'associe à une entité en fonction du contexte spécifié (avatar, achievement, etc.)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "La photo a été téléchargée et associée avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres de requête invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Entité non trouvée", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur lors du téléchargement ou du traitement de la photo", content = @Content)
    })
    Object savePhoto(
            @Parameter(description = "Contexte définissant le type d'entité (avatar, achievement, etc.)")
            @PathVariable String context,

            @Parameter(description = "ID de l'entité à laquelle associer la photo")
            @PathVariable Integer id,

            @Parameter(description = "Fichier image à télécharger")
            @RequestPart("file") MultipartFile photo,

            @Parameter(description = "Titre ou nom de la photo")
            @PathVariable String title
    ) throws IOException;
}
