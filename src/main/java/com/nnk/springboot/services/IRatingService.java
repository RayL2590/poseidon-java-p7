package com.nnk.springboot.services;

import com.nnk.springboot.domain.Rating;
import java.util.List;
import java.util.Optional;

/**
 * Interface pour le service de gestion des notations de crédit (Rating).
 * 
 * <p>Cette interface définit le contrat pour les opérations de gestion des notations
 * de crédit dans l'application de trading Poseidon. Elle respecte le principe ISP
 * (Interface Segregation Principle) en fournissant une interface spécialisée
 * pour les opérations Rating uniquement.</p>
 * 
 * <p>Responsabilités du service :</p>
 * <ul>
 *   <li><strong>Opérations CRUD</strong> : Create, Read, Update, Delete des notations</li>
 *   <li><strong>Validation métier</strong> : Cohérence des notations inter-agences</li>
 *   <li><strong>Logique de classification</strong> : Gestion des numéros d'ordre</li>
 *   <li><strong>Règles de gestion</strong> : Application des contraintes financières</li>
 * </ul>
 * 
 * <p>Contraintes métier à implémenter :</p>
 * <ul>
 *   <li><strong>Cohérence des notations</strong> : Équivalence entre agences</li>
 *   <li><strong>Unicité des ordres</strong> : Pas de doublons dans la classification</li>
 *   <li><strong>Validation des échelles</strong> : Respect des standards officiels</li>
 *   <li><strong>Intégrité référentielle</strong> : Gestion des dépendances</li>
 * </ul>
 * 
 * <p>Considérations de performance :</p>
 * <ul>
 *   <li><strong>Cache des notations</strong> : Optimisation pour consultations fréquentes</li>
 *   <li><strong>Requêtes optimisées</strong> : Minimisation des accès base de données</li>
 *   <li><strong>Index sur ordre</strong> : Performance des tris et recherches</li>
 *   <li><strong>Lazy loading</strong> : Chargement sélectif selon besoins</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.Rating
 * @see com.nnk.springboot.services.RatingService
 */
public interface IRatingService {
    
    /**
     * Récupère toutes les notations de crédit.
     * 
     * <p>Cette méthode retourne l'ensemble des notations stockées dans le système,
     * ordonnées par défaut selon le numéro d'ordre (meilleure notation en premier).
     * Utilisée principalement pour l'affichage des listes complètes et les analyses
     * de portefeuille globales.</p>
     * 
     * <p>Cas d'usage :</p>
     * <ul>
     *   <li><strong>Affichage liste complète</strong> : Interface de gestion des notations</li>
     *   <li><strong>Sélection dans formulaires</strong> : Dropdowns et listes de choix</li>
     *   <li><strong>Analyses statistiques</strong> : Distribution des notations</li>
     *   <li><strong>Export de données</strong> : Rapports et fichiers de référence</li>
     * </ul>
     * 
     * @return Liste de toutes les notations, ordonnées par numéro d'ordre croissant
     */
    List<Rating> findAll();
    
    /**
     * Récupère une notation de crédit par son identifiant.
     * 
     * <p>Cette méthode permet de retrouver une notation spécifique par son ID unique.
     * Retourne un Optional pour gérer élégamment les cas où la notation n'existe pas,
     * évitant les exceptions et facilitant la gestion d'erreurs dans les contrôleurs.</p>
     * 
     * <p>Validation d'entrée :</p>
     * <ul>
     *   <li><strong>ID null ou négatif</strong> : Retourne Optional.empty()</li>
     *   <li><strong>ID valide inexistant</strong> : Retourne Optional.empty()</li>
     *   <li><strong>ID valide existant</strong> : Retourne Optional.of(rating)</li>
     * </ul>
     * 
     * @param id L'identifiant de la notation à rechercher
     * @return Optional contenant la notation si trouvée, vide sinon
     */
    Optional<Rating> findById(Integer id);
    
    /**
     * Vérifie l'existence d'une notation par son identifiant.
     * 
     * <p>Méthode d'optimisation qui vérifie uniquement l'existence sans charger
     * l'entité complète. Particulièrement utile pour les validations préalables
     * aux opérations de mise à jour ou suppression, évitant le chargement inutile
     * de données lorsque seule l'existence importe.</p>
     * 
     * <p>Avantages performance :</p>
     * <ul>
     *   <li><strong>Requête COUNT optimisée</strong> : Plus rapide que SELECT complet</li>
     *   <li><strong>Moins de mémoire</strong> : Pas de chargement d'entité</li>
     *   <li><strong>Cache efficient</strong> : Utilise les index de base de données</li>
     * </ul>
     * 
     * @param id L'identifiant à vérifier
     * @return true si la notation existe, false sinon
     */
    boolean existsById(Integer id);
    
    /**
     * Sauvegarde une notation (création ou mise à jour).
     * 
     * <p>Cette méthode effectue la persistance d'une notation en appliquant
     * toutes les validations métier nécessaires. Elle gère automatiquement
     * la différence entre création (ID null) et mise à jour (ID existant),
     * appliquant les règles de gestion appropriées dans chaque cas.</p>
     * 
     * <p>Validations métier appliquées :</p>
     * <ul>
     *   <li><strong>Cohérence inter-agences</strong> : Vérification de l'équivalence des notations</li>
     *   <li><strong>Format des notations</strong> : Respect des échelles officielles</li>
     *   <li><strong>Unicité numéro d'ordre</strong> : Pas de doublons dans la classification</li>
     *   <li><strong>Contraintes métier</strong> : Règles spécifiques à l'environnement</li>
     * </ul>
     * 
     * <p>Comportement création vs mise à jour :</p>
     * <ul>
     *   <li><strong>Création (ID null)</strong> : Attribution automatique d'un numéro d'ordre si absent</li>
     *   <li><strong>Mise à jour (ID existant)</strong> : Vérification de l'existence préalable</li>
     *   <li><strong>Audit automatique</strong> : Horodatage des modifications</li>
     *   <li><strong>Validation préalable</strong> : Contrôles avant persistance</li>
     * </ul>
     * 
     * @param rating La notation à sauvegarder (ne doit pas être null)
     * @return La notation sauvegardée avec son ID assigné
     * @throws IllegalArgumentException si les données sont invalides ou incohérentes
     */
    Rating save(Rating rating);
    
    /**
     * Supprime une notation par son identifiant.
     * 
     * <p>Cette méthode effectue la suppression sécurisée d'une notation après
     * vérification de son existence et des contraintes d'intégrité référentielle.
     * Elle s'assure qu'aucune donnée liée ne sera orpheline suite à la suppression.</p>
     * 
     * <p>Vérifications préalables :</p>
     * <ul>
     *   <li><strong>Existence de l'entité</strong> : Vérification avant suppression</li>
     *   <li><strong>Contraintes référentielles</strong> : Pas de dépendances actives</li>
     *   <li><strong>Règles métier</strong> : Autorisation de suppression</li>
     *   <li><strong>Audit trail</strong> : Traçabilité de la suppression</li>
     * </ul>
     * 
     * <p>Gestion des cas d'erreur :</p>
     * <ul>
     *   <li><strong>ID invalide</strong> : IllegalArgumentException</li>
     *   <li><strong>Notation inexistante</strong> : IllegalArgumentException</li>
     *   <li><strong>Contraintes violées</strong> : DataIntegrityViolationException</li>
     *   <li><strong>Erreur technique</strong> : DataAccessException</li>
     * </ul>
     * 
     * @param id L'identifiant de la notation à supprimer
     * @throws IllegalArgumentException si l'ID est invalide ou la notation n'existe pas
     */
    void deleteById(Integer id);
    
    /**
     * Recherche les notations par agence de notation.
     * 
     * <p>Méthode spécialisée pour filtrer les notations selon l'agence d'origine.
     * Permet d'analyser les distributions de notations par agence et de détecter
     * les divergences d'opinion entre les différentes agences de notation.</p>
     * 
     * <p>Agences supportées :</p>
     * <ul>
     *   <li><strong>"MOODYS"</strong> : Notations Moody's uniquement</li>
     *   <li><strong>"SP"</strong> : Notations Standard & Poor's uniquement</li>
     *   <li><strong>"FITCH"</strong> : Notations Fitch uniquement</li>
     * </ul>
     * 
     * @param agency Le nom de l'agence ("MOODYS", "SP", "FITCH")
     * @return Liste des notations ayant une notation de l'agence spécifiée
     */
    List<Rating> findByAgency(String agency);
    
    /**
     * Recherche les notations dans une plage d'ordre donnée.
     * 
     * <p>Méthode utilitaire pour filtrer les notations selon leur qualité de crédit,
     * en utilisant la plage de numéros d'ordre. Particulièrement utile pour
     * segmenter les portefeuilles entre Investment Grade et Speculative Grade.</p>
     * 
     * <p>Plages typiques :</p>
     * <ul>
     *   <li><strong>1-12</strong> : Investment Grade (BBB-/Baa3 et au-dessus)</li>
     *   <li><strong>13-21</strong> : Speculative Grade (BB+/Ba1 et en-dessous)</li>
     *   <li><strong>1-3</strong> : Prime Grade (AAA/Aaa à AA-/Aa3)</li>
     * </ul>
     * 
     * @param minOrder Numéro d'ordre minimum (inclus)
     * @param maxOrder Numéro d'ordre maximum (inclus)
     * @return Liste des notations dans la plage spécifiée
     */
    List<Rating> findByOrderRange(Integer minOrder, Integer maxOrder);
    
    /**
     * Recherche les notations Investment Grade.
     * 
     * <p>Méthode de convenance qui retourne toutes les notations considérées
     * comme Investment Grade selon les standards internationaux. Correspond
     * généralement aux notations BBB-/Baa3 et au-dessus.</p>
     * 
     * @return Liste des notations Investment Grade
     */
    List<Rating> findInvestmentGrade();
    
    /**
     * Recherche les notations Speculative Grade.
     * 
     * <p>Méthode de convenance qui retourne toutes les notations considérées
     * comme Speculative Grade (aussi appelées "High Yield" ou "Junk").
     * Correspond généralement aux notations BB+/Ba1 et en-dessous.</p>
     * 
     * @return Liste des notations Speculative Grade
     */
    List<Rating> findSpeculativeGrade();
}