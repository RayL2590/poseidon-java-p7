package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.dto.TradeDTO;
import com.nnk.springboot.mapper.TradeMapper;
import com.nnk.springboot.services.ITradeService;
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
 * Contrôleur Spring MVC pour la gestion des transactions (Trade).
 * 
 * <p>Ce contrôleur gère l'ensemble des opérations CRUD (Create, Read, Update, Delete)
 * pour les entités Trade via une interface web. Il utilise le pattern DTO pour
 * la conversion des données entre les couches de présentation et de service,
 * avec une validation complète des transactions financières.</p>
 * 
 * <p>Spécificités métier des transactions de trading :</p>
 * <ul>
 *   <li><strong>Validation financière</strong> : Contrôle des montants et quantités</li>
 *   <li><strong>Traçabilité complète</strong> : Audit trail des modifications</li>
 *   <li><strong>Contrôles de risque</strong> : Limites et seuils de sécurité</li>
 *   <li><strong>Cohérence temporelle</strong> : Validation des dates de transaction</li>
 * </ul>
 * 
 * <p>Architecture respectant les principes SOLID :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Gestion uniquement de l'interface web pour Trade</li>
 *   <li><strong>OCP</strong> : Extensible pour nouvelles fonctionnalités de trading</li>
 *   <li><strong>LSP</strong> : Respect du contrat Controller Spring</li>
 *   <li><strong>ISP</strong> : Interface spécialisée pour les opérations Trade</li>
 *   <li><strong>DIP</strong> : Dépend d'abstractions (Service, Mapper)</li>
 * </ul>
 * 
 * <p>Gestion d'erreurs robuste :</p>
 * <ul>
 *   <li><strong>Validation Bean Validation</strong> : Contrôle format des transactions</li>
 *   <li><strong>Validation métier</strong> : Cohérence et sécurité financière</li>
 *   <li><strong>Logging détaillé</strong> : Traçabilité pour audit et conformité</li>
 *   <li><strong>Messages utilisateur</strong> : Feedback clair et actionnable</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping("/trade")
public class TradeController {
    
    /** Logger pour tracer les opérations et erreurs du contrôleur */
    private static final Logger logger = LoggerFactory.getLogger(TradeController.class);

    /** Service de gestion des Trade injecté par Spring */
    @Autowired
    private ITradeService tradeService;

    /** Mapper pour la conversion entre entités et DTOs injecté par Spring */
    @Autowired
    private TradeMapper tradeMapper;

    /**
     * Affiche la liste de toutes les transactions.
     * 
     * <p>Cette méthode récupère toutes les entités Trade depuis le service,
     * les convertit en DTOs pour l'affichage et gère les messages de statut
     * (succès/erreur) transmis via les paramètres de requête lors des redirections.</p>
     * 
     * <p>Les transactions sont automatiquement triées par date décroissante
     * (plus récentes en premier) pour faciliter le suivi des opérations récentes.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param success Paramètre optionnel contenant le message de succès à afficher
     * @param error Paramètre optionnel contenant le message d'erreur à afficher
     * @return Le nom de la vue Thymeleaf "trade/list"
     */
    @GetMapping("/list")
    public String home(Model model, 
                      @RequestParam(required = false) String success,
                      @RequestParam(required = false) String error) {
        try {
            List<Trade> trades = tradeService.findAll();
            List<TradeDTO> tradeDTOs = trades.stream()
                    .map(tradeMapper::toDTO)
                    .collect(Collectors.toList());
            
            model.addAttribute("trades", tradeDTOs);
            addStatusMessages(model, success, error);
            
            logger.info("Successfully loaded {} transactions", trades.size());
            
        } catch (Exception e) {
            logger.error("Error loading trade list: ", e);
            model.addAttribute("error", "Unable to load transactions. Please try again.");
            model.addAttribute("trades", List.of());
        }
        
        return "trade/list";
    }

    /**
     * Affiche le formulaire de création d'une nouvelle transaction.
     * 
     * <p>Cette méthode prépare un DTO vide pour le formulaire de création
     * et l'ajoute au modèle pour l'affichage. Le DTO vide permet au formulaire
     * Thymeleaf de se lier correctement aux champs de saisie.</p>
     * 
     * @param trade L'objet TradeDTO vide (créé automatiquement par Spring)
     * @return Le nom de la vue Thymeleaf "trade/add"
     */
    @GetMapping("/add")
    public String addTradeForm(Model model) {
        logger.info("Displaying add trade form");
        model.addAttribute("trade", new TradeDTO());
        return "trade/add";
    }

    /**
     * Traite la soumission du formulaire de création de transaction.
     * 
     * <p>Cette méthode effectue la validation complète des données saisies,
     * convertit le DTO en entité, sauvegarde la transaction via le service
     * et redirige vers la liste avec un message de confirmation.</p>
     * 
     * <p>Gestion d'erreurs :</p>
     * <ul>
     *   <li><strong>Erreurs de validation</strong> : Retour au formulaire avec messages</li>
     *   <li><strong>Erreurs métier</strong> : Affichage de messages d'erreur spécifiques</li>
     *   <li><strong>Succès</strong> : Redirection avec message de confirmation</li>
     * </ul>
     * 
     * @param tradeDTO Le DTO contenant les données saisies (avec validation)
     * @param result Les résultats de validation Spring
     * @param model Le modèle pour ajouter des données si nécessaire
     * @param redirectAttributes Pour transmettre les messages lors de la redirection
     * @return Redirection vers la liste ou retour au formulaire si erreur
     */
    @PostMapping("/validate")
    public String validate(@Valid TradeDTO tradeDTO, BindingResult result, 
                          Model model, RedirectAttributes redirectAttributes) {
        
        logger.info("Processing trade creation for account: {}", tradeDTO.getAccount());
        
        // Vérification des erreurs de validation Bean Validation
        if (result.hasErrors()) {
            logger.warn("Validation errors found in trade creation form");
            model.addAttribute("trade", tradeDTO);
            return "trade/add";
        }
        
        try {
            // Conversion DTO -> Entity et sauvegarde
            Trade trade = tradeMapper.toEntity(tradeDTO);
            Trade savedTrade = tradeService.save(trade);
            
            logger.info("Successfully created trade with ID: {}", savedTrade.getTradeId());
            redirectAttributes.addAttribute("success", 
                "Transaction successfully created for account " + savedTrade.getAccount());
            
            return "redirect:/trade/list";
            
        } catch (IllegalArgumentException e) {
            logger.warn("Business validation error during trade creation: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("trade", tradeDTO);
            return "trade/add";
            
        } catch (Exception e) {
            logger.error("Unexpected error during trade creation: ", e);
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            model.addAttribute("trade", tradeDTO);
            return "trade/add";
        }
    }

    /**
     * Affiche le formulaire de modification d'une transaction existante.
     * 
     * <p>Cette méthode récupère la transaction par son ID, la convertit en DTO
     * pour l'affichage dans le formulaire de modification. Si la transaction
     * n'existe pas, redirige vers la liste avec un message d'erreur.</p>
     * 
     * @param id L'identifiant de la transaction à modifier
     * @param model Le modèle pour passer les données à la vue
     * @param redirectAttributes Pour transmettre les messages d'erreur
     * @return Le nom de la vue "trade/update" ou redirection si erreur
     */
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model,
                                RedirectAttributes redirectAttributes) {
        
        logger.info("Displaying update form for trade ID: {}", id);
        
        try {
            Trade trade = tradeService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + id));
            
            TradeDTO tradeDTO = tradeMapper.toDTO(trade);
            model.addAttribute("trade", tradeDTO);
            
            return "trade/update";
            
        } catch (IllegalArgumentException e) {
            logger.warn("Trade not found for update: {}", e.getMessage());
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/trade/list";
            
        } catch (Exception e) {
            logger.error("Error loading trade for update: ", e);
            redirectAttributes.addAttribute("error", "Unable to load transaction for modification.");
            return "redirect:/trade/list";
        }
    }

    /**
     * Traite la soumission du formulaire de modification de transaction.
     * 
     * <p>Cette méthode effectue la validation des données modifiées,
     * met à jour l'entité existante avec les nouvelles données et
     * sauvegarde les changements via le service.</p>
     * 
     * @param id L'identifiant de la transaction à modifier
     * @param tradeDTO Le DTO contenant les données modifiées
     * @param result Les résultats de validation Spring
     * @param model Le modèle pour les données de retour si erreur
     * @param redirectAttributes Pour transmettre les messages lors de la redirection
     * @return Redirection vers la liste ou retour au formulaire si erreur
     */
    @PostMapping("/update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @Valid TradeDTO tradeDTO,
                             BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        
        logger.info("Processing trade update for ID: {}", id);
        
        // Vérification des erreurs de validation Bean Validation
        if (result.hasErrors()) {
            logger.warn("Validation errors found in trade update form for ID: {}", id);
            tradeDTO.setTradeId(id); // S'assurer que l'ID est défini
            model.addAttribute("trade", tradeDTO);
            return "trade/update";
        }
        
        try {
            // Vérification de l'existence de la transaction
            Trade existingTrade = tradeService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + id));
            
            // Mise à jour avec les nouvelles données
            tradeDTO.setTradeId(id); // S'assurer que l'ID est correct
            tradeMapper.updateEntityFromDTO(existingTrade, tradeDTO);
            
            Trade updatedTrade = tradeService.save(existingTrade);
            
            logger.info("Successfully updated trade ID: {}", updatedTrade.getTradeId());
            redirectAttributes.addAttribute("success", 
                "Transaction successfully updated for account " + updatedTrade.getAccount());
            
            return "redirect:/trade/list";
            
        } catch (IllegalArgumentException e) {
            logger.warn("Business validation error during trade update: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            tradeDTO.setTradeId(id);
            model.addAttribute("trade", tradeDTO);
            return "trade/update";
            
        } catch (Exception e) {
            logger.error("Unexpected error during trade update: ", e);
            model.addAttribute("error", "An unexpected error occurred during update. Please try again.");
            tradeDTO.setTradeId(id);
            model.addAttribute("trade", tradeDTO);
            return "trade/update";
        }
    }

    /**
     * Supprime une transaction.
     * 
     * <p>Cette méthode supprime définitivement une transaction après
     * vérification de son existence. La suppression est immédiate et
     * irréversible.</p>
     * 
     * @param id L'identifiant de la transaction à supprimer
     * @param redirectAttributes Pour transmettre les messages de statut
     * @return Redirection vers la liste des transactions
     */
    @GetMapping("/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        
        logger.info("Processing trade deletion for ID: {}", id);
        
        try {
            // Vérification de l'existence avant suppression pour un meilleur message d'erreur
            Trade trade = tradeService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + id));
            
            String accountInfo = trade.getAccount(); // Pour le message de confirmation
            
            tradeService.deleteById(id);
            
            logger.info("Successfully deleted trade ID: {} for account: {}", id, accountInfo);
            redirectAttributes.addAttribute("success", 
                "Transaction successfully deleted for account " + accountInfo);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Trade deletion failed: {}", e.getMessage());
            redirectAttributes.addAttribute("error", e.getMessage());
            
        } catch (Exception e) {
            logger.error("Unexpected error during trade deletion: ", e);
            redirectAttributes.addAttribute("error", "Unable to delete transaction. Please try again.");
        }
        
        return "redirect:/trade/list";
    }

    /**
     * Ajoute les messages de statut au modèle pour l'affichage dans la vue.
     * 
     * <p>Méthode utilitaire pour centraliser la gestion des messages de
     * succès et d'erreur transmis via les paramètres de requête.</p>
     * 
     * @param model Le modèle Spring MVC
     * @param success Message de succès optionnel
     * @param error Message d'erreur optionnel
     */
    private void addStatusMessages(Model model, String success, String error) {
        if (success != null && !success.trim().isEmpty()) {
            model.addAttribute("success", success);
        }
        if (error != null && !error.trim().isEmpty()) {
            model.addAttribute("error", error);
        }
    }
}
