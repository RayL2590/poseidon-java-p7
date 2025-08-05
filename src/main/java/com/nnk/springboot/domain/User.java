package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entité JPA représentant un utilisateur de l'application Poseidon Trading.
 * 
 * <p>Cette entité modélise les utilisateurs autorisés à accéder au système de trading.
 * Elle contient les informations d'identification et d'autorisation nécessaires pour
 * l'authentification et le contrôle d'accès via Spring Security.</p>
 * 
 * <p>Fonctionnalités de sécurité :</p>
 * <ul>
 *   <li><strong>Authentification</strong> : Nom d'utilisateur et mot de passe</li>
 *   <li><strong>Autorisation</strong> : Système de rôles pour le contrôle d'accès</li>
 *   <li><strong>Validation</strong> : Tous les champs sont obligatoires avec Bean Validation</li>
 *   <li><strong>Chiffrement</strong> : Les mots de passe sont chiffrés avec BCrypt</li>
 * </ul>
 * 
 * <p>Caractéristiques techniques :</p>
 * <ul>
 *   <li>Entité JPA mappée sur la table "users"</li>
 *   <li>Identifiant auto-généré avec stratégie AUTO</li>
 *   <li>Validation Bean Validation sur tous les champs requis</li>
 *   <li>Intégration avec Spring Security pour l'authentification</li>
 * </ul>
 * 
 * <p>Sécurité : Cette entité est au cœur du système de sécurité de l'application.
 * Les mots de passe ne doivent jamais être stockés en clair et sont automatiquement
 * chiffrés par le système lors de la création ou modification d'un utilisateur.</p>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "users")
public class User {
    /** 
     * Identifiant unique de l'utilisateur.
     * Clé primaire auto-générée avec stratégie AUTO.
     */
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    
    /** 
     * Nom d'utilisateur unique pour l'authentification.
     * 
     * <p>Identifiant de connexion utilisé avec le mot de passe pour l'authentification.
     * Doit être unique dans le système et ne peut pas être vide.</p>
     */
    @NotBlank(message = "Username is mandatory")
    private String username;
    
    /** 
     * Mot de passe chiffré de l'utilisateur.
     * 
     * <p>Stocké sous forme chiffrée avec BCrypt pour la sécurité.
     * Ne doit jamais être stocké en clair dans la base de données.
     * Automatiquement chiffré par le contrôleur lors de la création/modification.</p>
     */
    @NotBlank(message = "Password is mandatory")
    private String password;
    
    /** 
     * Nom complet de l'utilisateur.
     * 
     * <p>Nom d'affichage complet de l'utilisateur (prénom et nom).
     * Utilisé pour l'identification dans l'interface utilisateur et les logs d'audit.</p>
     */
    @NotBlank(message = "FullName is mandatory")
    private String fullname;
    
    /** 
     * Rôle de l'utilisateur dans le système.
     * 
     * <p>Définit les permissions et autorisations de l'utilisateur.
     * Utilisé par Spring Security pour le contrôle d'accès.
     * Exemples de rôles : ADMIN, USER, TRADER, etc.</p>
     */
    @NotBlank(message = "Role is mandatory")
    private String role;

    /**
     * Récupère l'identifiant unique de l'utilisateur.
     * 
     * @return L'identifiant unique de l'utilisateur
     */
    public Integer getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de l'utilisateur.
     * 
     * @param id L'identifiant unique à assigner à l'utilisateur
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Récupère le nom d'utilisateur.
     * 
     * @return Le nom d'utilisateur utilisé pour l'authentification
     */
    public String getUsername() {
        return username;
    }

    /**
     * Définit le nom d'utilisateur.
     * 
     * <p>Le nom d'utilisateur doit être unique dans le système
     * et ne peut pas être vide ou null.</p>
     * 
     * @param username Le nom d'utilisateur à assigner
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Récupère le mot de passe chiffré de l'utilisateur.
     * 
     * <p>Attention : Retourne le mot de passe sous sa forme chiffrée.
     * Ne jamais exposer cette valeur dans les logs ou interfaces utilisateur.</p>
     * 
     * @return Le mot de passe chiffré avec BCrypt
     */
    public String getPassword() {
        return password;
    }

    /**
     * Définit le mot de passe de l'utilisateur.
     * 
     * <p>Important : Le mot de passe doit être chiffré avec BCrypt avant d'être
     * stocké via cette méthode. Le contrôleur se charge automatiquement du
     * chiffrement lors de la création ou modification d'un utilisateur.</p>
     * 
     * <p>Ne jamais stocker un mot de passe en clair avec cette méthode.</p>
     * 
     * @param password Le mot de passe chiffré à assigner
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Récupère le nom complet de l'utilisateur.
     * 
     * @return Le nom complet (prénom et nom) de l'utilisateur
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Définit le nom complet de l'utilisateur.
     * 
     * <p>Utilisé pour l'affichage dans l'interface utilisateur
     * et l'identification dans les logs d'audit.</p>
     * 
     * @param fullname Le nom complet à assigner à l'utilisateur
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * Récupère le rôle de l'utilisateur.
     * 
     * @return Le rôle défini pour cet utilisateur
     */
    public String getRole() {
        return role;
    }

    /**
     * Définit le rôle de l'utilisateur.
     * 
     * <p>Le rôle détermine les permissions et autorisations de l'utilisateur
     * dans le système. Il est utilisé par Spring Security pour le contrôle d'accès
     * aux différentes ressources de l'application.</p>
     * 
     * <p>Exemples de rôles typiques : "ADMIN", "USER", "TRADER", "MANAGER"</p>
     * 
     * @param role Le rôle à assigner à l'utilisateur
     */
    public void setRole(String role) {
        this.role = role;
    }
}
