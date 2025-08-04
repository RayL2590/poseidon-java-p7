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
 * Contrôleur pour la gestion des points de courbe financière
 * Respecte les principes SOLID :
 * - SRP : Gestion uniquement de l'interface web pour CurvePoint
 * - OCP : Extensible pour nouvelles fonctionnalités
 * - LSP : Respect du contrat Controller
 * - ISP : Interface spécialisée pour les opérations CurvePoint
 * - DIP : Dépend d'abstractions (Service, Mapper)
 */
@Controller
@RequestMapping("/curvePoint")
public class CurveController {
    
    private static final Logger logger = LoggerFactory.getLogger(CurveController.class);

    @Autowired
    private ICurvePointService curvePointService;

    @Autowired
    private CurvePointMapper curvePointMapper;

    /**
     * Affiche la liste de tous les points de courbe
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
     * Affiche le formulaire d'ajout d'un nouveau point de courbe
     */
    @GetMapping("/add")
    public String addCurvePointForm(Model model) {
        model.addAttribute("curvePointDTO", new CurvePointDTO());
        return "curvePoint/add";
    }

    /**
     * Traite la soumission du formulaire de création
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
     * Affiche le formulaire de modification d'un point de courbe
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
     * Traite la soumission du formulaire de modification
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
     * Supprime un point de courbe
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
     * Gestionnaire d'exception global pour ce contrôleur
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        logger.error("Unhandled exception in CurveController", e);
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        model.addAttribute("curvePoints", List.of()); // Liste vide en cas d'erreur
        return "curvePoint/list";
    }
    
    /**
     * Ajoute les messages de statut au modèle
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
