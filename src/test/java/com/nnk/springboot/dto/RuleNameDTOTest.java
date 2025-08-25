package com.nnk.springboot.dto;

/**
 * Tests unitaires pour la classe RuleNameDTO.
 * 
 * <p>Cette classe de test vérifie :</p>
 * <ul>
 *   <li>La validation des données avec Bean Validation</li>
 *   <li>Les constructeurs (défaut, avec paramètres, tous paramètres)</li>
 *   <li>Les contraintes de validation sur les champs obligatoires</li>
 *   <li>Les contraintes de format pour les règles de négociation</li>
 *   <li>Les contraintes de taille sur les champs texte</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class RuleNameDTOTest {

    private final Validator validator;

    public RuleNameDTOTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validDTO_shouldHaveNoViolations() {
        RuleNameDTO dto = new RuleNameDTO(
            "CreditCheck",
            "Validation du crédit client",
            "{\"maxAmount\":1000}",
            "IF ${amount} > ${maxAmount} THEN REJECT"
        );
        dto.setSqlStr("SELECT * FROM trades");
        dto.setSqlPart("WHERE amount > 1000");

        Set<ConstraintViolation<RuleNameDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nameBlank_shouldFailValidation() {
        RuleNameDTO dto = new RuleNameDTO("", "desc");
        Set<ConstraintViolation<RuleNameDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void nameInvalidPattern_shouldFailValidation() {
        RuleNameDTO dto = new RuleNameDTO("_invalid", "desc");
        Set<ConstraintViolation<RuleNameDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void nameTooLong_shouldFailValidation() {
        String longName = "A".repeat(126);
        RuleNameDTO dto = new RuleNameDTO(longName, "desc");
        Set<ConstraintViolation<RuleNameDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void descriptionTooLong_shouldFailValidation() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "D".repeat(126));
        Set<ConstraintViolation<RuleNameDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void jsonTooLong_shouldFailValidation() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc", "J".repeat(126), "tpl");
        Set<ConstraintViolation<RuleNameDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("json")));
    }

    @Test
    void templateTooLong_shouldFailValidation() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc", "{}", "T".repeat(513));
        Set<ConstraintViolation<RuleNameDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("template")));
    }

    @Test
    void sqlStrTooLong_shouldFailValidation() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc");
        dto.setSqlStr("S".repeat(126));
        Set<ConstraintViolation<RuleNameDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sqlStr")));
    }

    @Test
    void sqlPartTooLong_shouldFailValidation() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc");
        dto.setSqlPart("P".repeat(126));
        Set<ConstraintViolation<RuleNameDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sqlPart")));
    }

    @Test
    void hasJsonConfiguration_shouldReturnTrueIfJsonPresent() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc", "{\"enabled\":true}", "tpl");
        assertTrue(dto.hasJsonConfiguration());
    }

    @Test
    void hasJsonConfiguration_shouldReturnFalseIfJsonEmpty() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc", "   ", "tpl");
        assertFalse(dto.hasJsonConfiguration());
    }

    @Test
    void hasTemplate_shouldReturnTrueIfTemplatePresent() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc", "{}", "tpl");
        assertTrue(dto.hasTemplate());
    }

    @Test
    void hasTemplate_shouldReturnFalseIfTemplateEmpty() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc", "{}", "   ");
        assertFalse(dto.hasTemplate());
    }

    @Test
    void hasSqlComponents_shouldReturnTrueIfSqlStrPresent() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc");
        dto.setSqlStr("SELECT *");
        assertTrue(dto.hasSqlComponents());
    }

    @Test
    void hasSqlComponents_shouldReturnTrueIfSqlPartPresent() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc");
        dto.setSqlPart("WHERE id=1");
        assertTrue(dto.hasSqlComponents());
    }

    @Test
    void hasSqlComponents_shouldReturnFalseIfNonePresent() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc");
        assertFalse(dto.hasSqlComponents());
    }

    @Test
    void getComplexityLevel_shouldReturnBasic() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc");
        assertEquals("BASIC", dto.getComplexityLevel());
    }

    @Test
    void getComplexityLevel_shouldReturnIntermediate() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc", "{\"enabled\":true}", null);
        assertEquals("INTERMEDIATE", dto.getComplexityLevel());
    }

    @Test
    void getComplexityLevel_shouldReturnAdvanced() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc", "{\"enabled\":true}", "tpl");
        assertEquals("ADVANCED", dto.getComplexityLevel());
    }

    @Test
    void getComplexityLevel_shouldReturnExpert() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc", "{\"enabled\":true}", "tpl");
        dto.setSqlStr("SELECT *");
        assertEquals("EXPERT", dto.getComplexityLevel());
    }

    @Test
    void getSummary_shouldReturnExpectedFormat() {
        RuleNameDTO dto = new RuleNameDTO("ValidName", "desc", "{\"enabled\":true}", "tpl");
        dto.setSqlStr("SELECT *");
        String summary = dto.getSummary();
        assertTrue(summary.contains("Rule: ValidName"));
        assertTrue(summary.contains("desc"));
        assertTrue(summary.contains("[EXPERT]"));
    }

    @Test
    void getSummary_shouldHandleNullNameAndDescription() {
        RuleNameDTO dto = new RuleNameDTO();
        String summary = dto.getSummary();
        assertTrue(summary.contains("Rule: Unnamed"));
        assertTrue(summary.contains("[BASIC]"));
    }
}
