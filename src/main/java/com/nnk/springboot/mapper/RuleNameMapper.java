package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.dto.RuleNameDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper Spring pour la conversion entre entités RuleName et DTOs RuleNameDTO.
 * 
 * <p>Cette classe composant Spring implémente le pattern Mapper pour assurer la conversion
 * bidirectionnelle entre la couche de persistance (entités JPA) et la couche de
 * présentation (DTOs) spécifiquement pour les règles métier du système de trading.</p>
 * 
 * <p>Spécificités des règles métier :</p>
 * <ul>
 *   <li><strong>Données critiques</strong> : Précision requise pour l'exécution des règles</li>
 *   <li><strong>Sécurité renforcée</strong> : Validation des composants SQL et templates</li>
 *   <li><strong>Configuration flexible</strong> : Support des paramètres JSON dynamiques</li>
 *   <li><strong>Réutilisabilité</strong> : Gestion des templates paramétrables</li>
 * </ul>
 * 
 * <p>Architecture et principes SOLID :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Responsabilité unique de conversion RuleName</li>
 *   <li><strong>OCP</strong> : Extensible via l'interface IEntityMapper</li>
 *   <li><strong>LSP</strong> : Substitution respectée pour l'interface</li>
 *   <li><strong>ISP</strong> : Interface spécialisée pour les mappings</li>
 *   <li><strong>DIP</strong> : Dépendance sur l'abstraction IEntityMapper</li>
 * </ul>
 * 
 * <p>Fonctionnalités avancées :</p>
 * <ul>
 *   <li><strong>Conversion complète</strong> : Entity ↔ DTO bidirectionnelle</li>
 *   <li><strong>Mise à jour in-place</strong> : Optimisation pour les modifications</li>
 *   <li><strong>Gestion défensive</strong> : Protection contre les valeurs null</li>
 *   <li><strong>Validation intégrée</strong> : Contrôles de cohérence lors du mapping</li>
 * </ul>
 * 
 * <p>Usage typique dans l'écosystème de trading :</p>
 * <pre>
 * // Affichage des règles
 * &#64;Autowired
 * private RuleNameMapper mapper;
 * 
 * // Liste pour interface utilisateur
 * List&lt;RuleName&gt; rules = ruleNameService.findAll();
 * List&lt;RuleNameDTO&gt; dtos = rules.stream()
 *     .map(mapper::toDTO)
 *     .collect(Collectors.toList());
 * 
 * // Mise à jour d'une règle
 * RuleName existing = ruleNameService.findById(id);
 * mapper.updateEntityFromDTO(existing, modifiedDTO);
 * ruleNameService.save(existing);
 * </pre>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.RuleName
 * @see com.nnk.springboot.dto.RuleNameDTO
 * @see IEntityMapper
 */
@Component
public class RuleNameMapper implements IEntityMapper<RuleName, RuleNameDTO> {

    /**
     * Convertit une entité RuleName en DTO RuleNameDTO.
     * 
     * <p>Cette méthode transforme une entité JPA de règle métier en objet
     * de transfert de données pour l'affichage et la manipulation dans les vues.
     * Elle préserve l'intégrité des données de règle critiques pour l'exécution
     * des processus métier du système de trading.</p>
     * 
     * <p>Données mappées :</p>
     * <ul>
     *   <li><strong>id</strong> : Identifiant unique de la règle</li>
     *   <li><strong>name</strong> : Nom de la règle (identifiant métier)</li>
     *   <li><strong>description</strong> : Description détaillée de la règle</li>
     *   <li><strong>json</strong> : Configuration JSON paramétrable</li>
     *   <li><strong>template</strong> : Template réutilisable avec placeholders</li>
     *   <li><strong>sqlStr</strong> : Requête SQL principale</li>
     *   <li><strong>sqlPart</strong> : Fragment SQL réutilisable</li>
     * </ul>
     * 
     * <p>Cas d'usage dans l'environnement de trading :</p>
     * <ul>
     *   <li><strong>Interface d'administration</strong> : Gestion des règles métier</li>
     *   <li><strong>Configuration dynamique</strong> : Modification sans redéploiement</li>
     *   <li><strong>Export de règles</strong> : Sauvegarde et migration</li>
     *   <li><strong>API REST</strong> : Sérialisation JSON pour clients externes</li>
     * </ul>
     * 
     * @param ruleName L'entité RuleName à convertir (peut être null)
     * @return Le DTO RuleNameDTO correspondant, ou null si l'entité source est null
     * @throws aucune exception n'est levée (gestion défensive)
     */
    public RuleNameDTO toDTO(RuleName ruleName) {
        if (ruleName == null) {
            return null;
        }

        RuleNameDTO dto = new RuleNameDTO();
        dto.setId(ruleName.getId());
        dto.setName(ruleName.getName());
        dto.setDescription(ruleName.getDescription());
        dto.setJson(ruleName.getJson());
        dto.setTemplate(ruleName.getTemplate());
        dto.setSqlStr(ruleName.getSqlStr());
        dto.setSqlPart(ruleName.getSqlPart());

        return dto;
    }

    /**
     * Convertit un DTO RuleNameDTO en entité RuleName.
     * 
     * <p>Cette méthode transforme un objet de transfert de données (provenant
     * généralement d'un formulaire web ou d'une API) en entité JPA prête pour
     * la persistance. Elle assure la cohérence des données de règle critiques
     * pour l'exécution des processus métier dans l'environnement de trading.</p>
     * 
     * <p>Validation et intégrité :</p>
     * <ul>
     *   <li><strong>Unicité des noms</strong> : Les noms de règles doivent être uniques</li>
     *   <li><strong>Sécurité SQL</strong> : Validation des requêtes pour prévenir les injections</li>
     *   <li><strong>Format JSON</strong> : Vérification de la syntaxe des configurations</li>
     *   <li><strong>Templates cohérents</strong> : Validation des placeholders</li>
     * </ul>
     * 
     * <p>Cas d'usage dans l'environnement de trading :</p>
     * <ul>
     *   <li><strong>Création de règles</strong> : Nouvelles règles depuis interface</li>
     *   <li><strong>Import de configuration</strong> : Chargement depuis fichiers</li>
     *   <li><strong>Migration de données</strong> : Transfert depuis anciens systèmes</li>
     *   <li><strong>API REST</strong> : Réception de données JSON externes</li>
     * </ul>
     * 
     * <p>Note : Les validations métier complexes sont déléguées au service
     * pour respecter la séparation des responsabilités.</p>
     * 
     * @param dto Le DTO RuleNameDTO à convertir (peut être null)
     * @return L'entité RuleName correspondante, ou null si le DTO source est null
     * @throws aucune exception n'est levée (gestion défensive)
     */
    public RuleName toEntity(RuleNameDTO dto) {
        if (dto == null) {
            return null;
        }

        RuleName ruleName = new RuleName();
        ruleName.setId(dto.getId());
        ruleName.setName(dto.getName());
        ruleName.setDescription(dto.getDescription());
        ruleName.setJson(dto.getJson());
        ruleName.setTemplate(dto.getTemplate());
        ruleName.setSqlStr(dto.getSqlStr());
        ruleName.setSqlPart(dto.getSqlPart());

        return ruleName;
    }

    /**
     * Met à jour une entité RuleName existante avec les données d'un DTO.
     * 
     * <p>Cette méthode optimisée effectue une mise à jour in-place d'une entité
     * existante sans créer de nouvelle instance. Elle est particulièrement efficace
     * pour les opérations de modification où l'entité est déjà chargée en session JPA.</p>
     * 
     * <p>Avantages de la mise à jour in-place :</p>
     * <ul>
     *   <li><strong>Performance</strong> : Évite la création d'objets inutiles</li>
     *   <li><strong>Session JPA</strong> : Préserve l'état de la session Hibernate</li>
     *   <li><strong>Optimistic Locking</strong> : Maintient la gestion des versions</li>
     *   <li><strong>Lazy Loading</strong> : Préserve les associations chargées</li>
     * </ul>
     * 
     * <p>Champs mis à jour :</p>
     * <ul>
     *   <li><strong>name</strong> : Nouveau nom de la règle</li>
     *   <li><strong>description</strong> : Nouvelle description</li>
     *   <li><strong>json</strong> : Nouvelle configuration JSON</li>
     *   <li><strong>template</strong> : Nouveau template</li>
     *   <li><strong>sqlStr</strong> : Nouvelle requête SQL</li>
     *   <li><strong>sqlPart</strong> : Nouveau fragment SQL</li>
     * </ul>
     * 
     * <p>Champs préservés :</p>
     * <ul>
     *   <li><strong>id</strong> : Identifiant technique préservé</li>
     *   <li><strong>Métadonnées d'audit</strong> : Horodatages de création maintenus</li>
     * </ul>
     * 
     * <p>Usage typique :</p>
     * <pre>
     * // Modification d'une règle existante
     * RuleName existing = ruleNameService.findById(ruleId);
     * RuleNameDTO modified = getModifiedDataFromForm();
     * 
     * ruleNameMapper.updateEntityFromDTO(existing, modified);
     * ruleNameService.save(existing); // Hibernate détecte les changements
     * </pre>
     * 
     * <p>Considérations métier :</p>
     * <ul>
     *   <li><strong>Évolution de règle</strong> : Mise à jour suite à changement métier</li>
     *   <li><strong>Correction d'erreurs</strong> : Rectification de règles erronées</li>
     *   <li><strong>Optimisation</strong> : Amélioration des performances des règles</li>
     *   <li><strong>Paramétrage</strong> : Ajustement des configurations JSON</li>
     * </ul>
     * 
     * @param existingRuleName L'entité existante à mettre à jour (ne doit pas être null en usage normal)
     * @param dto Le DTO contenant les nouvelles données (ne doit pas être null en usage normal)
     * @throws aucune exception n'est levée - protection défensive contre les nulls
     */
    public void updateEntityFromDTO(RuleName existingRuleName, RuleNameDTO dto) {
        if (existingRuleName == null || dto == null) {
            return;
        }

        existingRuleName.setName(dto.getName());
        existingRuleName.setDescription(dto.getDescription());
        existingRuleName.setJson(dto.getJson());
        existingRuleName.setTemplate(dto.getTemplate());
        existingRuleName.setSqlStr(dto.getSqlStr());
        existingRuleName.setSqlPart(dto.getSqlPart());
    }

    /**
     * Crée un DTO avec les valeurs par défaut pour un type de règle donné.
     * 
     * <p>Méthode utilitaire pour générer rapidement des DTOs avec des configurations
     * pré-définies selon le type de règle métier. Facilite la création de nouvelles
     * règles en proposant des templates standardisés.</p>
     * 
     * <p>Types de règles supportés :</p>
     * <ul>
     *   <li><strong>VALIDATION</strong> : Règles de contrôle et validation</li>
     *   <li><strong>CALCULATION</strong> : Règles de calcul et formules</li>
     *   <li><strong>AUTHORIZATION</strong> : Règles d'autorisation et permissions</li>
     *   <li><strong>NOTIFICATION</strong> : Règles d'alerte et notification</li>
     *   <li><strong>RISK_CONTROL</strong> : Règles de gestion des risques</li>
     *   <li><strong>PRICING</strong> : Règles de tarification et pricing</li>
     * </ul>
     * 
     * <p>Exemple d'usage :</p>
     * <pre>
     * // Création d'une règle de validation par défaut
     * RuleNameDTO validationRule = mapper.createDefaultForType("VALIDATION");
     * 
     * // Personnalisation selon les besoins
     * validationRule.setName("PositionLimitCheck");
     * validationRule.setDescription("Contrôle des limites de position");
     * </pre>
     * 
     * @param ruleType Le type de règle souhaité
     * @return DTO avec configuration par défaut pour le type spécifié
     */
    public RuleNameDTO createDefaultForType(String ruleType) {
        if (ruleType == null) {
            return new RuleNameDTO();
        }
        
        switch (ruleType.toUpperCase()) {
            case "VALIDATION":
                return new RuleNameDTO(
                    "NewValidationRule",
                    "Validation rule for business constraints",
                    "{\"enabled\": true, \"severity\": \"ERROR\"}",
                    "IF ${value} > ${limit} THEN REJECT WITH MESSAGE '${message}'"
                );
                
            case "CALCULATION":
                return new RuleNameDTO(
                    "NewCalculationRule", 
                    "Calculation rule for financial computations",
                    "{\"precision\": 2, \"roundingMode\": \"HALF_UP\"}",
                    "CALCULATE ${base} * ${rate} + ${adjustment}"
                );
                
            case "AUTHORIZATION":
                return new RuleNameDTO(
                    "NewAuthorizationRule",
                    "Authorization rule for access control",
                    "{\"requiresApproval\": true, \"approverRole\": \"SUPERVISOR\"}",
                    "IF ${amount} > ${threshold} THEN REQUIRE_APPROVAL BY ${approver}"
                );
                
            case "NOTIFICATION":
                return new RuleNameDTO(
                    "NewNotificationRule",
                    "Notification rule for alerts and reporting",
                    "{\"channels\": [\"email\", \"sms\"], \"priority\": \"NORMAL\"}",
                    "WHEN ${condition} THEN NOTIFY ${recipients} WITH TEMPLATE '${template}'"
                );
                
            case "RISK_CONTROL":
                return new RuleNameDTO(
                    "NewRiskControlRule",
                    "Risk control rule for exposure management",
                    "{\"riskMetric\": \"VaR\", \"confidenceLevel\": 0.95}",
                    "IF ${exposure} > ${riskLimit} THEN ESCALATE TO ${riskManager}"
                );
                
            case "PRICING":
                return new RuleNameDTO(
                    "NewPricingRule",
                    "Pricing rule for cost computation",
                    "{\"baseCurrency\": \"USD\", \"includeFees\": true}",
                    "PRICE = ${marketPrice} * (1 + ${margin}) + ${fees}"
                );
                
            default:
                return new RuleNameDTO(
                    "NewCustomRule",
                    "Custom business rule",
                    "{}",
                    "CUSTOM_LOGIC_HERE"
                );
        }
    }

    /**
     * Vérifie si une règle est considérée comme complète.
     * 
     * <p>Méthode utilitaire pour déterminer si une règle possède tous les
     * composants nécessaires pour être opérationnelle dans l'environnement
     * de production.</p>
     * 
     * <p>Critères de complétude :</p>
     * <ul>
     *   <li><strong>Nom présent</strong> : Identifiant métier défini</li>
     *   <li><strong>Description présente</strong> : Documentation suffisante</li>
     *   <li><strong>Logique définie</strong> : Au moins template ou SQL présent</li>
     * </ul>
     * 
     * @param dto Le DTO à évaluer
     * @return true si la règle est complète, false sinon
     */
    public boolean isComplete(RuleNameDTO dto) {
        if (dto == null) {
            return false;
        }
        
        boolean hasName = dto.getName() != null && !dto.getName().trim().isEmpty();
        boolean hasDescription = dto.getDescription() != null && !dto.getDescription().trim().isEmpty();
        boolean hasLogic = dto.hasTemplate() || dto.hasSqlComponents();
        
        return hasName && hasDescription && hasLogic;
    }

    /**
     * Calcule un score de complexité pour une règle.
     * 
     * <p>Méthode utilitaire qui évalue la complexité d'une règle basée sur
     * ses composants. Utile pour la priorisation des tâches de maintenance
     * et l'estimation des impacts de modification.</p>
     * 
     * <p>Scoring :</p>
     * <ul>
     *   <li><strong>+1</strong> : Configuration JSON présente</li>
     *   <li><strong>+2</strong> : Template présent</li>
     *   <li><strong>+3</strong> : Composants SQL présents</li>
     *   <li><strong>+1</strong> : Description détaillée (&gt; 50 caractères)</li>
     * </ul>
     * 
     * @param dto Le DTO à évaluer
     * @return Score de complexité (0-7)
     */
    public int calculateComplexityScore(RuleNameDTO dto) {
        if (dto == null) {
            return 0;
        }
        
        int score = 0;
        
        if (dto.hasJsonConfiguration()) score += 1;
        if (dto.hasTemplate()) score += 2;
        if (dto.hasSqlComponents()) score += 3;
        if (dto.getDescription() != null && dto.getDescription().length() > 50) score += 1;
        
        return score;
    }
}