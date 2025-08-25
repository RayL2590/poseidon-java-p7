package com.nnk.springboot.repositories;

import com.nnk.springboot.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité Trade.
 * 
 * <p>Cette interface étend JpaRepository pour fournir les opérations CRUD de base
 * et définit des méthodes de requête personnalisées pour la gestion des transactions
 * dans l'application de trading Poseidon.</p>
 * 
 * <p>Fonctionnalités fournies :</p>
 * <ul>
 *   <li><strong>CRUD automatique</strong> : Via l'héritage de JpaRepository</li>
 *   <li><strong>Recherche par compte</strong> : Pour l'agrégation des positions</li>
 *   <li><strong>Filtrage par type</strong> : Selon les types de transactions</li>
 *   <li><strong>Recherche temporelle</strong> : Filtrage par périodes</li>
 * </ul>
 * 
 * <p>Optimisations de performance :</p>
 * <ul>
 *   <li><strong>Index sur account</strong> : Recherche rapide par compte</li>
 *   <li><strong>Index sur tradeDate</strong> : Filtrage temporel efficace</li>
 *   <li><strong>Cache de premier niveau</strong> : Optimisation Hibernate automatique</li>
 *   <li><strong>Requêtes paginées</strong> : Gestion des grands volumes</li>
 * </ul>
 * 
 * <p>Cas d'usage métier :</p>
 * <ul>
 *   <li><strong>Calcul de positions</strong> : Agrégation par compte</li>
 *   <li><strong>Reporting périodique</strong> : Filtrage par dates</li>
 *   <li><strong>Monitoring des statuts</strong> : Suivi des transactions</li>
 *   <li><strong>Analyse des performances</strong> : Statistiques par trader</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.Trade
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface TradeRepository extends JpaRepository<Trade, Integer> {

    /**
     * Recherche toutes les transactions ordonnées par date décroissante.
     * 
     * <p>Méthode de convenance pour récupérer toutes les transactions dans l'ordre
     * chronologique inverse (plus récentes en premier), facilitant l'affichage 
     * dans les interfaces utilisateur.</p>
     * 
     * @return Liste de toutes les transactions triées par date décroissante
     */
    List<Trade> findAllByOrderByTradeDateDesc();

    /**
     * Recherche les transactions par compte.
     * 
     * <p>Méthode essentielle pour identifier toutes les transactions associées à un
     * compte spécifique. Utilisée pour le calcul des positions et l'évaluation des risques.</p>
     * 
     * @param account Le nom du compte à rechercher
     * @return Liste des transactions pour ce compte, triées par date décroissante
     */
    List<Trade> findByAccountOrderByTradeDateDesc(String account);

    /**
     * Recherche les transactions par type.
     * 
     * <p>Permet de filtrer les transactions selon leur type (BUY, SELL, SWAP, etc.).
     * Essentiel pour l'analyse statistique et le reporting spécialisé.</p>
     * 
     * @param type Le type de transaction à rechercher
     * @return Liste des transactions de ce type, triées par date décroissante
     */
    List<Trade> findByTypeOrderByTradeDateDesc(String type);

    /**
     * Recherche les transactions par statut.
     * 
     * <p>Permet de filtrer les transactions selon leur état dans le cycle de vie
     * (PENDING, EXECUTED, CANCELLED, etc.). Essentiel pour le monitoring des processus.</p>
     * 
     * @param status Le statut des transactions à rechercher
     * @return Liste des transactions avec ce statut, triées par date décroissante
     */
    List<Trade> findByStatusOrderByTradeDateDesc(String status);

    /**
     * Recherche les transactions par trader.
     * 
     * <p>Permet d'identifier toutes les transactions effectuées par un trader
     * spécifique. Utilisé pour l'évaluation des performances et l'audit.</p>
     * 
     * @param trader Le nom ou identifiant du trader
     * @return Liste des transactions de ce trader, triées par date décroissante
     */
    List<Trade> findByTraderOrderByTradeDateDesc(String trader);

    /**
     * Recherche les transactions par période.
     * 
     * <p>Méthode essentielle pour le reporting périodique et l'analyse des tendances
     * sur des plages de dates définies.</p>
     * 
     * @param startDate Date de début (incluse)
     * @param endDate Date de fin (incluse)
     * @return Liste des transactions dans cette période, triées par date décroissante
     */
    List<Trade> findByTradeDateBetweenOrderByTradeDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Calcule la valeur totale des achats pour un compte.
     * 
     * <p>Requête d'agrégation qui calcule la somme des valeurs d'achat
     * (buyQuantity * buyPrice) pour toutes les transactions d'un compte.</p>
     * 
     * @param account Le compte pour lequel calculer la valeur totale d'achat
     * @return Valeur totale des achats, null si aucune transaction d'achat
     */
    @Query("SELECT SUM(t.buyQuantity * t.buyPrice) FROM Trade t WHERE t.account = :account AND t.buyQuantity IS NOT NULL AND t.buyPrice IS NOT NULL")
    Double sumBuyValueByAccount(@Param("account") String account);

    /**
     * Calcule la valeur totale des ventes pour un compte.
     * 
     * <p>Requête d'agrégation qui calcule la somme des valeurs de vente
     * (sellQuantity * sellPrice) pour toutes les transactions d'un compte.</p>
     * 
     * @param account Le compte pour lequel calculer la valeur totale de vente
     * @return Valeur totale des ventes, null si aucune transaction de vente
     */
    @Query("SELECT SUM(t.sellQuantity * t.sellPrice) FROM Trade t WHERE t.account = :account AND t.sellQuantity IS NOT NULL AND t.sellPrice IS NOT NULL")
    Double sumSellValueByAccount(@Param("account") String account);

    /**
     * Recherche les transactions les plus récemment créées.
     * 
     * <p>Méthode utile pour les tableaux de bord et le suivi des dernières
     * transactions ajoutées au système.</p>
     * 
     * @param pageable Paramètres de pagination pour limiter les résultats
     * @return Liste des dernières transactions créées
     */
    @Query("SELECT t FROM Trade t ORDER BY t.tradeId DESC")
    List<Trade> findRecentTrades(org.springframework.data.domain.Pageable pageable);

    /**
     * Compte le nombre de transactions par statut.
     * 
     * <p>Requête d'agrégation pour obtenir rapidement les statistiques de
     * distribution des transactions selon leur statut.</p>
     * 
     * @return Nombre de transactions pour le statut donné
     */
    long countByStatus(String status);

    /**
     * Recherche les transactions par titre/sécurité.
     * 
     * <p>Permet de filtrer les transactions selon le produit financier traité,
     * utile pour l'analyse par instrument financier.</p>
     * 
     * @param security Le code du titre/sécurité à rechercher
     * @return Liste des transactions pour cette sécurité, triées par date décroissante
     */
    List<Trade> findBySecurityOrderByTradeDateDesc(String security);

    /**
     * Recherche les transactions avec montants supérieurs à un seuil.
     * 
     * <p>Méthode pour identifier les transactions importantes basées sur les montants
     * d'achat ou de vente, utile pour les contrôles de risque et l'audit.</p>
     * 
     * @param minAmount Montant minimum pour filtrer les transactions
     * @return Liste des transactions avec montants élevés
     */
    @Query("SELECT t FROM Trade t WHERE " +
           "(t.buyQuantity * t.buyPrice > :minAmount AND t.buyQuantity IS NOT NULL AND t.buyPrice IS NOT NULL) " +
           "OR (t.sellQuantity * t.sellPrice > :minAmount AND t.sellQuantity IS NOT NULL AND t.sellPrice IS NOT NULL) " +
           "ORDER BY t.tradeDate DESC")
    List<Trade> findHighValueTrades(@Param("minAmount") Double minAmount);

    /**
     * Calcule les statistiques de trading pour une période.
     * 
     * <p>Requête complexe qui retourne les statistiques agrégées pour une période :
     * nombre total de transactions, valeur totale des achats, valeur totale des ventes.</p>
     * 
     * @param startDate Date de début de la période
     * @param endDate Date de fin de la période
     * @return Tableau contenant [count, totalBuyValue, totalSellValue]
     */
    @Query("SELECT " +
           "COUNT(t), " +
           "COALESCE(SUM(CASE WHEN t.buyQuantity IS NOT NULL AND t.buyPrice IS NOT NULL THEN t.buyQuantity * t.buyPrice ELSE 0 END), 0), " +
           "COALESCE(SUM(CASE WHEN t.sellQuantity IS NOT NULL AND t.sellPrice IS NOT NULL THEN t.sellQuantity * t.sellPrice ELSE 0 END), 0) " +
           "FROM Trade t WHERE t.tradeDate BETWEEN :startDate AND :endDate")
    Object[] getTradingStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Recherche les transactions par livre de trading.
     * 
     * <p>Permet de filtrer les transactions selon le portefeuille ou livre
     * dans lequel elles sont enregistrées.</p>
     * 
     * @param book Le nom du livre de trading
     * @return Liste des transactions pour ce livre, triées par date décroissante
     */
    List<Trade> findByBookOrderByTradeDateDesc(String book);
}
