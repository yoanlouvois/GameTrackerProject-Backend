package com.et4.gametrackerproject.services.auth;

import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.model.auth.CustomUserDetails;
import com.et4.gametrackerproject.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service d'authentification qui implémente l'interface UserDetailsService de Spring Security.
 * Cette classe fait le pont entre la logique d'authentification de Spring Security
 * et les utilisateurs stockés dans votre base de données.
 */
@Service
@Slf4j
public class ApplicationUserDetailsService implements UserDetailsService {

    /**
     * Service qui gère les opérations liées aux utilisateurs.
     * Il est utilisé pour récupérer les données des utilisateurs depuis la base de données.
     */
    @Autowired
    private UserService userService;

    /**
     * Charge un utilisateur par son nom d'utilisateur (email dans ce cas).
     * Cette méthode est appelée par Spring Security lors du processus d'authentification
     * pour vérifier les identifiants de l'utilisateur.
     *
     * @param email L'email de l'utilisateur (utilisé comme nom d'utilisateur)
     * @return Un objet UserDetails contenant les informations de l'utilisateur nécessaires
     *         pour l'authentification et l'autorisation
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            log.info("Attempting to load user with email: {}", email);
            UserDto user = userService.getUserByEmail(email);
            log.info("User found: {}", user.getEmail());

            return new CustomUserDetails(
                    user.getEmail(),
                    user.getPassword(),
                    new ArrayList<>(),
                    user.getId()  // Ajouter l'ID utilisateur ici
            );
        } catch (Exception e) {
            log.error("Failed to load user: {}", e.getMessage());
            throw new UsernameNotFoundException("User not found with email: " + email, e);
        }
    }

}
