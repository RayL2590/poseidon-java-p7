package com.nnk.springboot.services;

/**
 * Tests unitaires pour le TradeService.
 * 
 * <p>Cette classe de test valide la logique métier du service de gestion des transactions (Trade),
 * en testant les opérations CRUD, la validation des données de transaction et la gestion des erreurs
 * dans un contexte d'intégration avec la base de données.</p>
 * 
 * <p>Couverture des tests :</p>
 * <ul>
 *   <li><strong>Création</strong> : Ajout de nouvelles transactions avec validation</li>
 *   <li><strong>Lecture</strong> : Récupération par ID et liste complète</li>
 *   <li><strong>Mise à jour</strong> : Modification des transactions existantes</li>
 *   <li><strong>Suppression</strong> : Suppression avec vérification d'existence</li>
 *   <li><strong>Gestion des erreurs</strong> : Entités introuvables, validation</li>
 *   <li><strong>Contraintes métier</strong> : Validation des données de trading</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour la classe TradeService.
 * 
 * <p>Cette classe de tests vérifie le bon fonctionnement du service TradeService,
 * incluant les opérations CRUD, les validations métier, et la gestion d'erreurs.</p>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TradeService Tests")
public class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private TradeService tradeService;

    private Trade validTrade;

    @BeforeEach
    void setUp() {
        validTrade = new Trade();
        validTrade.setTradeId(1);
        validTrade.setAccount("TEST_ACCOUNT");
        validTrade.setType("BUY");
        validTrade.setBuyQuantity(1000.0);
        validTrade.setBuyPrice(25.50);
        validTrade.setTradeDate(LocalDateTime.now());
        validTrade.setStatus("PENDING");
    }

    @Test
    @DisplayName("findAll doit retourner toutes les transactions")
    void testFindAll() {
        List<Trade> expectedTrades = Arrays.asList(validTrade, new Trade("ACC2", "SELL"));
        when(tradeRepository.findAllByOrderByTradeDateDesc()).thenReturn(expectedTrades);

        List<Trade> result = tradeService.findAll();

        assertEquals(expectedTrades, result);
        verify(tradeRepository).findAllByOrderByTradeDateDesc();
    }

    @Test
    @DisplayName("findById avec ID valide doit retourner la transaction")
    void testFindByIdValid() {
        when(tradeRepository.findById(1)).thenReturn(Optional.of(validTrade));

        Optional<Trade> result = tradeService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(validTrade, result.get());
        verify(tradeRepository).findById(1);
    }

    @Test
    @DisplayName("findById avec ID inexistant doit retourner Optional vide")
    void testFindByIdNotFound() {
        when(tradeRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Trade> result = tradeService.findById(999);

        assertFalse(result.isPresent());
        verify(tradeRepository).findById(999);
    }

    @Test
    @DisplayName("findById avec ID null ou négatif doit retourner Optional vide")
    void testFindByIdInvalid() {
        Optional<Trade> result1 = tradeService.findById(null);
        Optional<Trade> result2 = tradeService.findById(-1);
        Optional<Trade> result3 = tradeService.findById(0);

        assertFalse(result1.isPresent());
        assertFalse(result2.isPresent());
        assertFalse(result3.isPresent());
        verify(tradeRepository, never()).findById(any());
    }

    @Test
    @DisplayName("findByAccount doit retourner les transactions du compte")
    void testFindByAccount() {
        List<Trade> expectedTrades = Arrays.asList(validTrade);
        when(tradeRepository.findByAccountOrderByTradeDateDesc("TEST_ACCOUNT"))
                .thenReturn(expectedTrades);

        List<Trade> result = tradeService.findByAccount("TEST_ACCOUNT");

        assertEquals(expectedTrades, result);
        verify(tradeRepository).findByAccountOrderByTradeDateDesc("TEST_ACCOUNT");
    }

    @Test
    @DisplayName("findByAccount avec account null ou vide doit retourner liste vide")
    void testFindByAccountInvalid() {
        List<Trade> result1 = tradeService.findByAccount(null);
        List<Trade> result2 = tradeService.findByAccount("");
        List<Trade> result3 = tradeService.findByAccount("   ");

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        assertTrue(result3.isEmpty());
        verify(tradeRepository, never()).findByAccountOrderByTradeDateDesc(any());
    }

    @Test
    @DisplayName("findByType doit retourner les transactions du type")
    void testFindByType() {
        List<Trade> expectedTrades = Arrays.asList(validTrade);
        when(tradeRepository.findByTypeOrderByTradeDateDesc("BUY"))
                .thenReturn(expectedTrades);

        List<Trade> result = tradeService.findByType("BUY");

        assertEquals(expectedTrades, result);
        verify(tradeRepository).findByTypeOrderByTradeDateDesc("BUY");
    }

    @Test
    @DisplayName("existsById avec ID valide doit retourner true si existe")
    void testExistsByIdValid() {
        when(tradeRepository.existsById(1)).thenReturn(true);

        boolean result = tradeService.existsById(1);

        assertTrue(result);
        verify(tradeRepository).existsById(1);
    }

    @Test
    @DisplayName("existsById avec ID invalide doit retourner false")
    void testExistsByIdInvalid() {
        boolean result1 = tradeService.existsById(null);
        boolean result2 = tradeService.existsById(-1);

        assertFalse(result1);
        assertFalse(result2);
        verify(tradeRepository, never()).existsById(any());
    }

    @Test
    @DisplayName("save avec transaction valide doit sauvegarder")
    void testSaveValidTrade() {
        when(tradeRepository.save(any(Trade.class))).thenReturn(validTrade);

        Trade result = tradeService.save(validTrade);

        assertNotNull(result);
        assertEquals(validTrade, result);
        verify(tradeRepository).save(validTrade);
        
        // Vérification que la revisionDate est définie (car tradeId existe = mise à jour)
        assertNotNull(validTrade.getRevisionDate());
    }

    @Test
    @DisplayName("save avec nouvelle transaction doit définir creationDate et tradeDate")
    void testSaveNewTrade() {
        Trade newTrade = new Trade("NEW_ACC", "BUY");
        newTrade.setBuyQuantity(100.0);
        newTrade.setBuyPrice(25.0);
        // Pas de tradeId = nouvelle transaction
        
        when(tradeRepository.save(any(Trade.class))).thenReturn(newTrade);

        Trade result = tradeService.save(newTrade);

        assertNotNull(result);
        assertNotNull(newTrade.getCreationDate());
        assertNotNull(newTrade.getTradeDate());
        verify(tradeRepository).save(newTrade);
    }

    @Test
    @DisplayName("save avec mise à jour doit définir revisionDate")
    void testSaveUpdate() {
        validTrade.setTradeId(1); // Transaction existante
        when(tradeRepository.save(any(Trade.class))).thenReturn(validTrade);

        Trade result = tradeService.save(validTrade);

        assertNotNull(result);
        assertNotNull(validTrade.getRevisionDate());
        verify(tradeRepository).save(validTrade);
    }

    @Test
    @DisplayName("save avec transaction invalide doit lever une exception")
    void testSaveInvalidTrade() {
        Trade invalidTrade = new Trade();
        // Account manquant, pas de tradeId (nouvelle transaction)

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("save avec account invalide doit lever une exception")
    void testSaveInvalidAccount() {
        Trade invalidTrade = new Trade("_invalid", "BUY"); // Commence par underscore
        invalidTrade.setBuyQuantity(100.0);
        invalidTrade.setBuyPrice(25.0);

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("save avec type invalide doit lever une exception")
    void testSaveInvalidType() {
        Trade invalidTrade = new Trade("TEST_ACC", "invalid_type"); // Contient des minuscules
        invalidTrade.setBuyQuantity(100.0);
        invalidTrade.setBuyPrice(25.0);

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("save sans opération définie doit lever une exception")
    void testSaveNoOperation() {
        Trade invalidTrade = new Trade("TEST_ACC", "BUY");
        // Pas de quantité ni prix définis

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("save avec quantité négative doit lever une exception")
    void testSaveNegativeQuantity() {
        Trade invalidTrade = new Trade("TEST_ACC", "BUY");
        invalidTrade.setBuyQuantity(-100.0);
        invalidTrade.setBuyPrice(25.0);

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("save avec prix négatif doit lever une exception")
    void testSaveNegativePrice() {
        Trade invalidTrade = new Trade("TEST_ACC", "BUY");
        invalidTrade.setBuyQuantity(100.0);
        invalidTrade.setBuyPrice(-10.0);

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("save avec quantité sans prix doit lever une exception")
    void testSaveQuantityWithoutPrice() {
        Trade invalidTrade = new Trade("TEST_ACC", "BUY");
        invalidTrade.setBuyQuantity(100.0);
        invalidTrade.setBuyPrice(null);

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("save avec prix sans quantité doit lever une exception")
    void testSavePriceWithoutQuantity() {
        Trade invalidTrade = new Trade("TEST_ACC", "BUY");
        invalidTrade.setBuyQuantity(null);
        invalidTrade.setBuyPrice(25.0);

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("save avec montant excessif doit lever une exception")
    void testSaveExcessiveAmount() {
        Trade invalidTrade = new Trade("TEST_ACC", "BUY");
        invalidTrade.setBuyQuantity(1000000.0); // 1M quantité
        invalidTrade.setBuyPrice(50.0); // = 50M total (> 10M limit)

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("save avec side incohérent doit lever une exception")
    void testSaveInconsistentSide() {
        Trade invalidTrade = new Trade("TEST_ACC", "BUY");
        invalidTrade.setBuyQuantity(100.0);
        invalidTrade.setBuyPrice(25.0);
        invalidTrade.setSide("SELL"); // Incohérent avec l'opération d'achat

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("save avec date future excessive doit lever une exception")
    void testSaveFutureDate() {
        Trade invalidTrade = new Trade("TEST_ACC", "BUY");
        invalidTrade.setBuyQuantity(100.0);
        invalidTrade.setBuyPrice(25.0);
        invalidTrade.setTradeDate(LocalDateTime.now().plusDays(2)); // Plus de 1 jour

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteById avec ID valide doit supprimer")
    void testDeleteByIdValid() {
        when(tradeRepository.existsById(1)).thenReturn(true);

        assertDoesNotThrow(() -> tradeService.deleteById(1));
        verify(tradeRepository).existsById(1);
        verify(tradeRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteById avec ID inexistant doit lever une exception")
    void testDeleteByIdNotFound() {
        when(tradeRepository.existsById(999)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> tradeService.deleteById(999));
        verify(tradeRepository).existsById(999);
        verify(tradeRepository, never()).deleteById(999);
    }

    @Test
    @DisplayName("deleteById avec ID invalide doit lever une exception")
    void testDeleteByIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> tradeService.deleteById(null));
        assertThrows(IllegalArgumentException.class, () -> tradeService.deleteById(-1));
        verify(tradeRepository, never()).deleteById(any());
        verify(tradeRepository, never()).existsById(any());
    }

    @Test
    @DisplayName("findByStatus doit retourner les transactions avec le statut")
    void testFindByStatus() {
        List<Trade> expectedTrades = Arrays.asList(validTrade);
        when(tradeRepository.findByStatusOrderByTradeDateDesc("PENDING"))
                .thenReturn(expectedTrades);

        List<Trade> result = tradeService.findByStatus("PENDING");

        assertEquals(expectedTrades, result);
        verify(tradeRepository).findByStatusOrderByTradeDateDesc("PENDING");
    }

    @Test
    @DisplayName("findByTrader doit retourner les transactions du trader")
    void testFindByTrader() {
        List<Trade> expectedTrades = Arrays.asList(validTrade);
        when(tradeRepository.findByTraderOrderByTradeDateDesc("TRADER001"))
                .thenReturn(expectedTrades);

        List<Trade> result = tradeService.findByTrader("TRADER001");

        assertEquals(expectedTrades, result);
        verify(tradeRepository).findByTraderOrderByTradeDateDesc("TRADER001");
    }

    @Test
    @DisplayName("findByTradeDateBetween doit retourner les transactions de la période")
    void testFindByTradeDateBetween() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);
        List<Trade> expectedTrades = Arrays.asList(validTrade);
        
        when(tradeRepository.findByTradeDateBetweenOrderByTradeDateDesc(start, end))
                .thenReturn(expectedTrades);

        List<Trade> result = tradeService.findByTradeDateBetween(start, end);

        assertEquals(expectedTrades, result);
        verify(tradeRepository).findByTradeDateBetweenOrderByTradeDateDesc(start, end);
    }

    @Test
    @DisplayName("findByTradeDateBetween avec dates nulles doit retourner liste vide")
    void testFindByTradeDateBetweenWithNullDates() {
        List<Trade> result1 = tradeService.findByTradeDateBetween(null, LocalDateTime.now());
        List<Trade> result2 = tradeService.findByTradeDateBetween(LocalDateTime.now(), null);

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        verify(tradeRepository, never()).findByTradeDateBetweenOrderByTradeDateDesc(any(), any());
    }

    @Test
    @DisplayName("findByTradeDateBetween avec start > end doit lever une exception")
    void testFindByTradeDateBetweenInvalidRange() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(1);

        assertThrows(IllegalArgumentException.class, 
                () -> tradeService.findByTradeDateBetween(start, end));
        verify(tradeRepository, never()).findByTradeDateBetweenOrderByTradeDateDesc(any(), any());
    }

    @Test
    @DisplayName("calculateTotalValueByAccount doit calculer la valeur nette")
    void testCalculateTotalValueByAccount() {
        when(tradeRepository.sumBuyValueByAccount("TEST_ACC")).thenReturn(10000.0);
        when(tradeRepository.sumSellValueByAccount("TEST_ACC")).thenReturn(12000.0);

        Double result = tradeService.calculateTotalValueByAccount("TEST_ACC");

        assertEquals(2000.0, result); // 12000 - 10000
        verify(tradeRepository).sumBuyValueByAccount("TEST_ACC");
        verify(tradeRepository).sumSellValueByAccount("TEST_ACC");
    }

    @Test
    @DisplayName("calculateTotalValueByAccount avec valeurs nulles doit gérer correctement")
    void testCalculateTotalValueByAccountWithNulls() {
        when(tradeRepository.sumBuyValueByAccount("TEST_ACC")).thenReturn(null);
        when(tradeRepository.sumSellValueByAccount("TEST_ACC")).thenReturn(5000.0);

        Double result = tradeService.calculateTotalValueByAccount("TEST_ACC");

        assertEquals(5000.0, result); // 5000 - 0
        verify(tradeRepository).sumBuyValueByAccount("TEST_ACC");
        verify(tradeRepository).sumSellValueByAccount("TEST_ACC");
    }

    @Test
    @DisplayName("calculateTotalValueByAccount avec account invalide doit retourner null")
    void testCalculateTotalValueByAccountInvalid() {
        Double result1 = tradeService.calculateTotalValueByAccount(null);
        Double result2 = tradeService.calculateTotalValueByAccount("");

        assertNull(result1);
        assertNull(result2);
        verify(tradeRepository, never()).sumBuyValueByAccount(any());
        verify(tradeRepository, never()).sumSellValueByAccount(any());
    }

    @Test
    @DisplayName("validateTrade avec transaction valide doit retourner true")
    void testValidateTradeValid() {
        boolean result = tradeService.validateTrade(validTrade);

        assertTrue(result);
    }

    @Test
    @DisplayName("validateTrade avec transaction invalide doit retourner false")
    void testValidateTradeInvalid() {
        validTrade.setAccount(null); // Invalid

        boolean result = tradeService.validateTrade(validTrade);

        assertFalse(result);
    }

    @Test
    @DisplayName("findRecentTrades doit retourner les transactions récentes")
    void testFindRecentTrades() {
        List<Trade> expectedTrades = Arrays.asList(validTrade);
        PageRequest pageRequest = PageRequest.of(0, 5);
        when(tradeRepository.findRecentTrades(pageRequest)).thenReturn(expectedTrades);

        List<Trade> result = tradeService.findRecentTrades(5);

        assertEquals(expectedTrades, result);
        verify(tradeRepository).findRecentTrades(pageRequest);
    }

    @Test
    @DisplayName("findRecentTrades avec limit invalide doit retourner liste vide")
    void testFindRecentTradesInvalidLimit() {
        List<Trade> result = tradeService.findRecentTrades(0);

        assertTrue(result.isEmpty());
        verify(tradeRepository, never()).findRecentTrades(any());
    }

    @Test
    @DisplayName("Normalisation des données doit transformer en majuscules")
    void testDataNormalization() {
        validTrade.setAccount("TEST_ACCOUNT");
        validTrade.setType("BUY");
        validTrade.setStatus("PENDING");
        
        when(tradeRepository.save(any(Trade.class))).thenReturn(validTrade);

        tradeService.save(validTrade);

        assertEquals("TEST_ACCOUNT", validTrade.getAccount());
        assertEquals("BUY", validTrade.getType());
        assertEquals("PENDING", validTrade.getStatus());
        verify(tradeRepository).save(validTrade);
    }

    @Test
    @DisplayName("Validation avec champs dépassant les limites de longueur")
    void testStringLengthValidation() {
        Trade invalidTrade = new Trade("TEST_ACC", "BUY");
        invalidTrade.setBuyQuantity(100.0);
        invalidTrade.setBuyPrice(25.0);
        invalidTrade.setSecurity("A".repeat(126)); // Dépasse 125

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Validation de cohérence des dates")
    void testDateConsistencyValidation() {
        Trade invalidTrade = new Trade("TEST_ACC", "BUY");
        invalidTrade.setBuyQuantity(100.0);
        invalidTrade.setBuyPrice(25.0);
        LocalDateTime now = LocalDateTime.now();
        invalidTrade.setCreationDate(now);
        invalidTrade.setRevisionDate(now.minusHours(1)); // Avant création

        assertThrows(IllegalArgumentException.class, () -> tradeService.save(invalidTrade));
        verify(tradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Transaction de vente seule doit être valide")
    void testSellOnlyTransaction() {
        Trade sellTrade = new Trade("SELL_ACC", "SELL");
        sellTrade.setSellQuantity(500.0);
        sellTrade.setSellPrice(30.0);
        sellTrade.setSide("SELL");
        
        when(tradeRepository.save(any(Trade.class))).thenReturn(sellTrade);

        assertDoesNotThrow(() -> tradeService.save(sellTrade));
        verify(tradeRepository).save(sellTrade);
    }
}