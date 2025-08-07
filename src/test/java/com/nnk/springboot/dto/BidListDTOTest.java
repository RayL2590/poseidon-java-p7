package com.nnk.springboot.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe BidListDTO.
 * 
 * <p>Cette classe de test vérifie :</p>
 * <ul>
 *   <li>La validation des données avec Bean Validation</li>
 *   <li>Les constructeurs (défaut, avec paramètres, tous paramètres)</li>
 *   <li>Les contraintes de validation sur les champs obligatoires</li>
 *   <li>Les contraintes de taille et de format</li>
 *   <li>Les contraintes numériques (valeurs positives, décimales)</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 */
class BidListDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validation_WithValidData_ShouldPass() {
        // Given
        BidListDTO dto = new BidListDTO();
        dto.setAccount("TRADING_ACCOUNT_001");
        dto.setType("EQUITY");
        dto.setBidQuantity(1000.50);
        dto.setAskQuantity(950.25);
        dto.setBid(125.50);
        dto.setAsk(125.75);
        dto.setBenchmark("S&P500");
        dto.setCommentary("Valid trading offer");
        dto.setSecurity("AAPL");
        dto.setStatus("ACTIVE");
        dto.setTrader("John Doe");
        dto.setBook("BOOK_001");
        dto.setCreationName("Creator");
        dto.setRevisionName("Revisor");
        dto.setDealName("Deal_001");
        dto.setDealType("SPOT");
        dto.setSourceListId("SOURCE_001");
        dto.setSide("BUY");

        // When
        Set<ConstraintViolation<BidListDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty(), "Valid data should not produce validation errors");
    }

    @Test
    void validation_WithNullAccount_ShouldFail() {
        // Given
        BidListDTO dto = new BidListDTO();
        dto.setAccount(null);
        dto.setType("EQUITY");

        // When
        Set<ConstraintViolation<BidListDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Null account should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Account is mandatory")),
                "Should contain account mandatory error message");
    }

    @Test
    void validation_WithBlankAccount_ShouldFail() {
        // Given
        BidListDTO dto = new BidListDTO();
        dto.setAccount("   ");  // Blank string
        dto.setType("EQUITY");

        // When
        Set<ConstraintViolation<BidListDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Blank account should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Account is mandatory")),
                "Should contain account mandatory error message");
    }

    @Test
    void validation_WithTooLongAccount_ShouldFail() {
        // Given
        BidListDTO dto = new BidListDTO();
        dto.setAccount("A".repeat(31));  // 31 characters, exceeds limit of 30
        dto.setType("EQUITY");

        // When
        Set<ConstraintViolation<BidListDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Too long account should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Account must be less than 30 characters")),
                "Should contain account size error message");
    }

    @Test
    void validation_WithNullType_ShouldFail() {
        // Given
        BidListDTO dto = new BidListDTO();
        dto.setAccount("ACCOUNT_001");
        dto.setType(null);

        // When
        Set<ConstraintViolation<BidListDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Null type should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Type is mandatory")),
                "Should contain type mandatory error message");
    }

    @Test
    void validation_WithNegativeBidQuantity_ShouldFail() {
        // Given
        BidListDTO dto = new BidListDTO();
        dto.setAccount("ACCOUNT_001");
        dto.setType("EQUITY");
        dto.setBidQuantity(-100.0);  // Negative value

        // When
        Set<ConstraintViolation<BidListDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Negative bid quantity should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Bid quantity must be positive or zero")),
                "Should contain bid quantity positive error message");
    }

    @Test
    void validation_WithNegativeAsk_ShouldFail() {
        // Given
        BidListDTO dto = new BidListDTO();
        dto.setAccount("ACCOUNT_001");
        dto.setType("EQUITY");
        dto.setAsk(-50.0);  // Negative value

        // When
        Set<ConstraintViolation<BidListDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Negative ask should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Ask must be positive or zero")),
                "Should contain ask positive error message");
    }

    @Test
    void validation_WithInvalidDecimalPlaces_ShouldFail() {
        // Given
        BidListDTO dto = new BidListDTO();
        dto.setAccount("ACCOUNT_001");
        dto.setType("EQUITY");
        dto.setBidQuantity(100.123);  // 3 decimal places, exceeds limit of 2

        // When
        Set<ConstraintViolation<BidListDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty(), "Invalid decimal places should produce validation error");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Bid quantity must be a valid number with max 2 decimal places")),
                "Should contain decimal places error message");
    }

    @Test
    void constructor_WithParameters_ShouldSetFields() {
        // Given
        String account = "ACCOUNT_001";
        String type = "EQUITY";
        Double bidQuantity = 1000.0;

        // When
        BidListDTO dto = new BidListDTO(account, type, bidQuantity);

        // Then
        assertEquals(account, dto.getAccount(), "Account should be set correctly");
        assertEquals(type, dto.getType(), "Type should be set correctly");
        assertEquals(bidQuantity, dto.getBidQuantity(), "Bid quantity should be set correctly");
        assertNull(dto.getBidListId(), "BidListId should be null");
        assertNull(dto.getAskQuantity(), "AskQuantity should be null");
        assertNull(dto.getBid(), "Bid should be null");
        assertNull(dto.getAsk(), "Ask should be null");
    }

    @Test
    void noArgsConstructor_ShouldWork() {
        // When
        BidListDTO dto = new BidListDTO();

        // Then
        assertNotNull(dto, "DTO should be created");
        assertNull(dto.getBidListId(), "BidListId should be null");
        assertNull(dto.getAccount(), "Account should be null");
        assertNull(dto.getType(), "Type should be null");
        assertNull(dto.getBidQuantity(), "BidQuantity should be null");
        assertNull(dto.getAskQuantity(), "AskQuantity should be null");
        assertNull(dto.getBid(), "Bid should be null");
        assertNull(dto.getAsk(), "Ask should be null");
        assertNull(dto.getBenchmark(), "Benchmark should be null");
        assertNull(dto.getCommentary(), "Commentary should be null");
        assertNull(dto.getSecurity(), "Security should be null");
        assertNull(dto.getStatus(), "Status should be null");
        assertNull(dto.getTrader(), "Trader should be null");
        assertNull(dto.getBook(), "Book should be null");
        assertNull(dto.getCreationName(), "CreationName should be null");
        assertNull(dto.getRevisionName(), "RevisionName should be null");
        assertNull(dto.getDealName(), "DealName should be null");
        assertNull(dto.getDealType(), "DealType should be null");
        assertNull(dto.getSourceListId(), "SourceListId should be null");
        assertNull(dto.getSide(), "Side should be null");
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        // Given
        Integer bidListId = 1;
        String account = "ACCOUNT_001";
        String type = "EQUITY";
        Double bidQuantity = 1000.0;
        Double askQuantity = 950.0;
        Double bid = 125.50;
        Double ask = 125.75;
        String benchmark = "S&P500";
        String commentary = "Test commentary";
        String security = "AAPL";
        String status = "ACTIVE";
        String trader = "John Doe";
        String book = "BOOK_001";
        String creationName = "Creator";
        String revisionName = "Revisor";
        String dealName = "Deal_001";
        String dealType = "SPOT";
        String sourceListId = "SOURCE_001";
        String side = "BUY";

        // When
        BidListDTO dto = new BidListDTO(
            bidListId, account, type, bidQuantity, askQuantity, bid, ask,
            benchmark, commentary, security, status, trader, book,
            creationName, revisionName, dealName, dealType, sourceListId, side
        );

        // Then
        assertEquals(bidListId, dto.getBidListId(), "BidListId should be set correctly");
        assertEquals(account, dto.getAccount(), "Account should be set correctly");
        assertEquals(type, dto.getType(), "Type should be set correctly");
        assertEquals(bidQuantity, dto.getBidQuantity(), "BidQuantity should be set correctly");
        assertEquals(askQuantity, dto.getAskQuantity(), "AskQuantity should be set correctly");
        assertEquals(bid, dto.getBid(), "Bid should be set correctly");
        assertEquals(ask, dto.getAsk(), "Ask should be set correctly");
        assertEquals(benchmark, dto.getBenchmark(), "Benchmark should be set correctly");
        assertEquals(commentary, dto.getCommentary(), "Commentary should be set correctly");
        assertEquals(security, dto.getSecurity(), "Security should be set correctly");
        assertEquals(status, dto.getStatus(), "Status should be set correctly");
        assertEquals(trader, dto.getTrader(), "Trader should be set correctly");
        assertEquals(book, dto.getBook(), "Book should be set correctly");
        assertEquals(creationName, dto.getCreationName(), "CreationName should be set correctly");
        assertEquals(revisionName, dto.getRevisionName(), "RevisionName should be set correctly");
        assertEquals(dealName, dto.getDealName(), "DealName should be set correctly");
        assertEquals(dealType, dto.getDealType(), "DealType should be set correctly");
        assertEquals(sourceListId, dto.getSourceListId(), "SourceListId should be set correctly");
        assertEquals(side, dto.getSide(), "Side should be set correctly");
    }
}
