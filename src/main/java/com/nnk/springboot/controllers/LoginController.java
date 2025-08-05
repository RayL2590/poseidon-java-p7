package com.nnk.springboot.controllers;

import com.nnk.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Contrôleur Spring MVC pour la gestion de l'authentification et des accès sécurisés.
 * 
 * <p>Ce contrôleur gère les aspects liés à l'authentification des utilisateurs,
 * l'affichage des pages de connexion, l'accès aux ressources sécurisées et
 * la gestion des erreurs d'autorisation dans l'application Poseidon Trading.</p>
 * 
 * <p>Il fournit les points d'entrée pour :</p>
 * <ul>
 *   <li>La page de connexion utilisateur</li>
 *   <li>L'accès aux détails des utilisateurs pour les utilisateurs autorisés</li>
 *   <li>La gestion des erreurs d'autorisation (page 403)</li>
 * </ul>
 * 
 * <p>Ce contrôleur travaille en collaboration avec Spring Security pour
 * assurer la sécurité de l'application.</p>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Controller
public class LoginController {

    /** Repository des utilisateurs injecté par Spring pour l'accès aux données utilisateur */
    @Autowired
    private UserRepository userRepository;

    /**
     * Affiche la page de connexion de l'application.
     * 
     * <p>Cette méthode gère l'affichage du formulaire de connexion pour
     * l'authentification des utilisateurs. Elle est accessible publiquement
     * et constitue le point d'entrée principal pour l'authentification.</p>
     * 
     * <p>La gestion de l'authentification proprement dite est déléguée
     * à Spring Security qui intercepte les soumissions du formulaire.</p>
     * 
     * @return ModelAndView contenant la vue "login" pour l'affichage du formulaire de connexion
     */
    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        return mav;
    }

    /**
     * Affiche la liste des utilisateurs pour les utilisateurs autorisés.
     * 
     * <p>Cette méthode est accessible uniquement aux utilisateurs authentifiés
     * ayant les droits d'accès appropriés (path sécurisé "/secure/article-details").
     * Elle récupère tous les utilisateurs depuis le repository et les affiche
     * dans la vue de liste des utilisateurs.</p>
     * 
     * <p>L'accès à cette ressource est contrôlé par Spring Security et nécessite
     * une authentification préalable. En cas d'accès non autorisé, l'utilisateur
     * sera redirigé vers la page d'erreur 403.</p>
     * 
     * @return ModelAndView contenant la liste de tous les utilisateurs et la vue "user/list"
     */
    @GetMapping("/secure/article-details")
    public ModelAndView getAllUserArticles() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("users", userRepository.findAll());
        mav.setViewName("user/list");
        return mav;
    }

    /**
     * Affiche la page d'erreur d'autorisation (HTTP 403 - Forbidden).
     * 
     * <p>Cette méthode gère l'affichage de la page d'erreur lorsqu'un utilisateur
     * tente d'accéder à une ressource pour laquelle il n'a pas les autorisations
     * nécessaires. Elle fournit un message explicite à l'utilisateur concernant
     * l'interdiction d'accès.</p>
     * 
     * <p>Cette page est généralement appelée par Spring Security lorsqu'un
     * utilisateur authentifié n'a pas les rôles ou permissions requis pour
     * accéder à une ressource particulière.</p>
     * 
     * @return ModelAndView contenant le message d'erreur et la vue "403" pour l'affichage de l'erreur d'autorisation
     */
    @GetMapping("/app/error")
    public ModelAndView error() {
        ModelAndView mav = new ModelAndView();
        String errorMessage= "You are not authorized for the requested data.";
        mav.addObject("errorMsg", errorMessage);
        mav.setViewName("403");
        return mav;
    }
}
