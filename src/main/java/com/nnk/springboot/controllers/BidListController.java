
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

@Controller
@RequestMapping("/bidList")
public class BidListController {
    private static final Logger logger = LoggerFactory.getLogger(BidListController.class);

    @Autowired
    private IBidListService bidListService;

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

    @GetMapping("/add")
    public String addBidForm(Model model) {
        // Utilisation du DTO pour le formulaire
        model.addAttribute("bidListDTO", new BidListDTO());
        return "bidList/add";
    }

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
    
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        logger.error("Unhandled exception in BidListController", e);
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        
        // En cas d'erreur, on renvoie une liste vide plutôt que de planter
        model.addAttribute("bidLists", List.of());
        return "bidList/list";
    }
    
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