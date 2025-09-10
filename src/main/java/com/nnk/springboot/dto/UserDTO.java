package com.nnk.springboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object pour les utilisateurs de l'application Poseidon Trading.
 * 
 * <p>Cette classe encapsule les données utilisateur pour les échanges entre
 * les couches de l'application, avec des validations renforcées conformes aux
 * exigences de sécurité pour les applications financières.</p>
 * 
 * <p>Validations de sécurité implémentées :</p>
 * <ul>
 *   <li><strong>Nom d'utilisateur</strong> : Obligatoire, 3-50 caractères, format alphanumérique</li>
 *   <li><strong>Mot de passe</strong> : Complexité renforcée selon standards bancaires</li>
 *   <li><strong>Nom complet</strong> : Obligatoire, format nom professionnel</li>
 *   <li><strong>Rôle</strong> : Limité aux rôles autorisés (USER, ADMIN)</li>
 * </ul>
 * 
 * <p>Exigences mot de passe (conformité projet) :</p>
 * <ul>
 *   <li><strong>Longueur minimale</strong> : Au moins 8 caractères</li>
 *   <li><strong>Majuscule</strong> : Au moins une lettre majuscule</li>
 *   <li><strong>Chiffre</strong> : Au moins un chiffre</li>
 *   <li><strong>Symbole</strong> : Au moins un symbole spécial</li>
 * </ul>
 * 
 * <p>Pattern de sécurité utilisé :</p>
 * <pre>
 * ^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$
 * 
 * Explication :
 * - ^                : Début de chaîne
 * - (?=.*[a-z])      : Au moins une minuscule
 * - (?=.*[A-Z])      : Au moins une majuscule
 * - (?=.*\\d)        : Au moins un chiffre
 * - (?=.*[@$!%*?&])  : Au moins un symbole spécial
 * - [A-Za-z\\d@$!%*?&]{8,} : 8 caractères minimum avec caractères autorisés
 * - $                : Fin de chaîne
 * </pre>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
public class UserDTO {

    /** Identifiant unique de l'utilisateur (optionnel pour création) */
    private Integer id;

    /**
     * Nom d'utilisateur unique pour l'authentification.
     * 
     * <p>Contraintes de validation :</p>
     * <ul>
     *   <li><strong>Obligatoire</strong> : Ne peut être vide ou null</li>
     *   <li><strong>Longueur</strong> : Entre 3 et 50 caractères</li>
     *   <li><strong>Format</strong> : Lettres, chiffres, tirets et underscores uniquement</li>
     * </ul>
     */
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", 
             message = "Le nom d'utilisateur ne peut contenir que des lettres, chiffres, tirets et underscores")
    private String username;

    /**
     * Mot de passe avec validation de complexité renforcée.
     * 
     * <p>Respecte les exigences de sécurité du projet :</p>
     * <ul>
     *   <li><strong>8 caractères minimum</strong> : Longueur suffisante</li>
     *   <li><strong>Au moins 1 majuscule</strong> : Complexité alphabétique</li>
     *   <li><strong>Au moins 1 chiffre</strong> : Composant numérique</li>
     *   <li><strong>Au moins 1 symbole</strong> : Caractère spécial (@$!%*?&)</li>
     * </ul>
     * 
     * <p>Cette validation s'applique uniquement côté présentation.
     * Le chiffrement BCrypt est géré par le service métier.</p>
     */
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, un chiffre et un symbole (@$!%*?&)")
    private String password;

    /**
     * Nom complet de l'utilisateur pour affichage professionnel.
     * 
     * <p>Contraintes de validation :</p>
     * <ul>
     *   <li><strong>Obligatoire</strong> : Nom requis pour identification</li>
     *   <li><strong>Longueur</strong> : Entre 2 et 100 caractères</li>
     *   <li><strong>Format</strong> : Lettres, espaces, tirets et apostrophes</li>
     * </ul>
     */
    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom complet doit contenir entre 2 et 100 caractères")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", 
             message = "Le nom complet ne peut contenir que des lettres, espaces, tirets et apostrophes")
    private String fullname;

    /**
     * Rôle de l'utilisateur dans l'application de trading.
     * 
     * <p>Rôles autorisés :</p>
     * <ul>
     *   <li><strong>USER</strong> : Utilisateur standard avec accès aux fonctionnalités de base</li>
     *   <li><strong>ADMIN</strong> : Administrateur avec accès complet et gestion utilisateurs</li>
     * </ul>
     */
    @NotBlank(message = "Le rôle est obligatoire")
    @Pattern(regexp = "^(USER|ADMIN)$", 
             message = "Le rôle doit être 'USER' ou 'ADMIN'")
    private String role;

    // Constructeurs

    /**
     * Constructeur par défaut requis pour la désérialisation.
     */
    public UserDTO() {
    }

    /**
     * Constructeur complet pour création d'instance avec toutes les données.
     * 
     * @param id Identifiant de l'utilisateur
     * @param username Nom d'utilisateur unique
     * @param password Mot de passe (sera validé et chiffré)
     * @param fullname Nom complet de l'utilisateur
     * @param role Rôle de l'utilisateur (USER ou ADMIN)
     */
    public UserDTO(Integer id, String username, String password, String fullname, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
    }

    /**
     * Constructeur sans ID pour création de nouvel utilisateur.
     * 
     * @param username Nom d'utilisateur unique
     * @param password Mot de passe (sera validé et chiffré)
     * @param fullname Nom complet de l'utilisateur
     * @param role Rôle de l'utilisateur (USER ou ADMIN)
     */
    public UserDTO(String username, String password, String fullname, String role) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
    }

    // Getters et Setters

    /**
     * @return L'identifiant unique de l'utilisateur
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id L'identifiant unique de l'utilisateur
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return Le nom d'utilisateur unique
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username Le nom d'utilisateur unique
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return Le mot de passe (validé selon exigences de sécurité)
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password Le mot de passe (sera validé et chiffré)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Le nom complet de l'utilisateur
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * @param fullname Le nom complet de l'utilisateur
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * @return Le rôle de l'utilisateur (USER ou ADMIN)
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role Le rôle de l'utilisateur (USER ou ADMIN)
     */
    public void setRole(String role) {
        this.role = role;
    }

    // Méthodes utilitaires

    /**
     * Indique si cet utilisateur a un rôle d'administrateur.
     * 
     * @return true si l'utilisateur est ADMIN, false sinon
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /**
     * Indique si cet utilisateur a un rôle d'utilisateur standard.
     * 
     * @return true si l'utilisateur est USER, false sinon
     */
    public boolean isUser() {
        return "USER".equals(role);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", role='" + role + '\'' +
                // Mot de passe intentionnellement omis pour la sécurité
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserDTO userDTO = (UserDTO) o;
        
        if (id != null ? !id.equals(userDTO.id) : userDTO.id != null) return false;
        return username != null ? username.equals(userDTO.username) : userDTO.username == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}