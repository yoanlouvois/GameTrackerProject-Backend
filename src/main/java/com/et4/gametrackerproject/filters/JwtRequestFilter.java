package com.et4.gametrackerproject.filters;

import com.et4.gametrackerproject.services.auth.ApplicationUserDetailsService;
import com.et4.gametrackerproject.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre qui intercepte chaque requête HTTP pour extraire et valider les tokens JWT.
 * Cette classe est un élément clé du système d'authentification basé sur JWT.
 * Elle s'exécute une fois pour chaque requête (OncePerRequestFilter).
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    /**
     * Service qui charge les utilisateurs pour la validation du token.
     */
    @Autowired
    private ApplicationUserDetailsService userDetailsService;

    /**
     * Utilitaire qui contient les méthodes pour manipuler les tokens JWT.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Méthode principale du filtre qui est exécutée pour chaque requête.
     * Elle extrait le token JWT de l'en-tête Authorization, le valide,
     * et établit le contexte de sécurité si le token est valide.
     *
     * @param request La requête HTTP entrante
     * @param response La réponse HTTP sortante
     * @param chain La chaîne de filtres à exécuter
     * @throws ServletException Si une erreur survient pendant le traitement
     * @throws IOException Si une erreur d'E/S survient pendant le traitement
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        final String path = request.getServletPath();

        if (isPublicEndpoint(path)) {
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // le reste de ton code ne change pas

        // Vérification de l'existence et du format de l'en-tête Authorization
        // Les tokens JWT sont envoyés dans le format "Bearer [token]"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extraction du token (en supprimant le préfixe "Bearer ")
            jwt = authorizationHeader.substring(7);
            try {
                // Extraction du nom d'utilisateur à partir du token
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // En cas d'erreur dans le token (token expiré, signature invalide, etc.)
                // On a log l'erreur, mais on continue le traitement sans authentification.
                logger.error("JWT token invalide: " + e.getMessage());
            }
        }

        // Si un nom d'utilisateur a été extrait et qu'aucune authentification n'existe déjà
        // dans le contexte de sécurité, on procède à la validation.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Chargement des détails complets de l'utilisateur depuis la base de données
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validation du token en vérifiant qu'il correspond bien à l'utilisateur et qu'il n'est pas expiré
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Création d'un objet d'authentification pour Spring Security
                // Cet objet contient les détails de l'utilisateur et ses autorisations
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,       // Principal (l'utilisateur authentifié)
                        null,              // Credentials (null car déjà vérifié par le token)
                        userDetails.getAuthorities()  // Autorisations de l'utilisateur
                );

                // Ajout de détails supplémentaires sur la requête (adresse IP, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Établissement du contexte de sécurité avec l'authentification validée
                // Cela indique à Spring Security que l'utilisateur est authentifié
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Passage au filtre suivant dans la chaîne
        // C'est essentiel pour que la requête soit traitée normalement après l'authentification
        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("/gametracker/v1/auth/authenticate")
                || path.equals("/gametracker/v1/users/create")
                || path.startsWith("/admin/seed")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars");
    }
}
