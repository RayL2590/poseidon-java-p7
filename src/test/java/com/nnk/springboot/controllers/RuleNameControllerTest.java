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
    void handleGenericException_shouldReturnListView() {
        Exception e = new Exception("fail");
        String view = controller.handleGenericException(e, model);
        assertEquals("ruleName/list", view);
        verify(model).addAttribute(eq("errorMessage"), contains("fail"));
        verify(model).addAttribute(eq("ruleNames"), any());
    }
}
