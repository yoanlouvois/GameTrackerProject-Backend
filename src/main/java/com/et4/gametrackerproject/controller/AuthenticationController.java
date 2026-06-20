package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.dto.auth.AuthenticationRequest;
import com.et4.gametrackerproject.dto.auth.AuthenticationResponse;
import com.et4.gametrackerproject.services.auth.ApplicationUserDetailsService;
import com.et4.gametrackerproject.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.et4.gametrackerproject.utils.Constants.AUTHENTICATION_ENDPOINT;

/**
 * Contrôleur REST responsable de la gestion des opérations d'authentification.
 * Expose des endpoints pour permettre aux utilisateurs de s'authentifier et d'obtenir des tokens JWT.
 */
@RestController
@RequestMapping(AUTHENTICATION_ENDPOINT)
@Slf4j
public class AuthenticationController {

    /**
     * Gestionnaire d'authentification de Spring Security.
     * Utilisé pour valider les identifiants des utilisateurs.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Service de détails d'utilisateur personnalisé.
     * Utilisé pour charger les informations des utilisateurs depuis la base de données.
     */
    @Autowired
    private ApplicationUserDetailsService userDetailsService;

    /**
     * Utilitaire JWT pour la génération et la validation des tokens.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Endpoint d'authentification qui vérifie les identifiants de l'utilisateur
     * et génère un token JWT en cas de succès.
     *
     * @param request Objet contenant les identifiants de l'utilisateur (login et mot de passe)
     * @return ResponseEntity contenant le token JWT en cas de succès
     * @throws org.springframework.security.core.AuthenticationException si l'authentification échoue
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            log.info("Authentication attempt for user: {}", request.getLogin());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );

            log.info("User authenticated successfully: {}", request.getLogin());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Si aucune exception n'est lancée, récupère les détails complets de l'utilisateur
            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getLogin());

            // Génère un token JWT pour l'utilisateur authentifié
            final String jwt = jwtUtil.generateToken(userDetails);

            // Retourne une réponse 200 OK avec le token JWT
            return ResponseEntity.ok(AuthenticationResponse.builder().accessToken(jwt).build());
        } catch (BadCredentialsException e) {
            log.error("Authentication failed: Bad credentials for user {}", request.getLogin());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error: " + e.getMessage());
        }
    }

    /**
     * Note: Vous pourriez ajouter d'autres endpoints liés à l'authentification ici, comme:
     * - Un endpoint de rafraîchissement de token
     * - Un endpoint d'inscription pour les nouveaux utilisateurs
     * - Un endpoint de déconnexion (bien que les JWTs eux-mêmes ne puissent pas être explicitement invalidés)
     */
}
