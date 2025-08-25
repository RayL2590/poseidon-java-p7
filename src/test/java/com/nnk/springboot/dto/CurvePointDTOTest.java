package com.nnk.springboot.dto;

/**
 * Tests unitaires pour la classe CurvePointDTO.
 * 
 * <p>Cette classe de test vérifie :</p>
 * <ul>
 *   <li>La validation des données avec Bean Validation</li>
 *   <li>Les constructeurs (défaut, avec paramètres, tous paramètres)</li>
 *   <li>Les contraintes de validation sur les champs obligatoires</li>
 *   <li>Les contraintes numériques (valeurs positives, décimales)</li>
 *   <li>Les contraintes spécifiques aux points de courbe financiers</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe CurvePointDTO.
 * 
 * <p>Cette classe de test vérifie :</p>
 * <ul>
 *   <li>La validation des données financières avec Bean Validation</li>
 *   <li>Les constructeurs (défaut, avec paramètres, tous paramètres)</li>
 *   <li>Les contraintes sur les champs obligatoires (curveId, term, value)</li>
 *   <li>Les contraintes numériques (valeurs positives, précision décimale)</li>
 *   <li>Les cas spécifiques aux courbes financières</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 */
class CurvePointDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validation_WithValidFinancialData_ShouldPass() {
        // Given - Point de courbe de taux USD à 1 an avec taux 2.5%
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(1);
        dto.setTerm(1.0000);  // 1 an
        dto.setValue(2.5000); // 2.5% de taux

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Valid financial data should not produce validation errors");
    }

    @Test
    void validation_WithValidShortTermData_ShouldPass() {
        // Given - Point de courbe à 3 mois avec volatilité
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(2);
        dto.setTerm(0.2500);  // 3 mois (trimestre)
        dto.setValue(18.7500); // 18.75% de volatilité

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Valid short-term financial data should not produce validation errors");
    }

    @Test
    void validation_WithValidNegativeValue_ShouldPass() {
        // Given - Point avec taux négatif (cas réel en finance)
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(3);
        dto.setTerm(0.5000);   // 6 mois
        dto.setValue(-0.5000); // Taux négatif de -0.5%

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Valid negative values should be allowed for financial curves");
    }

    @Test
    void validation_WithNullCurveId_ShouldFail() {
        // Given
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(null);
        dto.setTerm(1.0000);
        dto.setValue(2.5000);

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Null curveId should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Curve ID is mandatory")),
                "Should contain curve ID mandatory error message");
    }

    @Test
    void validation_WithNegativeCurveId_ShouldFail() {
        // Given
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(-1);    // ID négatif invalide
        dto.setTerm(1.0000);
        dto.setValue(2.5000);

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Negative curveId should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Curve ID must be positive")),
                "Should contain curve ID positive error message");
    }

    @Test
    void validation_WithZeroCurveId_ShouldFail() {
        // Given
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(0);     // ID zéro invalide (doit être >= 1)
        dto.setTerm(1.0000);
        dto.setValue(2.5000);

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Zero curveId should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Curve ID must be positive")),
                "Should contain curve ID positive error message");
    }

    @Test
    void validation_WithNullTerm_ShouldFail() {
        // Given
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(1);
        dto.setTerm(null);     // Terme manquant
        dto.setValue(2.5000);

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Null term should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Term is mandatory")),
                "Should contain term mandatory error message");
    }

    @Test
    void validation_WithNegativeTerm_ShouldFail() {
        // Given
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(1);
        dto.setTerm(-0.5000);  // Terme négatif invalide
        dto.setValue(2.5000);

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Negative term should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Term must be positive or zero")),
                "Should contain term positive error message");
    }

    @Test
    void validation_WithZeroTerm_ShouldPass() {
        // Given - Terme zéro valide (overnight rate)
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(1);
        dto.setTerm(0.0000);   // Overnight/spot
        dto.setValue(1.5000);

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Zero term should be valid for overnight rates");
    }

    @Test
    void validation_WithNullValue_ShouldFail() {
        // Given
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(1);
        dto.setTerm(1.0000);
        dto.setValue(null);    // Valeur manquante

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Null value should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Value is mandatory")),
                "Should contain value mandatory error message");
    }

    @Test
    void validation_WithTooManyDecimals_ShouldFail() {
        // Given - Valeur avec 5 décimales (dépasse la limite de 4)
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(1);
        dto.setTerm(1.0000);
        dto.setValue(2.50001); // 5 décimales, dépasse la limite

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Too many decimal places should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Value must be a valid number with max 4 decimal places")),
                "Should contain decimal places error message for value");
    }

    @Test
    void validation_WithTooManyDecimalsInTerm_ShouldFail() {
        // Given - Terme avec 5 décimales (dépasse la limite de 4)
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(1);
        dto.setTerm(1.00001);  // 5 décimales, dépasse la limite
        dto.setValue(2.5000);

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Too many decimal places in term should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Term must be a valid number with max 4 decimal places")),
                "Should contain decimal places error message for term");
    }

    @Test
    void constructor_WithParameters_ShouldSetFields() {
        // Given
        Integer curveId = 1;
        Double term = 1.0000;
        Double value = 2.5000;

        // When
        CurvePointDTO dto = new CurvePointDTO(curveId, term, value);

        // Then
        assertEquals(curveId, dto.getCurveId(), "CurveId should be set correctly");
        assertEquals(term, dto.getTerm(), "Term should be set correctly");
        assertEquals(value, dto.getValue(), "Value should be set correctly");
        assertNull(dto.getId(), "Id should be null");
    }

    @Test
    void noArgsConstructor_ShouldWork() {
        // When
        CurvePointDTO dto = new CurvePointDTO();

        // Then
        assertNotNull(dto, "DTO should be created");
        assertNull(dto.getId(), "Id should be null");
        assertNull(dto.getCurveId(), "CurveId should be null");
        assertNull(dto.getTerm(), "Term should be null");
        assertNull(dto.getValue(), "Value should be null");
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        // Given
        Integer id = 1;
        Integer curveId = 2;
        Double term = 0.5000;
        Double value = 18.7500;

        // When
        CurvePointDTO dto = new CurvePointDTO(id, curveId, term, value);

        // Then
        assertEquals(id, dto.getId(), "Id should be set correctly");
        assertEquals(curveId, dto.getCurveId(), "CurveId should be set correctly");
        assertEquals(term, dto.getTerm(), "Term should be set correctly");
        assertEquals(value, dto.getValue(), "Value should be set correctly");
    }

    @Test
    void validation_WithPreciseFinancialCalculations_ShouldPass() {
        // Given - Test avec des valeurs financières réalistes précises
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(1);
        dto.setTerm(0.0833);   // 1 mois exact (1/12)
        dto.setValue(1.7500);  // Taux précis à 4 décimales

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Precise financial calculations should be valid");
    }

    @Test
    void validation_WithLongTermMaturity_ShouldPass() {
        // Given - Point de courbe à 30 ans (long terme)
        CurvePointDTO dto = new CurvePointDTO();
        dto.setCurveId(1);
        dto.setTerm(30.0000);  // 30 ans
        dto.setValue(4.2500);  // Taux long terme

        // When
        Set<ConstraintViolation<CurvePointDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Long-term maturity should be valid");
    }
}
