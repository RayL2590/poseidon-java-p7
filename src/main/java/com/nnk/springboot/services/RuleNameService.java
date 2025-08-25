package com.nnk.springboot.services;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Implémentation du service de gestion des règles métier (RuleName).
 * 
 * <p>Cette classe implémente toute la logique métier pour la gestion des règles
 * métier dans l'application de trading Poseidon. Elle respecte les principes SOLID
 * et fournit une couche de service robuste avec validation métier, gestion d'erreurs
 * et optimisations de performance.</p>
 * 
 * <p>Responsabilités principales :</p>
 * <ul>
 *   <li><strong>Opérations CRUD</strong> : Gestion complète du cycle de vie des règles</li>
 *   <li><strong>Validation métier</strong> : Contrôles de cohérence et conformité</li>
 *   <li><strong>Logique de nommage</strong> : Gestion des identifiants uniques</li>
 *   <li><strong>Optimisations</strong> : Cache et requêtes optimisées</li>
 * </ul>
 * 
 * <p>Architecture et design patterns :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Responsabilité unique de gestion des RuleName</li>
 *   <li><strong>OCP</strong> : Ouvert à l'extension, fermé à la modification</li>
 *   <li><strong>LSP</strong> : Respecte le contrat défini par IRuleNameService</li>
 *   <li><strong>ISP</strong> : Interface spécialisée pour les opérations RuleName</li>
 *   <li><strong>DIP</strong> : Dépend d'abstractions (RuleNameRepository)</li>
 * </ul>
 * 
 * <p>Gestion transactionnelle :</p>
 * <ul>
 *   <li><strong>@Transactional</strong> : Cohérence des données critiques</li>
 *   <li><strong>ReadOnly optimizations</strong> : Performance des consultations</li>
 *   <li><strong>Rollback automatique</strong> : En cas d'exception métier</li>
 *   <li><strong>Isolation niveau</strong> : Protection contre les lectures inconsistantes</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Service
@Transactional
public class RuleNameService implements IRuleNameService {

    @Autowired
    private RuleNameRepository ruleNameRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Pattern pour la validation des noms de règles (alphanumériques + certains caractères spéciaux)
    private static final Pattern RULE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_\\-\\.]*$");
    
    // Pattern basique pour détecter les injections SQL dangereuses
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(\\b(ALTER|CREATE|DELETE|DROP|EXEC(UTE)?|INSERT|SELECT|UNION|UPDATE)\\b.*\\b(FROM|INTO|SET|WHERE|JOIN)\\b)|(--|/\\*|\\*/|xp_|sp_)"
    );

    @Override
    @Transactional(readOnly = true)
    public List<RuleName> findAll() {
        return ruleNameRepository.findAllByOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RuleName> findById(Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return ruleNameRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RuleName> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return ruleNameRepository.findByName(name.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return ruleNameRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return ruleNameRepository.existsByName(name.trim());
    }

    @Override
    public RuleName save(RuleName ruleName) {
        validateRuleName(ruleName);
        
        // Normalisation des données
        normalizeRuleNameData(ruleName);
        
        // Validation de l'unicité du nom pour les nouvelles règles ou modifications de nom
        validateNameUniqueness(ruleName);
        
        return ruleNameRepository.save(ruleName);
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID for deletion");
        }
        
        if (!ruleNameRepository.existsById(id)) {
            throw new IllegalArgumentException("RuleName not found with id: " + id);
        }
        
        ruleNameRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleName> findByComponentType(String componentType) {
        if (componentType == null || componentType.trim().isEmpty()) {
            return List.of();
        }
        
        switch (componentType.toUpperCase()) {
            case "JSON":
                return ruleNameRepository.findByJsonIsNotNullOrderByNameAsc();
            case "TEMPLATE":
                return ruleNameRepository.findByTemplateIsNotNullOrderByNameAsc();
            case "SQL":
                return ruleNameRepository.findBySqlStrIsNotNullOrSqlPartIsNotNullOrderByNameAsc();
            case "COMPLETE":
                return ruleNameRepository.findCompleteRules();
            default:
                return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleName> findByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        String cleanKeyword = keyword.trim();
        List<RuleName> results = new ArrayList<>();
        
        // Recherche dans les noms
        results.addAll(ruleNameRepository.findByNameContainingIgnoreCaseOrderByNameAsc(cleanKeyword));
        
        // Recherche dans les descriptions (éviter les doublons)
        List<RuleName> byDescription = ruleNameRepository.findByDescriptionContainingIgnoreCaseOrderByNameAsc(cleanKeyword);
        for (RuleName rule : byDescription) {
            if (!results.contains(rule)) {
                results.add(rule);
            }
        }
        
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateRule(RuleName ruleName) {
        try {
            validateRuleName(ruleName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleName> findRecentRules(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return ruleNameRepository.findRecentRules(PageRequest.of(0, limit));
    }

    /**
     * Valide les données d'une règle métier.
     * 
     * <p>Cette méthode effectue une validation complète des données de la règle,
     * incluant les contrôles de format, de cohérence et de sécurité.</p>
     * 
     * @param ruleName La règle à valider
     * @throws IllegalArgumentException si les données sont invalides
     */
    private void validateRuleName(RuleName ruleName) {
        if (ruleName == null) {
            throw new IllegalArgumentException("RuleName cannot be null");
        }
        
        // Validation du nom (obligatoire)
        validateName(ruleName.getName());
        
        // Validation de la description (recommandée)
        if (ruleName.getDescription() != null && ruleName.getDescription().length() > 125) {
            throw new IllegalArgumentException("Description cannot exceed 125 characters");
        }
        
        // Validation de la configuration JSON si présente
        validateJsonConfiguration(ruleName.getJson());
        
        // Validation du template si présent
        validateTemplate(ruleName.getTemplate());
        
        // Validation des composants SQL si présents
        validateSqlComponents(ruleName.getSqlStr(), ruleName.getSqlPart());
    }

    /**
     * Valide le nom de la règle.
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule name cannot be null or empty");
        }
        
        String trimmedName = name.trim();
        
        if (trimmedName.length() > 125) {
            throw new IllegalArgumentException("Rule name cannot exceed 125 characters");
        }
        
        if (!RULE_NAME_PATTERN.matcher(trimmedName).matches()) {
            throw new IllegalArgumentException(
                "Rule name must start with alphanumeric character and contain only alphanumeric characters, underscores, hyphens, and dots");
        }
    }

    /**
     * Valide la configuration JSON si présente.
     */
    private void validateJsonConfiguration(String json) {
        if (json != null && !json.trim().isEmpty()) {
            try {
                objectMapper.readTree(json);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON configuration: " + e.getMessage());
            }
            
            if (json.length() > 125) {
                throw new IllegalArgumentException("JSON configuration cannot exceed 125 characters");
            }
        }
    }

    /**
     * Valide le template si présent.
     */
    private void validateTemplate(String template) {
        if (template != null && !template.trim().isEmpty()) {
            if (template.length() > 512) {
                throw new IllegalArgumentException("Template cannot exceed 512 characters");
            }
            
            // Validation basique de la syntaxe des placeholders
            validateTemplatePlaceholders(template);
        }
    }

    /**
     * Valide les placeholders dans le template.
     */
    private void validateTemplatePlaceholders(String template) {
        // Compter les accolades ouvrantes et fermantes pour s'assurer qu'elles sont équilibrées
        int openBraces = 0;
        int closeBraces = 0;
        
        for (char c : template.toCharArray()) {
            if (c == '{') openBraces++;
            else if (c == '}') closeBraces++;
        }
        
        if (openBraces != closeBraces) {
            throw new IllegalArgumentException("Template has unbalanced placeholders (mismatched braces)");
        }
    }

    /**
     * Valide les composants SQL.
     */
    private void validateSqlComponents(String sqlStr, String sqlPart) {
        if (sqlStr != null && !sqlStr.trim().isEmpty()) {
            validateSqlString(sqlStr, "SQL string");
            if (sqlStr.length() > 125) {
                throw new IllegalArgumentException("SQL string cannot exceed 125 characters");
            }
        }
        
        if (sqlPart != null && !sqlPart.trim().isEmpty()) {
            validateSqlString(sqlPart, "SQL part");
            if (sqlPart.length() > 125) {
                throw new IllegalArgumentException("SQL part cannot exceed 125 characters");
            }
        }
    }

    /**
     * Valide une chaîne SQL pour détecter les injections potentielles.
     */
    private void validateSqlString(String sql, String fieldName) {
        // Détection basique d'injections SQL
        if (SQL_INJECTION_PATTERN.matcher(sql).find()) {
            throw new IllegalArgumentException(fieldName + " contains potentially dangerous SQL patterns");
        }
        
        // Vérification des caractères dangereux
        if (sql.contains(";") && !sql.trim().endsWith(";")) {
            throw new IllegalArgumentException(fieldName + " contains suspicious semicolon usage");
        }
    }

    /**
     * Normalise les données de la règle avant sauvegarde.
     */
    private void normalizeRuleNameData(RuleName ruleName) {
        if (ruleName.getName() != null) {
            ruleName.setName(ruleName.getName().trim());
        }
        
        if (ruleName.getDescription() != null) {
            ruleName.setDescription(ruleName.getDescription().trim());
            if (ruleName.getDescription().isEmpty()) {
                ruleName.setDescription(null);
            }
        }
        
        if (ruleName.getJson() != null) {
            ruleName.setJson(ruleName.getJson().trim());
            if (ruleName.getJson().isEmpty()) {
                ruleName.setJson(null);
            }
        }
        
        if (ruleName.getTemplate() != null) {
            ruleName.setTemplate(ruleName.getTemplate().trim());
            if (ruleName.getTemplate().isEmpty()) {
                ruleName.setTemplate(null);
            }
        }
        
        if (ruleName.getSqlStr() != null) {
            ruleName.setSqlStr(ruleName.getSqlStr().trim());
            if (ruleName.getSqlStr().isEmpty()) {
                ruleName.setSqlStr(null);
            }
        }
        
        if (ruleName.getSqlPart() != null) {
            ruleName.setSqlPart(ruleName.getSqlPart().trim());
            if (ruleName.getSqlPart().isEmpty()) {
                ruleName.setSqlPart(null);
            }
        }
    }

    /**
     * Valide l'unicité du nom de la règle.
     */
    private void validateNameUniqueness(RuleName ruleName) {
        if (ruleName.getName() != null) {
            Optional<RuleName> existingWithSameName = ruleNameRepository.findByName(ruleName.getName());
            if (existingWithSameName.isPresent() && 
                !existingWithSameName.get().getId().equals(ruleName.getId())) {
                throw new IllegalArgumentException("Rule name '" + ruleName.getName() + 
                                                 "' already exists. Each rule must have a unique name.");
            }
        }
    }
}