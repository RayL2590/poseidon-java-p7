package com.nnk.springboot.services;

import com.nnk.springboot.domain.Trade;
import java.util.List;
import java.util.Optional;

/**
 * Interface pour le service de gestion des transactions (Trade).
 * 
 * <p>Cette interface définit le contrat pour les opérations de gestion des transactions
 * dans l'application de trading Poseidon. Elle respecte le principe ISP
 * (Interface Segregation Principle) en fournissant une interface spécialisée
 * pour les opérations Trade uniquement.</p>
 * 
 * <p>Responsabilités du service :</p>
 * <ul>
 *   <li><strong>Opérations CRUD</strong> : Create, Read, Update, Delete des transactions</li>
 *   <li><strong>Validation métier</strong> : Cohérence des données financières</li>
 *   <li><strong>Contrôles de risque</strong> : Validation des limites et seuils</li>
 *   <li><strong>Gestion du cycle de vie</strong> : États et transitions des transactions</li>
 * </ul>
 * 
 * <p>Contraintes métier à implémenter :</p>
 * <ul>
 *   <li><strong>Comptes valides</strong> : Vérification de l'existence des comptes</li>
 *   <li><strong>Montants positifs</strong> : Validation des quantités et prix</li>
 *   <li><strong>Types autorisés</strong> : Validation des types de transactions</li>
 *   <li><strong>Cohérence temporelle</strong> : Validation des dates de transaction</li>
 * </ul>
 * 
 * <p>Considérations de performance :</p>
 * <ul>
 *   <li><strong>Cache des transactions</strong> : Optimisation pour consultations fréquentes</li>
 *   <li><strong>Index sur comptes</strong> : Performance des recherches par compte</li>
 *   <li><strong>Pagination des résultats</strong> : Gestion des volumes importants</li>
 *   <li><strong>Requêtes optimisées</strong> : Minimisation des accès base de données</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.Trade
 * @see com.nnk.springboot.services.TradeService
 */
public interface ITradeService {
    
    /**
     * Récupère toutes les transactions.
     * 
     * <p>Cette méthode retourne l'ensemble des transactions stockées dans le système,
     * ordonnées par défaut selon la date de transaction décroissante (les plus récentes
     * en premier). Utilisée principalement pour l'affichage des listes complètes et
     * la gestion administrative des transactions.</p>
     * 
     * <p>Cas d'usage :</p>
     * <ul>
     *   <li><strong>Interface de trading</strong> : Vue d'ensemble des positions</li>
     *   <li><strong>Reporting financier</strong> : Génération de rapports globaux</li>
     *   <li><strong>Audit et conformité</strong> : Revue des transactions</li>
     *   <li><strong>Export de données</strong> : Sauvegarde complète des transactions</li>
     * </ul>
     * 
     * @return Liste de toutes les transactions, ordonnées par date décroissante
     */
    List<Trade> findAll();
    
    /**
     * Récupère une transaction par son identifiant.
     * 
     * <p>Cette méthode permet de retrouver une transaction spécifique par son ID unique.
     * Retourne un Optional pour gérer élégamment les cas où la transaction n'existe pas,
     * évitant les exceptions et facilitant la gestion d'erreurs dans les contrôleurs.</p>
     * 
     * <p>Validation d'entrée :</p>
     * <ul>
     *   <li><strong>ID null ou négatif</strong> : Retourne Optional.empty()</li>
     *   <li><strong>ID valide inexistant</strong> : Retourne Optional.empty()</li>
     *   <li><strong>ID valide existant</strong> : Retourne Optional.of(trade)</li>
     * </ul>
     * 
     * @param id L'identifiant de la transaction à rechercher
     * @return Optional contenant la transaction si trouvée, vide sinon
     */
    Optional<Trade> findById(Integer id);
    
    /**
     * Recherche les transactions par compte.
     * 
     * <p>Méthode essentielle pour identifier toutes les transactions associées à un
     * compte spécifique. Utilisée intensivement pour le calcul des positions,
     * l'évaluation des risques et la génération de rapports par compte.</p>
     * 
     * <p>Cas d'usage critiques :</p>
     * <ul>
     *   <li><strong>Calcul de positions</strong> : Agrégation par compte</li>
     *   <li><strong>Gestion des risques</strong> : Évaluation de l'exposition</li>
     *   <li><strong>Reporting client</strong> : Historique des transactions</li>
     *   <li><strong>Réconciliation</strong> : Vérification des mouvements</li>
     * </ul>
     * 
     * @param account Le nom du compte à rechercher
     * @return Liste des transactions pour ce compte, ordonnées par date décroissante
     */
    List<Trade> findByAccount(String account);
    
    /**
     * Recherche les transactions par type.
     * 
     * <p>Permet de filtrer les transactions selon leur type (BUY, SELL, SWAP, etc.).
     * Essentiel pour l'analyse statistique, le reporting spécialisé et la gestion
     * des processus spécifiques à chaque type de transaction.</p>
     * 
     * <p>Applications typiques :</p>
     * <ul>
     *   <li><strong>Analyse statistique</strong> : Répartition par type</li>
     *   <li><strong>Processus métier</strong> : Traitement différencié</li>
     *   <li><strong>Reporting réglementaire</strong> : Classification des opérations</li>
     *   <li><strong>Contrôle des risques</strong> : Limites par type d'opération</li>
     * </ul>
     * 
     * @param type Le type de transaction à rechercher
     * @return Liste des transactions de ce type, ordonnées par date décroissante
     */
    List<Trade> findByType(String type);
    
    /**
     * Vérifie l'existence d'une transaction par son identifiant.
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
     * @return true si la transaction existe, false sinon
     */
    boolean existsById(Integer id);
    
    /**
     * Sauvegarde une transaction (création ou mise à jour).
     * 
     * <p>Cette méthode effectue la persistance d'une transaction en appliquant toutes
     * les validations métier nécessaires. Elle gère automatiquement la différence
     * entre création (ID null) et mise à jour (ID existant), appliquant les
     * règles de gestion appropriées dans chaque cas.</p>
     * 
     * <p>Validations métier appliquées :</p>
     * <ul>
     *   <li><strong>Comptes valides</strong> : Vérification de l'existence des comptes</li>
     *   <li><strong>Montants positifs</strong> : Validation des quantités et prix</li>
     *   <li><strong>Types autorisés</strong> : Contrôle des types de transactions</li>
     *   <li><strong>Limites de risque</strong> : Respect des seuils définis</li>
     * </ul>
     * 
     * <p>Comportement création vs mise à jour :</p>
     * <ul>
     *   <li><strong>Création (ID null)</strong> : Attribution ID, date de création</li>
     *   <li><strong>Mise à jour (ID existant)</strong> : Vérification existence préalable</li>
     *   <li><strong>Audit automatique</strong> : Horodatage des modifications</li>
     *   <li><strong>Validation préalable</strong> : Contrôles avant persistance</li>
     * </ul>
     * 
     * @param trade La transaction à sauvegarder (ne doit pas être null)
     * @return La transaction sauvegardée avec son ID assigné
     * @throws IllegalArgumentException si les données sont invalides ou incohérentes
     */
    Trade save(Trade trade);
    
    /**
     * Supprime une transaction par son identifiant.
     * 
     * <p>Cette méthode effectue la suppression sécurisée d'une transaction après
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
     *   <li><strong>Transaction inexistante</strong> : IllegalArgumentException</li>
     *   <li><strong>Contraintes violées</strong> : DataIntegrityViolationException</li>
     *   <li><strong>Erreur technique</strong> : DataAccessException</li>
     * </ul>
     * 
     * @param id L'identifiant de la transaction à supprimer
     * @throws IllegalArgumentException si l'ID est invalide ou la transaction n'existe pas
     */
    void deleteById(Integer id);
    
    /**
     * Recherche les transactions par statut.
     * 
     * <p>Permet de filtrer les transactions selon leur état dans le cycle de vie
     * (PENDING, EXECUTED, CANCELLED, etc.). Essentiel pour le monitoring des
     * processus de traitement et la gestion opérationnelle.</p>
     * 
     * <p>États typiques :</p>
     * <ul>
     *   <li><strong>"PENDING"</strong> : Transactions en attente de traitement</li>
     *   <li><strong>"EXECUTED"</strong> : Transactions exécutées avec succès</li>
     *   <li><strong>"CANCELLED"</strong> : Transactions annulées</li>
     *   <li><strong>"FAILED"</strong> : Transactions échouées</li>
     * </ul>
     * 
     * @param status Le statut des transactions à rechercher
     * @return Liste des transactions avec ce statut
     */
    List<Trade> findByStatus(String status);
    
    /**
     * Recherche les transactions par trader.
     * 
     * <p>Permet d'identifier toutes les transactions effectuées par un trader
     * spécifique. Utilisé pour l'évaluation des performances, l'audit des
     * opérations et la gestion des limites par trader.</p>
     * 
     * <p>Applications typiques :</p>
     * <ul>
     *   <li><strong>Évaluation performance</strong> : Calcul des résultats par trader</li>
     *   <li><strong>Contrôle des risques</strong> : Monitoring des expositions</li>
     *   <li><strong>Audit opérationnel</strong> : Traçabilité des actions</li>
     *   <li><strong>Reporting managérial</strong> : Activité par personne</li>
     * </ul>
     * 
     * @param trader Le nom ou identifiant du trader
     * @return Liste des transactions de ce trader
     */
    List<Trade> findByTrader(String trader);
    
    /**
     * Recherche les transactions par période.
     * 
     * <p>Méthode essentielle pour le reporting périodique, l'analyse des tendances
     * et la génération de statistiques sur des plages de dates définies.</p>
     * 
     * <p>Cas d'usage fréquents :</p>
     * <ul>
     *   <li><strong>Reporting périodique</strong> : Transactions du mois/trimestre</li>
     *   <li><strong>Analyse de tendances</strong> : Évolution dans le temps</li>
     *   <li><strong>Calculs de performance</strong> : Rendements sur période</li>
     *   <li><strong>Conformité réglementaire</strong> : Déclarations périodiques</li>
     * </ul>
     * 
     * @param startDate Date de début (incluse)
     * @param endDate Date de fin (incluse)
     * @return Liste des transactions dans cette période
     */
    List<Trade> findByTradeDateBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    
    /**
     * Calcule la valeur totale des transactions pour un compte.
     * 
     * <p>Méthode utilitaire qui calcule la valeur monétaire totale de toutes les
     * transactions associées à un compte spécifique. Essentielle pour l'évaluation
     * des positions et le calcul des expositions.</p>
     * 
     * <p>Calcul effectué :</p>
     * <ul>
     *   <li><strong>Achats</strong> : Somme(buyQuantity * buyPrice)</li>
     *   <li><strong>Ventes</strong> : Somme(sellQuantity * sellPrice)</li>
     *   <li><strong>Résultat net</strong> : Différence entre ventes et achats</li>
     * </ul>
     * 
     * @param account Le compte pour lequel calculer la valeur totale
     * @return Valeur totale des transactions, null si aucune transaction
     */
    Double calculateTotalValueByAccount(String account);
    
    /**
     * Valide une transaction sans la sauvegarder.
     * 
     * <p>Méthode utilitaire pour valider une transaction avant sa persistance,
     * permettant de détecter les erreurs potentielles et de fournir un feedback
     * immédiat à l'utilisateur sans effectuer de transaction de base de données.</p>
     * 
     * <p>Validations effectuées :</p>
     * <ul>
     *   <li><strong>Données obligatoires</strong> : Compte et type requis</li>
     *   <li><strong>Format des montants</strong> : Quantités et prix valides</li>
     *   <li><strong>Cohérence des données</strong> : Relations logiques respectées</li>
     *   <li><strong>Limites métier</strong> : Seuils et contraintes respectés</li>
     * </ul>
     * 
     * @param trade La transaction à valider
     * @return true si la transaction est valide, false sinon
     */
    boolean validateTrade(Trade trade);
    
    /**
     * Recherche les transactions les plus récemment créées.
     * 
     * <p>Méthode utilitaire pour les tableaux de bord et le suivi des dernières
     * transactions ajoutées au système. Utile pour l'audit et le monitoring
     * des activités récentes.</p>
     * 
     * @param limit Nombre maximum de transactions à retourner
     * @return Liste des dernières transactions créées, ordonnée par ID décroissant
     */
    List<Trade> findRecentTrades(int limit);
}