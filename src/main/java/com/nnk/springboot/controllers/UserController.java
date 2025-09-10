package com.nnk.springboot.controllers;

import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Contrôleur Spring MVC pour la gestion des utilisateurs (User).
 * 
 * <p>Ce contrôleur gère l'ensemble des opérations CRUD (Create, Read, Update, Delete)
 * pour les entités User via une interface web. Il utilise le pattern DTO pour
 * la conversion des données entre les couches de présentation et de service.</p>
 * 
 * <p>Architecture respectant les principes SOLID :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Gestion uniquement de l'interface web pour User</li>
 *   <li><strong>OCP</strong> : Extensible pour nouvelles fonctionnalités</li>
 *   <li><strong>LSP</strong> : Respect du contrat Controller Spring</li>
 *   <li><strong>ISP</strong> : Interface spécialisée pour les opérations User</li>
 *   <li><strong>DIP</strong> : Dépend d'abstractions (Service)</li>
 * </ul>
 * 
 * <p>Toutes les méthodes incluent une gestion d'erreurs robuste et un logging approprié
 * pour faciliter le débogage et la maintenance. La sécurité est assurée par le chiffrement
 * des mots de passe et la validation de l'unicité des noms d'utilisateur.</p>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Controller
public class UserController {
    
    /** Service de gestion des utilisateurs injecté par Spring */
    @Autowired
    private IUserService userService;

    /**
     * Affiche la liste de tous les utilisateurs.
     * 
     * <p>Cette méthode récupère tous les utilisateurs sous forme de DTOs depuis le service
     * et gère les erreurs potentielles lors du chargement. En cas d'erreur technique,
     * elle redirige vers une page d'erreur générique.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue Thymeleaf "user/list" ou "error" en cas d'erreur
     */
    @RequestMapping("/user/list")
    public String home(Model model) {
        try {
            List<UserDTO> userDTOs = userService.findAllAsDTO();
            model.addAttribute("users", userDTOs);
            return "user/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors du chargement des utilisateurs");
            return "error";
        }
    }

    /**
     * Affiche le formulaire de création d'un nouvel utilisateur.
     * 
     * <p>Cette méthode prépare un DTO vide qui sera utilisé pour lier
     * les données du formulaire de création d'un nouvel utilisateur.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue Thymeleaf "user/add"
     */
    @GetMapping("/user/add")
    public String addUser(Model model) {
        model.addAttribute("user", new UserDTO());
        return "user/add";
    }

    /**
     * Traite la soumission du formulaire de création d'un utilisateur.
     * 
     * <p>Cette méthode valide les données soumises via Bean Validation, effectue
     * la création via le service métier qui gère le chiffrement du mot de passe
     * et la validation de l'unicité du nom d'utilisateur. En cas de succès,
     * elle redirige vers la liste avec un message de succès. En cas d'erreur
     * de validation ou d'exception métier, elle retourne au formulaire avec
     * un message d'erreur approprié.</p>
     * 
     * @param userDTO Les données de l'utilisateur à créer, validées avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/user/validate")
    public String validate(@Valid UserDTO userDTO, BindingResult result, Model model, 
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/add";
        }
        
        try {
            // Création via service métier avec DTO (validation + chiffrement)
            UserDTO savedUser = userService.saveFromDTO(userDTO);
            
            // Message de succès pour l'utilisateur
            redirectAttributes.addFlashAttribute("successMessage", 
                "Utilisateur '" + savedUser.getUsername() + "' créé avec succès");
            
            return "redirect:/user/list";
            
        } catch (IllegalArgumentException e) {
            // Erreurs métier (username existe, données invalides)
            model.addAttribute("errorMessage", e.getMessage());
            return "user/add";
            
        } catch (Exception e) {
            // Erreurs techniques inattendues
            model.addAttribute("errorMessage", 
                "Erreur technique lors de la création de l'utilisateur");
            return "user/add";
        }
    }

    /**
     * Affiche le formulaire de modification d'un utilisateur existant.
     * 
     * <p>Cette méthode récupère un utilisateur par son ID depuis le service
     * sous forme de DTO et le passe au formulaire de modification.
     * Elle inclut une validation de l'existence de l'entité et une gestion
     * d'erreurs robuste avec redirection vers la liste en cas d'erreur.</p>
     * 
     * @param id L'identifiant de l'utilisateur à modifier
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param redirectAttributes Attributs pour la redirection (messages d'erreur)
     * @return Le nom de la vue "user/update" ou redirection vers la liste en cas d'erreur
     */
    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, 
                                RedirectAttributes redirectAttributes) {
        try {
            UserDTO userDTO = userService.findByIdAsDTO(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur avec ID " + id + " non trouvé"));
            
            model.addAttribute("user", userDTO);
            return "user/update";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/user/list";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur technique lors du chargement de l'utilisateur");
            return "redirect:/user/list";
        }
    }

    /**
     * Traite la soumission du formulaire de modification d'un utilisateur.
     * 
     * <p>Cette méthode valide les données modifiées via Bean Validation,
     * effectue la mise à jour via le service métier qui gère le chiffrement
     * du mot de passe et la validation de l'unicité du nom d'utilisateur.
     * En cas de succès, elle redirige vers la liste avec un message de succès.
     * En cas d'erreur, elle retourne au formulaire avec un message d'erreur approprié.</p>
     * 
     * @param id L'identifiant de l'utilisateur à modifier
     * @param userDTO Les données modifiées de l'utilisateur, validées avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid UserDTO userDTO,
                             BindingResult result, Model model, 
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/update";
        }

        try {
            // Mise à jour via service métier avec DTO (validation unicité + chiffrement)
            UserDTO updatedUser = userService.updateFromDTO(id, userDTO);
            
            // Message de succès
            redirectAttributes.addFlashAttribute("successMessage", 
                "Utilisateur '" + updatedUser.getUsername() + "' mis à jour avec succès");
            
            return "redirect:/user/list";
            
        } catch (IllegalArgumentException e) {
            // Erreurs métier (utilisateur inexistant, username déjà utilisé)
            model.addAttribute("errorMessage", e.getMessage());
            return "user/update";
            
        } catch (Exception e) {
            // Erreurs techniques inattendues
            model.addAttribute("errorMessage", 
                "Erreur technique lors de la mise à jour de l'utilisateur");
            return "user/update";
        }
    }

    /**
     * Supprime un utilisateur par son identifiant.
     * 
     * <p>Cette méthode récupère d'abord l'utilisateur pour obtenir son nom
     * d'utilisateur (à des fins de message de confirmation), puis effectue
     * la suppression via le service métier. Elle redirige vers la liste
     * avec un message de succès ou d'erreur selon le résultat de l'opération.
     * La gestion d'erreurs inclut les cas de validation métier et les erreurs techniques.</p>
     * 
     * @param id L'identifiant de l'utilisateur à supprimer
     * @param redirectAttributes Attributs pour la redirection (messages de succès/erreur)
     * @return Redirection vers la liste avec un message de statut approprié
     */
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, 
                            RedirectAttributes redirectAttributes) {
        try {
            // Récupération pour message de confirmation
            UserDTO user = userService.findByIdAsDTO(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur avec ID " + id + " non trouvé"));
            
            String username = user.getUsername();
            
            // Suppression via service métier
            userService.deleteById(id);
            
            // Message de succès
            redirectAttributes.addFlashAttribute("successMessage", 
                "Utilisateur '" + username + "' supprimé avec succès");
            
        } catch (IllegalArgumentException e) {
            // Erreur utilisateur inexistant
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            
        } catch (Exception e) {
            // Erreur technique
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Erreur technique lors de la suppression de l'utilisateur");
        }
        
        return "redirect:/user/list";
    }
}
