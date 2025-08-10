package com.nnk.springboot.repositories;

import com.nnk.springboot.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité Rating.
 * 
 * <p>Cette interface étend JpaRepository pour fournir les opérations CRUD de base
 * et définit des méthodes de requête personnalisées pour les besoins spécifiques
 * de gestion des notations de crédit dans l'application de trading Poseidon.</p>
 * 
 * <p>Fonctionnalités fournies :</p>
 * <ul>
 *   <li><strong>CRUD automatique</strong> : Via l'héritage de JpaRepository</li>
 *   <li><strong>Requêtes par convention</strong> : Méthodes générées automatiquement</li>
 *   <li><strong>Tri et pagination</strong> : Support intégré pour les gros volumes</li>
 *   <li><strong>Requêtes optimisées</strong> : Pour les cas d'usage financiers spécifiques</li>
 * </ul>
 * 
 * <p>Optimisations de performance :</p>
 * <ul>
 *   <li><strong>Index sur orderNumber</strong> : Tri rapide des notations</li>
 *   <li><strong>Requêtes conditionnelles</strong> : Filtrage efficace par agence</li>
 *   <li><strong>Cache de premier niveau</strong> : Optimisation Hibernate automatique</li>
 *   <li><strong>Lazy loading</strong> : Chargement sélectif selon besoins</li>
 * </ul>
 * 
 * <p>Cas d'usage métier :</p>
 * <ul>
 *   <li><strong>Recherche par qualité</strong> : Investment vs Speculative Grade</li>
 *   <li><strong>Analyse par agence</strong> : Comparaison des notations inter-agences</li>
 *   <li><strong>Classification ordonnée</strong> : Hiérarchisation des risques</li>
 *   <li><strong>Validation unicité</strong> : Contrôle des doublons</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.Rating
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    /**
     * Recherche toutes les notations ordonnées par numéro d'ordre croissant.
     * 
     * <p>Méthode de convenance pour récupérer toutes les notations dans l'ordre
     * de qualité décroissante (meilleure notation en premier). Utilisée pour
     * l'affichage des listes et les analyses de distribution.</p>
     * 
     * @return Liste de toutes les notations triées par orderNumber ASC
     */
    List<Rating> findAllByOrderByOrderNumberAsc();

    /**
     * Recherche une notation par son numéro d'ordre.
     * 
     * <p>Méthode pour valider l'unicité des numéros d'ordre et permettre
     * la recherche directe par position dans la hiérarchie de qualité.</p>
     * 
     * @param orderNumber Le numéro d'ordre à rechercher
     * @return Optional contenant la notation si trouvée, vide sinon
     */
    Optional<Rating> findByOrderNumber(Integer orderNumber);

    /**
     * Recherche les notations ayant une notation Moody's définie.
     * 
     * <p>Filtre les notations qui possèdent une évaluation de l'agence Moody's,
     * utile pour les analyses spécifiques à cette agence ou les comparaisons
     * de méthodologies de notation.</p>
     * 
     * @return Liste des notations avec une notation Moody's, triée par ordre
     */
    List<Rating> findByMoodysRatingIsNotNullOrderByOrderNumberAsc();

    /**
     * Recherche les notations ayant une notation Standard & Poor's définie.
     * 
     * <p>Filtre les notations qui possèdent une évaluation de l'agence S&P,
     * permettant l'analyse des tendances et biais spécifiques à cette agence.</p>
     * 
     * @return Liste des notations avec une notation S&P, triée par ordre
     */
    List<Rating> findBySandPRatingIsNotNullOrderByOrderNumberAsc();

    /**
     * Recherche les notations ayant une notation Fitch définie.
     * 
     * <p>Filtre les notations qui possèdent une évaluation de l'agence Fitch,
     * utile pour l'analyse de la troisième opinion dans les cas de divergence
     * entre Moody's et S&P.</p>
     * 
     * @return Liste des notations avec une notation Fitch, triée par ordre
     */
    List<Rating> findByFitchRatingIsNotNullOrderByOrderNumberAsc();

    /**
     * Recherche les notations dans une plage de numéros d'ordre.
     * 
     * <p>Méthode pour filtrer les notations selon leur qualité de crédit,
     * permettant de segmenter facilement entre Investment Grade et
     * Speculative Grade ou d'autres catégories personnalisées.</p>
     * 
     * @param minOrder Numéro d'ordre minimum (inclus)
     * @param maxOrder Numéro d'ordre maximum (inclus)
     * @return Liste des notations dans la plage spécifiée, triée par ordre
     */
    List<Rating> findByOrderNumberBetweenOrderByOrderNumberAsc(Integer minOrder, Integer maxOrder);

    /**
     * Recherche les notations avec un numéro d'ordre supérieur ou égal à la valeur donnée.
     * 
     * <p>Méthode utile pour identifier toutes les notations de qualité inférieure
     * à un seuil donné, par exemple toutes les notations Speculative Grade.</p>
     * 
     * @param orderNumber Numéro d'ordre minimum (inclus)
     * @return Liste des notations avec ordre >= orderNumber, triée par ordre
     */
    List<Rating> findByOrderNumberGreaterThanEqualOrderByOrderNumberAsc(Integer orderNumber);

    /**
     * Recherche la notation avec le numéro d'ordre le plus élevé.
     * 
     * <p>Méthode d'optimisation pour la génération automatique de nouveaux
     * numéros d'ordre, évitant de charger toute la liste pour trouver le maximum.</p>
     * 
     * @return Optional contenant la notation avec l'ordre le plus élevé
     */
    Optional<Rating> findTopByOrderByOrderNumberDesc();

    /**
     * Compte le nombre de notations par type (Investment vs Speculative Grade).
     * 
     * <p>Requête d'agrégation pour obtenir rapidement la distribution des notations
     * sans charger toutes les entités, optimisant les tableaux de bord et rapports.</p>
     * 
     * @param maxInvestmentOrder Numéro d'ordre maximum pour Investment Grade (typiquement 12)
     * @return Tableau contenant [count_investment_grade, count_speculative_grade]
     */
    @Query("SELECT " +
           "SUM(CASE WHEN r.orderNumber <= :maxInvestmentOrder THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN r.orderNumber > :maxInvestmentOrder THEN 1 ELSE 0 END) " +
           "FROM Rating r WHERE r.orderNumber IS NOT NULL")
    Object[] countByGradeType(Integer maxInvestmentOrder);

    /**
     * Recherche les notations avec des divergences entre agences.
     * 
     * <p>Requête complexe pour identifier les cas où les agences de notation
     * ont des opinions significativement différentes, utile pour l'analyse
     * des risques et la prise de décision d'investissement.</p>
     * 
     * @return Liste des notations présentant des divergences inter-agences
     */
    @Query("SELECT r FROM Rating r WHERE " +
           "(r.moodysRating IS NOT NULL AND r.sandPRating IS NOT NULL AND " +
           "((r.moodysRating LIKE 'Aaa%' OR r.moodysRating LIKE 'Aa%' OR r.moodysRating LIKE 'A%' OR r.moodysRating LIKE 'Baa%') " +
           "AND (r.sandPRating LIKE 'BB%' OR r.sandPRating LIKE 'B%' OR r.sandPRating LIKE 'CCC%' OR r.sandPRating = 'CC' OR r.sandPRating = 'C' OR r.sandPRating = 'D')) " +
           "OR " +
           "((r.moodysRating LIKE 'Ba%' OR r.moodysRating LIKE 'B%' OR r.moodysRating LIKE 'Caa%' OR r.moodysRating = 'Ca' OR r.moodysRating = 'C') " +
           "AND (r.sandPRating LIKE 'AAA%' OR r.sandPRating LIKE 'AA%' OR r.sandPRating LIKE 'A%' OR r.sandPRating LIKE 'BBB%'))) " +
           "ORDER BY r.orderNumber ASC")
    List<Rating> findRatingsWithAgencyDivergence();

    /**
     * Recherche les notations les plus récemment créées.
     * 
     * <p>Méthode utile pour les tableaux de bord et le suivi des dernières
     * mises à jour de notations dans le système.</p>
     * 
     * @param limit Nombre maximum de résultats à retourner
     * @return Liste des dernières notations créées
     */
    @Query("SELECT r FROM Rating r ORDER BY r.id DESC")
    List<Rating> findRecentRatings(org.springframework.data.domain.Pageable pageable);

    /**
     * Vérifie l'existence d'une notation avec des valeurs spécifiques.
     * 
     * <p>Méthode pour détecter les doublons potentiels lors de la création
     * de nouvelles notations, basée sur la combinaison des trois notations.</p>
     * 
     * @param moodysRating Notation Moody's
     * @param sandPRating Notation S&P
     * @param fitchRating Notation Fitch
     * @return true si une notation identique existe déjà, false sinon
     */
    boolean existsByMoodysRatingAndSandPRatingAndFitchRating(
        String moodysRating, String sandPRating, String fitchRating);
}