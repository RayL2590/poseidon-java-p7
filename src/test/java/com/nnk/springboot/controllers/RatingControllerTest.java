package com.nnk.springboot.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.dto.RatingDTO;
import com.nnk.springboot.mapper.RatingMapper;
import com.nnk.springboot.services.IRatingService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour le contrôleur RatingController.
 * 
 * <p>Cette classe de tests valide le comportement du contrôleur pour la gestion des notations de crédit.
 * Elle couvre l'ensemble des opérations CRUD avec une attention particulière aux spécificités financières
 * comme la validation des formats de notation, la cohérence inter-agences et l'intégrité des données.</p>
 * 
 * <p>Scénarios de test couverts :</p>
 * <ul>
 *   <li><strong>Happy Path</strong> : Flux nominaux d'utilisation</li>
 *   <li><strong>Validation métier</strong> : Contraintes spécifiques aux notations financières</li>
 *   <li><strong>Gestion d'erreurs</strong> : Cas d'échec et récupération gracieuse</li>
 *   <li><strong>Cas limites</strong> : Valeurs extrêmes et cas particuliers</li>
 *   <li><strong>API endpoints</strong> : Tests des endpoints REST</li>
 *   <li><strong>Filtres</strong> : Tests des fonctionnalités de filtrage par type</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */
@WebMvcTest(controllers = RatingController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRatingService ratingService;
    
    @MockBean
    private RatingMapper ratingMapper;
    
    private Rating ratingEntity;
    private RatingDTO ratingDTO;
    private List<Rating> ratingEntities;

    @BeforeEach
    void setUp() {
        // Données de test représentatives des notations de crédit
        ratingEntity = new Rating("Aaa", "AAA", "AAA", 1);
        ratingEntity.setId(1);
        
        ratingDTO = new RatingDTO();
        ratingDTO.setId(1);
        ratingDTO.setMoodysRating("Aaa");
        ratingDTO.setSandPRating("AAA");
        ratingDTO.setFitchRating("AAA");
        ratingDTO.setOrderNumber(1);
        
        // Liste de notations pour les tests de liste
        ratingEntities = Arrays.asList(
            new Rating("Aaa", "AAA", "AAA", 1),    // Investment Grade - Best
            new Rating("Baa3", "BBB-", "BBB-", 10), // Investment Grade - Lowest
            new Rating("Ba1", "BB+", "BB+", 11)     // Speculative Grade
        );
        ratingEntities.get(0).setId(1);
        ratingEntities.get(1).setId(2);
        ratingEntities.get(2).setId(3);
        
        // Configuration des mocks du mapper par défaut
        when(ratingMapper.toDTO(any(Rating.class))).thenAnswer(invocation -> {
            Rating rating = invocation.getArgument(0);
            RatingDTO dto = new RatingDTO();
            dto.setId(rating.getId());
            dto.setMoodysRating(rating.getMoodysRating());
            dto.setSandPRating(rating.getSandPRating());
            dto.setFitchRating(rating.getFitchRating());
            dto.setOrderNumber(rating.getOrderNumber());
            return dto;
        });
        
        when(ratingMapper.toEntity(any(RatingDTO.class))).thenAnswer(invocation -> {
            RatingDTO dto = invocation.getArgument(0);
            Rating entity = new Rating(dto.getMoodysRating(), dto.getSandPRating(), dto.getFitchRating(), dto.getOrderNumber());
            entity.setId(dto.getId());
            return entity;
        });
    }

    // ================== TESTS HAPPY PATH ==================

    @Test
    void home_ShouldReturnListView() throws Exception {
        // Given
        when(ratingService.findAll()).thenReturn(ratingEntities);
        
        // When & Then
        mockMvc.perform(get("/rating/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attributeExists("ratings"))
                .andExpect(model().attribute("ratings", org.hamcrest.Matchers.hasSize(3)));
        
        verify(ratingService).findAll();
    }
    
    @Test
    void home_WithSuccessParam_ShouldAddSuccessMessage() throws Exception {
        // Given
        when(ratingService.findAll()).thenReturn(ratingEntities);
        
        // When & Then
        mockMvc.perform(get("/rating/list").param("success", "Credit rating added successfully"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attribute("successMessage", "Credit rating added successfully"));
        
        mockMvc.perform(get("/rating/list").param("success", "Credit rating updated successfully"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("successMessage", "Credit rating updated successfully"));
        
        mockMvc.perform(get("/rating/list").param("success", "Credit rating deleted successfully"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("successMessage", "Credit rating deleted successfully"));
    }
    
    @Test
    void home_WithErrorParam_ShouldAddErrorMessage() throws Exception {
        // Given
        when(ratingService.findAll()).thenReturn(ratingEntities);
        
        // When & Then
        mockMvc.perform(get("/rating/list").param("error", "notfound"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attribute("errorMessage", "Credit rating not found"));
        
        mockMvc.perform(get("/rating/list").param("error", "invalid"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", "Invalid rating ID provided"));
        
        mockMvc.perform(get("/rating/list").param("error", "unexpected"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", "An unexpected error occurred"));
    }
    
    @Test
    void home_WithServiceException_ShouldHandleError() throws Exception {
        // Given
        when(ratingService.findAll()).thenThrow(new RuntimeException("Database connection failed"));
        
        // When & Then
        mockMvc.perform(get("/rating/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("Error loading credit ratings")))
                .andExpect(model().attribute("ratings", org.hamcrest.Matchers.hasSize(0)));
    }
    
    @Test
    void addRatingForm_ShouldReturnAddView() throws Exception {
        // When & Then
        mockMvc.perform(get("/rating/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(model().attributeExists("ratingDTO"))
                .andExpect(model().attribute("ratingDTO", org.hamcrest.Matchers.isA(RatingDTO.class)));
    }
    
    @Test
    void showUpdateForm_WithValidId_ShouldReturnUpdateView() throws Exception {
        // Given
        when(ratingService.findById(1)).thenReturn(Optional.of(ratingEntity));
        when(ratingMapper.toDTO(ratingEntity)).thenReturn(ratingDTO);
        
        // When & Then
        mockMvc.perform(get("/rating/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(model().attributeExists("ratingDTO"))
                .andExpect(model().attribute("ratingDTO", org.hamcrest.Matchers.hasProperty("moodysRating", org.hamcrest.Matchers.is("Aaa"))));
        
        verify(ratingService).findById(1);
        verify(ratingMapper).toDTO(ratingEntity);
    }
    
    @Test
    void showUpdateForm_WithInvalidId_ShouldRedirectToList() throws Exception {
        // When & Then
        mockMvc.perform(get("/rating/update/0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list?error=notfound"));
        
        verify(ratingService, never()).findById(anyInt());
    }
    
    @Test
    void showUpdateForm_WithNonExistentId_ShouldRedirectWithError() throws Exception {
        // Given
        when(ratingService.findById(999)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/rating/update/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list?error=notfound"));
        
        verify(ratingService).findById(999);
    }

    // ================== TESTS SPÉCIFIQUES FINANCIERS ==================
    
    @Test
    void validate_WithValidInvestmentGradeRating_ShouldCreateRating() throws Exception {
        // Given - Notation Investment Grade complète
        Rating savedRating = new Rating("Aa1", "AA+", "AA+", 3);
        savedRating.setId(5);
        
        when(ratingMapper.toEntity(any(RatingDTO.class))).thenReturn(ratingEntity);
        when(ratingService.save(any(Rating.class))).thenReturn(savedRating);
        
        // When & Then
        mockMvc.perform(post("/rating/validate")
                .param("moodysRating", "Aa1")
                .param("sandPRating", "AA+")
                .param("fitchRating", "AA+")
                .param("orderNumber", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list?success=Credit+rating+added+successfully"));
        
        verify(ratingMapper).toEntity(any(RatingDTO.class));
        verify(ratingService).save(any(Rating.class));
    }
    
    @Test
    void validate_WithValidSpeculativeGradeRating_ShouldCreateRating() throws Exception {
        // Given - Notation Speculative Grade
        when(ratingMapper.toEntity(any(RatingDTO.class))).thenReturn(ratingEntity);
        when(ratingService.save(any(Rating.class))).thenReturn(ratingEntity);
        
        // When & Then
        mockMvc.perform(post("/rating/validate")
                .param("moodysRating", "B1")
                .param("sandPRating", "B+")
                .param("fitchRating", "B+")
                .param("orderNumber", "15"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list?success=Credit+rating+added+successfully"));
    }
    
    @Test
    void validate_WithPartialRatings_ShouldCreateRating() throws Exception {
        // Given - Notation avec seulement certaines agences (cas réaliste)
        when(ratingMapper.toEntity(any(RatingDTO.class))).thenReturn(ratingEntity);
        when(ratingService.save(any(Rating.class))).thenReturn(ratingEntity);
        
        // When & Then - Seulement Moody's et S&P, pas Fitch
        mockMvc.perform(post("/rating/validate")
                .param("moodysRating", "Baa2")
                .param("sandPRating", "BBB")
                .param("fitchRating", "")  // Pas de notation Fitch
                .param("orderNumber", "8"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list?success=Credit+rating+added+successfully"));
    }
    
    @Test
    void validate_WithValidationErrors_ShouldReturnAddView() throws Exception {
        // When & Then - Order number négatif (violation de @Min(1))
        mockMvc.perform(post("/rating/validate")
                .param("moodysRating", "Aaa")
                .param("sandPRating", "AAA")
                .param("fitchRating", "AAA")
                .param("orderNumber", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(model().attributeExists("ratingDTO"))
                .andExpect(model().hasErrors());
        
        verify(ratingService, never()).save(any(Rating.class));
    }
    
    @Test
    void validate_WithInvalidMoodysRating_ShouldReturnAddViewWithErrors() throws Exception {
        // When & Then - Format Moody's invalide
        mockMvc.perform(post("/rating/validate")
                .param("moodysRating", "AAA")  // Format S&P dans champ Moody's
                .param("sandPRating", "AAA")
                .param("fitchRating", "AAA")
                .param("orderNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(model().hasErrors());
        
        verify(ratingService, never()).save(any(Rating.class));
    }
    
    @Test
    void validate_WithInvalidOrderNumber_ShouldReturnAddViewWithErrors() throws Exception {
        // When & Then - Order number négatif ou zéro
        mockMvc.perform(post("/rating/validate")
                .param("moodysRating", "Aaa")
                .param("sandPRating", "AAA")
                .param("fitchRating", "AAA")
                .param("orderNumber", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(model().hasErrors());
        
        verify(ratingService, never()).save(any(Rating.class));
    }
    
    @Test
    void validate_WithBusinessValidationError_ShouldReturnAddViewWithError() throws Exception {
        // Given
        when(ratingMapper.toEntity(any(RatingDTO.class))).thenReturn(ratingEntity);
        when(ratingService.save(any(Rating.class)))
            .thenThrow(new IllegalArgumentException("Order number already exists"));
        
        // When & Then
        mockMvc.perform(post("/rating/validate")
                .param("moodysRating", "Aaa")
                .param("sandPRating", "AAA")
                .param("fitchRating", "AAA")
                .param("orderNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/add"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Order number already exists"));
    }

    // ================== TESTS UPDATE ==================
    
    @Test
    void updateRating_WithValidData_ShouldUpdate() throws Exception {
        // Given - Mise à jour d'une notation
        when(ratingService.findById(1)).thenReturn(Optional.of(ratingEntity));
        when(ratingService.save(any(Rating.class))).thenReturn(ratingEntity);
        
        // When & Then - Dégradation de AAA à AA+
        mockMvc.perform(post("/rating/update/1")
                .param("moodysRating", "Aa1")      // Dégradation
                .param("sandPRating", "AA+")       // Dégradation
                .param("fitchRating", "AA+")       // Dégradation
                .param("orderNumber", "3"))        // Nouvel ordre
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list?success=Credit+rating+updated+successfully"));
        
        verify(ratingService).findById(1);
        verify(ratingMapper).updateEntityFromDTO(any(Rating.class), any(RatingDTO.class));
        verify(ratingService).save(any(Rating.class));
    }
    
    @Test
    void updateRating_WithValidationErrors_ShouldReturnUpdateView() throws Exception {
        // When & Then - Order number invalide
        mockMvc.perform(post("/rating/update/1")
                .param("moodysRating", "Aaa")
                .param("sandPRating", "AAA")
                .param("fitchRating", "AAA")
                .param("orderNumber", "-1"))    // Invalide
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(model().attributeExists("ratingDTO"))
                .andExpect(model().hasErrors());
        
        verify(ratingService, never()).save(any(Rating.class));
    }
    
    @Test
    void updateRating_WithNonExistentId_ShouldReturnUpdateViewWithError() throws Exception {
        // Given
        when(ratingService.findById(999)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(post("/rating/update/999")
                .param("moodysRating", "Aaa")
                .param("sandPRating", "AAA")
                .param("fitchRating", "AAA")
                .param("orderNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("Rating not found with id")));
        
        verify(ratingService).findById(999);
        verify(ratingService, never()).save(any(Rating.class));
    }

    // ================== TESTS DELETE ==================
    
    @Test
    void deleteRating_WithValidId_ShouldRedirectWithSuccess() throws Exception {
        // Given
        when(ratingService.findById(1)).thenReturn(Optional.of(ratingEntity));
        
        // When & Then
        mockMvc.perform(get("/rating/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list?success=Credit+rating+deleted+successfully"));
        
        verify(ratingService).deleteById(1);
    }
    
    @Test
    void deleteRating_WithInvalidId_ShouldRedirectWithError() throws Exception {
        // When & Then
        mockMvc.perform(get("/rating/delete/0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list?error=notfound"));
        
        verify(ratingService, never()).deleteById(anyInt());
    }
    
    @Test
    void deleteRating_WithNonExistentId_ShouldRedirectWithError() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Rating not found with id: 999"))
            .when(ratingService).deleteById(999);
        
        // When & Then
        mockMvc.perform(get("/rating/delete/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list?error=notfound"));
        
        verify(ratingService).deleteById(999);
    }
    
    @Test
    void deleteRating_WithServiceException_ShouldRedirectWithError() throws Exception {
        // Given
        doThrow(new RuntimeException("Foreign key constraint violation"))
            .when(ratingService).deleteById(1);
        
        // When & Then
        mockMvc.perform(get("/rating/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list?error=unexpected"));
        
        verify(ratingService).deleteById(1);
    }

    // ================== TESTS FILTRAGE ==================
    
    @Test
    void filterByType_InvestmentGrade_ShouldReturnFilteredRatings() throws Exception {
        // Given
        List<Rating> investmentGradeRatings = Arrays.asList(
            ratingEntities.get(0), // Aaa/AAA
            ratingEntities.get(1)  // Baa3/BBB-
        );
        when(ratingService.findInvestmentGrade()).thenReturn(investmentGradeRatings);
        
        // When & Then
        mockMvc.perform(get("/rating/filter").param("type", "investment"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attribute("ratings", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(model().attribute("filterDescription", "Investment Grade Ratings (BBB-/Baa3 and above)"))
                .andExpect(model().attribute("activeFilter", "investment"));
        
        verify(ratingService).findInvestmentGrade();
    }
    
    @Test
    void filterByType_SpeculativeGrade_ShouldReturnFilteredRatings() throws Exception {
        // Given
        List<Rating> speculativeGradeRatings = Arrays.asList(ratingEntities.get(2)); // Ba1/BB+
        when(ratingService.findSpeculativeGrade()).thenReturn(speculativeGradeRatings);
        
        // When & Then
        mockMvc.perform(get("/rating/filter").param("type", "speculative"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attribute("ratings", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(model().attribute("filterDescription", "Speculative Grade Ratings (BB+/Ba1 and below)"))
                .andExpect(model().attribute("activeFilter", "speculative"));
        
        verify(ratingService).findSpeculativeGrade();
    }
    
    @Test
    void filterByType_All_ShouldReturnAllRatings() throws Exception {
        // Given
        when(ratingService.findAll()).thenReturn(ratingEntities);
        
        // When & Then
        mockMvc.perform(get("/rating/filter").param("type", "all"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attribute("ratings", org.hamcrest.Matchers.hasSize(3)))
                .andExpect(model().attribute("filterDescription", "All Credit Ratings"))
                .andExpect(model().attribute("activeFilter", "all"));
        
        verify(ratingService).findAll();
    }

    // ================== TESTS API ==================
    
    @Test
    void getRatingsApi_WithoutAgencyParam_ShouldReturnAllRatings() throws Exception {
        // Given
        when(ratingService.findAll()).thenReturn(ratingEntities);
        when(ratingMapper.toDTO(any(Rating.class))).thenReturn(ratingDTO);
        
        // When & Then
        mockMvc.perform(get("/rating/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(3)));
        
        verify(ratingService).findAll();
    }
    
    @Test
    void getRatingsApi_WithAgencyParam_ShouldReturnFilteredRatings() throws Exception {
        // Given
        when(ratingService.findByAgency("MOODYS")).thenReturn(ratingEntities);
        when(ratingMapper.toDTO(any(Rating.class))).thenReturn(ratingDTO);
        
        // When & Then
        mockMvc.perform(get("/rating/api").param("agency", "moodys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(3)));
        
        verify(ratingService).findByAgency("MOODYS");
    }
    
    @Test
    void getRatingsApi_WithServiceException_ShouldReturnEmptyList() throws Exception {
        // Given
        when(ratingService.findAll()).thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        mockMvc.perform(get("/rating/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
    }

    // ================== TESTS EDGE CASES ==================
    
    @Test
    void showUpdateForm_WithServiceException_ShouldRedirectWithError() throws Exception {
        // Given
        when(ratingService.findById(1)).thenThrow(new RuntimeException("Database timeout"));
        
        // When & Then
        mockMvc.perform(get("/rating/update/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list?error=unexpected"));
        
        verify(ratingService).findById(1);
    }
    
    @Test
    void updateRating_WithServiceException_ShouldReturnUpdateViewWithError() throws Exception {
        // Given
        when(ratingService.findById(1)).thenReturn(Optional.of(ratingEntity));
        when(ratingService.save(any(Rating.class)))
            .thenThrow(new RuntimeException("Constraint violation"));
        
        // When & Then
        mockMvc.perform(post("/rating/update/1")
                .param("moodysRating", "Aaa")
                .param("sandPRating", "AAA")
                .param("fitchRating", "AAA")
                .param("orderNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/update"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("An unexpected error occurred")));
    }
    
    @Test
    void handleGenericException_ShouldReturnErrorView() throws Exception {
        // Given
        when(ratingService.findAll()).thenThrow(new OutOfMemoryError("Memory exhausted"));
        
        // When & Then
        mockMvc.perform(get("/rating/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("An unexpected error occurred")))
                .andExpect(model().attribute("ratings", org.hamcrest.Matchers.hasSize(0)));
    }
    
    @Test
    void home_WithBothSuccessAndErrorParams_ShouldShowBothMessages() throws Exception {
        // Given
        when(ratingService.findAll()).thenReturn(ratingEntities);
        
        // When & Then
        mockMvc.perform(get("/rating/list")
                .param("success", "Rating created")
                .param("error", "Warning: data inconsistency"))
                .andExpect(status().isOk())
                .andExpect(view().name("rating/list"))
                .andExpect(model().attribute("successMessage", "Rating created"))
                .andExpect(model().attribute("errorMessage", "Warning: data inconsistency"));
    }
}