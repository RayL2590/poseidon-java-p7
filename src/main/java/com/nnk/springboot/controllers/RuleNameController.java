package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.dto.RuleNameDTO;
import com.nnk.springboot.mapper.RuleNameMapper;
import com.nnk.springboot.services.IRuleNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur Spring MVC pour la gestion des règles métier (RuleName).
 * 
 * <p>Ce contrôleur gère l'ensemble des opérations CRUD (Create, Read, Update, Delete)
 * pour les entités RuleName via une interface web. Il utilise le pattern DTO pour
 * la conversion des données entre les couches de présentation et de service,
 * avec une validation complète des règles métier du système de trading.</p>
 * 
 * <p>Spécificités métier des règles de trading :</p>
 * <ul>
 *   <li><strong>Configuration dynamique</strong> : Paramétrage sans redéploiement</li>
 *   <li><strong>Validation sécurisée</strong> : Protection contre injections SQL</li>
 *   <li><strong>Templates réutilisables</strong> : Règles paramétrables</li>
 *   <li><strong>Audit trail</strong> : Traçabilité des modifications pour conformité</li>
 * </ul>
 * 
 * <p>Architecture respectant les principes SOLID :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Gestion uniquement de l'interface web pour RuleName</li>
 *   <li><strong>OCP</strong> : Extensible pour nouvelles fonctionnalités de règles</li>
 *   <li><strong>LSP</strong> : Respect du contrat Controller Spring</li>
 *   <li><strong>ISP</strong> : Interface spécialisée pour les opérations RuleName</li>
 *   <li><strong>DIP</strong> : Dépend d'abstractions (Service, Mapper)</li>
 * </ul>
 * 
 * <p>Gestion d'erreurs robuste :</p>
 * <ul>
 *   <li><strong>Validation Bean Validation</strong> : Contrôle format des règles</li>
 *   <li><strong>Validation métier</strong> : Cohérence et sécurité des règles</li>
 *   <li><strong>Logging détaillé</strong> : Traçabilité pour audit et maintenance</li>
 *   <li><strong>Messages utilisateur</strong> : Feedback clair et actionnable</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping("/ruleName")
public class RuleNameController {
    
    /** Logger pour tracer les opérations et erreurs du contrôleur */
    private static final Logger logger = LoggerFactory.getLogger(RuleNameController.class);

    /** Service de gestion des RuleName injecté par Spring */
    @Autowired
    private IRuleNameService ruleNameService;

    /** Mapper pour la conversion entre entités et DTOs injecté par Spring */
    @Autowired
    private RuleNameMapper ruleNameMapper;

    /**
     * Affiche la liste de toutes les règles métier.
     * 
     * <p>Cette méthode récupère toutes les entités RuleName depuis le service,
     * les convertit en DTOs pour l'affichage et gère les messages de statut
     * (succès/erreur) transmis via les paramètres de requête lors des redirections.</p>
     * 
     * <p>Les règles sont automatiquement triées par nom croissant (ordre alphabétique)
     * pour faciliter la recherche et la gestion dans l'interface d'administration.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param success Paramètre optionnel contenant le message de succès à afficher
     * @param error Paramètre optionnel contenant le message d'erreur à afficher
     * @return Le nom de la vue Thymeleaf "ruleName/list"
     */
    @GetMapping("/list")
    public String home(Model model, 
                      @RequestParam(required = false) String success,
                      @RequestParam(required = false) String error) {
        try {
            List<RuleName> ruleNames = ruleNameService.findAll();
            List<RuleNameDTO> ruleNameDTOs = ruleNames.stream()
                    .map(ruleNameMapper::toDTO)
                    .collect(Collectors.toList());
            
            model.addAttribute("ruleNames", ruleNameDTOs);
            addStatusMessages(model, success, error);
            
            logger.info("Successfully loaded {} business rules", ruleNames.size());
            return "ruleName/list";
            
        } catch (Exception e) {
            logger.error("Error loading rules list", e);
            model.addAttribute("errorMessage", "Error loading business rules: " + e.getMessage());
            model.addAttribute("ruleNames", List.of()); // Liste vide en cas d'erreur
            return "ruleName/list";
        }
    }

    /**
     * Affiche le formulaire de création d'une nouvelle règle métier.
     * 
     * <p>Cette méthode prépare un DTO vide qui sera utilisé pour lier
     * les données du formulaire de création d'une nouvelle RuleName.
     * Le formulaire permet la saisie complète des composants de règle.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue Thymeleaf "ruleName/add"
     */
    @GetMapping("/add")
    public String addRuleForm(Model model) {
        model.addAttribute("ruleNameDTO", new RuleNameDTO());
        return "ruleName/add";
    }

    /**
     * Traite la soumission du formulaire de création d'une règle métier.
     * 
     * <p>Cette méthode valide les données soumises via Bean Validation (formats
     * des noms, syntaxe JSON), les convertit de DTO vers entité via le mapper, 
     * applique les validations métier via le service et sauvegarde. En cas de succès,
     * elle redirige vers la liste avec un message de confirmation.</p>
     * 
     * <p>Validations appliquées :</p>
     * <ul>
     *   <li><strong>Format des noms</strong> : Regex pour identifiants valides</li>
     *   <li><strong>Unicité des noms</strong> : Pas de doublons dans les identifiants</li>
     *   <li><strong>Syntaxe JSON</strong> : Configuration valide si présente</li>
     *   <li><strong>Sécurité SQL</strong> : Protection contre injections</li>
     * </ul>
     * 
     * @param ruleNameDTO Les données de la RuleName à créer, validées avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute("ruleNameDTO") RuleNameDTO ruleNameDTO, 
                          BindingResult result, 
                          Model model,
                          RedirectAttributes redirectAttributes) {
        logger.info("Attempting to create new RuleName: name={}, description={}", 
                   ruleNameDTO.getName(), ruleNameDTO.getDescription());
        
        if (result.hasErrors()) {
            logger.warn("Validation errors found for new RuleName: {}", 
                       result.getAllErrors().stream()
                           .map(error -> error.getDefaultMessage())
                           .collect(Collectors.joining(", ")));
            return "ruleName/add";
        }
        
        try {
            RuleName ruleName = ruleNameMapper.toEntity(ruleNameDTO);
            RuleName savedRuleName = ruleNameService.save(ruleName);
            
            logger.info("Successfully created RuleName with ID: {} and name: {}", 
                       savedRuleName.getId(), savedRuleName.getName());
            redirectAttributes.addAttribute("success", "Business rule added successfully");
            return "redirect:/ruleName/list";
            
        } catch (IllegalArgumentException e) {
            logger.warn("Business validation error for new RuleName: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "ruleName/add";
            
        } catch (Exception e) {
            logger.error("Unexpected error creating RuleName", e);
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            return "ruleName/add";
        }
    }

    /**
     * Affiche le formulaire de modification d'une règle métier existante.
     * 
     * <p>Cette méthode récupère une RuleName par son ID depuis le service,
     * la convertit en DTO via le mapper et la passe au formulaire de modification.
     * Elle inclut une validation de l'existence de l'entité et une gestion
     * d'erreurs robuste pour les cas de règles inexistantes.</p>
     * 
     * @param id L'identifiant de la RuleName à modifier
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue "ruleName/update" ou redirection vers la liste en cas d'erreur
     */
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model) {
        logger.info("Attempting to load RuleName for update: ID={}", id);
        
        try {
            validateId(id);
            
            RuleName ruleName = ruleNameService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RuleName not found with id: " + id));
            
            RuleNameDTO ruleNameDTO = ruleNameMapper.toDTO(ruleName);
            model.addAttribute("ruleNameDTO", ruleNameDTO);
            
            logger.info("Successfully loaded RuleName for update: ID={}, name={}", 
                       id, ruleName.getName());
            return "ruleName/update";
            
        } catch (IllegalArgumentException e) {
            logger.warn("RuleName not found for update: ID={}", id);
            return "redirect:/ruleName/list?error=notfound";
            
        } catch (Exception e) {
            logger.error("Error loading RuleName for update: ID={}", id, e);
            return "redirect:/ruleName/list?error=unexpected";
        }
    }

    /**
     * Traite la soumission du formulaire de modification d'une règle métier.
     * 
     * <p>Cette méthode valide les données modifiées, vérifie l'existence de l'entité,
     * met à jour l'entité existante via le mapper pour préserver l'état JPA,
     * effectue la sauvegarde via le service et redirige vers la liste avec un
     * message de succès. La mise à jour in-place optimise les performances.</p>
     * 
     * @param id L'identifiant de la RuleName à modifier
     * @param ruleNameDTO Les données modifiées de la RuleName, validées avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/update/{id}")
    public String updateRuleName(@PathVariable Integer id, 
                                @Valid @ModelAttribute RuleNameDTO ruleNameDTO,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        logger.info("Attempting to update RuleName: ID={}", id);
        
        if (result.hasErrors()) {
            logger.warn("Validation errors found for RuleName update: ID={}, errors={}", 
                       id, result.getAllErrors().stream()
                               .map(error -> error.getDefaultMessage())
                               .collect(Collectors.joining(", ")));
            ruleNameDTO.setId(id); // Préserver l'ID pour le formulaire
            return "ruleName/update";
        }
        
        try {
            validateId(id);
            
            RuleName existingRuleName = ruleNameService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RuleName not found with id: " + id));
            
            ruleNameMapper.updateEntityFromDTO(existingRuleName, ruleNameDTO);
            RuleName updatedRuleName = ruleNameService.save(existingRuleName);
            
            logger.info("Successfully updated RuleName: ID={}, new name={}", 
                       updatedRuleName.getId(), updatedRuleName.getName());
            redirectAttributes.addAttribute("success", "Business rule updated successfully");
            return "redirect:/ruleName/list";
            
        } catch (IllegalArgumentException e) {
            logger.warn("Business validation error for RuleName update: ID={}, error={}", 
                       id, e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            ruleNameDTO.setId(id);
            return "ruleName/update";
            
        } catch (Exception e) {
            logger.error("Unexpected error updating RuleName: ID={}", id, e);
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            ruleNameDTO.setId(id);
            return "ruleName/update";
        }
    }

    /**
     * Supprime une règle métier par son identifiant.
     * 
     * <p>Cette méthode effectue la suppression sécurisée d'une RuleName via le service
     * après vérification de l'existence et des contraintes d'intégrité. Elle redirige
     * vers la liste avec un message de succès ou d'erreur selon le résultat de
     * l'opération. La suppression est auditée pour la conformité réglementaire.</p>
     * 
     * @param id L'identifiant de la RuleName à supprimer
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste avec un message de statut approprié
     */
    @GetMapping("/delete/{id}")
    public String deleteRuleName(@PathVariable Integer id, 
                                 RedirectAttributes redirectAttributes) {
        logger.info("Attempting to delete RuleName: ID={}", id);
        
        try {
            validateId(id);
            
            // Log de la règle avant suppression pour audit
            ruleNameService.findById(id).ifPresent(ruleName -> {
                logger.info("Deleting RuleName - ID: {}, Name: {}, Description: {}", 
                           ruleName.getId(), ruleName.getName(), ruleName.getDescription());
            });
            
            ruleNameService.deleteById(id);
            
            logger.info("Successfully deleted RuleName: ID={}", id);
            redirectAttributes.addAttribute("success", "Business rule deleted successfully");
            
        } catch (IllegalArgumentException e) {
            logger.warn("Cannot delete RuleName: ID={}, error={}", id, e.getMessage());
            redirectAttributes.addAttribute("error", "notfound");
            
        } catch (Exception e) {
            logger.error("Unexpected error deleting RuleName: ID={}", id, e);
            redirectAttributes.addAttribute("error", "unexpected");
        }
        
        return "redirect:/ruleName/list";
    }

    /**
     * Affiche les règles filtrées par type de composant.
     * 
     * <p>Cette méthode permet de filtrer et afficher les règles selon leurs
     * composants actifs (JSON, Template, SQL), facilitant la catégorisation
     * et l'analyse des règles selon leur complexité et leurs capacités.</p>
     * 
     * @param componentType Le type de composant à afficher ("json", "template", "sql", "complete", "all")
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue "ruleName/list" avec les données filtrées
     */
    @GetMapping("/filter")
    public String filterByComponent(@RequestParam String componentType, Model model) {
        try {
            List<RuleName> ruleNames;
            String filterDescription;
            
            switch (componentType.toLowerCase()) {
                case "json":
                    ruleNames = ruleNameService.findByComponentType("JSON");
                    filterDescription = "Rules with JSON Configuration";
                    break;
                case "template":
                    ruleNames = ruleNameService.findByComponentType("TEMPLATE");
                    filterDescription = "Rules with Templates";
                    break;
                case "sql":
                    ruleNames = ruleNameService.findByComponentType("SQL");
                    filterDescription = "Rules with SQL Components";
                    break;
                case "complete":
                    ruleNames = ruleNameService.findByComponentType("COMPLETE");
                    filterDescription = "Complete Rules (JSON + Template + SQL)";
                    break;
                case "all":
                default:
                    ruleNames = ruleNameService.findAll();
                    filterDescription = "All Business Rules";
                    break;
            }
            
            List<RuleNameDTO> ruleNameDTOs = ruleNames.stream()
                    .map(ruleNameMapper::toDTO)
                    .collect(Collectors.toList());
            
            model.addAttribute("ruleNames", ruleNameDTOs);
            model.addAttribute("filterDescription", filterDescription);
            model.addAttribute("activeFilter", componentType);
            
            logger.info("Successfully filtered {} rules by component type: {}", ruleNames.size(), componentType);
            return "ruleName/list";
            
        } catch (Exception e) {
            logger.error("Error filtering rules by component type: {}", componentType, e);
            model.addAttribute("errorMessage", "Error filtering rules: " + e.getMessage());
            model.addAttribute("ruleNames", List.of());
            return "ruleName/list";
        }
    }

    /**
     * Recherche de règles par mots-clés.
     * 
     * <p>Cette méthode permet la recherche textuelle dans les noms et descriptions
     * des règles, facilitant la découverte et la gestion des règles dans de gros
     * volumes de données.</p>
     * 
     * @param keyword Le mot-clé à rechercher
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue "ruleName/list" avec les résultats de recherche
     */
    @GetMapping("/search")
    public String searchRules(@RequestParam String keyword, Model model) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return "redirect:/ruleName/list";
            }
            
            List<RuleName> ruleNames = ruleNameService.findByKeyword(keyword);
            List<RuleNameDTO> ruleNameDTOs = ruleNames.stream()
                    .map(ruleNameMapper::toDTO)
                    .collect(Collectors.toList());
            
            model.addAttribute("ruleNames", ruleNameDTOs);
            model.addAttribute("searchKeyword", keyword);
            model.addAttribute("filterDescription", "Search results for: \"" + keyword + "\"");
            
            logger.info("Search for '{}' returned {} rules", keyword, ruleNames.size());
            return "ruleName/list";
            
        } catch (Exception e) {
            logger.error("Error searching rules with keyword: {}", keyword, e);
            model.addAttribute("errorMessage", "Error searching rules: " + e.getMessage());
            model.addAttribute("ruleNames", List.of());
            return "ruleName/list";
        }
    }

    /**
     * API endpoint pour récupérer les règles au format JSON.
     * 
     * <p>Cette méthode fournit un accès programmatique aux données de règles
     * pour les intégrations avec d'autres systèmes, les analyses ou les
     * applications client JavaScript.</p>
     * 
     * @param componentType Type de composant à filtrer (optionnel)
     * @return Liste des règles au format JSON
     */
    @GetMapping("/api")
    @ResponseBody
    public List<RuleNameDTO> getRulesApi(@RequestParam(required = false) String componentType) {
        try {
            List<RuleName> ruleNames;
            
            if (componentType != null && !componentType.trim().isEmpty()) {
                ruleNames = ruleNameService.findByComponentType(componentType.toUpperCase());
                logger.info("API request for {} rules returned {} results", componentType, ruleNames.size());
            } else {
                ruleNames = ruleNameService.findAll();
                logger.info("API request for all rules returned {} results", ruleNames.size());
            }
            
            return ruleNames.stream()
                    .map(ruleNameMapper::toDTO)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("Error in rules API endpoint", e);
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
     * @return Le nom de la vue "ruleName/list" avec un message d'erreur
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        logger.error("Unhandled exception in RuleNameController", e);
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        model.addAttribute("ruleNames", List.of()); // Liste vide en cas d'erreur
        return "ruleName/list";
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
                    model.addAttribute("errorMessage", "Business rule not found");
                    break;
                case "invalid":
                    model.addAttribute("errorMessage", "Invalid rule ID provided");
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
