package com.nnk.springboot.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité JPA représentant une règle métier dans le système de trading Poseidon.
 * 
 * <p>Cette entité modélise les règles de gestion et de validation utilisées dans
 * le système de trading. Elle permet de définir des règles personnalisables avec
 * leurs noms, descriptions, configurations JSON, templates et requêtes SQL associées.
 * Ces règles peuvent être utilisées pour valider des transactions, appliquer des
 * politiques de risque ou automatiser des processus métier.</p>
 * 
 * <p>Composants d'une règle :</p>
 * <ul>
 *   <li><strong>Nom</strong> : Identifiant unique et lisible de la règle</li>
 *   <li><strong>Description</strong> : Explication détaillée du comportement de la règle</li>
 *   <li><strong>JSON</strong> : Configuration au format JSON pour paramétrer la règle</li>
 *   <li><strong>Template</strong> : Modèle de règle réutilisable</li>
 *   <li><strong>SQL</strong> : Requêtes SQL pour l'exécution de la règle</li>
 * </ul>
 * 
 * <p>Caractéristiques techniques :</p>
 * <ul>
 *   <li>Entité JPA mappée sur la table "rule_name"</li>
 *   <li>Identifiant auto-généré avec stratégie IDENTITY</li>
 *   <li>Utilisation de Lombok pour la génération automatique des getters/setters</li>
 *   <li>Support des templates étendus (jusqu'à 512 caractères)</li>
 * </ul>
 * 
 * <p>Cas d'usage dans l'environnement financier :</p>
 * <ul>
 *   <li>Validation des limites de crédit et de risque</li>
 *   <li>Contrôles de conformité réglementaire</li>
 *   <li>Règles d'autorisation des transactions</li>
 *   <li>Calculs automatiques de pricing et marges</li>
 *   <li>Alertes et notifications automatisées</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rule_name")
public class RuleName {

    /** 
     * Identifiant unique de la règle métier.
     * Clé primaire auto-générée avec stratégie IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    /** 
     * Nom de la règle.
     * 
     * <p>Identifiant lisible et unique de la règle, utilisé pour référencer
     * la règle dans l'application. Doit être explicite et refléter le
     * comportement de la règle.</p>
     * 
     * <p>Exemples de noms de règles :</p>
     * <ul>
     *   <li>"MaxPositionLimit" : Limite maximale de position</li>
     *   <li>"MinimumMarginRequirement" : Exigence de marge minimale</li>
     *   <li>"CreditRiskValidation" : Validation du risque de crédit</li>
     * </ul>
     */
    @Column(name = "name", length = 125)
    private String name;

    /** 
     * Description détaillée de la règle.
     * 
     * <p>Explication complète du comportement et de l'objectif de la règle.
     * Cette description aide les utilisateurs et développeurs à comprendre
     * quand et comment la règle doit être appliquée.</p>
     * 
     * <p>Devrait inclure :</p>
     * <ul>
     *   <li>L'objectif métier de la règle</li>
     *   <li>Les conditions d'application</li>
     *   <li>Les actions résultantes</li>
     *   <li>Les exceptions éventuelles</li>
     * </ul>
     */
    @Column(name = "description", length = 125)
    private String description;

    /** 
     * Configuration JSON de la règle.
     * 
     * <p>Paramètres de configuration au format JSON permettant de personnaliser
     * le comportement de la règle sans modifier le code. Cette approche offre
     * une flexibilité maximale pour adapter les règles aux besoins spécifiques.</p>
     * 
     * <p>Exemple de configuration JSON :</p>
     * <pre>
     * {
     *   "maxAmount": 1000000,
     *   "currency": "USD",
     *   "riskLevel": "HIGH",
     *   "notifications": ["email", "sms"]
     * }
     * </pre>
     */
    @Column(name = "json", length = 125)
    private String json;

    /** 
     * Template de la règle.
     * 
     * <p>Modèle de règle réutilisable pouvant contenir des placeholders
     * et des expressions dynamiques. Les templates permettent de créer
     * des règles paramétrables et réutilisables pour différents contextes.</p>
     * 
     * <p>Capacité étendue jusqu'à 512 caractères pour supporter des
     * templates complexes avec conditions multiples et logique métier
     * élaborée.</p>
     * 
     * <p>Exemple de template :</p>
     * <pre>
     * IF position.amount > ${maxLimit} 
     * THEN REJECT TRANSACTION 
     * WITH MESSAGE "Position limit exceeded"
     * </pre>
     */
    @Column(name = "template", length = 512)
    private String template;

    /** 
     * Requête SQL principale associée à la règle.
     * 
     * <p>Requête SQL utilisée pour évaluer les conditions de la règle
     * ou récupérer les données nécessaires à son exécution. Cette requête
     * est généralement utilisée pour les validations complexes nécessitant
     * l'accès aux données historiques ou de référence.</p>
     * 
     * <p>Exemples d'usage :</p>
     * <ul>
     *   <li>Vérification des limites basées sur l'historique</li>
     *   <li>Calculs agrégés pour évaluation des risques</li>
     *   <li>Validation des données de référence</li>
     * </ul>
     */
    @Column(name = "sqlStr", length = 125)
    private String sqlStr;

    /** 
     * Fragment SQL partiel pour la règle.
     * 
     * <p>Portion de requête SQL réutilisable pouvant être intégrée dans
     * des requêtes plus larges. Utile pour construire des requêtes complexes
     * de manière modulaire et pour optimiser les performances en évitant
     * la duplication de sous-requêtes.</p>
     * 
     * <p>Applications typiques :</p>
     * <ul>
     *   <li>Conditions WHERE réutilisables</li>
     *   <li>Jointures standards entre tables</li>
     *   <li>Calculs métier récurrents</li>
     *   <li>Filtres de sécurité et d'autorisation</li>
     * </ul>
     */
    @Column(name = "sqlPart", length = 125)
    private String sqlPart;

    /**
     * Constructeur de convenance pour les tests et la création rapide d'instances.
     * 
     * <p>Ce constructeur permet de créer une RuleName avec les champs essentiels
     * pour les tests unitaires ou la création rapide d'instances. Il initialise
     * le nom et la description qui sont les éléments de base d'une règle.</p>
     * 
     * <p>Usage typique pour les tests :</p>
     * <pre>
     * // Règle simple de validation
     * RuleName creditRule = new RuleName("CreditCheck", "Validation du crédit client");
     * 
     * // Règle de limite de position
     * RuleName positionRule = new RuleName("PositionLimit", "Contrôle des limites de position");
     * </pre>
     * 
     * @param name Le nom de la règle
     * @param description La description de la règle
     */
    public RuleName(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Constructeur complet pour la création de règles avec configuration.
     * 
     * <p>Ce constructeur permet de créer une RuleName complète avec toutes
     * les informations de configuration, incluant les paramètres JSON et
     * les templates SQL.</p>
     * 
     * @param name Le nom de la règle
     * @param description La description de la règle
     * @param json La configuration JSON
     * @param template Le template de la règle
     */
    public RuleName(String name, String description, String json, String template) {
        this.name = name;
        this.description = description;
        this.json = json;
        this.template = template;
    }
}
