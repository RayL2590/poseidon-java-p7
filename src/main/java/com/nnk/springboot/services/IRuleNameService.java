package com.nnk.springboot.services;

import com.nnk.springboot.domain.RuleName;
import java.util.List;
import java.util.Optional;

/**
 * Interface pour le service de gestion des règles métier (RuleName).
 * 
 * <p>Cette interface définit le contrat pour les opérations de gestion des règles
 * métier dans l'application de trading Poseidon. Elle respecte le principe ISP
 * (Interface Segregation Principle) en fournissant une interface spécialisée
 * pour les opérations RuleName uniquement.</p>
 * 
 * <p>Responsabilités du service :</p>
 * <ul>
 *   <li><strong>Opérations CRUD</strong> : Create, Read, Update, Delete des règles</li>
 *   <li><strong>Validation métier</strong> : Cohérence des configurations et templates</li>
 *   <li><strong>Logique de nommage</strong> : Unicité et format des noms de règles</li>
 *   <li><strong>Gestion des templates</strong> : Validation et paramétrage des modèles</li>
 * </ul>
 * 
 * <p>Contraintes métier à implémenter :</p>
 * <ul>
 *   <li><strong>Unicité des noms</strong> : Pas de doublons dans les identifiants</li>
 *   <li><strong>Validation JSON</strong> : Format correct des configurations</li>
 *   <li><strong>Templates cohérents</strong> : Validation des placeholders et syntaxe</li>
 *   <li><strong>SQL sécurisé</strong> : Validation des requêtes SQL intégrées</li>
 * </ul>
 * 
 * <p>Considérations de performance :</p>
 * <ul>
 *   <li><strong>Cache des règles</strong> : Optimisation pour consultations fréquentes</li>
 *   <li><strong>Index sur noms</strong> : Performance des recherches par nom</li>
 *   <li><strong>Lazy loading</strong> : Chargement sélectif des templates volumineux</li>
 *   <li><strong>Requêtes optimisées</strong> : Minimisation des accès base de données</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.RuleName
 * @see com.nnk.springboot.services.RuleNameService
 */
public interface IRuleNameService {
    
    /**
     * Récupère toutes les règles métier.
     * 
     * <p>Cette méthode retourne l'ensemble des règles stockées dans le système,
     * ordonnées par défaut selon le nom (ordre alphabétique). Utilisée principalement 
     * pour l'affichage des listes complètes et la gestion administrative des règles.</p>
     * 
     * <p>Cas d'usage :</p>
     * <ul>
     *   <li><strong>Interface d'administration</strong> : Liste complète des règles</li>
     *   <li><strong>Sélection dans formulaires</strong> : Dropdowns et listes de choix</li>
     *   <li><strong>Audit et conformité</strong> : Revue des règles actives</li>
     *   <li><strong>Export de configuration</strong> : Sauvegarde des règles métier</li>
     * </ul>
     * 
     * @return Liste de toutes les règles, ordonnées par nom croissant
     */
    List<RuleName> findAll();
    
    /**
     * Récupère une règle métier par son identifiant.
     * 
     * <p>Cette méthode permet de retrouver une règle spécifique par son ID unique.
     * Retourne un Optional pour gérer élégamment les cas où la règle n'existe pas,
     * évitant les exceptions et facilitant la gestion d'erreurs dans les contrôleurs.</p>
     * 
     * <p>Validation d'entrée :</p>
     * <ul>
     *   <li><strong>ID null ou négatif</strong> : Retourne Optional.empty()</li>
     *   <li><strong>ID valide inexistant</strong> : Retourne Optional.empty()</li>
     *   <li><strong>ID valide existant</strong> : Retourne Optional.of(ruleName)</li>
     * </ul>
     * 
     * @param id L'identifiant de la règle à rechercher
     * @return Optional contenant la règle si trouvée, vide sinon
     */
    Optional<RuleName> findById(Integer id);
    
    /**
     * Recherche une règle métier par son nom.
     * 
     * <p>Méthode principale pour identifier de manière unique une règle par son nom
     * métier. Essentielle pour l'exécution des règles et les références croisées
     * dans l'application. Le nom étant l'identifiant métier principal.</p>
     * 
     * <p>Cas d'usage critiques :</p>
     * <ul>
     *   <li><strong>Exécution de règles</strong> : Recherche par nom pour application</li>
     *   <li><strong>Validation croisée</strong> : Référencement entre règles</li>
     *   <li><strong>Configuration dynamique</strong> : Chargement à la demande</li>
     *   <li><strong>API externe</strong> : Interface stable pour intégrations</li>
     * </ul>
     * 
     * @param name Le nom de la règle à rechercher
     * @return Optional contenant la règle si trouvée, vide sinon
     */
    Optional<RuleName> findByName(String name);
    
    /**
     * Vérifie l'existence d'une règle par son identifiant.
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
     * @return true si la règle existe, false sinon
     */
    boolean existsById(Integer id);
    
    /**
     * Vérifie l'existence d'une règle par son nom.
     * 
     * <p>Méthode essentielle pour garantir l'unicité des noms de règles lors de
     * la création ou modification. Permet de détecter les doublons potentiels
     * avant la persistance et d'assurer l'intégrité des identifiants métier.</p>
     * 
     * @param name Le nom de la règle à vérifier
     * @return true si une règle avec ce nom existe déjà, false sinon
     */
    boolean existsByName(String name);
    
    /**
     * Sauvegarde une règle métier (création ou mise à jour).
     * 
     * <p>Cette méthode effectue la persistance d'une règle en appliquant toutes
     * les validations métier nécessaires. Elle gère automatiquement la différence
     * entre création (ID null) et mise à jour (ID existant), appliquant les
     * règles de gestion appropriées dans chaque cas.</p>
     * 
     * <p>Validations métier appliquées :</p>
     * <ul>
     *   <li><strong>Unicité du nom</strong> : Vérification de l'unicité des identifiants</li>
     *   <li><strong>Format JSON</strong> : Validation de la syntaxe des configurations</li>
     *   <li><strong>Templates valides</strong> : Contrôle des placeholders et syntaxe</li>
     *   <li><strong>SQL sécurisé</strong> : Validation des requêtes intégrées</li>
     * </ul>
     * 
     * <p>Comportement création vs mise à jour :</p>
     * <ul>
     *   <li><strong>Création (ID null)</strong> : Vérification unicité du nom</li>
     *   <li><strong>Mise à jour (ID existant)</strong> : Vérification existence préalable</li>
     *   <li><strong>Audit automatique</strong> : Horodatage des modifications</li>
     *   <li><strong>Validation préalable</strong> : Contrôles avant persistance</li>
     * </ul>
     * 
     * @param ruleName La règle à sauvegarder (ne doit pas être null)
     * @return La règle sauvegardée avec son ID assigné
     * @throws IllegalArgumentException si les données sont invalides ou incohérentes
     */
    RuleName save(RuleName ruleName);
    
    /**
     * Supprime une règle par son identifiant.
     * 
     * <p>Cette méthode effectue la suppression sécurisée d'une règle après
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
     *   <li><strong>Règle inexistante</strong> : IllegalArgumentException</li>
     *   <li><strong>Contraintes violées</strong> : DataIntegrityViolationException</li>
     *   <li><strong>Erreur technique</strong> : DataAccessException</li>
     * </ul>
     * 
     * @param id L'identifiant de la règle à supprimer
     * @throws IllegalArgumentException si l'ID est invalide ou la règle n'existe pas
     */
    void deleteById(Integer id);
    
    /**
     * Recherche les règles par type de composant.
     * 
     * <p>Méthode de filtrage pour identifier les règles selon leurs composants
     * actifs (JSON, Template, SQL). Utile pour la catégorisation et l'analyse
     * des règles selon leur complexité et leurs capacités.</p>
     * 
     * <p>Types de composants supportés :</p>
     * <ul>
     *   <li><strong>"JSON"</strong> : Règles avec configuration JSON</li>
     *   <li><strong>"TEMPLATE"</strong> : Règles avec templates paramétrables</li>
     *   <li><strong>"SQL"</strong> : Règles avec composants SQL</li>
     *   <li><strong>"COMPLETE"</strong> : Règles avec tous les composants</li>
     * </ul>
     * 
     * @param componentType Le type de composant ("JSON", "TEMPLATE", "SQL", "COMPLETE")
     * @return Liste des règles correspondant au type spécifié
     */
    List<RuleName> findByComponentType(String componentType);
    
    /**
     * Recherche les règles par mots-clés dans le nom ou la description.
     * 
     * <p>Méthode de recherche textuelle pour trouver des règles dont le nom ou
     * la description contiennent les mots-clés spécifiés. Recherche insensible
     * à la casse pour une utilisation conviviale dans les interfaces de recherche.</p>
     * 
     * <p>Algorithme de recherche :</p>
     * <ul>
     *   <li><strong>Recherche combinée</strong> : Nom ET description</li>
     *   <li><strong>Insensible à la casse</strong> : Ignore majuscules/minuscules</li>
     *   <li><strong>Correspondance partielle</strong> : Recherche de sous-chaînes</li>
     *   <li><strong>Résultats ordonnés</strong> : Tri par pertinence puis nom</li>
     * </ul>
     * 
     * @param keyword Le mot-clé à rechercher
     * @return Liste des règles correspondantes, ordonnée par nom
     */
    List<RuleName> findByKeyword(String keyword);
    
    /**
     * Valide une règle métier sans la sauvegarder.
     * 
     * <p>Méthode utilitaire pour valider une règle avant sa persistance, permettant
     * de détecter les erreurs potentielles et de fournir un feedback immédiat
     * à l'utilisateur sans effectuer de transaction de base de données.</p>
     * 
     * <p>Validations effectuées :</p>
     * <ul>
     *   <li><strong>Données obligatoires</strong> : Nom et description requis</li>
     *   <li><strong>Format JSON</strong> : Syntaxe correcte si présent</li>
     *   <li><strong>Template valide</strong> : Placeholders cohérents si présent</li>
     *   <li><strong>SQL sécurisé</strong> : Requêtes valides si présentes</li>
     * </ul>
     * 
     * @param ruleName La règle à valider
     * @return true si la règle est valide, false sinon
     */
    boolean validateRule(RuleName ruleName);
    
    /**
     * Recherche les règles les plus récemment créées.
     * 
     * <p>Méthode utilitaire pour les tableaux de bord et le suivi des dernières
     * règles ajoutées au système. Utile pour l'audit et le monitoring des
     * modifications du système de règles.</p>
     * 
     * @param limit Nombre maximum de règles à retourner
     * @return Liste des dernières règles créées, ordonnée par ID décroissant
     */
    List<RuleName> findRecentRules(int limit);
}