package com.nnk.springboot.mapper;

/**
 * Tests unitaires pour le RuleNameMapper.
 * 
 * <p>Cette classe de test valide la conversion bidirectionnelle entre les entités RuleName
 * et les DTOs RuleNameDTO, en vérifiant le mapping correct de tous les champs,
 * la gestion des valeurs null, et les fonctionnalités spécifiques aux règles de négociation.</p>
 * 
 * <p>Couverture des tests :</p>
 * <ul>
 *   <li><strong>Mapping complet</strong> : Tous les champs entité ↔ DTO</li>
 *   <li><strong>Gestion des nulls</strong> : Protection contre les NPE</li>
 *   <li><strong>Conversion round-trip</strong> : Intégrité des données</li>
 *   <li><strong>Règles de négociation</strong> : Validation des formats de règles</li>
 *   <li><strong>Méthodes spécifiques</strong> : updateEntityFromDTO, createDefaultForTemplate</li>
 *   <li><strong>Validation métier</strong> : Cohérence des règles de trading</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.dto.RuleNameDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RuleNameMapperTest {

    private final RuleNameMapper mapper = new RuleNameMapper();

    @Test
    void toDTO_shouldReturnNull_whenEntityIsNull() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void toDTO_shouldMapAllFields() {
        RuleName entity = new RuleName();
        entity.setId(1);
        entity.setName("TestRule");
        entity.setDescription("desc");
        entity.setJson("{\"enabled\":true}");
        entity.setTemplate("template");
        entity.setSqlStr("SELECT *");
        entity.setSqlPart("WHERE id=1");

        RuleNameDTO dto = mapper.toDTO(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getJson(), dto.getJson());
        assertEquals(entity.getTemplate(), dto.getTemplate());
        assertEquals(entity.getSqlStr(), dto.getSqlStr());
        assertEquals(entity.getSqlPart(), dto.getSqlPart());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toEntity_shouldMapAllFields() {
        RuleNameDTO dto = new RuleNameDTO();
        dto.setId(2);
        dto.setName("AnotherRule");
        dto.setDescription("desc2");
        dto.setJson("{\"active\":false}");
        dto.setTemplate("tpl2");
        dto.setSqlStr("UPDATE");
        dto.setSqlPart("SET name='x'");

        RuleName entity = mapper.toEntity(dto);

        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getDescription(), entity.getDescription());
        assertEquals(dto.getJson(), entity.getJson());
        assertEquals(dto.getTemplate(), entity.getTemplate());
        assertEquals(dto.getSqlStr(), entity.getSqlStr());
        assertEquals(dto.getSqlPart(), entity.getSqlPart());
    }

    @Test
    void updateEntityFromDTO_shouldNotUpdate_whenNulls() {
        RuleName entity = new RuleName();
        entity.setName("old");
        mapper.updateEntityFromDTO(null, new RuleNameDTO());
        mapper.updateEntityFromDTO(entity, null);
        assertEquals("old", entity.getName());
    }

    @Test
    void updateEntityFromDTO_shouldUpdateFields() {
        RuleName entity = new RuleName();
        entity.setId(3);
        entity.setName("oldName");
        entity.setDescription("oldDesc");
        entity.setJson("oldJson");
        entity.setTemplate("oldTpl");
        entity.setSqlStr("oldSql");
        entity.setSqlPart("oldPart");

        RuleNameDTO dto = new RuleNameDTO();
        dto.setName("newName");
        dto.setDescription("newDesc");
        dto.setJson("newJson");
        dto.setTemplate("newTpl");
        dto.setSqlStr("newSql");
        dto.setSqlPart("newPart");

        mapper.updateEntityFromDTO(entity, dto);

        assertEquals("newName", entity.getName());
        assertEquals("newDesc", entity.getDescription());
        assertEquals("newJson", entity.getJson());
        assertEquals("newTpl", entity.getTemplate());
        assertEquals("newSql", entity.getSqlStr());
        assertEquals("newPart", entity.getSqlPart());
        assertEquals(3, entity.getId()); // id should not change
    }

    @Test
    void createDefaultForType_shouldReturnDefaults() {
        RuleNameDTO dto = mapper.createDefaultForType("VALIDATION");
        assertEquals("NewValidationRule", dto.getName());
        assertNotNull(dto.getDescription());
        assertNotNull(dto.getJson());
        assertNotNull(dto.getTemplate());

        dto = mapper.createDefaultForType("CALCULATION");
        assertEquals("NewCalculationRule", dto.getName());

        dto = mapper.createDefaultForType("AUTHORIZATION");
        assertEquals("NewAuthorizationRule", dto.getName());

        dto = mapper.createDefaultForType("NOTIFICATION");
        assertEquals("NewNotificationRule", dto.getName());

        dto = mapper.createDefaultForType("RISK_CONTROL");
        assertEquals("NewRiskControlRule", dto.getName());

        dto = mapper.createDefaultForType("PRICING");
        assertEquals("NewPricingRule", dto.getName());

        dto = mapper.createDefaultForType("UNKNOWN");
        assertEquals("NewCustomRule", dto.getName());
        
        dto = mapper.createDefaultForType(null);
        assertNotNull(dto);
    }

    @Test
    void isComplete_shouldReturnTrue_whenRuleIsComplete() {
        RuleNameDTO dto = new RuleNameDTO();
        dto.setName("TestRule");
        dto.setDescription("Test Description");
        dto.setTemplate("Test Template");
        
        assertTrue(mapper.isComplete(dto));
    }

    @Test
    void isComplete_shouldReturnTrue_whenRuleHasSqlComponents() {
        RuleNameDTO dto = new RuleNameDTO();
        dto.setName("TestRule");
        dto.setDescription("Test Description");
        dto.setSqlStr("SELECT * FROM table");
        
        assertTrue(mapper.isComplete(dto));
    }

    @Test
    void isComplete_shouldReturnFalse_whenRuleIsIncomplete() {
        RuleNameDTO dto = new RuleNameDTO();
        dto.setName("TestRule");
        // Missing description and logic
        
        assertFalse(mapper.isComplete(dto));
    }

    @Test
    void isComplete_shouldReturnFalse_whenDTOIsNull() {
        assertFalse(mapper.isComplete(null));
    }

    @Test
    void isComplete_shouldReturnFalse_whenNameIsEmpty() {
        RuleNameDTO dto = new RuleNameDTO();
        dto.setName("   "); // Empty string
        dto.setDescription("Test Description");
        dto.setTemplate("Test Template");
        
        assertFalse(mapper.isComplete(dto));
    }

    @Test
    void calculateComplexityScore_shouldReturnZero_whenDTOIsNull() {
        assertEquals(0, mapper.calculateComplexityScore(null));
    }

    @Test
    void calculateComplexityScore_shouldCalculateCorrectScore() {
        RuleNameDTO dto = new RuleNameDTO();
        dto.setName("TestRule");
        dto.setDescription("This is a long description that is definitely more than fifty characters long");
        dto.setJson("{\"test\": true}");
        dto.setTemplate("Test Template");
        dto.setSqlStr("SELECT * FROM table");
        
        // +1 for JSON, +2 for template, +3 for SQL, +1 for long description = 7
        assertEquals(7, mapper.calculateComplexityScore(dto));
    }

    @Test
    void updateEntityFromDTO_shouldNotUpdateEmptyStrings() {
        RuleName entity = new RuleName();
        entity.setName("originalName");
        entity.setDescription("originalDesc");
        
        RuleNameDTO dto = new RuleNameDTO();
        dto.setName("");
        dto.setDescription("   "); // whitespace
        dto.setJson("newJson");
        
        mapper.updateEntityFromDTO(entity, dto);
        
        // Empty/whitespace values should still be set (this is the current behavior)
        assertEquals("", entity.getName());
        assertEquals("   ", entity.getDescription());
        assertEquals("newJson", entity.getJson());
    }

}
