package com.nnk.springboot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * Data Transfer Object (DTO) pour l'entité RuleName.
 * 
 * <p>Cette classe DTO encapsule les données des règles métier pour la communication
 * entre la couche de présentation et la couche métier. Elle assure une validation
 * rigoureuse des règles saisies par l'utilisateur et respecte les contraintes
 * de sécurité et de cohérence pour les règles de gestion financières.</p>
 * 
 * <p>Validation des règles métier :</p>
 * <ul>
 *   <li><strong>Noms standardisés</strong> : Format et unicité des identifiants</li>
 *   <li><strong>Configurations JSON</strong> : Syntaxe valide pour paramétrage</li>
 *   <li><strong>Templates sécurisés</strong> : Validation des placeholders</li>
 *   <li><strong>SQL sécurisé</strong> : Protection contre les injections</li>
 * </ul>
 * 
 * <p>Utilisation dans l'environnement de trading :</p>
 * <ul>
 *   <li><strong>Saisie de règles</strong> : Formulaires de création/modification</li>
 *   <li><strong>Configuration dynamique</strong> : Paramétrage sans redéploiement</li>
 *   <li><strong>Validation métier</strong> : Contrôle de cohérence des règles</li>
 *   <li><strong>Audit et conformité</strong> : Traçabilité des changements</li>
 * </ul>
 * 
 * <p>Types de règles supportées :</p>
 * <ul>
 *   <li><strong>Validation</strong> : Contrôles de limites et seuils</li>
 *   <li><strong>Calcul</strong> : Formules de pricing et marges</li>
 *   <li><strong>Autorisation</strong> : Règles d'accès et permissions</li>
 *   <li><strong>Notification</strong> : Alertes et rapports automatisés</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.RuleName
 * @see com.nnk.springboot.mapper.RuleNameMapper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleNameDTO {

    /** 
     * Identifiant unique de la règle métier.
     * 
     * <p>Utilisé pour l'identification lors des opérations de mise à jour
     * et de suppression. Reste null lors de la création d'une nouvelle règle.</p>
     */
    private Integer id;

    /** 
     * Nom de la règle.
     * 
     * <p>Identifiant métier unique de la règle, utilisé pour la référencer
     * dans l'application. Doit être explicite et refléter le comportement
     * de la règle. Validation par expression régulière pour assurer un
     * format cohérent et sécurisé.</p>
     * 
     * <p>Format accepté : Alphanumériques, underscores, tirets et points,
     * commençant obligatoirement par un caractère alphanumé rique.</p>
     * 
     * <p>Exemples valides : "CreditCheck", "max_position_limit", "price-validation"</p>
     * <p>Exemples invalides : "_invalid", "-invalid", "invalid space"</p>
     */
    @NotBlank(message = "Rule name is required")
    @Size(max = 125, message = "Rule name must be less than 125 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_\\-\\.]*$", 
             message = "Rule name must start with alphanumeric character and contain only alphanumeric characters, underscores, hyphens, and dots")
    private String name;

    /** 
     * Description détaillée de la règle.
     * 
     * <p>Explication complète du comportement et de l'objectif de la règle.
     * Cette description aide les utilisateurs et développeurs à comprendre
     * quand et comment la règle doit être appliquée.</p>
     * 
     * <p>Recommandations pour la description :</p>
     * <ul>
     *   <li>Objectif métier de la règle</li>
     *   <li>Conditions d'application</li>
     *   <li>Actions résultantes</li>
     *   <li>Exceptions éventuelles</li>
     * </ul>
     */
    @Size(max = 125, message = "Description must be less than 125 characters")
    private String description;

    /** 
     * Configuration JSON de la règle.
     * 
     * <p>Paramètres de configuration au format JSON permettant de personnaliser
     * le comportement de la règle sans modifier le code. Cette approche offre
     * une flexibilité maximale pour adapter les règles aux besoins spécifiques.</p>
     * 
     * <p>Exemples de configuration JSON valides :</p>
     * <pre>
     * {"maxAmount": 1000000, "currency": "USD"}
     * {"threshold": 50, "action": "alert"}
     * {"enabled": true, "priority": "HIGH"}
     * </pre>
     * 
     * <p>Validation : La syntaxe JSON est vérifiée via un validateur personnalisé
     * qui s'assure de la bonne formation du JSON sans l'interpréter.</p>
     */
    @Size(max = 125, message = "JSON configuration must be less than 125 characters")
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
     * <p>Exemples de templates valides :</p>
     * <pre>
     * "IF ${position.amount} > ${maxLimit} THEN REJECT"
     * "WHEN ${riskLevel} = 'HIGH' ALERT ${supervisor}"
     * "CALCULATE ${price} * ${margin} + ${fee}"
     * </pre>
     * 
     * <p>Format des placeholders : ${nomVariable} pour une compatibilité
     * maximale avec les moteurs de template standards.</p>
     */
    @Size(max = 512, message = "Template must be less than 512 characters")
    private String template;

    /** 
     * Requête SQL principale associée à la règle.
     * 
     * <p>Requête SQL utilisée pour évaluer les conditions de la règle
     * ou récupérer les données nécessaires à son exécution. Cette requête
     * est généralement utilisée pour les validations complexes nécessitant
     * l'accès aux données historiques ou de référence.</p>
     * 
     * <p>Contraintes de sécurité : La requête est validée pour prévenir
     * les injections SQL et assurer la sécurité de l'application.</p>
     * 
     * <p>Exemples d'usage :</p>
     * <ul>
     *   <li>SELECT COUNT(*) FROM trades WHERE amount > ?</li>
     *   <li>SELECT AVG(price) FROM historical_prices WHERE date > ?</li>
     *   <li>SELECT risk_limit FROM client_limits WHERE client_id = ?</li>
     * </ul>
     */
    @Size(max = 125, message = "SQL string must be less than 125 characters")
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
     * 
     * <p>Exemples de fragments SQL :</p>
     * <pre>
     * "status = 'ACTIVE' AND created_date > ?"
     * "JOIN client_profile cp ON cp.id = c.profile_id"
     * "amount * rate + commission"
     * </pre>
     */
    @Size(max = 125, message = "SQL part must be less than 125 characters")
    private String sqlPart;

    /**
     * Constructeur de convenance pour la création rapide de règles.
     * 
     * <p>Ce constructeur permet de créer rapidement un DTO avec les champs
     * essentiels (nom et description) pour les tests unitaires et la création
     * rapide d'instances. Les autres champs peuvent être ajoutés ultérieurement
     * selon les besoins spécifiques de la règle.</p>
     * 
     * <p>Exemples d'usage :</p>
     * <pre>
     * // Règle simple de validation
     * RuleNameDTO creditRule = new RuleNameDTO("CreditCheck", "Validation du crédit client");
     * 
     * // Règle de limite de position  
     * RuleNameDTO positionRule = new RuleNameDTO("PositionLimit", "Contrôle des limites de position");
     * 
     * // Règle de calcul de marge
     * RuleNameDTO marginRule = new RuleNameDTO("MarginCalculation", "Calcul automatique des marges");
     * </pre>
     * 
     * @param name Le nom de la règle (identifiant métier)
     * @param description La description de la règle
     */
    public RuleNameDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Constructeur complet pour la création de règles avec configuration.
     * 
     * <p>Ce constructeur permet de créer une RuleName complète avec toutes
     * les informations de configuration, incluant les paramètres JSON et
     * les templates SQL. Utilisé principalement lors de l'import de règles
     * complexes ou de la migration depuis d'autres systèmes.</p>
     * 
     * <p>Exemple d'usage :</p>
     * <pre>
     * RuleNameDTO complexRule = new RuleNameDTO(
     *     "AdvancedRiskCheck",
     *     "Contrôle avancé du risque de contrepartie",
     *     "{\"maxExposure\": 5000000, \"currency\": \"EUR\"}",
     *     "IF ${exposure} > ${maxExposure} THEN REJECT TRANSACTION"
     * );
     * </pre>
     * 
     * @param name Le nom de la règle
     * @param description La description de la règle
     * @param json La configuration JSON
     * @param template Le template de la règle
     */
    public RuleNameDTO(String name, String description, String json, String template) {
        this.name = name;
        this.description = description;
        this.json = json;
        this.template = template;
    }

    /**
     * Vérifie si cette règle possède une configuration JSON.
     * 
     * <p>Méthode utilitaire qui détermine si la règle possède une configuration
     * JSON valide, indiquant qu'elle est paramétrable dynamiquement.</p>
     * 
     * @return true si une configuration JSON est présente, false sinon
     */
    public boolean hasJsonConfiguration() {
        return json != null && !json.trim().isEmpty();
    }

    /**
     * Vérifie si cette règle possède un template.
     * 
     * <p>Méthode utilitaire qui détermine si la règle possède un template,
     * indiquant qu'elle peut être réutilisée avec différents paramètres.</p>
     * 
     * @return true si un template est présent, false sinon
     */
    public boolean hasTemplate() {
        return template != null && !template.trim().isEmpty();
    }

    /**
     * Vérifie si cette règle possède des composants SQL.
     * 
     * <p>Méthode utilitaire qui détermine si la règle possède soit une requête
     * SQL complète soit un fragment SQL, indiquant qu'elle interagit avec
     * la base de données pour son exécution.</p>
     * 
     * @return true si des composants SQL sont présents, false sinon
     */
    public boolean hasSqlComponents() {
        return (sqlStr != null && !sqlStr.trim().isEmpty()) || 
               (sqlPart != null && !sqlPart.trim().isEmpty());
    }

    /**
     * Détermine le niveau de complexité de la règle.
     * 
     * <p>Méthode utilitaire qui évalue la complexité de la règle basée sur
     * les composants qu'elle contient. Utile pour la catégorisation et
     * l'analyse des règles dans l'interface d'administration.</p>
     * 
     * <p>Niveaux de complexité :</p>
     * <ul>
     *   <li><strong>BASIC</strong> : Nom et description uniquement</li>
     *   <li><strong>INTERMEDIATE</strong> : Avec JSON ou Template ou SQL</li>
     *   <li><strong>ADVANCED</strong> : Avec au moins 2 composants avancés</li>
     *   <li><strong>EXPERT</strong> : Avec JSON, Template et SQL</li>
     * </ul>
     * 
     * @return Le niveau de complexité de la règle
     */
    public String getComplexityLevel() {
        int complexityScore = 0;
        
        if (hasJsonConfiguration()) complexityScore++;
        if (hasTemplate()) complexityScore++;
        if (hasSqlComponents()) complexityScore++;
        
        switch (complexityScore) {
            case 0:
                return "BASIC";
            case 1:
                return "INTERMEDIATE";
            case 2:
                return "ADVANCED";
            case 3:
                return "EXPERT";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Génère un résumé lisible de la règle.
     * 
     * <p>Méthode utilitaire qui crée une description condensée de la règle
     * incluant ses principales caractéristiques. Utile pour les tooltips,
     * les logs et les interfaces d'administration.</p>
     * 
     * @return Résumé descriptif de la règle
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Rule: ").append(name != null ? name : "Unnamed");
        
        if (description != null && !description.trim().isEmpty()) {
            summary.append(" - ").append(description);
        }
        
        summary.append(" [").append(getComplexityLevel()).append("]");
        
        return summary.toString();
    }
}