package com.nnk.springboot.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;

/**
 * Service personnalisé d'authentification pour l'application de trading Poseidon.
 * 
 * <p>Cette classe implémente l'interface UserDetailsService de Spring Security pour
 * intégrer le système d'authentification avec la base de données utilisateur de l'application.
 * Elle fait le pont entre le modèle utilisateur métier et les exigences de Spring Security.</p>
 * 
 * <p>Responsabilités principales :</p>
 * <ul>
 *   <li><strong>Chargement utilisateur</strong> : Récupération depuis la base de données</li>
 *   <li><strong>Conversion de modèle</strong> : User métier → UserDetails Spring Security</li>
 *   <li><strong>Gestion des rôles</strong> : Mapping rôles métier → GrantedAuthority</li>
 *   <li><strong>Logging sécurisé</strong> : Traçabilité des tentatives d'authentification</li>
 * </ul>
 * 
 * <p>Architecture de sécurité :</p>
 * <ul>
 *   <li><strong>Bridge Pattern</strong> : Interface entre couches métier et sécurité</li>
 *   <li><strong>Service Spring</strong> : Géré par le conteneur IoC</li>
 *   <li><strong>Repository Pattern</strong> : Accès données via UserRepository</li>
 *   <li><strong>Exception handling</strong> : Gestion appropriée des utilisateurs inexistants</li>
 * </ul>
 * 
 * <p>Sécurité et conformité :</p>
 * <ul>
 *   <li><strong>Logging différencié</strong> : DEBUG pour succès, WARN pour échecs</li>
 *   <li><strong>Protection informations</strong> : Pas de log des mots de passe</li>
 *   <li><strong>Traçabilité audit</strong> : Tentatives d'authentification tracées</li>
 *   <li><strong>Exception standardisée</strong> : UsernameNotFoundException pour échecs</li>
 * </ul>
 * 
 * <p>Intégration avec l'écosystème trading :</p>
 * <ul>
 *   <li><strong>Rôles métier</strong> : ADMIN, USER, TRADER, MANAGER, etc.</li>
 *   <li><strong>Autorisation granulaire</strong> : Contrôle d'accès par fonctionnalité</li>
 *   <li><strong>Session management</strong> : Support des sessions utilisateur</li>
 *   <li><strong>Audit compliance</strong> : Traçabilité pour conformité réglementaire</li>
 * </ul>
 * 
 * <p>Flux d'authentification :</p>
 * <ol>
 *   <li>Spring Security appelle loadUserByUsername()</li>
 *   <li>Recherche de l'utilisateur en base de données</li>
 *   <li>Si trouvé : conversion en UserDetails</li>
 *   <li>Si non trouvé : UsernameNotFoundException</li>
 *   <li>Retour à Spring Security pour validation mot de passe</li>
 * </ol>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see com.nnk.springboot.domain.User
 * @see com.nnk.springboot.repositories.UserRepository
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /** Logger pour la traçabilité des opérations d'authentification */
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    /** Repository pour l'accès aux données utilisateur */
    @Autowired
    private UserRepository userRepository;

    /**
     * Charge un utilisateur par son nom d'utilisateur pour l'authentification Spring Security.
     * 
     * <p>Cette méthode est le point d'entrée principal du processus d'authentification.
     * Elle recherche l'utilisateur dans la base de données, le convertit en objet
     * UserDetails compatible avec Spring Security, et gère les cas d'erreur appropriés.</p>
     * 
     * <p>Processus de chargement :</p>
     * <ol>
     *   <li><strong>Logging tentative</strong> : Enregistrement de la tentative d'authentification</li>
     *   <li><strong>Recherche en base</strong> : Via UserRepository.findByUsername()</li>
     *   <li><strong>Gestion d'échec</strong> : UsernameNotFoundException si non trouvé</li>
     *   <li><strong>Conversion successful</strong> : User métier → UserDetails Spring</li>
     *   <li><strong>Mapping des rôles</strong> : Rôle métier → GrantedAuthority</li>
     * </ol>
     * 
     * <p>Objet UserDetails retourné contient :</p>
     * <ul>
     *   <li><strong>Username</strong> : Identifiant de connexion</li>
     *   <li><strong>Password</strong> : Mot de passe chiffré (BCrypt)</li>
     *   <li><strong>Authorities</strong> : Liste des rôles/permissions</li>
     *   <li><strong>Account status</strong> : Enabled=true (par défaut)</li>
     * </ul>
     * 
     * <p>Sécurité et logging :</p>
     * <ul>
     *   <li><strong>DEBUG success</strong> : Utilisateur trouvé avec ID (pas de mot de passe)</li>
     *   <li><strong>WARN failure</strong> : Utilisateur non trouvé (tentative suspecte)</li>
     *   <li><strong>Exception standard</strong> : UsernameNotFoundException pour Spring Security</li>
     *   <li><strong>Pas de leak</strong> : Aucune information sensible dans les logs</li>
     * </ul>
     * 
     * <p>Considérations performance :</p>
     * <ul>
     *   <li><strong>Requête unique</strong> : findByUsername() optimisée</li>
     *   <li><strong>Lazy loading</strong> : Pas de chargement d'associations inutiles</li>
     *   <li><strong>Cache potentiel</strong> : Peut être mis en cache par Spring Security</li>
     * </ul>
     * 
     * <p>Cas d'usage typiques :</p>
     * <ul>
     *   <li><strong>Login formulaire</strong> : Authentification interactive</li>
     *   <li><strong>Remember-me</strong> : Reconnexion automatique</li>
     *   <li><strong>API authentication</strong> : Basic auth ou token-based</li>
     *   <li><strong>Session validation</strong> : Vérification de validité de session</li>
     * </ul>
     * 
     * @param username Le nom d'utilisateur à authentifier (ne doit pas être null)
     * @return UserDetails contenant les informations d'authentification et d'autorisation
     * @throws UsernameNotFoundException Si l'utilisateur n'existe pas dans la base de données
     * @throws org.springframework.dao.DataAccessException Si erreur d'accès à la base de données
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Tentative de connexion pour l'utilisateur: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Utilisateur non trouvé: {}", username);
                    return new UsernameNotFoundException("Utilisateur non trouvé: " + username);
                });

        logger.debug("Utilisateur trouvé: {} (ID: {})", user.getUsername(), user.getId());

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            getGrantedAuthorities(user.getRole())
        );
    }

    /**
     * Convertit un rôle métier unique en collection de GrantedAuthority pour Spring Security.
     * 
     * <p>Cette méthode effectue la traduction entre le modèle métier de l'application
     * et les autorisations Spring Security. Elle prend le rôle stocké dans l'entité
     * User (par exemple "ADMIN", "USER") et le convertit en objet GrantedAuthority
     * que Spring Security peut utiliser pour les décisions d'autorisation.</p>
     * 
     * <p>Processus de conversion :</p>
     * <ol>
     *   <li><strong>Validation entrée</strong> : Vérification que le rôle n'est pas null</li>
     *   <li><strong>Création authority</strong> : Instanciation SimpleGrantedAuthority</li>
     *   <li><strong>Préfixe automatique</strong> : Ajout du préfixe "ROLE_"</li>
     *   <li><strong>Collection retour</strong> : Encapsulation en Collection&lt;GrantedAuthority&gt;</li>
     * </ol>
     * 
     * <p>Format des rôles :</p>
     * <ul>
     *   <li><strong>Entrée métier</strong> : "ADMIN", "USER", "MANAGER"</li>
     *   <li><strong>Spring Security</strong> : "ROLE_ADMIN", "ROLE_USER", "ROLE_MANAGER"</li>
     *   <li><strong>Convention</strong> : Préfixe "ROLE_" automatiquement ajouté</li>
     *   <li><strong>Case sensitive</strong> : Respecte la casse du rôle stocké</li>
     * </ul>
     * 
     * <p>Sécurité et validation :</p>
     * <ul>
     *   <li><strong>Null safety</strong> : Gestion des rôles null ou vides</li>
     *   <li><strong>Immutabilité</strong> : Nouvelle collection créée à chaque appel</li>
     *   <li><strong>Pas de side effect</strong> : N'affecte pas l'entité User originale</li>
     *   <li><strong>Thread safe</strong> : Opération sans état partagé</li>
     * </ul>
     * 
     * <p>Intégration Spring Security :</p>
     * <ul>
     *   <li><strong>@PreAuthorize</strong> : Utilisable avec hasRole('ADMIN')</li>
     *   <li><strong>Method security</strong> : Compatible avec @Secured</li>
     *   <li><strong>URL security</strong> : Fonctionne avec HttpSecurity.authorizeRequests()</li>
     *   <li><strong>Expression language</strong> : Intégrable dans SpEL security</li>
     * </ul>
     * 
     * <p>Exemples d'utilisation dans l'application :</p>
     * <ul>
     *   <li><strong>ROLE_ADMIN</strong> : Accès complet à toutes les fonctionnalités</li>
     *   <li><strong>ROLE_USER</strong> : Accès aux données de trading en lecture</li>
     *   <li><strong>ROLE_MANAGER</strong> : Gestion des utilisateurs et rapports</li>
     * </ul>
     * 
     * @param role Le rôle métier de l'utilisateur (peut être null)
     * @return Collection contenant le GrantedAuthority avec préfixe "ROLE_" pour Spring Security
     * @see org.springframework.security.core.authority.SimpleGrantedAuthority
     * @see org.springframework.security.access.prepost.PreAuthorize
     */
    private Collection<GrantedAuthority> getGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role != null && !role.trim().isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }
}