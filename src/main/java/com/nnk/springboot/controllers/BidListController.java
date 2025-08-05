
package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidListDTO;
import com.nnk.springboot.mapper.BidListMapper;
import com.nnk.springboot.services.IBidListService;
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
 * Contrôleur Spring MVC pour la gestion des entités BidList.
 * 
 * <p>Ce contrôleur gère l'ensemble des opérations CRUD (Create, Read, Update, Delete)
 * pour les BidList via une interface web. Il utilise le pattern DTO pour la conversion
 * des données entre les couches de présentation et de service.</p>
 * 
 * <p>Toutes les méthodes incluent une gestion d'erreurs robuste et un logging approprié
 * pour faciliter le débogage et la maintenance.</p>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping("/bidList")
public class BidListController {
    /** Logger pour tracer les opérations et erreurs du contrôleur */
    private static final Logger logger = LoggerFactory.getLogger(BidListController.class);

    /** Service de gestion des BidList injecté par Spring */
    @Autowired
    private IBidListService bidListService;

    /**
     * Affiche la liste de toutes les BidList.
     * 
     * <p>Cette méthode récupère toutes les entités BidList depuis le service,
     * les convertit en DTOs pour l'affichage et gère les messages de statut
     * (succès/erreur) transmis via les paramètres de requête.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param success Paramètre optionnel indiquant le type de succès (created, updated, deleted)
     * @param error Paramètre optionnel indiquant le type d'erreur (notfound, invalid, unexpected)
     * @return Le nom de la vue Thymeleaf "bidList/list"
     */
    @GetMapping("/list")
    public String home(Model model, 
                      @RequestParam(value = "success", required = false) String success,
                      @RequestParam(value = "error", required = false) String error) {
        try {
            // Conversion Entity -> DTO pour l'affichage
            List<BidList> bidListEntities = bidListService.findAll();
            List<BidListDTO> bidListDTOs = bidListEntities.stream()
                    .map(BidListMapper::toDTO)
                    .collect(Collectors.toList());
            
            model.addAttribute("bidLists", bidListDTOs);
            
            // Messages de succès/erreur
            addStatusMessages(model, success, error);
            
            return "bidList/list";
        } catch (Exception e) {
            logger.error("Error loading BidList list", e);
            model.addAttribute("errorMessage", "Error loading data: " + e.getMessage());
            return "bidList/list";
        }
    }

    /**
     * Affiche le formulaire de création d'une nouvelle BidList.
     * 
     * <p>Cette méthode prépare un DTO vide qui sera utilisé pour lier
     * les données du formulaire de création.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue Thymeleaf "bidList/add"
     */
    @GetMapping("/add")
    public String addBidForm(Model model) {
        // Utilisation du DTO pour le formulaire
        model.addAttribute("bidListDTO", new BidListDTO());
        return "bidList/add";
    }

    /**
     * Traite la soumission du formulaire de création d'une BidList.
     * 
     * <p>Cette méthode valide les données soumises, les convertit de DTO vers entité,
     * les sauvegarde via le service et redirige vers la liste avec un message de succès.
     * En cas d'erreur de validation ou d'exception, elle retourne au formulaire avec
     * un message d'erreur approprié.</p>
     * 
     * @param bidListDTO Les données de la BidList à créer, validées avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute("bidListDTO") BidListDTO bidListDTO, 
                          BindingResult result, 
                          Model model,
                          RedirectAttributes redirectAttributes) {
        logger.info("Attempting to create new BidList: account={}, type={}", 
                   bidListDTO.getAccount(), bidListDTO.getType());
        
        if (result.hasErrors()) {
            logger.warn("Validation errors found: {}", result.getAllErrors());
            model.addAttribute("bidListDTO", bidListDTO);
            return "bidList/add";
        }
        
        try {
            // Conversion DTO -> Entity pour la persistance
            BidList bidListEntity = BidListMapper.toEntity(bidListDTO);
            BidList savedBid = bidListService.save(bidListEntity);
            
            logger.info("BidList created successfully with ID: {}", savedBid.getBidListId());
            redirectAttributes.addAttribute("success", "created");
            return "redirect:/bidList/list";
        } catch (IllegalArgumentException e) {
            logger.error("Business validation error while saving BidList", e);
            model.addAttribute("bidListDTO", bidListDTO);
            model.addAttribute("errorMessage", "Validation error: " + e.getMessage());
            return "bidList/add";
        } catch (Exception e) {
            logger.error("Unexpected error while saving BidList", e);
            model.addAttribute("bidListDTO", bidListDTO);
            model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            return "bidList/add";
        }
    }

    /**
     * Affiche le formulaire de modification d'une BidList existante.
     * 
     * <p>Cette méthode récupère une BidList par son ID, la convertit en DTO
     * et la passe au formulaire de modification. Elle inclut une validation
     * de l'ID et une gestion d'erreurs robuste.</p>
     * 
     * @param id L'identifiant de la BidList à modifier
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue "bidList/update" ou redirection vers la liste en cas d'erreur
     */
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        logger.info("Attempting to load BidList for update: ID={}", id);
        
        try {
            if (id == null || id <= 0) {
                logger.warn("Invalid ID provided for update: {}", id);
                return "redirect:/bidList/list?error=invalid";
            }
            
            BidList bidListEntity = bidListService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("BidList not found with ID: " + id));
            
            // Conversion Entity -> DTO pour l'affichage/édition
            BidListDTO bidListDTO = BidListMapper.toDTO(bidListEntity);
            model.addAttribute("bidListDTO", bidListDTO);
            
            logger.info("BidList loaded successfully for update: ID={}", id);
            return "bidList/update";
        } catch (IllegalArgumentException e) {
            logger.warn("BidList not found for update: ID={}", id);
            return "redirect:/bidList/list?error=notfound";
        } catch (Exception e) {
            logger.error("Unexpected error while loading BidList for update: ID={}", id, e);
            return "redirect:/bidList/list?error=unexpected";
        }
    }

    /**
     * Traite la soumission du formulaire de modification d'une BidList.
     * 
     * <p>Cette méthode valide les données modifiées, vérifie l'existence de l'entité,
     * effectue la mise à jour via le service et redirige vers la liste avec un message
     * de succès. En cas d'erreur, elle retourne au formulaire avec un message d'erreur.</p>
     * 
     * @param id L'identifiant de la BidList à modifier
     * @param bidListDTO Les données modifiées de la BidList, validées avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/update/{id}")
    public String updateBid(@PathVariable("id") Integer id, 
                           @Valid @ModelAttribute("bidListDTO") BidListDTO bidListDTO,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        logger.info("Attempting to update BidList: ID={}", id);
        
        if (result.hasErrors()) {
            logger.warn("Validation errors found during update: {}", result.getAllErrors());
            bidListDTO.setBidListId(id);
            model.addAttribute("bidListDTO", bidListDTO);
            return "bidList/update";
        }
        
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Invalid ID: " + id);
            }
            
            // Vérifier que l'entité existe avant la mise à jour
            if (!bidListService.existsById(id)) {
                throw new IllegalArgumentException("BidList not found with ID: " + id);
            }
            
            // Conversion DTO -> Entity pour la persistance
            bidListDTO.setBidListId(id);
            BidList bidListEntity = BidListMapper.toEntity(bidListDTO);
            BidList updatedBid = bidListService.save(bidListEntity);
            
            logger.info("BidList updated successfully: ID={}", updatedBid.getBidListId());
            redirectAttributes.addAttribute("success", "updated");
            return "redirect:/bidList/list";
        } catch (IllegalArgumentException e) {
            logger.error("Validation error while updating BidList: ID={}", id, e);
            bidListDTO.setBidListId(id);
            model.addAttribute("bidListDTO", bidListDTO);
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "bidList/update";
        } catch (Exception e) {
            logger.error("Unexpected error while updating BidList: ID={}", id, e);
            bidListDTO.setBidListId(id);
            model.addAttribute("bidListDTO", bidListDTO);
            model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
            return "bidList/update";
        }
    }

    /**
     * Supprime une BidList par son identifiant.
     * 
     * <p>Cette méthode effectue la suppression d'une BidList via le service
     * après validation de l'ID. Elle redirige vers la liste avec un message
     * de succès ou d'erreur selon le résultat de l'opération.</p>
     * 
     * @param id L'identifiant de la BidList à supprimer
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste avec un message de statut approprié
     */
    @GetMapping("/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, 
                           RedirectAttributes redirectAttributes) {
        logger.info("Attempting to delete BidList: ID={}", id);
        
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Invalid ID: " + id);
            }
            
            bidListService.deleteById(id);
            logger.info("BidList deleted successfully: ID={}", id);
            redirectAttributes.addAttribute("success", "deleted");
            return "redirect:/bidList/list";
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting BidList: ID={}", id, e);
            redirectAttributes.addAttribute("error", "notfound");
            return "redirect:/bidList/list";
        } catch (Exception e) {
            logger.error("Unexpected error while deleting BidList: ID={}", id, e);
            redirectAttributes.addAttribute("error", "unexpected");
            return "redirect:/bidList/list";
        }
    }
    
    /**
     * Gestionnaire d'exceptions global pour ce contrôleur.
     * 
     * <p>Cette méthode capture toutes les exceptions non gérées spécifiquement
     * et retourne une vue d'erreur avec un message approprié et une liste vide
     * pour éviter les erreurs d'affichage.</p>
     * 
     * @param e L'exception capturée
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue "bidList/list" avec un message d'erreur
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        logger.error("Unhandled exception in BidListController", e);
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        
        // En cas d'erreur, on renvoie une liste vide plutôt que de planter
        model.addAttribute("bidLists", List.of());
        return "bidList/list";
    }
    
    /**
     * Méthode utilitaire pour ajouter les messages de statut au modèle.
     * 
     * <p>Cette méthode privée centralise la logique d'ajout des messages
     * de succès et d'erreur au modèle en fonction des paramètres reçus
     * lors des redirections.</p>
     * 
     * @param model Le modèle Spring MVC auquel ajouter les messages
     * @param success Le type de message de succès (created, updated, deleted)
     * @param error Le type de message d'erreur (notfound, invalid, unexpected)
     */
    private void addStatusMessages(Model model, String success, String error) {
        if ("deleted".equals(success)) {
            model.addAttribute("successMessage", "BidList deleted successfully");
        } else if ("created".equals(success)) {
            model.addAttribute("successMessage", "BidList created successfully");
        } else if ("updated".equals(success)) {
            model.addAttribute("successMessage", "BidList updated successfully");
        }
        
        if ("notfound".equals(error)) {
            model.addAttribute("errorMessage", "BidList not found");
        } else if ("invalid".equals(error)) {
            model.addAttribute("errorMessage", "Invalid ID provided");
        } else if ("unexpected".equals(error)) {
            model.addAttribute("errorMessage", "An unexpected error occurred");
        }
    }
}