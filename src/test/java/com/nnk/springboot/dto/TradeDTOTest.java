package com.nnk.springboot.dto;

/**
 * Tests unitaires pour la classe TradeDTO.
 * 
 * <p>Cette classe de test vérifie :</p>
 * <ul>
 *   <li>La validation des données avec Bean Validation</li>
 *   <li>Les constructeurs (défaut, avec paramètres, tous paramètres)</li>
 *   <li>Les contraintes de validation sur les champs obligatoires</li>
 *   <li>Les contraintes numériques pour les données de transaction</li>
 *   <li>Les contraintes de format sur les types et comptes</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe TradeDTO.
 * 
 * <p>Cette classe de tests vérifie le bon fonctionnement du DTO TradeDTO,
 * incluant les validations Bean Validation, les méthodes utilitaires
 * et la cohérence des données financières.</p>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@DisplayName("TradeDTO Tests")
public class TradeDTOTest {

    private Validator validator;
    private TradeDTO tradeDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        // Création d'un TradeDTO valide de base
        tradeDTO = new TradeDTO();
        tradeDTO.setAccount("TEST_ACCOUNT");
        tradeDTO.setType("BUY");
        tradeDTO.setBuyQuantity(1000.0);
        tradeDTO.setBuyPrice(50.25);
    }

    @Test
    @DisplayName("TradeDTO valide ne doit pas avoir d'erreurs de validation")
    void testValidTradeDTO() {
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Account ne peut pas être null ou vide")
    void testAccountValidation() {
        // Test avec account null
        tradeDTO.setAccount(null);
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Account is required"));
        
        // Test avec account vide
        tradeDTO.setAccount("");
        violations = validator.validate(tradeDTO);
        assertTrue(violations.size() >= 1); // Au moins une violation (peut être @NotBlank et @Pattern)
        
        // Test avec account contenant seulement des espaces
        tradeDTO.setAccount("   ");
        violations = validator.validate(tradeDTO);
        assertTrue(violations.size() >= 1); // Au moins une violation
    }

    @Test
    @DisplayName("Account doit respecter le pattern de validation")
    void testAccountPattern() {
        // Pattern invalide - commence par underscore
        tradeDTO.setAccount("_INVALID");
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("must start with alphanumeric"));
        
        // Pattern invalide - contient des minuscules
        tradeDTO.setAccount("invalid_account");
        violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        
        // Pattern invalide - contient des espaces
        tradeDTO.setAccount("INVALID ACCOUNT");
        violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        
        // Pattern valide
        tradeDTO.setAccount("VALID_ACCOUNT-123");
        violations = validator.validate(tradeDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Account ne peut pas dépasser 30 caractères")
    void testAccountLength() {
        tradeDTO.setAccount("A".repeat(31)); // 31 caractères
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("must be less than 30"));
        
        // Test à la limite
        tradeDTO.setAccount("A".repeat(30)); // 30 caractères
        violations = validator.validate(tradeDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Type ne peut pas être null ou vide")
    void testTypeValidation() {
        // Test avec type null
        tradeDTO.setType(null);
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Type is required"));
        
        // Test avec type vide
        tradeDTO.setType("");
        violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Type ne peut pas dépasser 30 caractères")
    void testTypeLength() {
        tradeDTO.setType("A".repeat(31)); // 31 caractères
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("must be less than 30"));
    }

    @Test
    @DisplayName("BuyQuantity doit être positive si spécifiée")
    void testBuyQuantityValidation() {
        // Test avec quantité négative
        tradeDTO.setBuyQuantity(-100.0);
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("must be positive"));
        
        // Test avec zéro
        tradeDTO.setBuyQuantity(0.0);
        violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        
        // Test avec valeur positive
        tradeDTO.setBuyQuantity(100.5);
        violations = validator.validate(tradeDTO);
        assertTrue(violations.isEmpty());
        
        // Test avec null (autorisé)
        tradeDTO.setBuyQuantity(null);
        violations = validator.validate(tradeDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("BuyPrice doit être positive si spécifiée")
    void testBuyPriceValidation() {
        // Test avec prix négatif
        tradeDTO.setBuyPrice(-10.0);
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("must be positive"));
        
        // Test avec prix très petit mais positif
        tradeDTO.setBuyPrice(0.0001);
        violations = validator.validate(tradeDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("SellQuantity et SellPrice suivent les mêmes règles")
    void testSellFieldsValidation() {
        tradeDTO.setBuyQuantity(null);
        tradeDTO.setBuyPrice(null);
        tradeDTO.setSellQuantity(500.0);
        tradeDTO.setSellPrice(60.75);
        
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertTrue(violations.isEmpty());
        
        // Test avec des valeurs négatives
        tradeDTO.setSellQuantity(-100.0);
        tradeDTO.setSellPrice(-50.0);
        violations = validator.validate(tradeDTO);
        assertEquals(2, violations.size());
    }

    @Test
    @DisplayName("Test des contraintes de précision décimale")
    void testDecimalPrecision() {
        // Test avec trop de décimales pour la quantité (max 2)
        tradeDTO.setBuyQuantity(100.123); // 3 décimales
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("2 decimal places"));
        
        // Test avec trop de décimales pour le prix (max 4)
        tradeDTO.setBuyQuantity(100.12); // OK
        tradeDTO.setBuyPrice(50.12345); // 5 décimales
        violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("4 decimal places"));
    }

    @Test
    @DisplayName("Test des longueurs de champs optionnels")
    void testOptionalFieldsLength() {
        tradeDTO.setSecurity("A".repeat(126)); // Dépasse 125
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
        
        tradeDTO.setSecurity("A".repeat(125)); // À la limite
        violations = validator.validate(tradeDTO);
        assertTrue(violations.isEmpty());
        
        tradeDTO.setStatus("VERYLONGSTATUS"); // Dépasse 10
        violations = validator.validate(tradeDTO);
        assertEquals(1, violations.size());
    }

    @Test
    @DisplayName("Test des constructeurs")
    void testConstructors() {
        // Constructeur par défaut
        TradeDTO emptyDTO = new TradeDTO();
        assertNull(emptyDTO.getAccount());
        assertNull(emptyDTO.getType());
        
        // Constructeur avec account et type
        TradeDTO basicDTO = new TradeDTO("ACC001", "SELL");
        assertEquals("ACC001", basicDTO.getAccount());
        assertEquals("SELL", basicDTO.getType());
        
        // Constructeur avec quantités
        TradeDTO fullDTO = new TradeDTO("ACC002", "BUY", 1000.0, null);
        assertEquals("ACC002", fullDTO.getAccount());
        assertEquals("BUY", fullDTO.getType());
        assertEquals(1000.0, fullDTO.getBuyQuantity());
        assertNull(fullDTO.getSellQuantity());
    }

    @Test
    @DisplayName("Test de la méthode hasBuyTransaction")
    void testHasBuyTransaction() {
        // Avec buyQuantity positive
        tradeDTO.setBuyQuantity(100.0);
        assertTrue(tradeDTO.hasBuyTransaction());
        
        // Avec buyQuantity null
        tradeDTO.setBuyQuantity(null);
        assertFalse(tradeDTO.hasBuyTransaction());
        
        // Avec buyQuantity zéro
        tradeDTO.setBuyQuantity(0.0);
        assertFalse(tradeDTO.hasBuyTransaction());
        
        // Avec buyQuantity négative
        tradeDTO.setBuyQuantity(-50.0);
        assertFalse(tradeDTO.hasBuyTransaction());
    }

    @Test
    @DisplayName("Test de la méthode hasSellTransaction")
    void testHasSellTransaction() {
        // Configuration pour un sell
        tradeDTO.setBuyQuantity(null);
        tradeDTO.setBuyPrice(null);
        tradeDTO.setSellQuantity(200.0);
        tradeDTO.setSellPrice(45.50);
        
        assertTrue(tradeDTO.hasSellTransaction());
        
        tradeDTO.setSellQuantity(null);
        assertFalse(tradeDTO.hasSellTransaction());
    }

    @Test
    @DisplayName("Test des calculs de valeur totale")
    void testTotalValueCalculations() {
        // Test getTotalBuyValue
        tradeDTO.setBuyQuantity(100.0);
        tradeDTO.setBuyPrice(25.50);
        assertEquals(2550.0, tradeDTO.getTotalBuyValue());
        
        // Test avec données manquantes
        tradeDTO.setBuyPrice(null);
        assertNull(tradeDTO.getTotalBuyValue());
        
        // Test getTotalSellValue
        tradeDTO.setBuyQuantity(null);
        tradeDTO.setBuyPrice(null);
        tradeDTO.setSellQuantity(50.0);
        tradeDTO.setSellPrice(30.75);
        assertEquals(1537.5, tradeDTO.getTotalSellValue());
    }

    @Test
    @DisplayName("Test de la méthode isComplete")
    void testIsComplete() {
        // Transaction complète avec achat
        tradeDTO.setAccount("TEST_ACC");
        tradeDTO.setType("BUY");
        tradeDTO.setBuyQuantity(100.0);
        tradeDTO.setBuyPrice(25.0);
        tradeDTO.setSellQuantity(null);
        tradeDTO.setSellPrice(null);
        
        assertTrue(tradeDTO.isComplete());
        
        // Transaction complète avec vente
        tradeDTO.setBuyQuantity(null);
        tradeDTO.setBuyPrice(null);
        tradeDTO.setSellQuantity(50.0);
        tradeDTO.setSellPrice(30.0);
        
        assertTrue(tradeDTO.isComplete());
        
        // Transaction incomplète - manque account
        tradeDTO.setAccount(null);
        assertFalse(tradeDTO.isComplete());
        
        // Transaction incomplète - manque opération
        tradeDTO.setAccount("TEST_ACC");
        tradeDTO.setSellQuantity(null);
        tradeDTO.setSellPrice(null);
        assertFalse(tradeDTO.isComplete());
    }

    @Test
    @DisplayName("Test de la méthode getSummary")
    void testGetSummary() {
        // Transaction avec achat
        tradeDTO.setAccount("TEST_ACCOUNT");
        tradeDTO.setType("BUY");
        tradeDTO.setBuyQuantity(100.0);
        tradeDTO.setBuyPrice(25.5);
        tradeDTO.setSecurity("AAPL");
        
        String summary = tradeDTO.getSummary();
        assertTrue(summary.contains("TEST_ACCOUNT"));
        assertTrue(summary.contains("BUY"));
        assertTrue(summary.contains("BUY: 100.0"));
        assertTrue(summary.contains("@ 25.5"));
        assertTrue(summary.contains("(AAPL)"));
        
        // Transaction avec vente
        tradeDTO.setBuyQuantity(null);
        tradeDTO.setBuyPrice(null);
        tradeDTO.setSellQuantity(50.0);
        tradeDTO.setSellPrice(30.0);
        tradeDTO.setSecurity(null);
        
        summary = tradeDTO.getSummary();
        assertTrue(summary.contains("SELL: 50.0"));
        assertTrue(summary.contains("@ 30.0"));
        assertFalse(summary.contains("AAPL"));
    }

    @Test
    @DisplayName("Test avec dates")
    void testWithDates() {
        LocalDateTime now = LocalDateTime.now();
        tradeDTO.setTradeDate(now);
        tradeDTO.setCreationDate(now);
        tradeDTO.setRevisionDate(now.plusHours(1));
        
        assertEquals(now, tradeDTO.getTradeDate());
        assertEquals(now, tradeDTO.getCreationDate());
        assertEquals(now.plusHours(1), tradeDTO.getRevisionDate());
        
        // Pas de validation temporelle dans le DTO (sera fait dans le service)
        Set<ConstraintViolation<TradeDTO>> violations = validator.validate(tradeDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test des méthodes equals et hashCode")
    void testEqualsAndHashCode() {
        TradeDTO dto1 = new TradeDTO("ACC001", "BUY");
        dto1.setTradeId(1);
        
        TradeDTO dto2 = new TradeDTO("ACC001", "BUY");
        dto2.setTradeId(1);
        
        // Lombok génère automatiquement equals et hashCode
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        
        // Différence sur un champ
        dto2.setType("SELL");
        assertNotEquals(dto1, dto2);
    }
}