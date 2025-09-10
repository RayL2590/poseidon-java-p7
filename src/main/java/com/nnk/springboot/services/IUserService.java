package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for User management operations with DTO support.
 */
public interface IUserService {

    /**
     * Récupère tous les utilisateurs de l'application.
     * 
     * @return Liste de tous les utilisateurs enregistrés
     */
    List<User> findAll();

    /**
     * Recherche un utilisateur par son identifiant unique.
     * 
     * @param id L'identifiant unique de l'utilisateur
     * @return Optional contenant l'utilisateur s'il existe, vide sinon
     */
    Optional<User> findById(Integer id);

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     * 
     * @param username Le nom d'utilisateur unique
     * @return Optional contenant l'utilisateur s'il existe, vide sinon
     */
    Optional<User> findByUsername(String username);

    /**
     * Crée un nouvel utilisateur avec chiffrement automatique du mot de passe.
     * 
     * <p>Cette méthode applique les validations métier, chiffre automatiquement
     * le mot de passe avec BCrypt et sauvegarde l'utilisateur en base.</p>
     * 
     * @param user L'utilisateur à créer (mot de passe en clair)
     * @return L'utilisateur créé avec l'ID généré et mot de passe chiffré
     * @throws IllegalArgumentException Si l'utilisateur existe déjà ou données invalides
     */
    User save(User user);

    /**
     * Met à jour un utilisateur existant avec gestion sécurisée du mot de passe.
     * 
     * <p>Cette méthode met à jour l'utilisateur existant, re-chiffre le mot de passe
     * si celui-ci a été modifié, et applique les validations métier.</p>
     * 
     * @param id L'identifiant de l'utilisateur à mettre à jour
     * @param user Les nouvelles données de l'utilisateur
     * @return L'utilisateur mis à jour
     * @throws IllegalArgumentException Si l'utilisateur n'existe pas
     */
    User update(Integer id, User user);

    /**
     * Supprime un utilisateur par son identifiant.
     * 
     * @param id L'identifiant de l'utilisateur à supprimer
     * @throws IllegalArgumentException Si l'utilisateur n'existe pas
     */
    void deleteById(Integer id);

    /**
     * Valide qu'un nom d'utilisateur est unique dans le système.
     * 
     * @param username Le nom d'utilisateur à vérifier
     * @return true si le nom d'utilisateur est disponible, false sinon
     */
    boolean isUsernameAvailable(String username);

    /**
     * Valide qu'un nom d'utilisateur est unique pour un utilisateur donné.
     * 
     * @param username Le nom d'utilisateur à vérifier
     * @param userId L'ID de l'utilisateur à exclure de la vérification
     * @return true si le nom d'utilisateur est disponible, false sinon
     */
    boolean isUsernameAvailable(String username, Integer userId);

    // DTO Methods - New methods for SOLID compliance
    
    /**
     * Finds all users and returns them as DTOs.
     */
    List<UserDTO> findAllAsDTO();

    /**
     * Finds a user by ID and returns as DTO.
     */
    Optional<UserDTO> findByIdAsDTO(Integer id);

    /**
     * Creates a new user from DTO with validation and password encryption.
     */
    UserDTO saveFromDTO(UserDTO userDTO);

    /**
     * Updates an existing user from DTO.
     */
    UserDTO updateFromDTO(Integer id, UserDTO userDTO);
}