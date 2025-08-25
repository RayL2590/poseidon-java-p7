package com.nnk.springboot.repositories;

import com.nnk.springboot.domain.RuleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité RuleName.
 * 
 * <p>Cette interface étend JpaRepository pour fournir les opérations CRUD de base
 * et définit des méthodes de requête personnalisées pour la gestion des règles
 * métier dans l'application de trading Poseidon.</p>
 * 
 * <p>Fonctionnalités fournies :</p>
 * <ul>
 *   <li><strong>CRUD automatique</strong> : Via l'héritage de JpaRepository</li>
 *   <li><strong>Recherche par nom</strong> : Pour l'identification unique des règles</li>
 *   <li><strong>Filtrage par type</strong> : Selon les templates et configurations JSON</li>
 *   <li><strong>Requêtes SQL actives</strong> : Pour les règles avec composants SQL définis</li>
 * </ul>
 * 
 * <p>Optimisations de performance :</p>
 * <ul>
 *   <li><strong>Index sur name</strong> : Recherche rapide par identifiant métier</li>
 *   <li><strong>Requêtes conditionnelles</strong> : Filtrage efficace par composants</li>
 *   <li><strong>Cache de premier niveau</strong> : Optimisation Hibernate automatique</li>
 *   <li><strong>Lazy loading</strong> : Chargement sélectif des templates volumineux</li>
 * </ul>
 * 
 * <p>Cas d'usage métier :</p>
 * <ul>
 *   <li><strong>Validation de règles</strong> : Recherche par nom pour exécution</li>
 *   <li><strong>Configuration dynamique</strong> : Filtrage par présence de JSON</li>
 *   <li><strong>Règles SQL actives</strong> : Pour les validations de base de données</li>
 *   <li><strong>Gestion des templates</strong> : Pour la réutilisabilité des règles</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.RuleName
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface RuleNameRepository extends JpaRepository<RuleName, Integer> {

    /**
     * Recherche une règle par son nom.
     * 
     * <p>Méthode principale pour identifier de manière unique une règle métier
     * par son nom. Utilisée pour l'exécution des règles et les validations.</p>
     * 
     * @param name Le nom de la règle à rechercher
     * @return Optional contenant la règle si trouvée, vide sinon
     */
    Optional<RuleName> findByName(String name);

    /**
     * Recherche toutes les règles ordonnées par nom.
     * 
     * <p>Méthode de convenance pour récupérer toutes les règles dans l'ordre
     * alphabétique, facilitant l'affichage dans les interfaces utilisateur.</p>
     * 
     * @return Liste de toutes les règles triées par nom ASC
     */
    List<RuleName> findAllByOrderByNameAsc();

    /**
     * Recherche les règles ayant une configuration JSON définie.
     * 
     * <p>Filtre les règles qui possèdent une configuration JSON, utile pour
     * identifier les règles paramétrables et configurables dynamiquement.</p>
     * 
     * @return Liste des règles avec configuration JSON, triée par nom
     */
    List<RuleName> findByJsonIsNotNullOrderByNameAsc();

    /**
     * Recherche les règles ayant un template défini.
     * 
     * <p>Filtre les règles qui possèdent un template, permettant d'identifier
     * les règles réutilisables et paramétrables avec des placeholders.</p>
     * 
     * @return Liste des règles avec template, triée par nom
     */
    List<RuleName> findByTemplateIsNotNullOrderByNameAsc();

    /**
     * Recherche les règles ayant des composants SQL définis.
     * 
     * <p>Filtre les règles qui possèdent soit une requête SQL complète (sqlStr)
     * soit un fragment SQL (sqlPart), utile pour identifier les règles qui
     * interagissent avec la base de données.</p>
     * 
     * @return Liste des règles avec composants SQL, triée par nom
     */
    List<RuleName> findBySqlStrIsNotNullOrSqlPartIsNotNullOrderByNameAsc();

    /**
     * Recherche les règles par nom contenant un mot-clé (insensible à la casse).
     * 
     * <p>Méthode de recherche textuelle pour trouver des règles dont le nom
     * contient un terme spécifique, utile pour les fonctionnalités de recherche.</p>
     * 
     * @param keyword Le mot-clé à rechercher dans le nom
     * @return Liste des règles correspondantes, triée par nom
     */
    List<RuleName> findByNameContainingIgnoreCaseOrderByNameAsc(String keyword);

    /**
     * Recherche les règles par description contenant un mot-clé (insensible à la casse).
     * 
     * <p>Méthode de recherche textuelle pour trouver des règles dont la description
     * contient un terme spécifique, permettant une recherche sémantique plus large.</p>
     * 
     * @param keyword Le mot-clé à rechercher dans la description
     * @return Liste des règles correspondantes, triée par nom
     */
    List<RuleName> findByDescriptionContainingIgnoreCaseOrderByNameAsc(String keyword);

    /**
     * Vérifie l'existence d'une règle avec un nom donné.
     * 
     * <p>Méthode pour détecter les doublons potentiels lors de la création
     * de nouvelles règles, assurant l'unicité des noms.</p>
     * 
     * @param name Le nom de la règle à vérifier
     * @return true si une règle avec ce nom existe déjà, false sinon
     */
    boolean existsByName(String name);

    /**
     * Compte le nombre total de règles actives par type de composant.
     * 
     * <p>Requête d'agrégation pour obtenir rapidement les statistiques de
     * distribution des règles selon leurs composants, optimisant les
     * tableaux de bord et rapports de gestion.</p>
     * 
     * @return Tableau contenant [count_with_json, count_with_template, count_with_sql]
     */
    @Query("SELECT " +
           "SUM(CASE WHEN r.json IS NOT NULL THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN r.template IS NOT NULL THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN r.sqlStr IS NOT NULL OR r.sqlPart IS NOT NULL THEN 1 ELSE 0 END) " +
           "FROM RuleName r")
    Object[] countByComponentType();

    /**
     * Recherche les règles complètes (avec tous les composants définis).
     * 
     * <p>Requête pour identifier les règles les plus sophistiquées qui possèdent
     * à la fois une configuration JSON, un template et des composants SQL,
     * indiquant des règles métier complexes et complètes.</p>
     * 
     * @return Liste des règles complètes, triée par nom
     */
    @Query("SELECT r FROM RuleName r WHERE " +
           "r.json IS NOT NULL AND " +
           "r.template IS NOT NULL AND " +
           "(r.sqlStr IS NOT NULL OR r.sqlPart IS NOT NULL) " +
           "ORDER BY r.name ASC")
    List<RuleName> findCompleteRules();

    /**
     * Recherche les règles les plus récemment créées.
     * 
     * <p>Méthode utile pour les tableaux de bord et le suivi des dernières
     * règles ajoutées au système.</p>
     * 
     * @param limit Nombre maximum de résultats à retourner
     * @return Liste des dernières règles créées
     */
    @Query("SELECT r FROM RuleName r ORDER BY r.id DESC")
    List<RuleName> findRecentRules(org.springframework.data.domain.Pageable pageable);

    /**
     * Recherche les règles par type de template (recherche de motifs).
     * 
     * <p>Méthode avancée pour identifier les règles basées sur des motifs
     * spécifiques dans leurs templates, utile pour la catégorisation
     * automatique et l'analyse des règles.</p>
     * 
     * @param templatePattern Motif à rechercher dans le template (SQL LIKE)
     * @return Liste des règles correspondant au motif, triée par nom
     */
    @Query("SELECT r FROM RuleName r WHERE r.template LIKE %:templatePattern% ORDER BY r.name ASC")
    List<RuleName> findByTemplatePattern(String templatePattern);
}
