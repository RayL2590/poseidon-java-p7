package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

/**
 * Contrôleur Spring MVC pour la gestion des utilisateurs de l'application.
 * 
 * <p>Ce contrôleur gère l'ensemble des opérations CRUD (Create, Read, Update, Delete)
 * pour les entités User via une interface web. Il inclut la gestion sécurisée des
 * mots de passe avec chiffrement BCrypt et la validation des données utilisateur.</p>
 * 
 * <p>Fonctionnalités principales :</p>
 * <ul>
 *   <li>Affichage de la liste des utilisateurs</li>
 *   <li>Création de nouveaux utilisateurs avec chiffrement du mot de passe</li>
 *   <li>Modification des utilisateurs existants</li>
 *   <li>Suppression d'utilisateurs</li>
 *   <li>Validation des données avec Bean Validation</li>
 * </ul>
 * 
 * <p>Sécurité : Tous les mots de passe sont automatiquement chiffrés avec BCrypt
 * avant la sauvegarde en base de données. Lors de la modification, le mot de passe
 * existant est masqué pour des raisons de sécurité.</p>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Controller
public class UserController {
    /** Repository des utilisateurs injecté par Spring pour l'accès aux données */
    @Autowired
    private UserRepository userRepository;

    /**
     * Affiche la liste de tous les utilisateurs de l'application.
     * 
     * <p>Cette méthode récupère tous les utilisateurs depuis le repository
     * et les passe à la vue pour affichage. Elle constitue la page principale
     * de gestion des utilisateurs.</p>
     * 
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue Thymeleaf "user/list" contenant la liste des utilisateurs
     */
    @RequestMapping("/user/list")
    public String home(Model model)
    {
        model.addAttribute("users", userRepository.findAll());
        return "user/list";
    }

    /**
     * Affiche le formulaire de création d'un nouvel utilisateur.
     * 
     * <p>Cette méthode prépare l'affichage du formulaire de création d'utilisateur.
     * Un objet User vide est automatiquement lié au formulaire pour la saisie
     * des données du nouvel utilisateur.</p>
     * 
     * @param bid L'objet User vide qui sera lié au formulaire (binding automatique Spring)
     * @return Le nom de la vue Thymeleaf "user/add" contenant le formulaire de création
     */
    @GetMapping("/user/add")
    public String addUser(User bid) {
        return "user/add";
    }

    /**
     * Traite la soumission du formulaire de création d'un utilisateur.
     * 
     * <p>Cette méthode valide les données soumises via Bean Validation, chiffre
     * le mot de passe avec BCrypt pour la sécurité, sauvegarde l'utilisateur
     * en base de données et redirige vers la liste des utilisateurs en cas de succès.
     * En cas d'erreur de validation, elle retourne au formulaire de création.</p>
     * 
     * <p>Sécurité : Le mot de passe est automatiquement chiffré avec BCrypt
     * avant la sauvegarde pour garantir la sécurité des données.</p>
     * 
     * @param user L'objet User contenant les données saisies, validé avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Redirection vers la liste des utilisateurs en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/user/validate")
    public String validate(@Valid User user, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
            model.addAttribute("users", userRepository.findAll());
            return "redirect:/user/list";
        }
        return "user/add";
    }

    /**
     * Affiche le formulaire de modification d'un utilisateur existant.
     * 
     * <p>Cette méthode récupère un utilisateur par son ID, masque son mot de passe
     * pour des raisons de sécurité (en le vidant), et passe l'utilisateur au
     * formulaire de modification. Une exception est levée si l'utilisateur
     * n'existe pas.</p>
     * 
     * <p>Sécurité : Le mot de passe existant est automatiquement masqué (défini à "")
     * pour éviter d'exposer le hash du mot de passe dans le formulaire.</p>
     * 
     * @param id L'identifiant de l'utilisateur à modifier
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Le nom de la vue "user/update" contenant le formulaire de modification
     * @throws IllegalArgumentException si l'utilisateur avec l'ID spécifié n'existe pas
     */
    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setPassword("");
        model.addAttribute("user", user);
        return "user/update";
    }

    /**
     * Traite la soumission du formulaire de modification d'un utilisateur.
     * 
     * <p>Cette méthode valide les données modifiées via Bean Validation, chiffre
     * le nouveau mot de passe avec BCrypt, met à jour l'utilisateur en base de
     * données et redirige vers la liste des utilisateurs en cas de succès.
     * En cas d'erreur de validation, elle retourne au formulaire de modification.</p>
     * 
     * <p>Sécurité : Le mot de passe est systématiquement re-chiffré avec BCrypt,
     * même s'il s'agit du même mot de passe (recommandation de sécurité).</p>
     * 
     * @param id L'identifiant de l'utilisateur à modifier
     * @param user L'objet User contenant les données modifiées, validé avec Bean Validation
     * @param result Le résultat de la validation Bean Validation
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Redirection vers la liste des utilisateurs en cas de succès, ou retour au formulaire en cas d'erreur
     */
    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid User user,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/update";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setId(id);
        userRepository.save(user);
        model.addAttribute("users", userRepository.findAll());
        return "redirect:/user/list";
    }

    /**
     * Supprime un utilisateur par son identifiant.
     * 
     * <p>Cette méthode récupère l'utilisateur par son ID, le supprime de la base
     * de données et redirige vers la liste des utilisateurs. Une exception est
     * levée si l'utilisateur n'existe pas.</p>
     * 
     * <p>Attention : Cette opération est irréversible. L'utilisateur et toutes
     * ses données associées seront définitivement supprimés de la base de données.</p>
     * 
     * @param id L'identifiant de l'utilisateur à supprimer
     * @param model Le modèle Spring MVC pour passer les données à la vue
     * @return Redirection vers la liste des utilisateurs après suppression
     * @throws IllegalArgumentException si l'utilisateur avec l'ID spécifié n'existe pas
     */
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        model.addAttribute("users", userRepository.findAll());
        return "redirect:/user/list";
    }
}
