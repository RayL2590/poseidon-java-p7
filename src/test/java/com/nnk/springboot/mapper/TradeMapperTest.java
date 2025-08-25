package com.nnk.springboot.mapper;

/**
 * Tests unitaires pour le TradeMapper.
 * 
 * <p>Cette classe de test valide la conversion bidirectionnelle entre les entités Trade
 * et les DTOs TradeDTO, en vérifiant le mapping correct de tous les champs,
 * la gestion des valeurs null, et les fonctionnalités spécifiques aux transactions financières.</p>
 * 
 * <p>Couverture des tests :</p>
 * <ul>
 *   <li><strong>Mapping complet</strong> : Tous les champs entité ↔ DTO</li>
 *   <li><strong>Gestion des nulls</strong> : Protection contre les NPE</li>
 *   <li><strong>Conversion round-trip</strong> : Intégrité des données</li>
 *   <li><strong>Données de transaction</strong> : Validation des montants et types</li>
 *   <li><strong>Méthodes spécifiques</strong> : updateEntityFromDTO, createDefaultForBook</li>
 *   <li><strong>Timestamps</strong> : Gestion des dates de transaction</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.dto.TradeDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe TradeMapper.
 * 
 * <p>Cette classe de tests vérifie le bon fonctionnement du mapper TradeMapper,
 * incluant les conversions bidirectionnelles entre entités et DTOs, les mises à jour
 * in-place et les méthodes utilitaires.</p>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@DisplayName("TradeMapper Tests")
public class TradeMapperTest {

    private TradeMapper tradeMapper;
    private Trade trade;
    private TradeDTO tradeDTO;

    @BeforeEach
    void setUp() {
        tradeMapper = new TradeMapper();
        
        // Création d'une entité Trade complète
        trade = new Trade();
        trade.setTradeId(1);
        trade.setAccount("TEST_ACCOUNT");
        trade.setType("BUY");
        trade.setBuyQuantity(1000.0);
        trade.setBuyPrice(25.50);
        trade.setSellQuantity(null);
        trade.setSellPrice(null);
        trade.setTradeDate(LocalDateTime.of(2024, 1, 15, 10, 30));
        trade.setSecurity("AAPL");
        trade.setStatus("EXECUTED");
        trade.setTrader("TRADER001");
        trade.setBenchmark("SP500");
        trade.setBook("BOOK_A");
        trade.setCreationName("admin");
        trade.setCreationDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        trade.setRevisionName("admin");
        trade.setRevisionDate(LocalDateTime.of(2024, 1, 15, 11, 0));
        trade.setDealName("DEAL_001");
        trade.setDealType("CASH");
        trade.setSourceListId("SRC_001");
        trade.setSide("BUY");
        
        // Création d'un DTO TradeDTO correspondant
        tradeDTO = new TradeDTO();
        tradeDTO.setTradeId(1);
        tradeDTO.setAccount("TEST_ACCOUNT");
        tradeDTO.setType("BUY");
        tradeDTO.setBuyQuantity(1000.0);
        tradeDTO.setBuyPrice(25.50);
        tradeDTO.setSellQuantity(null);
        tradeDTO.setSellPrice(null);
        tradeDTO.setTradeDate(LocalDateTime.of(2024, 1, 15, 10, 30));
        tradeDTO.setSecurity("AAPL");
        tradeDTO.setStatus("EXECUTED");
        tradeDTO.setTrader("TRADER001");
        tradeDTO.setBenchmark("SP500");
        tradeDTO.setBook("BOOK_A");
        tradeDTO.setCreationName("admin");
        tradeDTO.setCreationDate(LocalDateTime.of(2024, 1, 15, 10, 0));
        tradeDTO.setRevisionName("admin");
        tradeDTO.setRevisionDate(LocalDateTime.of(2024, 1, 15, 11, 0));
        tradeDTO.setDealName("DEAL_001");
        tradeDTO.setDealType("CASH");
        tradeDTO.setSourceListId("SRC_001");
        tradeDTO.setSide("BUY");
    }

    @Test
    @DisplayName("Conversion Trade vers TradeDTO doit mapper tous les champs")
    void testToDTO() {
        TradeDTO result = tradeMapper.toDTO(trade);
        
        assertNotNull(result);
        assertEquals(trade.getTradeId(), result.getTradeId());
        assertEquals(trade.getAccount(), result.getAccount());
        assertEquals(trade.getType(), result.getType());
        assertEquals(trade.getBuyQuantity(), result.getBuyQuantity());
        assertEquals(trade.getBuyPrice(), result.getBuyPrice());
        assertEquals(trade.getSellQuantity(), result.getSellQuantity());
        assertEquals(trade.getSellPrice(), result.getSellPrice());
        assertEquals(trade.getTradeDate(), result.getTradeDate());
        assertEquals(trade.getSecurity(), result.getSecurity());
        assertEquals(trade.getStatus(), result.getStatus());
        assertEquals(trade.getTrader(), result.getTrader());
        assertEquals(trade.getBenchmark(), result.getBenchmark());
        assertEquals(trade.getBook(), result.getBook());
        assertEquals(trade.getCreationName(), result.getCreationName());
        assertEquals(trade.getCreationDate(), result.getCreationDate());
        assertEquals(trade.getRevisionName(), result.getRevisionName());
        assertEquals(trade.getRevisionDate(), result.getRevisionDate());
        assertEquals(trade.getDealName(), result.getDealName());
        assertEquals(trade.getDealType(), result.getDealType());
        assertEquals(trade.getSourceListId(), result.getSourceListId());
        assertEquals(trade.getSide(), result.getSide());
    }

    @Test
    @DisplayName("Conversion TradeDTO vers Trade doit mapper tous les champs")
    void testToEntity() {
        Trade result = tradeMapper.toEntity(tradeDTO);
        
        assertNotNull(result);
        assertEquals(tradeDTO.getTradeId(), result.getTradeId());
        assertEquals(tradeDTO.getAccount(), result.getAccount());
        assertEquals(tradeDTO.getType(), result.getType());
        assertEquals(tradeDTO.getBuyQuantity(), result.getBuyQuantity());
        assertEquals(tradeDTO.getBuyPrice(), result.getBuyPrice());
        assertEquals(tradeDTO.getSellQuantity(), result.getSellQuantity());
        assertEquals(tradeDTO.getSellPrice(), result.getSellPrice());
        assertEquals(tradeDTO.getTradeDate(), result.getTradeDate());
        assertEquals(tradeDTO.getSecurity(), result.getSecurity());
        assertEquals(tradeDTO.getStatus(), result.getStatus());
        assertEquals(tradeDTO.getTrader(), result.getTrader());
        assertEquals(tradeDTO.getBenchmark(), result.getBenchmark());
        assertEquals(tradeDTO.getBook(), result.getBook());
        assertEquals(tradeDTO.getCreationName(), result.getCreationName());
        assertEquals(tradeDTO.getCreationDate(), result.getCreationDate());
        assertEquals(tradeDTO.getRevisionName(), result.getRevisionName());
        assertEquals(tradeDTO.getRevisionDate(), result.getRevisionDate());
        assertEquals(tradeDTO.getDealName(), result.getDealName());
        assertEquals(tradeDTO.getDealType(), result.getDealType());
        assertEquals(tradeDTO.getSourceListId(), result.getSourceListId());
        assertEquals(tradeDTO.getSide(), result.getSide());
    }

    @Test
    @DisplayName("Conversion avec entité null doit retourner null")
    void testToDTOWithNull() {
        TradeDTO result = tradeMapper.toDTO(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Conversion avec DTO null doit retourner null")
    void testToEntityWithNull() {
        Trade result = tradeMapper.toEntity(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Conversion bidirectionnelle doit préserver les données")
    void testBidirectionalConversion() {
        // Trade -> DTO -> Trade
        TradeDTO dto = tradeMapper.toDTO(trade);
        Trade reconvertedTrade = tradeMapper.toEntity(dto);
        
        assertEquals(trade.getTradeId(), reconvertedTrade.getTradeId());
        assertEquals(trade.getAccount(), reconvertedTrade.getAccount());
        assertEquals(trade.getType(), reconvertedTrade.getType());
        assertEquals(trade.getBuyQuantity(), reconvertedTrade.getBuyQuantity());
        assertEquals(trade.getBuyPrice(), reconvertedTrade.getBuyPrice());
        assertEquals(trade.getTradeDate(), reconvertedTrade.getTradeDate());
        
        // DTO -> Trade -> DTO
        Trade entity = tradeMapper.toEntity(tradeDTO);
        TradeDTO reconvertedDTO = tradeMapper.toDTO(entity);
        
        assertEquals(tradeDTO.getTradeId(), reconvertedDTO.getTradeId());
        assertEquals(tradeDTO.getAccount(), reconvertedDTO.getAccount());
        assertEquals(tradeDTO.getType(), reconvertedDTO.getType());
        assertEquals(tradeDTO.getBuyQuantity(), reconvertedDTO.getBuyQuantity());
        assertEquals(tradeDTO.getBuyPrice(), reconvertedDTO.getBuyPrice());
        assertEquals(tradeDTO.getTradeDate(), reconvertedDTO.getTradeDate());
    }

    @Test
    @DisplayName("Mise à jour d'entité depuis DTO doit mettre à jour les champs modifiables")
    void testUpdateEntityFromDTO() {
        // Préparation d'un DTO avec des modifications
        TradeDTO modifiedDTO = new TradeDTO();
        modifiedDTO.setTradeId(1); // ID ne change pas
        modifiedDTO.setAccount("MODIFIED_ACCOUNT");
        modifiedDTO.setType("SELL");
        modifiedDTO.setBuyQuantity(null);
        modifiedDTO.setBuyPrice(null);
        modifiedDTO.setSellQuantity(500.0);
        modifiedDTO.setSellPrice(30.75);
        modifiedDTO.setTradeDate(LocalDateTime.of(2024, 1, 16, 14, 0));
        modifiedDTO.setSecurity("MSFT");
        modifiedDTO.setStatus("PENDING");
        modifiedDTO.setTrader("TRADER002");
        modifiedDTO.setBenchmark("NASDAQ");
        modifiedDTO.setBook("BOOK_B");
        modifiedDTO.setRevisionName("supervisor");
        modifiedDTO.setRevisionDate(LocalDateTime.of(2024, 1, 16, 15, 0));
        modifiedDTO.setDealName("DEAL_002");
        modifiedDTO.setDealType("MARGIN");
        modifiedDTO.setSourceListId("SRC_002");
        modifiedDTO.setSide("SELL");
        
        // Application des modifications
        tradeMapper.updateEntityFromDTO(trade, modifiedDTO);
        
        // Vérification des champs mis à jour
        assertEquals("MODIFIED_ACCOUNT", trade.getAccount());
        assertEquals("SELL", trade.getType());
        assertNull(trade.getBuyQuantity());
        assertNull(trade.getBuyPrice());
        assertEquals(500.0, trade.getSellQuantity());
        assertEquals(30.75, trade.getSellPrice());
        assertEquals(LocalDateTime.of(2024, 1, 16, 14, 0), trade.getTradeDate());
        assertEquals("MSFT", trade.getSecurity());
        assertEquals("PENDING", trade.getStatus());
        assertEquals("TRADER002", trade.getTrader());
        assertEquals("NASDAQ", trade.getBenchmark());
        assertEquals("BOOK_B", trade.getBook());
        assertEquals("supervisor", trade.getRevisionName());
        assertEquals(LocalDateTime.of(2024, 1, 16, 15, 0), trade.getRevisionDate());
        assertEquals("DEAL_002", trade.getDealName());
        assertEquals("MARGIN", trade.getDealType());
        assertEquals("SRC_002", trade.getSourceListId());
        assertEquals("SELL", trade.getSide());
        
        // Vérification que l'ID et les données de création ne changent pas
        assertEquals(1, trade.getTradeId());
        assertEquals("admin", trade.getCreationName());
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 0), trade.getCreationDate());
    }

    @Test
    @DisplayName("Mise à jour avec entité null ne doit rien faire")
    void testUpdateEntityFromDTOWithNullEntity() {
        // Ne doit pas lever d'exception
        assertDoesNotThrow(() -> tradeMapper.updateEntityFromDTO(null, tradeDTO));
    }

    @Test
    @DisplayName("Mise à jour avec DTO null ne doit rien faire")
    void testUpdateEntityFromDTOWithNullDTO() {
        Trade originalTrade = new Trade("ORIGINAL_ACC", "BUY");
        originalTrade.setBuyQuantity(100.0);
        
        // Ne doit pas lever d'exception ni modifier l'entité
        assertDoesNotThrow(() -> tradeMapper.updateEntityFromDTO(originalTrade, null));
        assertEquals("ORIGINAL_ACC", originalTrade.getAccount());
        assertEquals("BUY", originalTrade.getType());
        assertEquals(100.0, originalTrade.getBuyQuantity());
    }

    @Test
    @DisplayName("Création de DTO par défaut pour type BUY")
    void testCreateDefaultForTypeBuy() {
        TradeDTO result = tradeMapper.createDefaultForType("BUY");
        
        assertNotNull(result);
        assertEquals("DEFAULT_ACCOUNT", result.getAccount());
        assertEquals("BUY", result.getType());
        assertEquals("PENDING", result.getStatus());
        assertEquals("BUY", result.getSide());
    }

    @Test
    @DisplayName("Création de DTO par défaut pour type SELL")
    void testCreateDefaultForTypeSell() {
        TradeDTO result = tradeMapper.createDefaultForType("SELL");
        
        assertNotNull(result);
        assertEquals("DEFAULT_ACCOUNT", result.getAccount());
        assertEquals("SELL", result.getType());
        assertEquals("PENDING", result.getStatus());
        assertEquals("SELL", result.getSide());
    }

    @Test
    @DisplayName("Création de DTO par défaut pour type SWAP")
    void testCreateDefaultForTypeSwap() {
        TradeDTO result = tradeMapper.createDefaultForType("SWAP");
        
        assertNotNull(result);
        assertEquals("DEFAULT_ACCOUNT", result.getAccount());
        assertEquals("SWAP", result.getType());
        assertEquals("PENDING", result.getStatus());
        assertEquals("BOTH", result.getSide());
    }

    @Test
    @DisplayName("Création de DTO par défaut pour type inconnu")
    void testCreateDefaultForUnknownType() {
        TradeDTO result = tradeMapper.createDefaultForType("UNKNOWN");
        
        assertNotNull(result);
        assertEquals("DEFAULT_ACCOUNT", result.getAccount());
        assertEquals("GENERAL", result.getType());
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    @DisplayName("Création de DTO par défaut avec type null")
    void testCreateDefaultForNullType() {
        TradeDTO result = tradeMapper.createDefaultForType(null);
        
        assertNotNull(result);
        assertNull(result.getAccount());
        assertNull(result.getType());
    }

    @Test
    @DisplayName("Test isExecutable avec transaction valide")
    void testIsExecutableValid() {
        TradeDTO validDTO = new TradeDTO("ACC001", "BUY");
        validDTO.setBuyQuantity(100.0);
        validDTO.setBuyPrice(25.0);
        
        assertTrue(tradeMapper.isExecutable(validDTO));
    }

    @Test
    @DisplayName("Test isExecutable avec transaction incomplète")
    void testIsExecutableIncomplete() {
        // Sans account
        TradeDTO incompleteDTO = new TradeDTO(null, "BUY");
        incompleteDTO.setBuyQuantity(100.0);
        incompleteDTO.setBuyPrice(25.0);
        assertFalse(tradeMapper.isExecutable(incompleteDTO));
        
        // Sans type
        incompleteDTO = new TradeDTO("ACC001", null);
        incompleteDTO.setBuyQuantity(100.0);
        incompleteDTO.setBuyPrice(25.0);
        assertFalse(tradeMapper.isExecutable(incompleteDTO));
        
        // Sans opération définie
        incompleteDTO = new TradeDTO("ACC001", "BUY");
        assertFalse(tradeMapper.isExecutable(incompleteDTO));
    }

    @Test
    @DisplayName("Test isExecutable avec DTO null")
    void testIsExecutableWithNull() {
        assertFalse(tradeMapper.isExecutable(null));
    }

    @Test
    @DisplayName("Test calculateRiskScore avec différents scénarios")
    void testCalculateRiskScore() {
        // Transaction de faible montant
        TradeDTO lowRiskDTO = new TradeDTO("ACC001", "BUY");
        lowRiskDTO.setBuyQuantity(100.0);
        lowRiskDTO.setBuyPrice(10.0); // Total: 1000
        lowRiskDTO.setBenchmark("SP500");
        lowRiskDTO.setStatus("PENDING");
        
        int lowScore = tradeMapper.calculateRiskScore(lowRiskDTO);
        assertEquals(0, lowScore); // Aucun facteur de risque
        
        // Transaction de montant élevé
        TradeDTO highRiskDTO = new TradeDTO("ACC001", "SWAP");
        highRiskDTO.setBuyQuantity(50000.0);
        highRiskDTO.setBuyPrice(25.0); // Total: 1.25M
        // Pas de benchmark
        highRiskDTO.setStatus("EXECUTED");
        
        int highScore = tradeMapper.calculateRiskScore(highRiskDTO);
        assertTrue(highScore >= 5); // +3 (montant) +2 (type) +1 (pas benchmark) +1 (statut)
    }

    @Test
    @DisplayName("Test calculateRiskScore avec DTO null")
    void testCalculateRiskScoreWithNull() {
        int score = tradeMapper.calculateRiskScore(null);
        assertEquals(0, score);
    }

    @Test
    @DisplayName("Test avec transaction de vente uniquement")
    void testSellOnlyTransaction() {
        Trade sellTrade = new Trade("SELL_ACC", "SELL");
        sellTrade.setSellQuantity(200.0);
        sellTrade.setSellPrice(45.0);
        
        TradeDTO dto = tradeMapper.toDTO(sellTrade);
        assertNotNull(dto);
        assertEquals("SELL_ACC", dto.getAccount());
        assertEquals("SELL", dto.getType());
        assertNull(dto.getBuyQuantity());
        assertNull(dto.getBuyPrice());
        assertEquals(200.0, dto.getSellQuantity());
        assertEquals(45.0, dto.getSellPrice());
        
        // Test de l'exécutabilité
        assertTrue(tradeMapper.isExecutable(dto));
    }

    @Test
    @DisplayName("Test avec champs optionnels vides ou null")
    void testWithOptionalFields() {
        Trade minimalTrade = new Trade("MIN_ACC", "BUY");
        minimalTrade.setBuyQuantity(50.0);
        minimalTrade.setBuyPrice(20.0);
        // Tous les autres champs restent null
        
        TradeDTO dto = tradeMapper.toDTO(minimalTrade);
        assertNotNull(dto);
        assertEquals("MIN_ACC", dto.getAccount());
        assertEquals("BUY", dto.getType());
        assertEquals(50.0, dto.getBuyQuantity());
        assertEquals(20.0, dto.getBuyPrice());
        assertNull(dto.getSecurity());
        assertNull(dto.getStatus());
        assertNull(dto.getTrader());
        
        // Reconversion
        Trade reconvertedTrade = tradeMapper.toEntity(dto);
        assertNotNull(reconvertedTrade);
        assertEquals("MIN_ACC", reconvertedTrade.getAccount());
        assertEquals("BUY", reconvertedTrade.getType());
    }
}