package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidListDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour le BidListMapper.
 * 
 * <p>Cette classe de test valide la conversion bidirectionnelle entre les entités BidList
 * et les DTOs BidListDTO, en vérifiant le mapping correct de tous les champs,
 * la gestion des valeurs null, et l'intégrité des données financières.</p>
 * 
 * <p>Couverture des tests :</p>
 * <ul>
 *   <li><strong>Mapping complet</strong> : Tous les champs entity ↔ DTO</li>
 *   <li><strong>Gestion des nulls</strong> : Protection contre les NPE</li>
 *   <li><strong>Conversion round-trip</strong> : Intégrité des données</li>
 *   <li><strong>Valeurs financières</strong> : Précision des montants et quantités</li>
 *   <li><strong>Données d'audit</strong> : Conservation des métadonnées</li>
 *   <li><strong>Edge cases</strong> : Valeurs limites et cas particuliers</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */
@DisplayName("BidListMapper - Tests de conversion Entity/DTO")
class BidListMapperTest {

    @Test
    @DisplayName("toDTO() - Avec entité valide - Doit mapper tous les champs")
    void toDTO_WithValidEntity_ShouldMapAllFields() {
        // Given - Création d'une entité BidList complète avec toutes les données
        BidList entity = new BidList();
        entity.setBidListId(1);
        entity.setAccount("TRADING_ACCOUNT_001");
        entity.setType("EQUITY");
        entity.setBidQuantity(1500.75);
        entity.setAskQuantity(1200.50);
        entity.setBid(125.45);
        entity.setAsk(125.67);
        entity.setBenchmark("S&P500");
        entity.setBidListDate(LocalDateTime.of(2024, 1, 15, 10, 30));
        entity.setCommentary("High priority trade");
        entity.setSecurity("AAPL");
        entity.setStatus("ACTIVE");
        entity.setTrader("John.Doe");
        entity.setBook("EQUITY_BOOK_01");
        entity.setCreationName("System.Admin");
        entity.setCreationDate(LocalDateTime.of(2024, 1, 15, 9, 0));
        entity.setRevisionName("Jane.Smith");
        entity.setRevisionDate(LocalDateTime.of(2024, 1, 15, 11, 15));
        entity.setDealName("APPLE_DEAL_Q1");
        entity.setDealType("SPOT");
        entity.setSourceListId("SRC_LIST_001");
        entity.setSide("BUY");

        // When - Conversion vers DTO
        BidListDTO result = BidListMapper.toDTO(entity);

        // Then - Vérification du mapping complet
        assertThat(result).isNotNull();
        assertThat(result.getBidListId()).isEqualTo(1);
        assertThat(result.getAccount()).isEqualTo("TRADING_ACCOUNT_001");
        assertThat(result.getType()).isEqualTo("EQUITY");
        assertThat(result.getBidQuantity()).isEqualTo(1500.75);
        assertThat(result.getAskQuantity()).isEqualTo(1200.50);
        assertThat(result.getBid()).isEqualTo(125.45);
        assertThat(result.getAsk()).isEqualTo(125.67);
        assertThat(result.getBenchmark()).isEqualTo("S&P500");
        assertThat(result.getCommentary()).isEqualTo("High priority trade");
        assertThat(result.getSecurity()).isEqualTo("AAPL");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getTrader()).isEqualTo("John.Doe");
        assertThat(result.getBook()).isEqualTo("EQUITY_BOOK_01");
        assertThat(result.getCreationName()).isEqualTo("System.Admin");
        assertThat(result.getRevisionName()).isEqualTo("Jane.Smith");
        assertThat(result.getDealName()).isEqualTo("APPLE_DEAL_Q1");
        assertThat(result.getDealType()).isEqualTo("SPOT");
        assertThat(result.getSourceListId()).isEqualTo("SRC_LIST_001");
        assertThat(result.getSide()).isEqualTo("BUY");
    }

    @Test
    @DisplayName("toDTO() - Avec entité null - Doit retourner null")
    void toDTO_WithNullEntity_ShouldReturnNull() {
        // Given
        BidList entity = null;

        // When
        BidListDTO result = BidListMapper.toDTO(entity);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toEntity() - Avec DTO valide - Doit mapper tous les champs")
    void toEntity_WithValidDTO_ShouldMapAllFields() {
        // Given - Création d'un DTO BidListDTO complet
        BidListDTO dto = new BidListDTO();
        dto.setBidListId(2);
        dto.setAccount("HEDGE_FUND_XYZ");
        dto.setType("BOND");
        dto.setBidQuantity(2500.25);
        dto.setAskQuantity(2400.75);
        dto.setBid(98.35);
        dto.setAsk(98.55);
        dto.setBenchmark("NASDAQ");
        dto.setCommentary("Corporate bond position");
        dto.setSecurity("CORP_BOND_001");
        dto.setStatus("PENDING");
        dto.setTrader("Alice.Johnson");
        dto.setBook("FIXED_INCOME_BOOK");
        dto.setCreationName("Trading.System");
        dto.setRevisionName("Bob.Wilson");
        dto.setDealName("CORP_BOND_DEAL");
        dto.setDealType("FORWARD");
        dto.setSourceListId("SRC_LIST_002");
        dto.setSide("SELL");

        // When - Conversion vers entité
        BidList result = BidListMapper.toEntity(dto);

        // Then - Vérification du mapping complet
        assertThat(result).isNotNull();
        assertThat(result.getBidListId()).isEqualTo(2);
        assertThat(result.getAccount()).isEqualTo("HEDGE_FUND_XYZ");
        assertThat(result.getType()).isEqualTo("BOND");
        assertThat(result.getBidQuantity()).isEqualTo(2500.25);
        assertThat(result.getAskQuantity()).isEqualTo(2400.75);
        assertThat(result.getBid()).isEqualTo(98.35);
        assertThat(result.getAsk()).isEqualTo(98.55);
        assertThat(result.getBenchmark()).isEqualTo("NASDAQ");
        assertThat(result.getCommentary()).isEqualTo("Corporate bond position");
        assertThat(result.getSecurity()).isEqualTo("CORP_BOND_001");
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getTrader()).isEqualTo("Alice.Johnson");
        assertThat(result.getBook()).isEqualTo("FIXED_INCOME_BOOK");
        assertThat(result.getCreationName()).isEqualTo("Trading.System");
        assertThat(result.getRevisionName()).isEqualTo("Bob.Wilson");
        assertThat(result.getDealName()).isEqualTo("CORP_BOND_DEAL");
        assertThat(result.getDealType()).isEqualTo("FORWARD");
        assertThat(result.getSourceListId()).isEqualTo("SRC_LIST_002");
        assertThat(result.getSide()).isEqualTo("SELL");
    }

    @Test
    @DisplayName("toEntity() - Avec DTO null - Doit retourner null")
    void toEntity_WithNullDTO_ShouldReturnNull() {
        // Given
        BidListDTO dto = null;

        // When
        BidList result = BidListMapper.toEntity(dto);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Conversion round-trip - Entity→DTO→Entity - Doit préserver toutes les données")
    void roundTripConversion_EntityToDTOToEntity_ShouldPreserveAllData() {
        // Given - Entité source avec données complexes
        BidList originalEntity = new BidList();
        originalEntity.setBidListId(42);
        originalEntity.setAccount("COMPLEX_TRADING_ACC");
        originalEntity.setType("DERIVATIVE");
        originalEntity.setBidQuantity(3750.99);
        originalEntity.setAskQuantity(3650.88);
        originalEntity.setBid(157.89);
        originalEntity.setAsk(158.12);
        originalEntity.setBenchmark("EURIBOR");
        originalEntity.setCommentary("Complex derivative position with multiple legs");
        originalEntity.setSecurity("EUR_SWAP_5Y");
        originalEntity.setStatus("EXECUTED");
        originalEntity.setTrader("Expert.Trader");
        originalEntity.setBook("DERIVATIVES_BOOK");
        originalEntity.setCreationName("Auto.System");
        originalEntity.setRevisionName("Risk.Manager");
        originalEntity.setDealName("EURO_SWAP_DEAL_Q4");
        originalEntity.setDealType("SWAP");
        originalEntity.setSourceListId("SWAP_SRC_001");
        originalEntity.setSide("LONG");

        // When - Conversion round-trip : Entity → DTO → Entity
        BidListDTO intermediateDTO = BidListMapper.toDTO(originalEntity);
        BidList finalEntity = BidListMapper.toEntity(intermediateDTO);

        // Then - Vérification de la conservation parfaite des données
        assertThat(finalEntity).isNotNull();
        assertThat(finalEntity.getBidListId()).isEqualTo(originalEntity.getBidListId());
        assertThat(finalEntity.getAccount()).isEqualTo(originalEntity.getAccount());
        assertThat(finalEntity.getType()).isEqualTo(originalEntity.getType());
        assertThat(finalEntity.getBidQuantity()).isEqualTo(originalEntity.getBidQuantity());
        assertThat(finalEntity.getAskQuantity()).isEqualTo(originalEntity.getAskQuantity());
        assertThat(finalEntity.getBid()).isEqualTo(originalEntity.getBid());
        assertThat(finalEntity.getAsk()).isEqualTo(originalEntity.getAsk());
        assertThat(finalEntity.getBenchmark()).isEqualTo(originalEntity.getBenchmark());
        assertThat(finalEntity.getCommentary()).isEqualTo(originalEntity.getCommentary());
        assertThat(finalEntity.getSecurity()).isEqualTo(originalEntity.getSecurity());
        assertThat(finalEntity.getStatus()).isEqualTo(originalEntity.getStatus());
        assertThat(finalEntity.getTrader()).isEqualTo(originalEntity.getTrader());
        assertThat(finalEntity.getBook()).isEqualTo(originalEntity.getBook());
        assertThat(finalEntity.getCreationName()).isEqualTo(originalEntity.getCreationName());
        assertThat(finalEntity.getRevisionName()).isEqualTo(originalEntity.getRevisionName());
        assertThat(finalEntity.getDealName()).isEqualTo(originalEntity.getDealName());
        assertThat(finalEntity.getDealType()).isEqualTo(originalEntity.getDealType());
        assertThat(finalEntity.getSourceListId()).isEqualTo(originalEntity.getSourceListId());
        assertThat(finalEntity.getSide()).isEqualTo(originalEntity.getSide());
    }

    @Test
    @DisplayName("Conversion round-trip - DTO→Entity→DTO - Doit préserver toutes les données")
    void roundTripConversion_DTOToEntityToDTO_ShouldPreserveAllData() {
        // Given - DTO source avec données de trading avancées
        BidListDTO originalDTO = new BidListDTO();
        originalDTO.setBidListId(99);
        originalDTO.setAccount("ALGO_TRADING_001");
        originalDTO.setType("FOREX");
        originalDTO.setBidQuantity(50000.00);
        originalDTO.setAskQuantity(48000.00);
        originalDTO.setBid(1.0825);
        originalDTO.setAsk(1.0827);
        originalDTO.setBenchmark("ECB_RATE");
        originalDTO.setCommentary("High frequency trading position EUR/USD");
        originalDTO.setSecurity("EURUSD");
        originalDTO.setStatus("FILLED");
        originalDTO.setTrader("HFT.Robot");
        originalDTO.setBook("FX_TRADING_BOOK");
        originalDTO.setCreationName("Algorithm.V2");
        originalDTO.setRevisionName("Risk.Control");
        originalDTO.setDealName("FX_ALGO_DEAL_001");
        originalDTO.setDealType("SPOT");
        originalDTO.setSourceListId("FX_ALGO_SRC");
        originalDTO.setSide("SHORT");

        // When - Conversion round-trip : DTO → Entity → DTO
        BidList intermediateEntity = BidListMapper.toEntity(originalDTO);
        BidListDTO finalDTO = BidListMapper.toDTO(intermediateEntity);

        // Then - Vérification de la conservation parfaite des données
        assertThat(finalDTO).isNotNull();
        assertThat(finalDTO.getBidListId()).isEqualTo(originalDTO.getBidListId());
        assertThat(finalDTO.getAccount()).isEqualTo(originalDTO.getAccount());
        assertThat(finalDTO.getType()).isEqualTo(originalDTO.getType());
        assertThat(finalDTO.getBidQuantity()).isEqualTo(originalDTO.getBidQuantity());
        assertThat(finalDTO.getAskQuantity()).isEqualTo(originalDTO.getAskQuantity());
        assertThat(finalDTO.getBid()).isEqualTo(originalDTO.getBid());
        assertThat(finalDTO.getAsk()).isEqualTo(originalDTO.getAsk());
        assertThat(finalDTO.getBenchmark()).isEqualTo(originalDTO.getBenchmark());
        assertThat(finalDTO.getCommentary()).isEqualTo(originalDTO.getCommentary());
        assertThat(finalDTO.getSecurity()).isEqualTo(originalDTO.getSecurity());
        assertThat(finalDTO.getStatus()).isEqualTo(originalDTO.getStatus());
        assertThat(finalDTO.getTrader()).isEqualTo(originalDTO.getTrader());
        assertThat(finalDTO.getBook()).isEqualTo(originalDTO.getBook());
        assertThat(finalDTO.getCreationName()).isEqualTo(originalDTO.getCreationName());
        assertThat(finalDTO.getRevisionName()).isEqualTo(originalDTO.getRevisionName());
        assertThat(finalDTO.getDealName()).isEqualTo(originalDTO.getDealName());
        assertThat(finalDTO.getDealType()).isEqualTo(originalDTO.getDealType());
        assertThat(finalDTO.getSourceListId()).isEqualTo(originalDTO.getSourceListId());
        assertThat(finalDTO.getSide()).isEqualTo(originalDTO.getSide());
    }

    @Test
    @DisplayName("toDTO() - Avec champs null dans l'entité - Doit gérer les valeurs null")
    void toDTO_WithNullFieldsInEntity_ShouldHandleNullValues() {
        // Given - Entité avec seulement les champs obligatoires
        BidList entity = new BidList();
        entity.setBidListId(10);
        entity.setAccount("MINIMAL_ACCOUNT");
        entity.setType("SIMPLE");
        // Tous les autres champs restent null

        // When
        BidListDTO result = BidListMapper.toDTO(entity);

        // Then - Vérification que les null sont préservés
        assertThat(result).isNotNull();
        assertThat(result.getBidListId()).isEqualTo(10);
        assertThat(result.getAccount()).isEqualTo("MINIMAL_ACCOUNT");
        assertThat(result.getType()).isEqualTo("SIMPLE");
        assertThat(result.getBidQuantity()).isNull();
        assertThat(result.getAskQuantity()).isNull();
        assertThat(result.getBid()).isNull();
        assertThat(result.getAsk()).isNull();
        assertThat(result.getBenchmark()).isNull();
        assertThat(result.getCommentary()).isNull();
        assertThat(result.getSecurity()).isNull();
        assertThat(result.getStatus()).isNull();
        assertThat(result.getTrader()).isNull();
        assertThat(result.getBook()).isNull();
        assertThat(result.getCreationName()).isNull();
        assertThat(result.getRevisionName()).isNull();
        assertThat(result.getDealName()).isNull();
        assertThat(result.getDealType()).isNull();
        assertThat(result.getSourceListId()).isNull();
        assertThat(result.getSide()).isNull();
    }

    @Test
    @DisplayName("toEntity() - Avec champs null dans le DTO - Doit gérer les valeurs null")
    void toEntity_WithNullFieldsInDTO_ShouldHandleNullValues() {
        // Given - DTO avec seulement les champs essentiels
        BidListDTO dto = new BidListDTO();
        dto.setBidListId(20);
        dto.setAccount("BASIC_ACCOUNT");
        dto.setType("BASIC");
        // Tous les autres champs restent null

        // When
        BidList result = BidListMapper.toEntity(dto);

        // Then - Vérification que les null sont préservés
        assertThat(result).isNotNull();
        assertThat(result.getBidListId()).isEqualTo(20);
        assertThat(result.getAccount()).isEqualTo("BASIC_ACCOUNT");
        assertThat(result.getType()).isEqualTo("BASIC");
        assertThat(result.getBidQuantity()).isNull();
        assertThat(result.getAskQuantity()).isNull();
        assertThat(result.getBid()).isNull();
        assertThat(result.getAsk()).isNull();
        assertThat(result.getBenchmark()).isNull();
        assertThat(result.getCommentary()).isNull();
        assertThat(result.getSecurity()).isNull();
        assertThat(result.getStatus()).isNull();
        assertThat(result.getTrader()).isNull();
        assertThat(result.getBook()).isNull();
        assertThat(result.getCreationName()).isNull();
        assertThat(result.getRevisionName()).isNull();
        assertThat(result.getDealName()).isNull();
        assertThat(result.getDealType()).isNull();
        assertThat(result.getSourceListId()).isNull();
        assertThat(result.getSide()).isNull();
    }

    @Test
    @DisplayName("Mapping des valeurs financières - Doit préserver la précision numérique")
    void mapping_WithFinancialValues_ShouldPreserveNumericPrecision() {
        // Given - Valeurs financières avec précision maximale (2 décimales)
        BidList entity = new BidList();
        entity.setBidListId(999);
        entity.setAccount("PRECISION_TEST");
        entity.setType("PRECISION");
        entity.setBidQuantity(999999.99); // Valeur maximale avec 2 décimales
        entity.setAskQuantity(888888.88);
        entity.setBid(9999.99);           // Prix avec précision maximale
        entity.setAsk(10000.01);          // Différence minimale d'un centime

        // When - Conversion Entity → DTO → Entity
        BidListDTO dto = BidListMapper.toDTO(entity);
        BidList resultEntity = BidListMapper.toEntity(dto);

        // Then - Vérification de la précision financière
        assertThat(dto.getBidQuantity()).isEqualTo(999999.99);
        assertThat(dto.getAskQuantity()).isEqualTo(888888.88);
        assertThat(dto.getBid()).isEqualTo(9999.99);
        assertThat(dto.getAsk()).isEqualTo(10000.01);

        assertThat(resultEntity.getBidQuantity()).isEqualTo(999999.99);
        assertThat(resultEntity.getAskQuantity()).isEqualTo(888888.88);
        assertThat(resultEntity.getBid()).isEqualTo(9999.99);
        assertThat(resultEntity.getAsk()).isEqualTo(10000.01);

        // Vérification de l'écart bid-ask
        double bidAskSpread = resultEntity.getAsk() - resultEntity.getBid();
        assertThat(bidAskSpread).isEqualTo(0.02, within(0.001));
    }

    @Test
    @DisplayName("Mapping des chaînes - Doit préserver les caractères spéciaux et espaces")
    void mapping_WithSpecialCharactersAndSpaces_ShouldPreserveStringIntegrity() {
        // Given - Entité avec chaînes contenant caractères spéciaux
        BidList entity = new BidList();
        entity.setBidListId(777);
        entity.setAccount("SPECIAL_CHAR_ÀCCÔUNT€");
        entity.setType("TŸPÈ-SPÉCÏAL");
        entity.setBenchmark("S&P 500 €URIBOR");
        entity.setCommentary("Trade with émojis 🚀 and symbols: @#$%^&*()");
        entity.setSecurity("FR0000120271.PA");
        entity.setTrader("Jean-François.Müller");
        entity.setBook("BOOK with SPACES & symbols");
        entity.setCreationName("Système Créé");
        entity.setRevisionName("Révision François");
        entity.setDealName("DEAL_NAME with spaces & accents éèà");
        entity.setDealType("AÇCORD_SPÉCIAL");
        entity.setSourceListId("SRC-LIST_001/SPECIAL");
        entity.setSide("BÜŸ");

        // When - Conversion round-trip
        BidListDTO dto = BidListMapper.toDTO(entity);
        BidList result = BidListMapper.toEntity(dto);

        // Then - Vérification de la préservation des caractères spéciaux
        assertThat(result.getAccount()).isEqualTo("SPECIAL_CHAR_ÀCCÔUNT€");
        assertThat(result.getType()).isEqualTo("TŸPÈ-SPÉCÏAL");
        assertThat(result.getBenchmark()).isEqualTo("S&P 500 €URIBOR");
        assertThat(result.getCommentary()).isEqualTo("Trade with émojis 🚀 and symbols: @#$%^&*()");
        assertThat(result.getSecurity()).isEqualTo("FR0000120271.PA");
        assertThat(result.getTrader()).isEqualTo("Jean-François.Müller");
        assertThat(result.getBook()).isEqualTo("BOOK with SPACES & symbols");
        assertThat(result.getCreationName()).isEqualTo("Système Créé");
        assertThat(result.getRevisionName()).isEqualTo("Révision François");
        assertThat(result.getDealName()).isEqualTo("DEAL_NAME with spaces & accents éèà");
        assertThat(result.getDealType()).isEqualTo("AÇCORD_SPÉCIAL");
        assertThat(result.getSourceListId()).isEqualTo("SRC-LIST_001/SPECIAL");
        assertThat(result.getSide()).isEqualTo("BÜŸ");
    }

    @Test
    @DisplayName("Mapping des valeurs limites - Doit gérer les cas extrêmes")
    void mapping_WithBoundaryValues_ShouldHandleEdgeCases() {
        // Given - Valeurs aux limites (zéro, très petites, très grandes)
        BidList entity = new BidList();
        entity.setBidListId(Integer.MAX_VALUE);
        entity.setAccount("A"); // Chaîne d'un caractère
        entity.setType("T");    // Chaîne d'un caractère
        entity.setBidQuantity(0.0);           // Valeur minimale autorisée
        entity.setAskQuantity(0.01);          // Plus petite valeur significative
        entity.setBid(0.0);                   // Prix zéro
        entity.setAsk(9999999999.99);         // Très grande valeur financière
        entity.setBenchmark("");              // Chaîne vide
        entity.setCommentary("X".repeat(125)); // Chaîne de longueur maximale
        entity.setStatus("A");                // Statut d'un caractère

        // When - Conversion round-trip
        BidListDTO dto = BidListMapper.toDTO(entity);
        BidList result = BidListMapper.toEntity(dto);

        // Then - Vérification des valeurs limites
        assertThat(result.getBidListId()).isEqualTo(Integer.MAX_VALUE);
        assertThat(result.getAccount()).isEqualTo("A");
        assertThat(result.getType()).isEqualTo("T");
        assertThat(result.getBidQuantity()).isEqualTo(0.0);
        assertThat(result.getAskQuantity()).isEqualTo(0.01);
        assertThat(result.getBid()).isEqualTo(0.0);
        assertThat(result.getAsk()).isEqualTo(9999999999.99);
        assertThat(result.getBenchmark()).isEqualTo("");
        assertThat(result.getCommentary()).hasSize(125);
        assertThat(result.getStatus()).isEqualTo("A");
    }
}
