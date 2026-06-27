package com.et4.gametrackerproject.utils;

import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.model.auth.CustomUserDetails;
import com.et4.gametrackerproject.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Classe utilitaire qui fournit des méthodes pour manipuler les tokens JWT.
 * Elle gère le cycle de vie complet des tokens : génération, validation et extraction des données.
 */
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    @Autowired
    UserService userService;

    /**
     * Extrait le nom d'utilisateur (subject) à partir d'un token JWT.
     *
     * @param token Le token JWT à analyser
     * @return Le nom d'utilisateur contenu dans le token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration à partir d'un token JWT.
     *
     * @param token Le token JWT à analyser
     * @return La date d'expiration du token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Méthode générique pour extraire n'importe quelle revendication (claim) d'un token JWT.
     * Utilise une fonction qui prend un objet Claims et retourne la valeur souhaitée.
     *
     * @param token Le token JWT à analyser
     * @param claimsResolver Fonction qui extrait la donnée spécifique des claims
     * @return La donnée extraite du token
     * @param <T> Le type de la donnée à extraire
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait toutes les revendications (claims) d'un token JWT.
     * Cette méthode vérifie également la signature du token.
     *
     * @param token Le token JWT à analyser
     * @return Toutes les revendications contenues dans le token
     * @throws io.jsonwebtoken.JwtException si le token est invalide ou ne peut pas être analysé
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())  // Vérifie la signature du token avec notre clé
                .build()
                .parseSignedClaims(token)  // Analyse le token signé
                .getPayload();  // Récupère le contenu (payload)
    }

    /**
     * Vérifie si un token est expiré en comparant sa date d'expiration avec la date actuelle.
     *
     * @param token Le token JWT à vérifier
     * @return true si le token est expiré, false sinon
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Génère un token JWT pour un utilisateur.
     *
     * @param userDetails Les détails de l'utilisateur pour lequel générer le token
     * @return Un token JWT valide
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        if (userDetails instanceof CustomUserDetails) {
            claims.put("userId", ((CustomUserDetails) userDetails).getUserId());
        }

        return createToken(claims, userDetails.getUsername());
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    /**
     * Crée un token JWT avec les revendications spécifiées et le sujet (nom d'utilisateur).
     *
     * @param claims Map des revendications supplémentaires à inclure dans le token
     * @param subject Le sujet du token (généralement le nom d'utilisateur)
     * @return Un token JWT complet et signé
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)                // Revendications personnalisées
                .subject(subject)              // Sujet du token (username)
                .issuedAt(new Date(System.currentTimeMillis()))  // Date d'émission
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // Expiration après 10h
                .signWith(getSigningKey())                 // Signature avec la clé secrète
                .compact();                    // Création du token sous forme de chaîne
    }

    /**
     * Valide un token JWT en vérifiant à la fois l'utilisateur et l'expiration.
     *
     * @param token Le token JWT à valider
     * @param userDetails Les détails de l'utilisateur pour la validation
     * @return true si le token est valide pour cet utilisateur et non expiré, false sinon
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Vérifie que le nom d'utilisateur dans le token correspond à celui fourni
        // et que le token n'est pas expiré
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
