package com.nnk.springboot.dto;

/**
 * Tests unitaires pour la classe RatingDTO.
 * 
 * <p>Cette classe de test vérifie :</p>
 * <ul>
 *   <li>La validation des données avec Bean Validation</li>
 *   <li>Les constructeurs (défaut, avec paramètres, tous paramètres)</li>
 *   <li>Les contraintes de validation sur les champs obligatoires</li>
 *   <li>Les contraintes de format pour les notations financières</li>
 *   <li>Les contraintes numériques spécifiques aux ratings</li>
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
 * Tests unitaires pour la classe RatingDTO.
 * 
 * <p>Cette classe de test verifie :</p>
 * <ul>
 *   <li>La validation des notations de credit avec Bean Validation</li>
 *   <li>Les constructeurs (defaut, avec 3 parametres, avec 4 parametres, tous parametres)</li>
 *   <li>Les contraintes de validation sur les formats de notation (Moody's, S&P, Fitch)</li>
 *   <li>Les contraintes numeriques (orderNumber positif)</li>
 *   <li>Les contraintes de taille (125 caracteres max)</li>
 *   <li>Les methodes metier (isInvestmentGrade, isInvestmentGradeMoodys, etc.)</li>
 *   <li>Les cas specifiques aux notations financieres internationales</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */
class RatingDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Tests de validation - cas valides

    @Test
    void validation_WithValidInvestmentGradeRatings_ShouldPass() {
        // Given - Notation Investment Grade complete (AAA/Aaa)
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Aaa");
        dto.setSandPRating("AAA");
        dto.setFitchRating("AAA");
        dto.setOrderNumber(1);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Valid investment grade ratings should not produce validation errors");
    }

    @Test
    void validation_WithValidSpeculativeGradeRatings_ShouldPass() {
        // Given - Notation Speculative Grade (Ba1/BB+)
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Ba1");
        dto.setSandPRating("BB+");
        dto.setFitchRating("BB+");
        dto.setOrderNumber(11);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Valid speculative grade ratings should not produce validation errors");
    }

    @Test
    void validation_WithPartialRatings_ShouldPass() {
        // Given - Seules certaines agences ont donne une notation (cas realiste)
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("A2");
        dto.setSandPRating("A");
        dto.setFitchRating(null); // Pas de notation Fitch
        dto.setOrderNumber(6);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Partial ratings should be valid");
    }

    @Test
    void validation_WithEmptyRatings_ShouldPass() {
        // Given - Aucune notation fournie, seulement order number
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("");
        dto.setSandPRating("");
        dto.setFitchRating("");
        dto.setOrderNumber(5);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Empty ratings should be valid (optional fields)");
    }

    @Test
    void validation_WithLowestInvestmentGrade_ShouldPass() {
        // Given - Notation Investment Grade la plus basse (Baa3/BBB-)
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Baa3");
        dto.setSandPRating("BBB-");
        dto.setFitchRating("BBB-");
        dto.setOrderNumber(12);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Lowest investment grade ratings should be valid");
    }

    @Test
    void validation_WithHighestSpeculativeGrade_ShouldPass() {
        // Given - Notation Speculative Grade la plus elevee (Ba1/BB+)
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Ba1");
        dto.setSandPRating("BB+");
        dto.setFitchRating("BB+");
        dto.setOrderNumber(13);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Highest speculative grade ratings should be valid");
    }

    @Test
    void validation_WithDefaultRatings_ShouldPass() {
        // Given - Notations de defaut (C/D)
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("C");
        dto.setSandPRating("D");
        dto.setFitchRating("D");
        dto.setOrderNumber(21);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Default ratings should be valid");
    }

    // Tests de validation - cas invalides

    @Test
    void validation_WithInvalidMoodysFormat_ShouldFail() {
        // Given - Format Moody's invalide (utilise format S&P)
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("AAA");  // Format S&P dans champ Moody's
        dto.setSandPRating("AAA");
        dto.setFitchRating("AAA");
        dto.setOrderNumber(1);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Invalid Moody's format should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Moody's rating must follow standard format (e.g., Aaa, Aa1, A2, Baa3, Ba1, B2, Caa1, Ca, C)")),
                "Should contain Moody's format error message");
    }

    @Test
    void validation_WithInvalidSPFormat_ShouldFail() {
        // Given - Format S&P invalide (utilise format Moody's)
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Aaa");
        dto.setSandPRating("Aaa");   // Format Moody's dans champ S&P
        dto.setFitchRating("AAA");
        dto.setOrderNumber(1);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Invalid S&P format should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("S&P rating must follow standard format (e.g., AAA, AA+, A-, BBB, BB+, B-, CCC, D)")),
                "Should contain S&P format error message");
    }

    @Test
    void validation_WithInvalidFitchFormat_ShouldFail() {
        // Given - Format Fitch invalide
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Aaa");
        dto.setSandPRating("AAA");
        dto.setFitchRating("A1");    // Format Moody's dans champ Fitch
        dto.setOrderNumber(1);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Invalid Fitch format should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Fitch rating must follow standard format (e.g., AAA, AA+, A-, BBB, BB+, B-, CCC, D)")),
                "Should contain Fitch format error message");
    }

    @Test
    void validation_WithInvalidOrderNumber_ShouldFail() {
        // Given - Order number negatif ou zero
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Aaa");
        dto.setSandPRating("AAA");
        dto.setFitchRating("AAA");
        dto.setOrderNumber(0);       // Invalide: doit etre >= 1

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Zero order number should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Order number must be positive")),
                "Should contain order number positive error message");
    }

    @Test
    void validation_WithNegativeOrderNumber_ShouldFail() {
        // Given - Order number negatif
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Aaa");
        dto.setSandPRating("AAA");
        dto.setFitchRating("AAA");
        dto.setOrderNumber(-1);      // Invalide: negatif

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Negative order number should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Order number must be positive")),
                "Should contain order number positive error message");
    }

    @Test
    void validation_WithTooLongMoodysRating_ShouldFail() {
        // Given - Notation Moody's trop longue (> 125 caracteres)
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("A".repeat(126)); // 126 caracteres, depasse la limite
        dto.setSandPRating("AAA");
        dto.setFitchRating("AAA");
        dto.setOrderNumber(1);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Too long Moody's rating should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Moody's rating must be less than 125 characters")),
                "Should contain Moody's size error message");
    }

    @Test
    void validation_WithTooLongSPRating_ShouldFail() {
        // Given - Notation S&P trop longue
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Aaa");
        dto.setSandPRating("A".repeat(126)); // 126 caracteres, depasse la limite
        dto.setFitchRating("AAA");
        dto.setOrderNumber(1);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Too long S&P rating should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("S&P rating must be less than 125 characters")),
                "Should contain S&P size error message");
    }

    @Test
    void validation_WithTooLongFitchRating_ShouldFail() {
        // Given - Notation Fitch trop longue
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Aaa");
        dto.setSandPRating("AAA");
        dto.setFitchRating("A".repeat(126)); // 126 caracteres, depasse la limite
        dto.setOrderNumber(1);

        // When
        Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Too long Fitch rating should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Fitch rating must be less than 125 characters")),
                "Should contain Fitch size error message");
    }

    // Tests des constructeurs

    @Test
    void noArgsConstructor_ShouldWork() {
        // When
        RatingDTO dto = new RatingDTO();

        // Then
        assertNotNull(dto, "DTO should be created");
        assertNull(dto.getId(), "Id should be null");
        assertNull(dto.getMoodysRating(), "MoodysRating should be null");
        assertNull(dto.getSandPRating(), "SandPRating should be null");
        assertNull(dto.getFitchRating(), "FitchRating should be null");
        assertNull(dto.getOrderNumber(), "OrderNumber should be null");
    }

    @Test
    void constructor_WithThreeRatings_ShouldSetFields() {
        // Given
        String moodysRating = "A1";
        String sandPRating = "A+";
        String fitchRating = "A+";

        // When
        RatingDTO dto = new RatingDTO(moodysRating, sandPRating, fitchRating);

        // Then
        assertEquals(moodysRating, dto.getMoodysRating(), "MoodysRating should be set correctly");
        assertEquals(sandPRating, dto.getSandPRating(), "SandPRating should be set correctly");
        assertEquals(fitchRating, dto.getFitchRating(), "FitchRating should be set correctly");
        assertNull(dto.getId(), "Id should be null");
        assertNull(dto.getOrderNumber(), "OrderNumber should be null");
    }

    @Test
    void constructor_WithFourParameters_ShouldSetFields() {
        // Given
        String moodysRating = "Baa2";
        String sandPRating = "BBB";
        String fitchRating = "BBB";
        Integer orderNumber = 9;

        // When
        RatingDTO dto = new RatingDTO(moodysRating, sandPRating, fitchRating, orderNumber);

        // Then
        assertEquals(moodysRating, dto.getMoodysRating(), "MoodysRating should be set correctly");
        assertEquals(sandPRating, dto.getSandPRating(), "SandPRating should be set correctly");
        assertEquals(fitchRating, dto.getFitchRating(), "FitchRating should be set correctly");
        assertEquals(orderNumber, dto.getOrderNumber(), "OrderNumber should be set correctly");
        assertNull(dto.getId(), "Id should be null");
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        // Given
        Integer id = 1;
        String moodysRating = "Ba2";
        String sandPRating = "BB";
        String fitchRating = "BB";
        Integer orderNumber = 14;

        // When
        RatingDTO dto = new RatingDTO(id, moodysRating, sandPRating, fitchRating, orderNumber);

        // Then
        assertEquals(id, dto.getId(), "Id should be set correctly");
        assertEquals(moodysRating, dto.getMoodysRating(), "MoodysRating should be set correctly");
        assertEquals(sandPRating, dto.getSandPRating(), "SandPRating should be set correctly");
        assertEquals(fitchRating, dto.getFitchRating(), "FitchRating should be set correctly");
        assertEquals(orderNumber, dto.getOrderNumber(), "OrderNumber should be set correctly");
    }

    // Tests des methodes metier

    @Test
    void isInvestmentGrade_WithAllInvestmentGradeRatings_ShouldReturnTrue() {
        // Given - Toutes les notations sont Investment Grade
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("A2");
        dto.setSandPRating("A");
        dto.setFitchRating("A");

        // When & Then
        assertTrue(dto.isInvestmentGrade(), "Should be Investment Grade when all ratings are IG");
    }

    @Test
    void isInvestmentGrade_WithOnlyMoodysInvestmentGrade_ShouldReturnTrue() {
        // Given - Seule la notation Moody's est Investment Grade
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Baa3");  // Investment Grade
        dto.setSandPRating("BB+");    // Speculative Grade
        dto.setFitchRating("BB+");    // Speculative Grade

        // When & Then
        assertTrue(dto.isInvestmentGrade(), "Should be Investment Grade when any rating is IG");
    }

    @Test
    void isInvestmentGrade_WithOnlySPInvestmentGrade_ShouldReturnTrue() {
        // Given - Seule la notation S&P est Investment Grade
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Ba1");   // Speculative Grade
        dto.setSandPRating("BBB-");   // Investment Grade
        dto.setFitchRating("BB+");    // Speculative Grade

        // When & Then
        assertTrue(dto.isInvestmentGrade(), "Should be Investment Grade when S&P is IG");
    }

    @Test
    void isInvestmentGrade_WithOnlyFitchInvestmentGrade_ShouldReturnTrue() {
        // Given - Seule la notation Fitch est Investment Grade
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Ba1");   // Speculative Grade
        dto.setSandPRating("BB+");    // Speculative Grade
        dto.setFitchRating("BBB");    // Investment Grade

        // When & Then
        assertTrue(dto.isInvestmentGrade(), "Should be Investment Grade when Fitch is IG");
    }

    @Test
    void isInvestmentGrade_WithAllSpeculativeGradeRatings_ShouldReturnFalse() {
        // Given - Toutes les notations sont Speculative Grade
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Ba2");
        dto.setSandPRating("BB");
        dto.setFitchRating("BB");

        // When & Then
        assertFalse(dto.isInvestmentGrade(), "Should be Speculative Grade when all ratings are SG");
    }

    @Test
    void isInvestmentGrade_WithNullRatings_ShouldReturnFalse() {
        // Given - Toutes les notations sont null
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating(null);
        dto.setSandPRating(null);
        dto.setFitchRating(null);

        // When & Then
        assertFalse(dto.isInvestmentGrade(), "Should return false when all ratings are null");
    }

    @Test
    void isInvestmentGrade_WithEmptyRatings_ShouldReturnFalse() {
        // Given - Toutes les notations sont vides
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("");
        dto.setSandPRating("");
        dto.setFitchRating("");

        // When & Then
        assertFalse(dto.isInvestmentGrade(), "Should return false when all ratings are empty");
    }

    // Tests specifiques par agence

    @Test
    void validation_WithValidMoodysRatings_ShouldPass() {
        // Given - Test de tous les formats Moody's valides
        String[] validMoodysRatings = {
            "Aaa", "Aa1", "Aa2", "Aa3", "A1", "A2", "A3",
            "Baa1", "Baa2", "Baa3", "Ba1", "Ba2", "Ba3",
            "B1", "B2", "B3", "Caa1", "Caa2", "Caa3", "Ca", "C"
        };

        for (String rating : validMoodysRatings) {
            RatingDTO dto = new RatingDTO();
            dto.setMoodysRating(rating);
            dto.setOrderNumber(1);

            Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), 
                "Valid Moody's rating '" + rating + "' should not produce validation errors");
        }
    }

    @Test
    void validation_WithValidSPRatings_ShouldPass() {
        // Given - Test de tous les formats S&P valides
        String[] validSPRatings = {
            "AAA", "AA+", "AA", "AA-", "A+", "A", "A-",
            "BBB+", "BBB", "BBB-", "BB+", "BB", "BB-",
            "B+", "B", "B-", "CCC+", "CCC", "CCC-", "CC", "C", "D"
        };

        for (String rating : validSPRatings) {
            RatingDTO dto = new RatingDTO();
            dto.setSandPRating(rating);
            dto.setOrderNumber(1);

            Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), 
                "Valid S&P rating '" + rating + "' should not produce validation errors");
        }
    }

    @Test
    void validation_WithValidFitchRatings_ShouldPass() {
        // Given - Test de tous les formats Fitch valides (identiques a S&P)
        String[] validFitchRatings = {
            "AAA", "AA+", "AA", "AA-", "A+", "A", "A-",
            "BBB+", "BBB", "BBB-", "BB+", "BB", "BB-",
            "B+", "B", "B-", "CCC+", "CCC", "CCC-", "CC", "C", "D"
        };

        for (String rating : validFitchRatings) {
            RatingDTO dto = new RatingDTO();
            dto.setFitchRating(rating);
            dto.setOrderNumber(1);

            Set<ConstraintViolation<RatingDTO>> violations = validator.validate(dto);

            assertTrue(violations.isEmpty(), 
                "Valid Fitch rating '" + rating + "' should not produce validation errors");
        }
    }

    // Tests de coherence metier

    @Test
    void isInvestmentGrade_WithBorderlineMoodysRatings_ShouldBeCorrect() {
        // Given & When & Then - Test des notations limites Moody's
        assertTrue(new RatingDTO("Baa3", null, null).isInvestmentGrade(), 
            "Baa3 should be Investment Grade (lowest IG for Moody's)");
        assertFalse(new RatingDTO("Ba1", null, null).isInvestmentGrade(), 
            "Ba1 should be Speculative Grade (highest SG for Moody's)");
    }

    @Test
    void isInvestmentGrade_WithBorderlineSPRatings_ShouldBeCorrect() {
        // Given & When & Then - Test des notations limites S&P
        assertTrue(new RatingDTO(null, "BBB-", null).isInvestmentGrade(), 
            "BBB- should be Investment Grade (lowest IG for S&P)");
        assertFalse(new RatingDTO(null, "BB+", null).isInvestmentGrade(), 
            "BB+ should be Speculative Grade (highest SG for S&P)");
    }

    @Test
    void isInvestmentGrade_WithBorderlineFitchRatings_ShouldBeCorrect() {
        // Given & When & Then - Test des notations limites Fitch
        assertTrue(new RatingDTO(null, null, "BBB-").isInvestmentGrade(), 
            "BBB- should be Investment Grade (lowest IG for Fitch)");
        assertFalse(new RatingDTO(null, null, "BB+").isInvestmentGrade(), 
            "BB+ should be Speculative Grade (highest SG for Fitch)");
    }

    @Test
    void isInvestmentGrade_WithMixedBorderlineRatings_ShouldReturnTrue() {
        // Given - Melange de notations a la frontiere IG/SG
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Ba1");    // Speculative Grade
        dto.setSandPRating("BBB-");    // Investment Grade (limite basse)
        dto.setFitchRating("BB+");     // Speculative Grade (limite haute)

        // When & Then
        assertTrue(dto.isInvestmentGrade(), 
            "Should be Investment Grade when at least one rating is IG");
    }
}