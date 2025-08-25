package com.nnk.springboot.services;

/**
 * Tests unitaires pour le RuleNameService.
 * 
 * <p>Cette classe de test valide la logique métier du service de gestion des règles de négociation (RuleName),
 * en testant les opérations CRUD, la validation des données de règles et la gestion des erreurs
 * dans un contexte d'intégration avec la base de données.</p>
 * 
 * <p>Couverture des tests :</p>
 * <ul>
 *   <li><strong>Création</strong> : Ajout de nouvelles règles avec validation</li>
 *   <li><strong>Lecture</strong> : Récupération par ID et liste complète</li>
 *   <li><strong>Mise à jour</strong> : Modification des règles existantes</li>
 *   <li><strong>Suppression</strong> : Suppression avec vérification d'existence</li>
 *   <li><strong>Gestion des erreurs</strong> : Entités introuvables, validation</li>
 *   <li><strong>Contraintes métier</strong> : Validation des règles de négociation</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageRequest;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RuleNameServiceTest {

    @Mock
    private RuleNameRepository ruleNameRepository;

    @InjectMocks
    private RuleNameService ruleNameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll_shouldReturnOrderedList() {
        RuleName r1 = new RuleName();
        r1.setName("A");
        RuleName r2 = new RuleName();
        r2.setName("B");
        when(ruleNameRepository.findAllByOrderByNameAsc()).thenReturn(List.of(r1, r2));
        List<RuleName> result = ruleNameService.findAll();
        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getName());
    }

    @Test
    void findById_shouldReturnOptional() {
        RuleName rule = new RuleName();
        rule.setId(1);
        when(ruleNameRepository.findById(1)).thenReturn(Optional.of(rule));
        Optional<RuleName> result = ruleNameService.findById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void findById_shouldReturnEmptyForInvalidId() {
        Optional<RuleName> result = ruleNameService.findById(-1);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByName_shouldReturnOptional() {
        RuleName rule = new RuleName();
        rule.setName("Test");
        when(ruleNameRepository.findByName("Test")).thenReturn(Optional.of(rule));
        Optional<RuleName> result = ruleNameService.findByName("Test");
        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getName());
    }

    @Test
    void findByName_shouldReturnEmptyForBlank() {
        Optional<RuleName> result = ruleNameService.findByName(" ");
        assertTrue(result.isEmpty());
    }

    @Test
    void existsById_shouldReturnTrueIfExists() {
        when(ruleNameRepository.existsById(1)).thenReturn(true);
        assertTrue(ruleNameService.existsById(1));
    }

    @Test
    void existsById_shouldReturnFalseForInvalidId() {
        assertFalse(ruleNameService.existsById(null));
        assertFalse(ruleNameService.existsById(0));
    }

    @Test
    void existsByName_shouldReturnTrueIfExists() {
        when(ruleNameRepository.existsByName("Test")).thenReturn(true);
        assertTrue(ruleNameService.existsByName("Test"));
    }

    @Test
    void existsByName_shouldReturnFalseForBlank() {
        assertFalse(ruleNameService.existsByName(""));
    }

    @Test
    void save_shouldValidateAndSave() {
        RuleName rule = new RuleName();
        rule.setName("ValidName");
        rule.setDescription("desc");
        when(ruleNameRepository.findByName("ValidName")).thenReturn(Optional.empty());
        when(ruleNameRepository.save(any())).thenReturn(rule);
        RuleName saved = ruleNameService.save(rule);
        assertEquals("ValidName", saved.getName());
    }

    @Test
    void save_shouldThrowForInvalidName() {
        RuleName rule = new RuleName();
        rule.setName("_invalid");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> ruleNameService.save(rule));
        assertTrue(ex.getMessage().contains("Rule name must start with alphanumeric character"));
    }

    @Test
    void save_shouldThrowForDuplicateName() {
        RuleName rule = new RuleName();
        rule.setId(2);
        rule.setName("Duplicate");
        RuleName existing = new RuleName();
        existing.setId(1);
        existing.setName("Duplicate");
        when(ruleNameRepository.findByName("Duplicate")).thenReturn(Optional.of(existing));
        Exception ex = assertThrows(IllegalArgumentException.class, () -> ruleNameService.save(rule));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void deleteById_shouldDeleteIfExists() {
        when(ruleNameRepository.existsById(1)).thenReturn(true);
        doNothing().when(ruleNameRepository).deleteById(1);
        assertDoesNotThrow(() -> ruleNameService.deleteById(1));
        verify(ruleNameRepository).deleteById(1);
    }

    @Test
    void deleteById_shouldThrowForInvalidId() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> ruleNameService.deleteById(null));
        assertTrue(ex.getMessage().contains("Invalid ID"));
    }

    @Test
    void deleteById_shouldThrowIfNotExists() {
        when(ruleNameRepository.existsById(2)).thenReturn(false);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> ruleNameService.deleteById(2));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void findByComponentType_shouldReturnJsonRules() {
        RuleName rule = new RuleName();
        rule.setJson("{}");
        when(ruleNameRepository.findByJsonIsNotNullOrderByNameAsc()).thenReturn(List.of(rule));
        List<RuleName> result = ruleNameService.findByComponentType("JSON");
        assertEquals(1, result.size());
    }

    @Test
    void findByComponentType_shouldReturnEmptyForUnknown() {
        List<RuleName> result = ruleNameService.findByComponentType("UNKNOWN");
        assertTrue(result.isEmpty());
    }

    @Test
    void findByKeyword_shouldSearchNameAndDescription() {
        RuleName r1 = new RuleName();
        r1.setName("CreditCheck");
        RuleName r2 = new RuleName();
        r2.setDescription("Check");
        when(ruleNameRepository.findByNameContainingIgnoreCaseOrderByNameAsc("Check")).thenReturn(List.of(r1));
        when(ruleNameRepository.findByDescriptionContainingIgnoreCaseOrderByNameAsc("Check")).thenReturn(List.of(r2));
        List<RuleName> result = ruleNameService.findByKeyword("Check");
        assertEquals(2, result.size());
    }

    @Test
    void validateRule_shouldReturnTrueForValidRule() {
        RuleName rule = new RuleName();
        rule.setName("ValidName");
        assertTrue(ruleNameService.validateRule(rule));
    }

    @Test
    void validateRule_shouldReturnFalseForInvalidRule() {
        RuleName rule = new RuleName();
        rule.setName("_invalid");
        assertFalse(ruleNameService.validateRule(rule));
    }

    @Test
    void findRecentRules_shouldReturnLimitedList() {
        RuleName r = new RuleName();
        when(ruleNameRepository.findRecentRules(PageRequest.of(0, 2))).thenReturn(List.of(r));
        List<RuleName> result = ruleNameService.findRecentRules(2);
        assertEquals(1, result.size());
    }

    @Test
    void findRecentRules_shouldReturnEmptyForZeroLimit() {
        List<RuleName> result = ruleNameService.findRecentRules(0);
        assertTrue(result.isEmpty());
    }
}
