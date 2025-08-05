package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.dto.CurvePointDTO;
import com.nnk.springboot.mapper.CurvePointMapper;
import com.nnk.springboot.services.ICurvePointService;
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

/**
 * Contrôleur Spring MVC pour la gestion des points de courbe financière (CurvePoint).
 * 
 * <p>Ce contrôleur gère l'ensemble des opérations CRUD (Create, Read, Update, Delete)
 * pour les entités CurvePoint via une interface web. Il utilise le pattern DTO pour
 * la conversion des données entre les couches de présentation et de service.</p>
 * 
 * <p>Architecture respectant les principes SOLID :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Gestion uniquement de l'interface web pour CurvePoint</li>
 *   <li><strong>OCP</strong> : Extensible pour nouvelles fonctionnalités</li>
 *   <li><strong>LSP</strong> : Respect du contrat Controller Spring</li>
 *   <li><strong>ISP</strong> : Interface spécialisée pour les opérations CurvePoint</li>
 *   <li><strong>DIP</strong> : Dépend d'abstractions (Service, Mapper)</li>
 * </ul>
 * 
 * <p>Toutes les méthodes incluent une gestion d'erreurs robuste et un logging approprié
 * pour faciliter le débogage et la maintenance.</p>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping("/curvePoint")
public class CurveController {
    
    /** Logger pour tracer les opérations et erreurs du contrôleur */
    private static final Logger logger = LoggerFactory.getLogger(CurveController.class);

    /** Service de gestion des CurvePoint injecté par Spring */
    @Autowired
    private ICurvePointService curvePointService;

    /** Mapper pour la conversion entre entités et DTOs injecté par Spring */
    @Autowired
    private CurvePointMapper curvePointMapper;

    /**
     * Affiche la liste de tous les points de courbe financière.
     * 
     * <p>Cette méthode récupère toutes les entités CurvePoint depuis le service
     * et gère les messages de statut (succès/erreur) transmis via les paramètres
     * de requête lors des redirections.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param success Paramètre optionnel contenant le message de succès à afficher
     * @param error Paramètre optionnel contenant le message d'erreur à afficher
     * @return Le nom de la vue Thymeleaf "curvePoint/list"
     */
    @GetMapping("/list")
    public String home(Model model, 
                      @RequestParam(value = "success", required = false) String success,
                      @RequestParam(value = "error", required = false) String error) {
        try {
            List<CurvePoint> curvePoints = curvePointService.findAll();
            model.addAttribute("curvePoints", curvePoints);
            
            addStatusMessages(model, success, error);
            
            logger.info("Successfully loaded {} curve points", curvePoints.size());
            return "curvePoint/list";
            
        } catch (Exception e) {
            logger.error("Error loading curve points list", e);
            model.addAttribute("errorMessage", "Error loading curve points: " + e.getMessage());
            model.addAttribute("curvePoints", List.of()); // Liste vide en cas d'erreur
            return "curvePoint/list";
        }
    }

    /**
     * Affiche le formulaire de création d'un nouveau point de courbe.
     * 
     * <p>Cette méthode prépare un DTO vide qui sera utilisé pour lier
     * les données du formulaire de création d'un nouveau CurvePoint.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue Thymeleaf "curvePoint/add"
     */
    @GetMapping("/add")
    public String addCurvePointForm(Model model) {
        model.addAttribute("curvePointDTO", new CurvePointDTO());
        return "curvePoint/add";
    }

    /**
     * Traite la soumission du formulaire de création d'un point de courbe.
     * 
     * <p>Cette méthode valide les données soumises via Bean Validation, les convertit
     * de DTO vers entité via le mapper, les sauvegarde via le service et redirige
     * vers la liste avec un message de succès. En cas d'erreur de validation ou
     * d'exception métier, elle retourne au formulaire avec un message d'erreur approprié.</p>
     * 
     * @param curvePointDTO Les données du CurvePoint à créer, validées avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute("curvePointDTO") CurvePointDTO curvePointDTO, 
                          BindingResult result, 
                          Model model,
                          RedirectAttributes redirectAttributes) {
        logger.info("Attempting to create new CurvePoint: curveId={}, term={}, value={}", 
                   curvePointDTO.getCurveId(), curvePointDTO.getTerm(), curvePointDTO.getValue());
        
        if (result.hasErrors()) {
            logger.warn("Validation errors found for new CurvePoint");
            return "curvePoint/add";
        }
        
        try {
            CurvePoint curvePoint = curvePointMapper.toEntity(curvePointDTO);
            CurvePoint savedCurvePoint = curvePointService.save(curvePoint);
            
            logger.info("Successfully created CurvePoint with ID: {}", savedCurvePoint.getId());
            redirectAttributes.addAttribute("success", "Curve Point added successfully");
            return "redirect:/curvePoint/list";
            
        } catch (IllegalArgumentException e) {
            logger.warn("Business validation error for new CurvePoint: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "curvePoint/add";
            
        } catch (Exception e) {
            logger.error("Unexpected error creating CurvePoint", e);
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            return "curvePoint/add";
        }
    }

    /**
     * Affiche le formulaire de modification d'un point de courbe existant.
     * 
     * <p>Cette méthode récupère un CurvePoint par son ID depuis le service,
     * le convertit en DTO via le mapper et le passe au formulaire de modification.
     * Elle inclut une validation de l'existence de l'entité et une gestion
     * d'erreurs robuste.</p>
     * 
     * @param id L'identifiant du CurvePoint à modifier
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue "curvePoint/update" ou redirection vers la liste en cas d'erreur
     */
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        logger.info("Attempting to load CurvePoint for update: ID={}", id);
        
        try {
            CurvePoint curvePoint = curvePointService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CurvePoint not found with id: " + id));
            
            CurvePointDTO curvePointDTO = curvePointMapper.toDTO(curvePoint);
            model.addAttribute("curvePointDTO", curvePointDTO);
            
            logger.info("Successfully loaded CurvePoint for update: ID={}", id);
            return "curvePoint/update";
            
        } catch (IllegalArgumentException e) {
            logger.warn("CurvePoint not found for update: ID={}", id);
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/curvePoint/list";
            
        } catch (Exception e) {
            logger.error("Error loading CurvePoint for update: ID={}", id, e);
            model.addAttribute("errorMessage", "Error loading curve point: " + e.getMessage());
            return "redirect:/curvePoint/list";
        }
    }

    /**
     * Traite la soumission du formulaire de modification d'un point de courbe.
     * 
     * <p>Cette méthode valide les données modifiées, vérifie l'existence de l'entité,
     * met à jour l'entité existante via le mapper, effectue la sauvegarde via le service
     * et redirige vers la liste avec un message de succès. En cas d'erreur, elle retourne
     * au formulaire avec un message d'erreur et préserve l'ID dans le DTO.</p>
     * 
     * @param id L'identifiant du CurvePoint à modifier
     * @param curvePointDTO Les données modifiées du CurvePoint, validées avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/update/{id}")
    public String updateCurvePoint(@PathVariable("id") Integer id, 
                                  @Valid @ModelAttribute("curvePointDTO") CurvePointDTO curvePointDTO,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        logger.info("Attempting to update CurvePoint: ID={}", id);
        
        if (result.hasErrors()) {
            logger.warn("Validation errors found for CurvePoint update: ID={}", id);
            curvePointDTO.setId(id); // Préserver l'ID pour le formulaire
            return "curvePoint/update";
        }
        
        try {
            CurvePoint existingCurvePoint = curvePointService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CurvePoint not found with id: " + id));
            
            curvePointMapper.updateEntityFromDTO(existingCurvePoint, curvePointDTO);
            curvePointService.save(existingCurvePoint);
            
            logger.info("Successfully updated CurvePoint: ID={}", id);
            redirectAttributes.addAttribute("success", "Curve Point updated successfully");
            return "redirect:/curvePoint/list";
            
        } catch (IllegalArgumentException e) {
            logger.warn("Business validation error for CurvePoint update: ID={}, error={}", id, e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            curvePointDTO.setId(id);
            return "curvePoint/update";
            
        } catch (Exception e) {
            logger.error("Unexpected error updating CurvePoint: ID={}", id, e);
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            curvePointDTO.setId(id);
            return "curvePoint/update";
        }
    }

    /**
     * Supprime un point de courbe par son identifiant.
     * 
     * <p>Cette méthode effectue la suppression d'un CurvePoint via le service
     * et redirige vers la liste avec un message de succès ou d'erreur selon
     * le résultat de l'opération. La gestion d'erreurs inclut les cas de
     * validation métier et les erreurs techniques.</p>
     * 
     * @param id L'identifiant du CurvePoint à supprimer
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste avec un message de statut approprié
     */
    @GetMapping("/delete/{id}")
    public String deleteCurvePoint(@PathVariable("id") Integer id, 
                                  RedirectAttributes redirectAttributes) {
        logger.info("Attempting to delete CurvePoint: ID={}", id);
        
        try {
            curvePointService.deleteById(id);
            
            logger.info("Successfully deleted CurvePoint: ID={}", id);
            redirectAttributes.addAttribute("success", "Curve Point deleted successfully");
            
        } catch (IllegalArgumentException e) {
            logger.warn("Cannot delete CurvePoint: ID={}, error={}", id, e.getMessage());
            redirectAttributes.addAttribute("error", e.getMessage());
            
        } catch (Exception e) {
            logger.error("Unexpected error deleting CurvePoint: ID={}", id, e);
            redirectAttributes.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
        }
        
        return "redirect:/curvePoint/list";
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
     * @return Le nom de la vue "curvePoint/list" avec un message d'erreur
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        logger.error("Unhandled exception in CurveController", e);
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        model.addAttribute("curvePoints", List.of()); // Liste vide en cas d'erreur
        return "curvePoint/list";
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
            model.addAttribute("errorMessage", error);
        }
    }
}
