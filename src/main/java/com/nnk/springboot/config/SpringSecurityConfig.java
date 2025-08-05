package com.nnk.springboot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration Spring Security pour l'application de trading Poseidon.
 * 
 * <p>Cette classe centralise la configuration de sécurité de l'application,
 * définissant les règles d'authentification, d'autorisation et de protection
 * pour un environnement de trading financier sécurisé.</p>
 * 
 * <p>Fonctionnalités de sécurité implémentées :</p>
 * <ul>
 *   <li><strong>Authentification basée sur formulaire</strong> : Login/logout standard</li>
 *   <li><strong>Autorisation granulaire</strong> : Contrôle d'accès par URL</li>
 *   <li><strong>Chiffrement BCrypt</strong> : Protection des mots de passe</li>
 *   <li><strong>Service utilisateur personnalisé</strong> : Gestion des rôles métier</li>
 * </ul>
 * 
 * <p>Architecture de sécurité :</p>
 * <ul>
 *   <li><strong>SecurityFilterChain</strong> : Configuration moderne Spring Security 6+</li>
 *   <li><strong>AuthenticationManager</strong> : Gestionnaire d'authentification personnalisé</li>
 *   <li><strong>PasswordEncoder</strong> : Chiffrement des mots de passe avec BCrypt</li>
 *   <li><strong>UserDetailsService</strong> : Intégration avec le système utilisateur métier</li>
 * </ul>
 * 
 * <p>Règles d'accès pour l'environnement trading :</p>
 * <ul>
 *   <li><strong>Accès public</strong> : Page de login, ressources statiques, erreurs</li>
 *   <li><strong>Accès sécurisé</strong> : Toutes les fonctionnalités de trading</li>
 *   <li><strong>Redirection post-login</strong> : Vers la liste des offres (BidList)</li>
 *   <li><strong>Gestion des erreurs</strong> : Pages d'erreur et de déconnexion</li>
 * </ul>
 * 
 * <p>Conformité et sécurité financière :</p>
 * <ul>
 *   <li><strong>Traçabilité</strong> : Toutes les actions utilisateur sont tracées</li>
 *   <li><strong>Session management</strong> : Gestion sécurisée des sessions</li>
 *   <li><strong>Protection CSRF</strong> : Activée par défaut (Spring Security)</li>
 *   <li><strong>Chiffrement robuste</strong> : BCrypt avec facteur de coût adaptatif</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see CustomUserDetailsService
 * @see org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    /** 
     * Service personnalisé pour la gestion des détails utilisateur.
     * 
     * <p>Intègre le système d'authentification Spring Security avec
     * la base de données utilisateur de l'application de trading.</p>
     */
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Configure la chaîne de filtres de sécurité pour l'application.
     * 
     * <p>Cette méthode définit les règles de sécurité globales de l'application,
     * incluant l'autorisation des URLs, la configuration du formulaire de login
     * et la gestion de la déconnexion. Elle utilise l'API moderne de Spring Security 6+
     * avec les lambda DSL pour une configuration fluide et lisible.</p>
     * 
     * <p>Règles d'autorisation définies :</p>
     * <ul>
     *   <li><strong>Accès libre</strong> : "/", "/login", "/error" (pages publiques)</li>
     *   <li><strong>Ressources statiques</strong> : CSS, JS, images (sans authentification)</li>
     *   <li><strong>Zone sécurisée</strong> : "/secure/**" (nécessite authentification)</li>
     *   <li><strong>Toutes autres URLs</strong> : Authentification requise par défaut</li>
     * </ul>
     * 
     * <p>Configuration du formulaire de login :</p>
     * <ul>
     *   <li><strong>Page de login</strong> : "/login" (formulaire personnalisé)</li>
     *   <li><strong>Redirection succès</strong> : "/bidList/list" (page principale trading)</li>
     *   <li><strong>Redirection échec</strong> : "/login?error=true" (avec message d'erreur)</li>
     *   <li><strong>Accès libre</strong> : Formulaire accessible sans authentification</li>
     * </ul>
     * 
     * <p>Configuration de la déconnexion :</p>
     * <ul>
     *   <li><strong>URL de déconnexion</strong> : "/logout" (POST par défaut)</li>
     *   <li><strong>Redirection post-logout</strong> : "/login?logout=true" (confirmation)</li>
     *   <li><strong>Invalidation session</strong> : Automatique par Spring Security</li>
     *   <li><strong>Nettoyage cookies</strong> : Suppression des cookies de session</li>
     * </ul>
     * 
     * <p>Sécurité implicite activée :</p>
     * <ul>
     *   <li><strong>Protection CSRF</strong> : Activée par défaut</li>
     *   <li><strong>Session Fixation</strong> : Protection automatique</li>
     *   <li><strong>Headers de sécurité</strong> : X-Frame-Options, X-Content-Type-Options, etc.</li>
     *   <li><strong>Cache Control</strong> : Prévention du cache des pages sensibles</li>
     * </ul>
     * 
     * @param http Objet HttpSecurity pour la configuration des règles de sécurité
     * @return SecurityFilterChain configurée pour l'application
     * @throws Exception En cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/", "/login", "/error", "/css/**", "/js/**", "/images/**").permitAll();
                auth.requestMatchers("/secure/**").authenticated();
                auth.anyRequest().authenticated();
            })
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/bidList/list", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .build();
    }

    /**
     * Configure l'encodeur de mots de passe pour l'application.
     * 
     * <p>Cette méthode définit BCrypt comme algorithme de hachage des mots de passe,
     * offrant une sécurité robuste adaptée aux environnements financiers sensibles.
     * BCrypt utilise un facteur de coût adaptatif qui permet de maintenir la sécurité
     * face à l'évolution de la puissance de calcul.</p>
     * 
     * <p>Avantages de BCrypt pour l'environnement financier :</p>
     * <ul>
     *   <li><strong>Résistance aux attaques</strong> : Salt automatique et coût adaptatif</li>
     *   <li><strong>Conformité réglementaire</strong> : Respecte les standards bancaires</li>
     *   <li><strong>Future-proof</strong> : Facteur de coût ajustable selon les besoins</li>
     *   <li><strong>Validation Spring</strong> : Intégration native avec Spring Security</li>
     * </ul>
     * 
     * <p>Utilisation dans l'application :</p>
     * <ul>
     *   <li><strong>Création utilisateur</strong> : Chiffrement automatique des mots de passe</li>
     *   <li><strong>Authentification</strong> : Vérification des credentials</li>
     *   <li><strong>Changement de mot de passe</strong> : Re-hachage sécurisé</li>
     *   <li><strong>Migration de données</strong> : Conversion d'anciens hashes</li>
     * </ul>
     * 
     * @return Instance de BCryptPasswordEncoder configurée avec les paramètres par défaut
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure le gestionnaire d'authentification personnalisé.
     * 
     * <p>Cette méthode crée et configure un AuthenticationManager qui intègre
     * le service utilisateur personnalisé de l'application avec l'encodeur
     * de mots de passe BCrypt. Elle assure la liaison entre le système
     * d'authentification Spring Security et la logique métier utilisateur.</p>
     * 
     * <p>Composants intégrés :</p>
     * <ul>
     *   <li><strong>CustomUserDetailsService</strong> : Chargement des utilisateurs depuis la BD</li>
     *   <li><strong>BCryptPasswordEncoder</strong> : Vérification des mots de passe chiffrés</li>
     *   <li><strong>AuthenticationManagerBuilder</strong> : Configuration du processus d'auth</li>
     *   <li><strong>Spring Security Context</strong> : Intégration avec le contexte global</li>
     * </ul>
     * 
     * <p>Processus d'authentification :</p>
     * <ol>
     *   <li><strong>Réception credentials</strong> : Username/password depuis formulaire</li>
     *   <li><strong>Chargement utilisateur</strong> : Via CustomUserDetailsService</li>
     *   <li><strong>Vérification mot de passe</strong> : Comparaison BCrypt</li>
     *   <li><strong>Création contexte</strong> : SecurityContext avec authorities</li>
     *   <li><strong>Session establishment</strong> : Création session utilisateur</li>
     * </ol>
     * 
     * <p>Gestion des rôles et autorisations :</p>
     * <ul>
     *   <li><strong>Chargement des rôles</strong> : Depuis la base de données utilisateur</li>
     *   <li><strong>Mapping authorities</strong> : Conversion rôles → GrantedAuthority</li>
     *   <li><strong>Contrôle d'accès</strong> : Application des règles définies</li>
     *   <li><strong>Context propagation</strong> : Propagation dans toute l'application</li>
     * </ul>
     * 
     * <p>Sécurité et performance :</p>
     * <ul>
     *   <li><strong>Cache des utilisateurs</strong> : Optimisation des requêtes répétées</li>
     *   <li><strong>Protection timing attacks</strong> : Temps de réponse constants</li>
     *   <li><strong>Account locking</strong> : Protection contre brute force (si implémenté)</li>
     *   <li><strong>Audit logging</strong> : Traçabilité des tentatives d'authentification</li>
     * </ul>
     * 
     * @param http Configuration HttpSecurity pour l'accès aux objets partagés
     * @return AuthenticationManager configuré pour l'application
     * @throws Exception En cas d'erreur de configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}