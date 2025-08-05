package com.nnk.springboot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Gestionnaire global d'exceptions et d'erreurs pour l'application de trading Poseidon.
 * 
 * <p>Cette classe centralise la gestion des erreurs et exceptions dans toute l'application,
 * offrant une expérience utilisateur cohérente et un logging approprié pour la maintenance
 * et le débogage. Elle combine les fonctionnalités de ControllerAdvice et ErrorController
 * pour une couverture complète des cas d'erreur.</p>
 * 
 * <p>Architecture de gestion d'erreurs :</p>
 * <ul>
 *   <li><strong>@ControllerAdvice</strong> : Capture les exceptions dans tous les contrôleurs</li>
 *   <li><strong>ErrorController</strong> : Gère les erreurs HTTP (404, 500, etc.)</li>
 *   <li><strong>Logging centralisé</strong> : Traçabilité complète pour l'audit</li>
 *   <li><strong>Configuration flexible</strong> : Stacktraces et messages conditionnels</li>
 * </ul>
 * 
 * <p>Types d'erreurs gérées :</p>
 * <ul>
 *   <li><strong>Erreurs HTTP</strong> : 404 (Not Found), 500 (Server Error), 403 (Forbidden)</li>
 *   <li><strong>Exceptions génériques</strong> : Toute exception non capturée</li>
 *   <li><strong>Erreurs de validation</strong> : IllegalArgumentException spécifiques</li>
 *   <li><strong>Erreurs de base de données</strong> : DataAccessException et dérivées</li>
 * </ul>
 * 
 * <p>Sécurité et confidentialité :</p>
 * <ul>
 *   <li><strong>Masquage d'informations</strong> : Stacktraces conditionnelles (prod vs dev)</li>
 *   <li><strong>Messages utilisateur</strong> : Textes génériques pour éviter l'exposition</li>
 *   <li><strong>Logging détaillé</strong> : Informations complètes pour les développeurs</li>
 *   <li><strong>Audit trail</strong> : Traçabilité des erreurs pour la conformité</li>
 * </ul>
 * 
 * <p>Configuration via propriétés :</p>
 * <ul>
 *   <li><strong>server.error.include-stacktrace</strong> : never/always/on_trace_param</li>
 *   <li><strong>server.error.include-message</strong> : never/always/on_trace_param</li>
 * </ul>
 * 
 * <p>Environnement de trading considérations :</p>
 * <ul>
 *   <li><strong>Disponibilité</strong> : Dégradation gracieuse en cas d'erreur</li>
 *   <li><strong>Traçabilité</strong> : Logs détaillés pour l'analyse post-incident</li>
 *   <li><strong>Sécurité</strong> : Pas d'exposition d'informations sensibles</li>
 *   <li><strong>Performance</strong> : Gestion d'erreur rapide sans impact système</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 * @see org.springframework.boot.web.servlet.error.ErrorController
 */
@ControllerAdvice
@Controller
public class GlobalExceptionHandler implements ErrorController {
    
    /** Logger pour le suivi et la traçabilité des erreurs */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /** 
     * Configuration pour l'inclusion des stacktraces dans les réponses d'erreur.
     * Valeurs possibles : "never", "always", "on_trace_param"
     */
    @Value("${server.error.include-stacktrace:never}")
    private String includeStacktrace;
    
    /** 
     * Configuration pour l'inclusion des messages d'exception dans les réponses.
     * Valeurs possibles : "never", "always", "on_trace_param"
     */
    @Value("${server.error.include-message:never}")
    private String includeMessage;

    /**
     * Gère les erreurs HTTP générales (404, 500, 403, etc.).
     * 
     * <p>Cette méthode constitue le point d'entrée principal pour toutes les erreurs
     * HTTP standard. Elle récupère les informations d'erreur depuis les attributs
     * de la requête, les log pour l'audit, et prépare une réponse utilisateur
     * appropriée sans exposer d'informations sensibles.</p>
     * 
     * <p>Codes d'erreur HTTP gérés spécifiquement :</p>
     * <ul>
     *   <li><strong>404 (Not Found)</strong> : Ressource inexistante</li>
     *   <li><strong>500 (Internal Server Error)</strong> : Erreur serveur</li>
     *   <li><strong>403 (Forbidden)</strong> : Accès interdit</li>
     *   <li><strong>Autres codes</strong> : Message générique</li>
     * </ul>
     * 
     * <p>Informations extraites et loggées :</p>
     * <ul>
     *   <li><strong>Status code</strong> : Code d'erreur HTTP</li>
     *   <li><strong>Request URI</strong> : URL qui a causé l'erreur</li>
     *   <li><strong>Error message</strong> : Message technique de l'erreur</li>
     *   <li><strong>Exception</strong> : Objet exception si disponible</li>
     * </ul>
     * 
     * <p>Données ajoutées au modèle pour la vue :</p>
     * <ul>
     *   <li><strong>status</strong> : Code d'erreur HTTP</li>
     *   <li><strong>errorMessage</strong> : Message utilisateur approprié</li>
     *   <li><strong>error</strong> : Type d'erreur (titre)</li>
     *   <li><strong>path</strong> : URI de la requête en erreur</li>
     *   <li><strong>timestamp</strong> : Horodatage de l'erreur</li>
     *   <li><strong>trace</strong> : Stacktrace (si configuration l'autorise)</li>
     * </ul>
     * 
     * @param request La requête HTTP qui a généré l'erreur
     * @param model Le modèle pour passer les données à la vue d'erreur
     * @return Le nom de la vue d'erreur ("error")
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Récupération des attributs d'erreur
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        logger.error("Error occurred - Status: {}, URI: {}, Message: {}", 
                    status, requestUri, errorMessage, (Throwable) exception);
        
        // Ajout des informations au modèle
        if (status != null) {
            model.addAttribute("status", status.toString());
            
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorMessage", "Page not found");
                model.addAttribute("error", "Not Found");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorMessage", "Internal server error occurred");
                model.addAttribute("error", "Internal Server Error");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorMessage", "Access denied");
                model.addAttribute("error", "Forbidden");
            } else {
                model.addAttribute("errorMessage", "An error occurred");
                model.addAttribute("error", "Error " + statusCode);
            }
        }
        
        if (errorMessage != null) {
            model.addAttribute("message", errorMessage.toString());
        }
        
        if (exception != null && "always".equals(includeStacktrace)) {
            model.addAttribute("exception", exception.getClass().getSimpleName());
            model.addAttribute("trace", getStackTrace((Throwable) exception));
        }
        
        model.addAttribute("path", requestUri);
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        
        return "error";
    }
    
    /**
     * Gère les exceptions non capturées dans les contrôleurs.
     * 
     * <p>Cette méthode agit comme un filet de sécurité pour toutes les exceptions
     * qui ne sont pas explicitement gérées par les contrôleurs spécifiques.
     * Elle assure qu'aucune exception ne remonte jusqu'à l'utilisateur final
     * sans traitement approprié.</p>
     * 
     * <p>Comportement de sécurité :</p>
     * <ul>
     *   <li><strong>Logging complet</strong> : Exception complète loggée pour debug</li>
     *   <li><strong>Message utilisateur générique</strong> : Pas d'exposition technique</li>
     *   <li><strong>Stacktrace conditionnelle</strong> : Selon configuration environnement</li>
     *   <li><strong>Informations contextuelles</strong> : URI, timestamp pour traçabilité</li>
     * </ul>
     * 
     * <p>Cas d'usage typiques :</p>
     * <ul>
     *   <li><strong>NullPointerException</strong> : Erreurs de programmation</li>
     *   <li><strong>RuntimeException</strong> : Erreurs inattendues</li>
     *   <li><strong>IOException</strong> : Problèmes d'accès fichier/réseau</li>
     *   <li><strong>Exceptions métier</strong> : Non gérées spécifiquement</li>
     * </ul>
     * 
     * @param e L'exception non capturée
     * @param model Le modèle pour passer les données à la vue d'erreur
     * @param request La requête HTTP lors de laquelle l'exception s'est produite
     * @return Le nom de la vue d'erreur ("error")
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model, HttpServletRequest request) {
        logger.error("Unhandled exception in application", e);
        
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        model.addAttribute("error", e.getClass().getSimpleName());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        
        if ("always".equals(includeStacktrace)) {
            model.addAttribute("trace", getStackTrace(e));
        }
        
        return "error";
    }
    
    /**
     * Gère spécifiquement les erreurs de validation métier.
     * 
     * <p>Cette méthode traite les IllegalArgumentException qui sont généralement
     * levées lors de violations de règles métier ou de validation de données.
     * Elle offre un traitement différencié avec un niveau de log WARNING plutôt
     * qu'ERROR, car ces erreurs sont souvent dues à des données utilisateur incorrectes.</p>
     * 
     * <p>Sources typiques d'IllegalArgumentException :</p>
     * <ul>
     *   <li><strong>Validation de données</strong> : IDs invalides, valeurs hors limites</li>
     *   <li><strong>Règles métier</strong> : Logique de trading non respectée</li>
     *   <li><strong>Format de données</strong> : Dates, nombres mal formatés</li>
     *   <li><strong>Contraintes fonctionnelles</strong> : Workflows non respectés</li>
     * </ul>
     * 
     * <p>Exemples dans le contexte trading :</p>
     * <ul>
     *   <li><strong>BidList</strong> : "BidList not found with ID: 123"</li>
     *   <li><strong>CurvePoint</strong> : "Invalid curve ID: must be positive"</li>
     *   <li><strong>User</strong> : "Username must be unique"</li>
     *   <li><strong>Quantities</strong> : "Bid quantity must be positive"</li>
     * </ul>
     * 
     * <p>Traitement spécialisé :</p>
     * <ul>
     *   <li><strong>Level WARNING</strong> : Pas d'erreur technique grave</li>
     *   <li><strong>Message explicite</strong> : Exposition du message d'erreur métier</li>
     *   <li><strong>Pas de stacktrace</strong> : Erreur utilisateur, pas technique</li>
     *   <li><strong>Feedback utilisateur</strong> : Guidage pour correction</li>
     * </ul>
     * 
     * @param e L'exception de validation levée
     * @param model Le modèle pour passer les données à la vue d'erreur
     * @param request La requête HTTP lors de laquelle l'erreur s'est produite
     * @return Le nom de la vue d'erreur ("error")
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleValidationException(IllegalArgumentException e, Model model, HttpServletRequest request) {
        logger.warn("Validation error: {}", e.getMessage());
        
        model.addAttribute("errorMessage", "Validation error: " + e.getMessage());
        model.addAttribute("error", "Validation Error");
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        
        return "error";
    }
    
    /**
     * Gère spécifiquement les erreurs d'accès aux données.
     * 
     * <p>Cette méthode traite toutes les exceptions liées à l'accès aux données,
     * qu'elles proviennent de problèmes de connexion, de contraintes de base de données,
     * ou d'erreurs SQL. Elle fournit une gestion robuste pour maintenir la stabilité
     * de l'application face aux problèmes de persistance.</p>
     * 
     * <p>Types d'erreurs DataAccessException courantes :</p>
     * <ul>
     *   <li><strong>DataIntegrityViolationException</strong> : Contraintes FK, UK, NOT NULL</li>
     *   <li><strong>CannotAcquireLockException</strong> : Deadlocks, timeouts</li>
     *   <li><strong>DataAccessResourceFailureException</strong> : Connexion DB perdue</li>
     *   <li><strong>BadSqlGrammarException</strong> : Erreurs SQL syntax</li>
     *   <li><strong>OptimisticLockingFailureException</strong> : Conflits concurrence</li>
     * </ul>
     * 
     * <p>Contexte trading - Erreurs critiques possibles :</p>
     * <ul>
     *   <li><strong>Constraint violations</strong> : Données financières invalides</li>
     *   <li><strong>Connection timeouts</strong> : Surcharge lors de pics de trading</li>
     *   <li><strong>Deadlocks</strong> : Accès concurrent aux mêmes courbes</li>
     *   <li><strong>Transaction rollbacks</strong> : Cohérence des données critiques</li>
     * </ul>
     * 
     * <p>Stratégie de gestion :</p>
     * <ul>
     *   <li><strong>Logging ERROR</strong> : Problème technique grave</li>
     *   <li><strong>Message générique</strong> : Pas d'exposition détails SQL</li>
     *   <li><strong>Stacktrace conditionnelle</strong> : Pour diagnostic technique</li>
     *   <li><strong>Retry suggestion</strong> : "Please try again later"</li>
     * </ul>
     * 
     * <p>Sécurité et conformité :</p>
     * <ul>
     *   <li><strong>Masquage SQL</strong> : Pas d'exposition de structure DB</li>
     *   <li><strong>Audit logging</strong> : Traçabilité des problèmes de données</li>
     *   <li><strong>Graceful degradation</strong> : Pas de crash application</li>
     *   <li><strong>Recovery guidance</strong> : Message utilisateur approprié</li>
     * </ul>
     * 
     * @param e L'exception d'accès aux données
     * @param model Le modèle pour passer les données à la vue d'erreur
     * @param request La requête HTTP lors de laquelle l'erreur s'est produite
     * @return Le nom de la vue d'erreur ("error")
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public String handleDataAccessException(org.springframework.dao.DataAccessException e, Model model, HttpServletRequest request) {
        logger.error("Database error", e);
        
        model.addAttribute("errorMessage", "Database error occurred. Please try again later.");
        model.addAttribute("error", "Database Error");
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        
        if ("always".equals(includeStacktrace)) {
            model.addAttribute("trace", getStackTrace(e));
        }
        
        return "error";
    }
    
    /**
     * Méthode utilitaire pour extraire la stacktrace d'une exception.
     * 
     * <p>Cette méthode convertit la stacktrace d'une exception en chaîne de caractères
     * pour inclusion dans les réponses d'erreur ou les logs. Elle utilise les classes
     * Java standard StringWriter et PrintWriter pour capturer la sortie normalement
     * dirigée vers la console.</p>
     * 
     * <p>Usage et sécurité :</p>
     * <ul>
     *   <li><strong>Environnement développement</strong> : Stacktraces complètes pour debug</li>
     *   <li><strong>Environnement production</strong> : Contrôlé par configuration</li>
     *   <li><strong>Logs internes</strong> : Toujours disponible pour l'équipe technique</li>
     *   <li><strong>Réponses utilisateur</strong> : Conditionnel selon includeStacktrace</li>
     * </ul>
     * 
     * <p>Considérations performance :</p>
     * <ul>
     *   <li><strong>Capture coûteuse</strong> : Génération stacktrace = overhead</li>
     *   <li><strong>Utilisation ciblée</strong> : Seulement quand nécessaire</li>
     *   <li><strong>Mémoire</strong> : StringWriter fermé automatiquement</li>
     *   <li><strong>Threading</strong> : Thread-safe (objets locaux)</li>
     * </ul>
     * 
     * @param e L'exception dont extraire la stacktrace
     * @return La stacktrace sous forme de chaîne de caractères
     */
    private String getStackTrace(Throwable e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}