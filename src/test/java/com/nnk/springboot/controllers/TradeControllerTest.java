package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.dto.TradeDTO;
import com.nnk.springboot.mapper.TradeMapper;
import com.nnk.springboot.services.ITradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TradeController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class
})
@DisplayName("TradeController Tests")
public class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITradeService tradeService;

    @MockBean
    private TradeMapper tradeMapper;

    private Trade testTrade;
    private TradeDTO testTradeDTO;
    private List<Trade> testTrades;
    private List<TradeDTO> testTradeDTOs;

    @BeforeEach
    void setUp() {
        testTrade = new Trade();
        testTrade.setTradeId(1);
        testTrade.setAccount("TEST_ACCOUNT");
        testTrade.setType("BUY");
        testTrade.setBuyQuantity(1000.0);
        testTrade.setBuyPrice(25.50);
        testTrade.setTradeDate(LocalDateTime.now());
        testTrade.setStatus("PENDING");

        testTradeDTO = new TradeDTO();
        testTradeDTO.setTradeId(1);
        testTradeDTO.setAccount("TEST_ACCOUNT");
        testTradeDTO.setType("BUY");
        testTradeDTO.setBuyQuantity(1000.0);
        testTradeDTO.setBuyPrice(25.50);
        testTradeDTO.setTradeDate(LocalDateTime.now());
        testTradeDTO.setStatus("PENDING");

        testTrades = Arrays.asList(testTrade);
        testTradeDTOs = Arrays.asList(testTradeDTO);
    }

    @Test
    @DisplayName("GET /trade/list doit afficher la liste des transactions")
    @WithMockUser
    void testTradeList() throws Exception {
        when(tradeService.findAll()).thenReturn(testTrades);
        when(tradeMapper.toDTO(testTrade)).thenReturn(testTradeDTO);

        mockMvc.perform(get("/trade/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("trades"));

        verify(tradeService).findAll();
        verify(tradeMapper).toDTO(testTrade);
    }

    @Test
    @DisplayName("GET /trade/list avec message de succès doit afficher le message")
    @WithMockUser
    void testTradeListWithSuccessMessage() throws Exception {
        when(tradeService.findAll()).thenReturn(testTrades);
        when(tradeMapper.toDTO(testTrade)).thenReturn(testTradeDTO);

        mockMvc.perform(get("/trade/list").param("success", "Transaction created successfully"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success", "Transaction created successfully"));

        verify(tradeService).findAll();
    }

    @Test
    @DisplayName("GET /trade/list avec erreur service doit afficher message d'erreur")
    @WithMockUser
    void testTradeListWithServiceError() throws Exception {
        when(tradeService.findAll()).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/trade/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attributeExists("trades"));

        verify(tradeService).findAll();
    }

    @Test
    @DisplayName("GET /trade/add doit afficher le formulaire de création")
    @WithMockUser
    void testAddTradeForm() throws Exception {
        mockMvc.perform(get("/trade/add"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("tradeDTO"));
    }

    @Test
    @DisplayName("POST /trade/validate avec données valides doit créer et rediriger")
    @WithMockUser
    void testValidateTradeValid() throws Exception {
        when(tradeMapper.toEntity(any(TradeDTO.class))).thenReturn(testTrade);
        when(tradeService.save(any(Trade.class))).thenReturn(testTrade);

        mockMvc.perform(post("/trade/validate")
                .param("account", "TEST_ACCOUNT")
                .param("type", "BUY")
                .param("buyQuantity", "1000.0")
                .param("buyPrice", "25.50")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/trade/list?success=*"));

        verify(tradeMapper).toEntity(any(TradeDTO.class));
        verify(tradeService).save(any(Trade.class));
    }

    @Test
    @DisplayName("POST /trade/validate avec erreurs de validation doit retourner au formulaire")
    @WithMockUser
    void testValidateTradeWithValidationErrors() throws Exception {
        mockMvc.perform(post("/trade/validate")
                .param("account", "") // Account vide - erreur de validation
                .param("type", "BUY")
                .param("buyQuantity", "1000.0")
                .param("buyPrice", "25.50")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("tradeDTO"));

        verify(tradeService, never()).save(any());
    }

    @Test
    @DisplayName("POST /trade/validate avec erreur métier doit retourner au formulaire avec erreur")
    @WithMockUser
    void testValidateTradeWithBusinessError() throws Exception {
        when(tradeMapper.toEntity(any(TradeDTO.class))).thenReturn(testTrade);
        when(tradeService.save(any(Trade.class)))
                .thenThrow(new IllegalArgumentException("Invalid trade data"));

        mockMvc.perform(post("/trade/validate")
                .param("account", "TEST_ACCOUNT")
                .param("type", "BUY")
                .param("buyQuantity", "1000.0")
                .param("buyPrice", "25.50")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Invalid trade data"));

        verify(tradeService).save(any(Trade.class));
    }

    @Test
    @DisplayName("POST /trade/validate avec erreur inattendue doit retourner au formulaire")
    @WithMockUser
    void testValidateTradeWithUnexpectedError() throws Exception {
        when(tradeMapper.toEntity(any(TradeDTO.class))).thenReturn(testTrade);
        when(tradeService.save(any(Trade.class)))
                .thenThrow(new RuntimeException("Database connection lost"));

        mockMvc.perform(post("/trade/validate")
                .param("account", "TEST_ACCOUNT")
                .param("type", "BUY")
                .param("buyQuantity", "1000.0")
                .param("buyPrice", "25.50")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));

        verify(tradeService).save(any(Trade.class));
    }

    @Test
    @DisplayName("GET /trade/update/{id} avec ID valide doit afficher le formulaire de modification")
    @WithMockUser
    void testShowUpdateFormValid() throws Exception {
        when(tradeService.findById(1)).thenReturn(Optional.of(testTrade));
        when(tradeMapper.toDTO(testTrade)).thenReturn(testTradeDTO);

        mockMvc.perform(get("/trade/update/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("trade"))
                .andExpect(model().attribute("trade", testTradeDTO));

        verify(tradeService).findById(1);
        verify(tradeMapper).toDTO(testTrade);
    }

    @Test
    @DisplayName("GET /trade/update/{id} avec ID inexistant doit rediriger avec erreur")
    @WithMockUser
    void testShowUpdateFormNotFound() throws Exception {
        when(tradeService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/trade/update/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/trade/list?error=*"));

        verify(tradeService).findById(999);
        verify(tradeMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("GET /trade/update/{id} avec erreur service doit rediriger avec erreur")
    @WithMockUser
    void testShowUpdateFormWithServiceError() throws Exception {
        when(tradeService.findById(1)).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/trade/update/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/trade/list?error=*"));

        verify(tradeService).findById(1);
    }

    @Test
    @DisplayName("POST /trade/update/{id} avec données valides doit mettre à jour et rediriger")
    @WithMockUser
    void testUpdateTradeValid() throws Exception {
        when(tradeService.findById(1)).thenReturn(Optional.of(testTrade));
        when(tradeService.save(any(Trade.class))).thenReturn(testTrade);

        mockMvc.perform(post("/trade/update/1")
                .param("account", "UPDATED_ACCOUNT")
                .param("type", "SELL")
                .param("sellQuantity", "500.0")
                .param("sellPrice", "30.00")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/trade/list?success=*"));

        verify(tradeService).findById(1);
        verify(tradeMapper).updateEntityFromDTO(eq(testTrade), any(TradeDTO.class));
        verify(tradeService).save(testTrade);
    }

    @Test
    @DisplayName("POST /trade/update/{id} avec erreurs de validation doit retourner au formulaire")
    @WithMockUser
    void testUpdateTradeWithValidationErrors() throws Exception {
        mockMvc.perform(post("/trade/update/1")
                .param("account", "") // Account vide
                .param("type", "SELL")
                .param("sellQuantity", "500.0")
                .param("sellPrice", "30.00")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("trade"));

        verify(tradeService, never()).findById(any());
        verify(tradeService, never()).save(any());
    }

    @Test
    @DisplayName("POST /trade/update/{id} avec transaction inexistante doit retourner erreur")
    @WithMockUser
    void testUpdateTradeNotFound() throws Exception {
        when(tradeService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(post("/trade/update/999")
                .param("account", "UPDATED_ACCOUNT")
                .param("type", "SELL")
                .param("sellQuantity", "500.0")
                .param("sellPrice", "30.00")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));

        verify(tradeService).findById(999);
        verify(tradeService, never()).save(any());
    }

    @Test
    @DisplayName("POST /trade/update/{id} avec erreur métier doit retourner au formulaire")
    @WithMockUser
    void testUpdateTradeWithBusinessError() throws Exception {
        when(tradeService.findById(1)).thenReturn(Optional.of(testTrade));
        when(tradeService.save(any(Trade.class)))
                .thenThrow(new IllegalArgumentException("Invalid update data"));

        mockMvc.perform(post("/trade/update/1")
                .param("account", "UPDATED_ACCOUNT")
                .param("type", "SELL")
                .param("sellQuantity", "500.0")
                .param("sellPrice", "30.00")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Invalid update data"));

        verify(tradeService).save(any(Trade.class));
    }

    @Test
    @DisplayName("GET /trade/delete/{id} avec ID valide doit supprimer et rediriger")
    @WithMockUser
    void testDeleteTradeValid() throws Exception {
        when(tradeService.findById(1)).thenReturn(Optional.of(testTrade));

        mockMvc.perform(get("/trade/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/trade/list?success=*"));

        verify(tradeService).findById(1);
        verify(tradeService).deleteById(1);
    }

    @Test
    @DisplayName("GET /trade/delete/{id} avec ID inexistant doit rediriger avec erreur")
    @WithMockUser
    void testDeleteTradeNotFound() throws Exception {
        when(tradeService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/trade/delete/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/trade/list?error=*"));

        verify(tradeService).findById(999);
        verify(tradeService, never()).deleteById(any());
    }

    @Test
    @DisplayName("GET /trade/delete/{id} avec erreur service doit rediriger avec erreur")
    @WithMockUser
    void testDeleteTradeWithServiceError() throws Exception {
        when(tradeService.findById(1)).thenReturn(Optional.of(testTrade));
        doThrow(new RuntimeException("Delete failed")).when(tradeService).deleteById(1);

        mockMvc.perform(get("/trade/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/trade/list?error=*"));

        verify(tradeService).findById(1);
        verify(tradeService).deleteById(1);
    }

    @Test
    @DisplayName("Test de validation avec quantités négatives")
    @WithMockUser
    void testValidationWithNegativeQuantities() throws Exception {
        mockMvc.perform(post("/trade/validate")
                .param("account", "TEST_ACCOUNT")
                .param("type", "BUY")
                .param("buyQuantity", "-100.0")
                .param("buyPrice", "25.50")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());

        verify(tradeService, never()).save(any());
    }

    @Test
    @DisplayName("Test de validation avec prix négatifs")
    @WithMockUser
    void testValidationWithNegativePrices() throws Exception {
        mockMvc.perform(post("/trade/validate")
                .param("account", "TEST_ACCOUNT")
                .param("type", "BUY")
                .param("buyQuantity", "100.0")
                .param("buyPrice", "-25.50")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());

        verify(tradeService, never()).save(any());
    }

    @Test
    @DisplayName("Test de validation avec account invalide")
    @WithMockUser
    void testValidationWithInvalidAccount() throws Exception {
        mockMvc.perform(post("/trade/validate")
                .param("account", "_invalid_account")
                .param("type", "BUY")
                .param("buyQuantity", "100.0")
                .param("buyPrice", "25.50")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors());

        verify(tradeService, never()).save(any());
    }

    @Test
    @DisplayName("Test avec transaction de vente valide")
    @WithMockUser
    void testValidSellTransaction() throws Exception {
        Trade sellTrade = new Trade("SELL_ACC", "SELL");
        sellTrade.setTradeId(2);
        sellTrade.setSellQuantity(500.0);
        sellTrade.setSellPrice(30.0);

        when(tradeMapper.toEntity(any(TradeDTO.class))).thenReturn(sellTrade);
        when(tradeService.save(any(Trade.class))).thenReturn(sellTrade);

        mockMvc.perform(post("/trade/validate")
                .param("account", "SELL_ACC")
                .param("type", "SELL")
                .param("sellQuantity", "500.0")
                .param("sellPrice", "30.0")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/trade/list?success=*"));

        verify(tradeService).save(any(Trade.class));
    }

    @Test
    @DisplayName("Test avec transaction mixte (achat et vente)")
    @WithMockUser
    void testMixedTransaction() throws Exception {
        Trade mixedTrade = new Trade("MIX_ACC", "SWAP");
        mixedTrade.setTradeId(3);
        mixedTrade.setBuyQuantity(100.0);
        mixedTrade.setBuyPrice(25.0);
        mixedTrade.setSellQuantity(50.0);
        mixedTrade.setSellPrice(30.0);

        when(tradeMapper.toEntity(any(TradeDTO.class))).thenReturn(mixedTrade);
        when(tradeService.save(any(Trade.class))).thenReturn(mixedTrade);

        mockMvc.perform(post("/trade/validate")
                .param("account", "MIX_ACC")
                .param("type", "SWAP")
                .param("buyQuantity", "100.0")
                .param("buyPrice", "25.0")
                .param("sellQuantity", "50.0")
                .param("sellPrice", "30.0")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/trade/list?success=*"));

        verify(tradeService).save(any(Trade.class));
    }

    @Test
    @DisplayName("Test des champs optionnels dans le formulaire")
    @WithMockUser
    void testOptionalFieldsInForm() throws Exception {
        when(tradeMapper.toEntity(any(TradeDTO.class))).thenReturn(testTrade);
        when(tradeService.save(any(Trade.class))).thenReturn(testTrade);

        mockMvc.perform(post("/trade/validate")
                .param("account", "TEST_ACCOUNT")
                .param("type", "BUY")
                .param("buyQuantity", "1000.0")
                .param("buyPrice", "25.50")
                .param("security", "AAPL")
                .param("trader", "TRADER001")
                .param("benchmark", "SP500")
                .param("book", "BOOK_A")
                .param("side", "BUY")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/trade/list?success=*"));

        verify(tradeService).save(any(Trade.class));
    }
}