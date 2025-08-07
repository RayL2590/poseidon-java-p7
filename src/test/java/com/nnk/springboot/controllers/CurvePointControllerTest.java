package com.nnk.springboot.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.dto.CurvePointDTO;
import com.nnk.springboot.mapper.CurvePointMapper;
import com.nnk.springboot.services.ICurvePointService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour le contrôleur CurvePointController.
 * 
 * <p>Cette classe de tests valide le comportement du contrôleur pour la gestion des points de courbe financière.
 * Elle couvre l'ensemble des opérations CRUD avec une attention particulière aux spécificités financières
 * comme la validation des termes, la précision des valeurs et l'intégrité des courbes.</p>
 * 
 * <p>Scénarios de test couverts :</p>
 * <ul>
 *   <li><strong>Happy Path</strong> : Flux nominaux d'utilisation</li>
 *   <li><strong>Validation métier</strong> : Contraintes spécifiques aux données financières</li>
 *   <li><strong>Gestion d'erreurs</strong> : Cas d'échec et récupération gracieuse</li>
 *   <li><strong>Cas limites</strong> : Valeurs extrêmes et cas particuliers</li>
 *   <li><strong>Intégrité des courbes</strong> : Cohérence des données de courbe</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */
@WebMvcTest(controllers = CurvePointController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class CurvePointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICurvePointService curvePointService;
    
    @MockBean
    private CurvePointMapper curvePointMapper;
    
    private CurvePoint curvePointEntity;
    private CurvePointDTO curvePointDTO;
    private List<CurvePoint> curvePointEntities;

    @BeforeEach
    void setUp() {
        // Données de test représentatives d'une courbe de taux financière
        curvePointEntity = new CurvePoint(1, 1.0, 2.5000); // 1 an à 2.50%
        curvePointEntity.setId(1);
        
        curvePointDTO = new CurvePointDTO(1, 1.0, 2.5000);
        curvePointDTO.setId(1);
        
        // Courbe de taux complète pour les tests de liste
        curvePointEntities = Arrays.asList(
            new CurvePoint(1, 0.2500, 1.7500), // 3M : 1.75%
            new CurvePoint(1, 1.0000, 2.5000), // 1Y : 2.50%
            new CurvePoint(1, 5.0000, 3.2500)  // 5Y : 3.25%
        );
        curvePointEntities.get(0).setId(1);
        curvePointEntities.get(1).setId(2);
        curvePointEntities.get(2).setId(3);
    }

    // ================== TESTS HAPPY PATH ==================

    @Test
    void home_ShouldReturnListView() throws Exception {
        // Given
        when(curvePointService.findAll()).thenReturn(curvePointEntities);
        
        // When & Then
        mockMvc.perform(get("/curvePoint/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/list"))
                .andExpect(model().attributeExists("curvePoints"))
                .andExpect(model().attribute("curvePoints", org.hamcrest.Matchers.hasSize(3)));
        
        verify(curvePointService).findAll();
    }
    
    @Test
    void home_WithSuccessParam_ShouldAddSuccessMessage() throws Exception {
        // Given
        when(curvePointService.findAll()).thenReturn(curvePointEntities);
        
        // When & Then - Test success message
        mockMvc.perform(get("/curvePoint/list").param("success", "Curve Point added successfully"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/list"))
                .andExpect(model().attribute("successMessage", "Curve Point added successfully"));
        
        mockMvc.perform(get("/curvePoint/list").param("success", "Curve Point updated successfully"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("successMessage", "Curve Point updated successfully"));
        
        mockMvc.perform(get("/curvePoint/list").param("success", "Curve Point deleted successfully"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("successMessage", "Curve Point deleted successfully"));
    }
    
    @Test
    void home_WithErrorParam_ShouldAddErrorMessage() throws Exception {
        // Given
        when(curvePointService.findAll()).thenReturn(curvePointEntities);
        
        // When & Then
        mockMvc.perform(get("/curvePoint/list").param("error", "Test error message"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/list"))
                .andExpect(model().attribute("errorMessage", "Test error message"));
    }
    
    @Test
    void home_WithServiceException_ShouldHandleError() throws Exception {
        // Given
        when(curvePointService.findAll()).thenThrow(new RuntimeException("Database connection failed"));
        
        // When & Then
        mockMvc.perform(get("/curvePoint/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("Error loading curve points")))
                .andExpect(model().attribute("curvePoints", org.hamcrest.Matchers.hasSize(0)));
    }
    
    @Test
    void addCurvePointForm_ShouldReturnAddView() throws Exception {
        // When & Then
        mockMvc.perform(get("/curvePoint/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().attributeExists("curvePointDTO"))
                .andExpect(model().attribute("curvePointDTO", org.hamcrest.Matchers.isA(CurvePointDTO.class)));
    }
    
    @Test
    void showUpdateForm_WithValidId_ShouldReturnUpdateView() throws Exception {
        // Given
        when(curvePointService.findById(1)).thenReturn(Optional.of(curvePointEntity));
        when(curvePointMapper.toDTO(curvePointEntity)).thenReturn(curvePointDTO);
        
        // When & Then
        mockMvc.perform(get("/curvePoint/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(model().attributeExists("curvePointDTO"))
                .andExpect(model().attribute("curvePointDTO", org.hamcrest.Matchers.hasProperty("curveId", org.hamcrest.Matchers.is(1))));
        
        verify(curvePointService).findById(1);
        verify(curvePointMapper).toDTO(curvePointEntity);
    }
    
    @Test
    void showUpdateForm_WithNonExistentId_ShouldRedirectWithError() throws Exception {
        // Given
        when(curvePointService.findById(999)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/curvePoint/update/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"));
        
        verify(curvePointService).findById(999);
    }
    
    // ================== TESTS SPÉCIFIQUES FINANCIERS ==================
    
    @Test
    void validate_WithValidFinancialData_ShouldCreateCurvePoint() throws Exception {
        // Given - Données représentatives d'un point de courbe de taux 5 ans
        CurvePoint savedCurvePoint = new CurvePoint(1, 5.0000, 3.2500);
        savedCurvePoint.setId(5);
        
        when(curvePointMapper.toEntity(any(CurvePointDTO.class))).thenReturn(curvePointEntity);
        when(curvePointService.save(any(CurvePoint.class))).thenReturn(savedCurvePoint);
        
        // When & Then
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "1")
                .param("term", "5.0000")      // 5 ans
                .param("value", "3.2500"))    // 3.25% taux
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/curvePoint/list?success=*"));
        
        verify(curvePointMapper).toEntity(any(CurvePointDTO.class));
        verify(curvePointService).save(any(CurvePoint.class));
    }
    
    @Test
    void validate_WithShortTermFinancialData_ShouldCreateCurvePoint() throws Exception {
        // Given - Données pour point à court terme (3 mois)
        when(curvePointMapper.toEntity(any(CurvePointDTO.class))).thenReturn(curvePointEntity);
        when(curvePointService.save(any(CurvePoint.class))).thenReturn(curvePointEntity);
        
        // When & Then - Test avec un terme de 3 mois (0.2500 années)
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "1")
                .param("term", "0.2500")      // 3 mois
                .param("value", "1.7500"))    // 1.75% taux court terme
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/curvePoint/list?success=*"));
    }
    
    @Test
    void validate_WithNegativeValue_ShouldCreateCurvePoint() throws Exception {
        // Given - Cas des taux négatifs (réaliste en finance moderne)
        when(curvePointMapper.toEntity(any(CurvePointDTO.class))).thenReturn(curvePointEntity);
        when(curvePointService.save(any(CurvePoint.class))).thenReturn(curvePointEntity);
        
        // When & Then - Taux négatif valide
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "2")
                .param("term", "1.0000")
                .param("value", "-0.5000"))   // -0.5% (taux négatif)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/curvePoint/list?success=*"));
    }
    
    @Test
    void validate_WithValidationErrors_ShouldReturnAddView() throws Exception {
        // When & Then - Champs obligatoires manquants
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "")
                .param("term", "")
                .param("value", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().attributeExists("curvePointDTO"))
                .andExpect(model().hasErrors());
        
        verify(curvePointService, never()).save(any(CurvePoint.class));
    }
    
    @Test
    void validate_WithInvalidCurveId_ShouldReturnAddViewWithErrors() throws Exception {
        // When & Then - CurveId doit être positif
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "0")        // Invalid: must be positive
                .param("term", "1.0000")
                .param("value", "2.5000"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().hasErrors());
        
        verify(curvePointService, never()).save(any(CurvePoint.class));
    }
    
    @Test
    void validate_WithNegativeTerm_ShouldReturnAddViewWithErrors() throws Exception {
        // When & Then - Term ne peut pas être négatif
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "1")
                .param("term", "-1.0000")     // Invalid: negative term
                .param("value", "2.5000"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().hasErrors());
        
        verify(curvePointService, never()).save(any(CurvePoint.class));
    }
    
    @Test
    void validate_WithTooManyDecimals_ShouldReturnAddViewWithErrors() throws Exception {
        // When & Then - Test avec valeurs invalides (trop de décimales ne devrait pas être validé par @Digits)
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "1")
                .param("term", "1.12345")     // Plus de 4 décimales
                .param("value", "2.567890"))   // Plus de 4 décimales
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().hasErrors());
        
        verify(curvePointService, never()).save(any(CurvePoint.class));
    }
    
    @Test
    void validate_WithServiceBusinessException_ShouldReturnAddViewWithError() throws Exception {
        // Given
        when(curvePointMapper.toEntity(any(CurvePointDTO.class))).thenReturn(curvePointEntity);
        when(curvePointService.save(any(CurvePoint.class)))
            .thenThrow(new IllegalArgumentException("Duplicate term for curve"));
        
        // When & Then
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "1")
                .param("term", "1.0000")
                .param("value", "2.5000"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/add"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Duplicate term for curve"));
    }
    
    // ================== TESTS UPDATE ==================
    
    @Test
    void updateCurvePoint_WithValidTermAndValue_ShouldUpdate() throws Exception {
        // Given - Simulation d'une mise à jour de taux
        when(curvePointService.findById(1)).thenReturn(Optional.of(curvePointEntity));
        when(curvePointService.save(any(CurvePoint.class))).thenReturn(curvePointEntity);
        
        // When & Then - Mise à jour du taux de 2.50% à 2.75%
        mockMvc.perform(post("/curvePoint/update/1")
                .param("curveId", "1")
                .param("term", "1.0000")
                .param("value", "2.7500"))    // Nouveau taux : 2.75%
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/curvePoint/list?success=*"));
        
        verify(curvePointService).findById(1);
        verify(curvePointMapper).updateEntityFromDTO(any(CurvePoint.class), any(CurvePointDTO.class));
        verify(curvePointService).save(any(CurvePoint.class));
    }
    
    @Test
    void updateCurvePoint_WithValidationErrors_ShouldReturnUpdateView() throws Exception {
        // When & Then - Validation errors dans la mise à jour
        mockMvc.perform(post("/curvePoint/update/1")
                .param("curveId", "")         // Missing required field
                .param("term", "1.0000")
                .param("value", "2.7500"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(model().attributeExists("curvePointDTO"))
                .andExpect(model().hasErrors());
        
        verify(curvePointService, never()).save(any(CurvePoint.class));
    }
    
    @Test
    void updateCurvePoint_WithNonExistentId_ShouldReturnUpdateViewWithError() throws Exception {
        // Given
        when(curvePointService.findById(999)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(post("/curvePoint/update/999")
                .param("curveId", "1")
                .param("term", "1.0000")
                .param("value", "2.7500"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("CurvePoint not found with id")));
        
        verify(curvePointService).findById(999);
        verify(curvePointService, never()).save(any(CurvePoint.class));
    }
    
    @Test
    void updateCurvePoint_WithServiceException_ShouldReturnUpdateViewWithError() throws Exception {
        // Given
        when(curvePointService.findById(1)).thenReturn(Optional.of(curvePointEntity));
        when(curvePointService.save(any(CurvePoint.class)))
            .thenThrow(new RuntimeException("Database constraint violation"));
        
        // When & Then
        mockMvc.perform(post("/curvePoint/update/1")
                .param("curveId", "1")
                .param("term", "1.0000")
                .param("value", "2.7500"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/update"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("An unexpected error occurred")));
    }
    
    // ================== TESTS DELETE ==================
    
    @Test
    void deleteCurvePoint_ShouldMaintainCurveIntegrity() throws Exception {
        // When & Then - Suppression normale
        mockMvc.perform(get("/curvePoint/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/curvePoint/list?success=*"));
        
        verify(curvePointService).deleteById(1);
    }
    
    @Test
    void deleteCurvePoint_WithServiceValidationError_ShouldRedirectWithError() throws Exception {
        // Given - Simulation d'une contrainte métier (ex: point nécessaire pour l'intégrité de la courbe)
        doThrow(new IllegalArgumentException("Cannot delete critical curve point"))
            .when(curvePointService).deleteById(1);
        
        // When & Then
        mockMvc.perform(get("/curvePoint/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/curvePoint/list?error=*"));
        
        verify(curvePointService).deleteById(1);
    }
    
    @Test
    void deleteCurvePoint_WithNonExistentId_ShouldRedirectWithError() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("CurvePoint not found with id: 999"))
            .when(curvePointService).deleteById(999);
        
        // When & Then
        mockMvc.perform(get("/curvePoint/delete/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/curvePoint/list?error=*"));
        
        verify(curvePointService).deleteById(999);
    }
    
    @Test
    void deleteCurvePoint_WithUnexpectedServiceException_ShouldRedirectWithError() throws Exception {
        // Given
        doThrow(new RuntimeException("Foreign key constraint violation"))
            .when(curvePointService).deleteById(1);
        
        // When & Then
        mockMvc.perform(get("/curvePoint/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/curvePoint/list?error=*"));
        
        verify(curvePointService).deleteById(1);
    }
    
    // ================== TESTS EDGE CASES ==================
    
    @Test
    void showUpdateForm_WithServiceException_ShouldRedirectWithError() throws Exception {
        // Given
        when(curvePointService.findById(1)).thenThrow(new RuntimeException("Database connection timeout"));
        
        // When & Then
        mockMvc.perform(get("/curvePoint/update/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"));
        
        verify(curvePointService).findById(1);
    }
    
    @Test
    void validate_WithExtremePrecisionValues_ShouldCreateCurvePoint() throws Exception {
        // Given - Test avec valeurs à la limite de la précision financière
        when(curvePointMapper.toEntity(any(CurvePointDTO.class))).thenReturn(curvePointEntity);
        when(curvePointService.save(any(CurvePoint.class))).thenReturn(curvePointEntity);
        
        // When & Then - Valeurs avec précision maximale autorisée (4 décimales)
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "1")
                .param("term", "0.0027")      // 1 jour = 1/365 années
                .param("value", "2.3456"))    // 4 décimales exactement
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/curvePoint/list?success=*"));
    }
    
    @Test
    void validate_WithZeroTerm_ShouldCreateCurvePoint() throws Exception {
        // Given - Terme zéro (overnight rate)
        when(curvePointMapper.toEntity(any(CurvePointDTO.class))).thenReturn(curvePointEntity);
        when(curvePointService.save(any(CurvePoint.class))).thenReturn(curvePointEntity);
        
        // When & Then - Terme = 0 (taux overnight)
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "1")
                .param("term", "0.0000")      // Overnight
                .param("value", "1.5000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/curvePoint/list?success=*"));
    }
    
    @Test
    void validate_WithLongTermValue_ShouldCreateCurvePoint() throws Exception {
        // Given - Terme très long (30 ans)
        when(curvePointMapper.toEntity(any(CurvePointDTO.class))).thenReturn(curvePointEntity);
        when(curvePointService.save(any(CurvePoint.class))).thenReturn(curvePointEntity);
        
        // When & Then - Terme long (30 ans)
        mockMvc.perform(post("/curvePoint/validate")
                .param("curveId", "1")
                .param("term", "30.0000")     // 30 ans
                .param("value", "4.2500"))    // Taux long terme
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/curvePoint/list?success=*"));
    }
    
    @Test
    void handleGenericException_ShouldReturnErrorView() throws Exception {
        // Given
        when(curvePointService.findAll()).thenThrow(new OutOfMemoryError("Heap space exhausted"));
        
        // When & Then
        mockMvc.perform(get("/curvePoint/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("An unexpected error occurred")))
                .andExpect(model().attribute("curvePoints", org.hamcrest.Matchers.hasSize(0)));
    }
    
    @Test
    void home_WithBothSuccessAndErrorParams_ShouldShowBothMessages() throws Exception {
        // Given
        when(curvePointService.findAll()).thenReturn(curvePointEntities);
        
        // When & Then - Les deux messages doivent être affichés
        mockMvc.perform(get("/curvePoint/list")
                .param("success", "Operation completed")
                .param("error", "Warning: Check data integrity"))
                .andExpect(status().isOk())
                .andExpect(view().name("curvePoint/list"))
                .andExpect(model().attribute("successMessage", "Operation completed"))
                .andExpect(model().attribute("errorMessage", "Warning: Check data integrity"));
    }
}
