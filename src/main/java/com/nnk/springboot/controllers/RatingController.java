package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Rating;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import com.nnk.springboot.dto.RatingDTO;
import com.nnk.springboot.mapper.RatingMapper;
import com.nnk.springboot.services.IRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur Spring MVC pour la gestion des notations de crédit (Rating).
 * 
 * <p>Ce contrôleur gère l'ensemble des opérations CRUD (Create, Read, Update, Delete)
 * pour les entités Rating via une interface web. Il utilise le pattern DTO pour
 * la conversion des données entre les couches de présentation et de service,
 * avec une validation complète des standards de notation financière.</p>
 * 
 * <p>Spécificités métier des notations de crédit :</p>
 * <ul>
 *   <li><strong>Standards internationaux</strong> : Respect des échelles Moody's, S&P, Fitch</li>
 *   <li><strong>Validation croisée</strong> : Cohérence entre agences de notation</li>
 *   <li><strong>Classification ordonnée</strong> : Gestion des hiérarchies de qualité</li>
 *   <li><strong>Audit trail</strong> : Traçabilité des modifications pour conformité</li>
 * </ul>
 * 
 * <p>Architecture respectant les principes SOLID :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Gestion uniquement de l'interface web pour Rating</li>
 *   <li><strong>OCP</strong> : Extensible pour nouvelles fonctionnalités de notation</li>
 *   <li><strong>LSP</strong> : Respect du contrat Controller Spring</li>
 *   <li><strong>ISP</strong> : Interface spécialisée pour les opérations Rating</li>
 *   <li><strong>DIP</strong> : Dépend d'abstractions (Service, Mapper)</li>
 * </ul>
 * 
 * <p>Gestion d'erreurs robuste :</p>
 * <ul>
 *   <li><strong>Validation Bean Validation</strong> : Contrôle format des notations</li>
 *   <li><strong>Validation métier</strong> : Cohérence inter-agences</li>
 *   <li><strong>Logging détaillé</strong> : Traçabilité pour audit et maintenance</li>
 *   <li><strong>Messages utilisateur</strong> : Feedback clair et actionnable</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping("/rating")
public class RatingController {
    
    /** Logger pour tracer les opérations et erreurs du contrôleur */
    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);

    /** Service de gestion des Rating injecté par Spring */
    @Autowired
    private IRatingService ratingService;

    /** Mapper pour la conversion entre entités et DTOs injecté par Spring */
    @Autowired
    private RatingMapper ratingMapper;

    /**
     * Affiche la liste de toutes les notations de crédit.
     * 
     * <p>Cette méthode récupère toutes les entités Rating depuis le service,
     * les convertit en DTOs pour l'affichage et gère les messages de statut
     * (succès/erreur) transmis via les paramètres de requête lors des redirections.</p>
     * 
     * <p>Les notations sont automatiquement triées par numéro d'ordre croissant
     * (meilleure notation en premier) pour faciliter l'analyse de risque.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param success Paramètre optionnel contenant le message de succès à afficher
     * @param error Paramètre optionnel contenant le message d'erreur à afficher
     * @return Le nom de la vue Thymeleaf "rating/list"
     */
    @GetMapping("/list")
    public String home(Model model, 
                      @RequestParam(required = false) String success,
                      @RequestParam(required = false) String error) {
        try {
            List<Rating> ratings = ratingService.findAll();
            List<RatingDTO> ratingDTOs = ratings.stream()
                    .map(ratingMapper::toDTO)
                    .collect(Collectors.toList());
            
            model.addAttribute("ratings", ratingDTOs);
            addStatusMessages(model, success, error);
            
            logger.info("Successfully loaded {} credit ratings", ratings.size());
            return "rating/list";
            
        } catch (Exception e) {
            logger.error("Error loading ratings list", e);
            model.addAttribute("errorMessage", "Error loading credit ratings: " + e.getMessage());
            model.addAttribute("ratings", List.of()); // Liste vide en cas d'erreur
            return "rating/list";
        }
    }

    /**
     * Affiche le formulaire de création d'une nouvelle notation de crédit.
     * 
     * <p>Cette méthode prépare un DTO vide qui sera utilisé pour lier
     * les données du formulaire de création d'un nouveau Rating.
     * Le formulaire permet la saisie des notations selon les trois
     * principales agences internationales.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue Thymeleaf "rating/add"
     */
    @GetMapping("/add")
    public String addRatingForm(Model model) {
        model.addAttribute("ratingDTO", new RatingDTO());
        return "rating/add";
    }

    /**
     * Traite la soumission du formulaire de création d'une notation de crédit.
     * 
     * <p>Cette méthode valide les données soumises via Bean Validation (formats
     * des notations), les convertit de DTO vers entité via le mapper, applique
     * les validations métier via le service et sauvegarde. En cas de succès,
     * elle redirige vers la liste avec un message de confirmation.</p>
     * 
     * <p>Validations appliquées :</p>
     * <ul>
     *   <li><strong>Format des notations</strong> : Regex pour chaque agence</li>
     *   <li><strong>Cohérence inter-agences</strong> : Équivalence des évaluations</li>
     *   <li><strong>Unicité numéro d'ordre</strong> : Pas de doublons</li>
     *   <li><strong>Complétude minimale</strong> : Au moins une notation requise</li>
     * </ul>
     * 
     * @param ratingDTO Les données du Rating à créer, validées avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute("ratingDTO") RatingDTO ratingDTO, 
                          BindingResult result, 
                          Model model,
                          RedirectAttributes redirectAttributes) {
        logger.info("Attempting to create new Rating: Moody's={}, S&P={}, Fitch={}, Order={}", 
                   ratingDTO.getMoodysRating(), ratingDTO.getSandPRating(), 
                   ratingDTO.getFitchRating(), ratingDTO.getOrderNumber());
        
        if (result.hasErrors()) {
            logger.warn("Validation errors found for new Rating: {}", 
                       result.getAllErrors().stream()
                           .map(error -> error.getDefaultMessage())
                           .collect(Collectors.joining(", ")));
            return "rating/add";
        }
        
        try {
            Rating rating = ratingMapper.toEntity(ratingDTO);
            Rating savedRating = ratingService.save(rating);
            
            logger.info("Successfully created Rating with ID: {} and order: {}", 
                       savedRating.getId(), savedRating.getOrderNumber());
            redirectAttributes.addAttribute("success", "Credit rating added successfully");
            return "redirect:/rating/list";
            
        } catch (IllegalArgumentException e) {
            logger.warn("Business validation error for new Rating: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "rating/add";
            
        } catch (Exception e) {
            logger.error("Unexpected error creating Rating", e);
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            return "rating/add";
        }
    }

    /**
     * Affiche le formulaire de modification d'une notation de crédit existante.
     * 
     * <p>Cette méthode récupère un Rating par son ID depuis le service,
     * le convertit en DTO via le mapper et le passe au formulaire de modification.
     * Elle inclut une validation de l'existence de l'entité et une gestion
     * d'erreurs robuste pour les cas de notations inexistantes.</p>
     * 
     * @param id L'identifiant du Rating à modifier
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue "rating/update" ou redirection vers la liste en cas d'erreur
     */
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model) {
        logger.info("Attempting to load Rating for update: ID={}", id);
        
        try {
            validateId(id);
            
            Rating rating = ratingService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found with id: " + id));
            
            RatingDTO ratingDTO = ratingMapper.toDTO(rating);
            model.addAttribute("ratingDTO", ratingDTO);
            
            logger.info("Successfully loaded Rating for update: ID={}, Order={}", 
                       id, rating.getOrderNumber());
            return "rating/update";
            
        } catch (IllegalArgumentException e) {
            logger.warn("Rating not found for update: ID={}", id);
            return "redirect:/rating/list?error=notfound";
            
        } catch (Exception e) {
            logger.error("Error loading Rating for update: ID={}", id, e);
            return "redirect:/rating/list?error=unexpected";
        }
    }

    /**
     * Traite la soumission du formulaire de modification d'une notation de crédit.
     * 
     * <p>Cette méthode valide les données modifiées, vérifie l'existence de l'entité,
     * met à jour l'entité existante via le mapper pour préserver l'état JPA,
     * effectue la sauvegarde via le service et redirige vers la liste avec un
     * message de succès. La mise à jour in-place optimise les performances.</p>
     * 
     * @param id L'identifiant du Rating à modifier
     * @param ratingDTO Les données modifiées du Rating, validées avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/update/{id}")
    public String updateRating(@PathVariable Integer id, 
                              @Valid @ModelAttribute RatingDTO ratingDTO,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        logger.info("Attempting to update Rating: ID={}", id);
        
        if (result.hasErrors()) {
            logger.warn("Validation errors found for Rating update: ID={}, errors={}", 
                       id, result.getAllErrors().stream()
                               .map(error -> error.getDefaultMessage())
                               .collect(Collectors.joining(", ")));
            ratingDTO.setId(id); // Préserver l'ID pour le formulaire
            return "rating/update";
        }
        
        try {
            validateId(id);
            
            Rating existingRating = ratingService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rating not found with id: " + id));
            
            ratingMapper.updateEntityFromDTO(existingRating, ratingDTO);
            Rating updatedRating = ratingService.save(existingRating);
            
            logger.info("Successfully updated Rating: ID={}, new order={}", 
                       updatedRating.getId(), updatedRating.getOrderNumber());
            redirectAttributes.addAttribute("success", "Credit rating updated successfully");
            return "redirect:/rating/list";
            
        } catch (IllegalArgumentException e) {
            logger.warn("Business validation error for Rating update: ID={}, error={}", 
                       id, e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            ratingDTO.setId(id);
            return "rating/update";
            
        } catch (Exception e) {
            logger.error("Unexpected error updating Rating: ID={}", id, e);
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            ratingDTO.setId(id);
            return "rating/update";
        }
    }

    /**
     * Supprime une notation de crédit par son identifiant.
     * 
     * <p>Cette méthode effectue la suppression sécurisée d'un Rating via le service
     * après vérification de l'existence et des contraintes d'intégrité. Elle redirige
     * vers la liste avec un message de succès ou d'erreur selon le résultat de
     * l'opération. La suppression est auditée pour la conformité réglementaire.</p>
     * 
     * @param id L'identifiant du Rating à supprimer
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste avec un message de statut approprié
     */
    @GetMapping("/delete/{id}")
    public String deleteRating(@PathVariable Integer id, 
                              RedirectAttributes redirectAttributes) {
        logger.info("Attempting to delete Rating: ID={}", id);
        
        try {
            validateId(id);
            
            // Log de la notation avant suppression pour audit
            ratingService.findById(id).ifPresent(rating -> {
                logger.info("Deleting Rating - ID: {}, Moody's: {}, S&P: {}, Fitch: {}, Order: {}", 
                           rating.getId(), rating.getMoodysRating(), rating.getSandPRating(), 
                           rating.getFitchRating(), rating.getOrderNumber());
            });
            
            ratingService.deleteById(id);
            
            logger.info("Successfully deleted Rating: ID={}", id);
            redirectAttributes.addAttribute("success", "Credit rating deleted successfully");
            
        } catch (IllegalArgumentException e) {
            logger.warn("Cannot delete Rating: ID={}, error={}", id, e.getMessage());
            redirectAttributes.addAttribute("error", "notfound");
            
        } catch (Exception e) {
            logger.error("Unexpected error deleting Rating: ID={}", id, e);
            redirectAttributes.addAttribute("error", "unexpected");
        }
        
        return "redirect:/rating/list";
    }

    /**
     * Affiche les notations filtrées par type (Investment vs Speculative Grade).
     * 
     * <p>Cette méthode permet de filtrer et afficher les notations selon leur
     * qualité de crédit, facilitant l'analyse de portefeuille et la gestion
     * des risques. Elle supporte les filtres standard de l'industrie financière.</p>
     * 
     * @param type Le type de notation à afficher ("investment", "speculative", "all")
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue "rating/list" avec les données filtrées
     */
    @GetMapping("/filter")
    public String filterByType(@RequestParam String type, Model model) {
        try {
            List<Rating> ratings;
            String filterDescription;
            
            switch (type.toLowerCase()) {
                case "investment":
                    ratings = ratingService.findInvestmentGrade();
                    filterDescription = "Investment Grade Ratings (BBB-/Baa3 and above)";
                    break;
                case "speculative":
                    ratings = ratingService.findSpeculativeGrade();
                    filterDescription = "Speculative Grade Ratings (BB+/Ba1 and below)";
                    break;
                case "all":
                default:
                    ratings = ratingService.findAll();
                    filterDescription = "All Credit Ratings";
                    break;
            }
            
            List<RatingDTO> ratingDTOs = ratings.stream()
                    .map(ratingMapper::toDTO)
                    .collect(Collectors.toList());
            
            model.addAttribute("ratings", ratingDTOs);
            model.addAttribute("filterDescription", filterDescription);
            model.addAttribute("activeFilter", type);
            
            logger.info("Successfully filtered {} ratings by type: {}", ratings.size(), type);
            return "rating/list";
            
        } catch (Exception e) {
            logger.error("Error filtering ratings by type: {}", type, e);
            model.addAttribute("errorMessage", "Error filtering ratings: " + e.getMessage());
            model.addAttribute("ratings", List.of());
            return "rating/list";
        }
    }

    /**
     * API endpoint pour récupérer les notations au format JSON.
     * 
     * <p>Cette méthode fournit un accès programmatique aux données de notation
     * pour les intégrations avec d'autres systèmes, les analyses quantitatives
     * ou les applications client JavaScript.</p>
     * 
     * @param agency Agence de notation à filtrer (optionnel: "moodys", "sp", "fitch")
     * @return Liste des notations au format JSON
     */
    @GetMapping("/api")
    @ResponseBody
    public List<RatingDTO> getRatingsApi(@RequestParam(required = false) String agency) {
        try {
            List<Rating> ratings;
            
            if (agency != null && !agency.trim().isEmpty()) {
                ratings = ratingService.findByAgency(agency.toUpperCase());
                logger.info("API request for {} ratings returned {} results", agency, ratings.size());
            } else {
                ratings = ratingService.findAll();
                logger.info("API request for all ratings returned {} results", ratings.size());
            }
            
            return ratings.stream()
                    .map(ratingMapper::toDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error in ratings API endpoint", e);
            return List.of(); // Retourne liste vide en cas d'erreur
        }
    }
    
    /**
     * Gestionnaire d'exceptions global pour ce contrôleur.
     * 
     * <p>Cette méthode capture toutes les exceptions non gérées spécifiquement
     * dans les autres méthodes du contrôleur. Elle retourne une vue d'erreur
     * avec un message approprié et une liste vide pour éviter les erreurs
     * d'affichage dans la vue.</p>
     * 
     * @param e L'exception capturée
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue "rating/list" avec un message d'erreur
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        logger.error("Unhandled exception in RatingController", e);
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        model.addAttribute("ratings", List.of()); // Liste vide en cas d'erreur
        return "rating/list";
    }
    
    /**
     * Méthode utilitaire pour ajouter les messages de statut au modèle.
     * 
     * <p>Cette méthode privée centralise la logique d'ajout des messages
     * de succès et d'erreur au modèle en fonction des paramètres reçus
     * lors des redirections. Elle vérifie que les messages ne sont pas
     * null ou vides avant de les ajouter au modèle.</p>
     * 
     * @param model Le modèle Spring MVC auquel ajouter les messages
     * @param success Le message de succès à afficher (peut être null)
     * @param error Le message d'erreur à afficher (peut être null)
     */
    private void addStatusMessages(Model model, String success, String error) {
        if (success != null && !success.trim().isEmpty()) {
            model.addAttribute("successMessage", success);
        }
        
        if (error != null && !error.trim().isEmpty()) {
            switch (error) {
                case "notfound":
                    model.addAttribute("errorMessage", "Credit rating not found");
                    break;
                case "invalid":
                    model.addAttribute("errorMessage", "Invalid rating ID provided");
                    break;
                case "unexpected":
                    model.addAttribute("errorMessage", "An unexpected error occurred");
                    break;
                default:
                    model.addAttribute("errorMessage", error);
                    break;
            }
        }
    }

    /**
     * Méthode utilitaire pour valider les paramètres d'ID.
     * 
     * <p>Validation centralisée des identifiants pour éviter la duplication
     * de code et assurer une validation cohérente dans tout le contrôleur.</p>
     * 
     * @param id L'identifiant à valider
     * @throws IllegalArgumentException si l'ID est null ou invalide
     */
    private void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }
    }
}