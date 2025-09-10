package com.nnk.springboot.controllers;

/**
 * Tests unitaires pour le RuleNameController.
 * 
 * <p>Cette classe de test valide le comportement du contrôleur REST pour les règles de négociation (RuleName),
 * en testant les opérations CRUD via les endpoints HTTP et en vérifiant la gestion des erreurs,
 * la sécurité et la validation des données de règles.</p>
 * 
 * <p>Couverture des tests :</p>
 * <ul>
 *   <li><strong>GET /ruleName</strong> : Affichage de la liste des règles</li>
 *   <li><strong>GET /ruleName/add</strong> : Formulaire d'ajout de règle</li>
 *   <li><strong>POST /ruleName/validate</strong> : Création d'une nouvelle règle</li>
 *   <li><strong>GET /ruleName/update/{id}</strong> : Formulaire de modification</li>
 *   <li><strong>POST /ruleName/update/{id}</strong> : Mise à jour d'une règle</li>
 *   <li><strong>GET /ruleName/delete/{id}</strong> : Suppression d'une règle</li>
 *   <li><strong>Validation des données</strong> : Gestion des erreurs de validation</li>
 *   <li><strong>Gestion des erreurs</strong> : Cas d'entités introuvables</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.dto.RuleNameDTO;
import com.nnk.springboot.mapper.RuleNameMapper;
import com.nnk.springboot.services.IRuleNameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RuleNameControllerTest {

    @InjectMocks
    private RuleNameController controller;

    @Mock
    private IRuleNameService ruleNameService;

    @Mock
    private RuleNameMapper ruleNameMapper;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void home_shouldReturnListViewWithRules() {
        RuleName rule = new RuleName();
        rule.setId(1);
        rule.setName("Test");
        RuleNameDTO dto = new RuleNameDTO();
        dto.setId(1);
        dto.setName("Test");
        when(ruleNameService.findAll()).thenReturn(List.of(rule));
        when(ruleNameMapper.toDTO(rule)).thenReturn(dto);

        String view = controller.home(model, "success", "error");
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("ruleNames"), any());
        verify(model).addAttribute(eq("successMessage"), eq("success"));
        verify(model).addAttribute(eq("errorMessage"), eq("error"));
    }

    @Test
    void addRuleForm_shouldReturnAddView() {
        String view = controller.addRuleForm(model);
        assertEquals("ruleName/add", view);
        verify(model).addAttribute(eq("ruleNameDTO"), any(RuleNameDTO.class));
    }

    @Test
    void validate_shouldReturnAddViewOnValidationError() {
        when(bindingResult.hasErrors()).thenReturn(true);
        RuleNameDTO dto = new RuleNameDTO();
        String view = controller.validate(dto, bindingResult, model, redirectAttributes);
        assertEquals("ruleName/add", view);
    }

    @Test
    void validate_shouldRedirectOnSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        RuleNameDTO dto = new RuleNameDTO();
        RuleName entity = new RuleName();
        when(ruleNameMapper.toEntity(dto)).thenReturn(entity);
        when(ruleNameService.save(entity)).thenReturn(entity);

        String view = controller.validate(dto, bindingResult, model, redirectAttributes);
        assertEquals("redirect:/ruleName/list", view);
    }

    @Test
    void validate_shouldReturnAddViewOnBusinessException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        RuleNameDTO dto = new RuleNameDTO();
        RuleName entity = new RuleName();
        when(ruleNameMapper.toEntity(dto)).thenReturn(entity);
        when(ruleNameService.save(entity)).thenThrow(new IllegalArgumentException("Business error"));

        String view = controller.validate(dto, bindingResult, model, redirectAttributes);
        assertEquals("ruleName/add", view);
        verify(model).addAttribute(eq("errorMessage"), eq("Business error"));
    }

    @Test
    void showUpdateForm_shouldReturnUpdateViewOnSuccess() {
        RuleName rule = new RuleName();
        rule.setId(1);
        RuleNameDTO dto = new RuleNameDTO();
        when(ruleNameService.findById(1)).thenReturn(Optional.of(rule));
        when(ruleNameMapper.toDTO(rule)).thenReturn(dto);

        String view = controller.showUpdateForm(1, model);
        assertEquals("ruleName/update", view);
        verify(model).addAttribute(eq("ruleNameDTO"), eq(dto));
    }

    @Test
    void showUpdateForm_shouldRedirectOnNotFound() {
        when(ruleNameService.findById(1)).thenReturn(Optional.empty());
        String view = controller.showUpdateForm(1, model);
        assertTrue(view.contains("redirect:/ruleName/list?error=notfound"));
    }

    @Test
    void updateRuleName_shouldReturnUpdateViewOnValidationError() {
        when(bindingResult.hasErrors()).thenReturn(true);
        RuleNameDTO dto = new RuleNameDTO();
        String view = controller.updateRuleName(1, dto, bindingResult, model, redirectAttributes);
        assertEquals("ruleName/update", view);
    }

    @Test
    void updateRuleName_shouldRedirectOnSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        RuleName rule = new RuleName();
        RuleNameDTO dto = new RuleNameDTO();
        when(ruleNameService.findById(1)).thenReturn(Optional.of(rule));
        doNothing().when(ruleNameMapper).updateEntityFromDTO(rule, dto);
        when(ruleNameService.save(rule)).thenReturn(rule);

        String view = controller.updateRuleName(1, dto, bindingResult, model, redirectAttributes);
        assertEquals("redirect:/ruleName/list", view);
    }

    @Test
    void updateRuleName_shouldReturnUpdateViewOnBusinessException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        RuleName rule = new RuleName();
        RuleNameDTO dto = new RuleNameDTO();
        when(ruleNameService.findById(1)).thenReturn(Optional.of(rule));
        doNothing().when(ruleNameMapper).updateEntityFromDTO(rule, dto);
        when(ruleNameService.save(rule)).thenThrow(new IllegalArgumentException("Business error"));

        String view = controller.updateRuleName(1, dto, bindingResult, model, redirectAttributes);
        assertEquals("ruleName/update", view);
        verify(model).addAttribute(eq("errorMessage"), eq("Business error"));
    }

    @Test
    void deleteRuleName_shouldRedirectOnSuccess() {
        RuleName rule = new RuleName();
        rule.setId(1);
        when(ruleNameService.findById(1)).thenReturn(Optional.of(rule));
        doNothing().when(ruleNameService).deleteById(1);

        String view = controller.deleteRuleName(1, redirectAttributes);
        assertEquals("redirect:/ruleName/list", view);
        verify(redirectAttributes).addAttribute(eq("success"), any());
    }

    @Test
    void deleteRuleName_shouldRedirectOnNotFound() {
        when(ruleNameService.findById(2)).thenReturn(Optional.empty());
        doThrow(new IllegalArgumentException("not found")).when(ruleNameService).deleteById(2);

        String view = controller.deleteRuleName(2, redirectAttributes);
        assertEquals("redirect:/ruleName/list", view);
        verify(redirectAttributes).addAttribute(eq("error"), eq("notfound"));
    }

    @Test
    void filterByComponent_shouldReturnListView() {
        RuleName rule = new RuleName();
        RuleNameDTO dto = new RuleNameDTO();
        when(ruleNameService.findByComponentType("JSON")).thenReturn(List.of(rule));
        when(ruleNameMapper.toDTO(rule)).thenReturn(dto);

        String view = controller.filterByComponent("json", model);
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("ruleNames"), any());
        verify(model).addAttribute(eq("filterDescription"), any());
    }

    @Test
    void searchRules_shouldReturnListView() {
        RuleName rule = new RuleName();
        RuleNameDTO dto = new RuleNameDTO();
        when(ruleNameService.findByKeyword("test")).thenReturn(List.of(rule));
        when(ruleNameMapper.toDTO(rule)).thenReturn(dto);

        String view = controller.searchRules("test", model);
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("ruleNames"), any());
        verify(model).addAttribute(eq("searchKeyword"), eq("test"));
    }

    @Test
    void getRulesApi_shouldReturnList() {
        RuleName rule = new RuleName();
        RuleNameDTO dto = new RuleNameDTO();
        when(ruleNameService.findAll()).thenReturn(List.of(rule));
        when(ruleNameMapper.toDTO(rule)).thenReturn(dto);

        List<RuleNameDTO> result = controller.getRulesApi(null);
        assertEquals(1, result.size());
    }

    @Test
    void getRulesApi_shouldReturnFilteredList() {
        RuleName rule = new RuleName();
        RuleNameDTO dto = new RuleNameDTO();
        when(ruleNameService.findByComponentType("JSON")).thenReturn(List.of(rule));
        when(ruleNameMapper.toDTO(rule)).thenReturn(dto);

        List<RuleNameDTO> result = controller.getRulesApi("json");
        assertEquals(1, result.size());
    }

    @Test
    void getRulesApi_shouldReturnEmptyListOnException() {
        when(ruleNameService.findAll()).thenThrow(new RuntimeException("Database error"));

        List<RuleNameDTO> result = controller.getRulesApi(null);
        assertEquals(0, result.size());
    }

    @Test
    void handleGenericException_shouldReturnListView() {
        Exception e = new Exception("fail");
        String view = controller.handleGenericException(e, model);
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("errorMessage"), contains("fail"));
        verify(model).addAttribute(eq("ruleNames"), any());
    }

    @Test
    void filterByComponent_shouldHandleAllCases() {
        RuleName rule = new RuleName();
        RuleNameDTO dto = new RuleNameDTO();
        when(ruleNameMapper.toDTO(rule)).thenReturn(dto);

        // Test template filter
        when(ruleNameService.findByComponentType("TEMPLATE")).thenReturn(List.of(rule));
        String view = controller.filterByComponent("template", model);
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("filterDescription"), eq("Rules with Templates"));

        // Test sql filter
        when(ruleNameService.findByComponentType("SQL")).thenReturn(List.of(rule));
        view = controller.filterByComponent("sql", model);
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("filterDescription"), eq("Rules with SQL Components"));

        // Test complete filter
        when(ruleNameService.findByComponentType("COMPLETE")).thenReturn(List.of(rule));
        view = controller.filterByComponent("complete", model);
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("filterDescription"), eq("Complete Rules (JSON + Template + SQL)"));

        // Test all filter
        when(ruleNameService.findAll()).thenReturn(List.of(rule));
        view = controller.filterByComponent("all", model);
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("filterDescription"), eq("All Business Rules"));
    }

    @Test
    void filterByComponent_shouldHandleException() {
        when(ruleNameService.findByComponentType("JSON")).thenThrow(new RuntimeException("Database error"));

        String view = controller.filterByComponent("json", model);
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("errorMessage"), contains("Database error"));
        verify(model).addAttribute(eq("ruleNames"), eq(List.of()));
    }

    @Test
    void searchRules_shouldRedirectOnEmptyKeyword() {
        String view = controller.searchRules("   ", model);
        assertEquals("redirect:/ruleName/list", view);

        view = controller.searchRules(null, model);
        assertEquals("redirect:/ruleName/list", view);
    }

    @Test
    void searchRules_shouldHandleException() {
        when(ruleNameService.findByKeyword("test")).thenThrow(new RuntimeException("Search error"));

        String view = controller.searchRules("test", model);
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("errorMessage"), contains("Search error"));
        verify(model).addAttribute(eq("ruleNames"), eq(List.of()));
    }

    @Test
    void home_shouldHandleException() {
        when(ruleNameService.findAll()).thenThrow(new RuntimeException("Database error"));

        String view = controller.home(model, null, null);
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("errorMessage"), contains("Database error"));
        verify(model).addAttribute(eq("ruleNames"), eq(List.of()));
    }

    @Test
    void validate_shouldHandleUnexpectedException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        RuleNameDTO dto = new RuleNameDTO();
        RuleName entity = new RuleName();
        when(ruleNameMapper.toEntity(dto)).thenReturn(entity);
        when(ruleNameService.save(entity)).thenThrow(new RuntimeException("Unexpected error"));

        String view = controller.validate(dto, bindingResult, model, redirectAttributes);
        assertEquals("ruleName/add", view);
        verify(model).addAttribute(eq("errorMessage"), contains("Unexpected error"));
    }

    @Test
    void updateRuleName_shouldHandleNotFound() {
        when(bindingResult.hasErrors()).thenReturn(false);
        RuleNameDTO dto = new RuleNameDTO();
        when(ruleNameService.findById(999)).thenReturn(Optional.empty());

        String view = controller.updateRuleName(999, dto, bindingResult, model, redirectAttributes);
        assertEquals("ruleName/update", view);
        verify(model).addAttribute(eq("errorMessage"), contains("RuleName not found"));
    }

    @Test
    void updateRuleName_shouldHandleUnexpectedException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        RuleName rule = new RuleName();
        RuleNameDTO dto = new RuleNameDTO();
        when(ruleNameService.findById(1)).thenReturn(Optional.of(rule));
        doNothing().when(ruleNameMapper).updateEntityFromDTO(rule, dto);
        when(ruleNameService.save(rule)).thenThrow(new RuntimeException("Unexpected error"));

        String view = controller.updateRuleName(1, dto, bindingResult, model, redirectAttributes);
        assertEquals("ruleName/update", view);
        verify(model).addAttribute(eq("errorMessage"), contains("Unexpected error"));
    }

    @Test
    void showUpdateForm_shouldHandleUnexpectedException() {
        when(ruleNameService.findById(1)).thenThrow(new RuntimeException("Database error"));

        String view = controller.showUpdateForm(1, model);
        assertEquals("redirect:/ruleName/list?error=unexpected", view);
    }

    @Test
    void deleteRuleName_shouldHandleUnexpectedException() {
        doThrow(new RuntimeException("Database error")).when(ruleNameService).deleteById(1);

        String view = controller.deleteRuleName(1, redirectAttributes);
        assertEquals("redirect:/ruleName/list", view);
        verify(redirectAttributes).addAttribute(eq("error"), eq("unexpected"));
    }

    @Test
    void home_shouldHandleStatusMessages() {
        when(ruleNameService.findAll()).thenReturn(List.of());

        String view = controller.home(model, "success", "notfound");
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("successMessage"), eq("success"));
        verify(model).addAttribute(eq("errorMessage"), eq("Business rule not found"));
    }

    @Test
    void home_shouldHandleInvalidErrorMessage() {
        when(ruleNameService.findAll()).thenReturn(List.of());

        String view = controller.home(model, null, "invalid");
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("errorMessage"), eq("Invalid rule ID provided"));
    }

    @Test
    void home_shouldHandleUnexpectedErrorMessage() {
        when(ruleNameService.findAll()).thenReturn(List.of());

        String view = controller.home(model, null, "unexpected");
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("errorMessage"), eq("An unexpected error occurred"));
    }

    @Test
    void home_shouldHandleCustomErrorMessage() {
        when(ruleNameService.findAll()).thenReturn(List.of());

        String view = controller.home(model, null, "custom error");
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("errorMessage"), eq("custom error"));
    }

    @Test
    void showUpdateForm_shouldHandleInvalidId() {
        String view = controller.showUpdateForm(0, model);
        assertEquals("redirect:/ruleName/list?error=notfound", view);

        view = controller.showUpdateForm(null, model);
        assertEquals("redirect:/ruleName/list?error=notfound", view);
    }

    @Test
    void updateRuleName_shouldHandleInvalidId() {
        when(bindingResult.hasErrors()).thenReturn(false);
        RuleNameDTO dto = new RuleNameDTO();

        String view = controller.updateRuleName(-1, dto, bindingResult, model, redirectAttributes);
        assertEquals("ruleName/update", view);
        verify(model).addAttribute(eq("errorMessage"), contains("Invalid ID"));
    }

    @Test
    void deleteRuleName_shouldHandleInvalidId() {
        String view = controller.deleteRuleName(0, redirectAttributes);
        assertEquals("redirect:/ruleName/list", view);
        verify(redirectAttributes).addAttribute(eq("error"), eq("notfound"));
    }
}
