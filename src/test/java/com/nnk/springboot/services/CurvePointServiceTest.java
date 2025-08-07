package com.nnk.springboot.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.nnk.springboot.domain.CurvePoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CurvePointServiceTest {

    @Autowired
    private CurvePointService curvePointService;
    
    private CurvePoint validCurvePoint;
    
    @BeforeEach
    void setUp() {
        validCurvePoint = new CurvePoint(1, 1.0, 2.5);
        validCurvePoint.setAsOfDate(LocalDateTime.now());
    }

    // Happy Path Tests
    
    @Test
    void findAll_ShouldReturnAllCurvePoints() {
        // Given - Clear any existing data first
        List<CurvePoint> existingCurvePoints = curvePointService.findAll();
        int initialCount = existingCurvePoints.size();
        
        CurvePoint curvePoint1 = new CurvePoint(1, 1.0, 2.5);
        CurvePoint curvePoint2 = new CurvePoint(1, 5.0, 3.0);
        curvePointService.save(curvePoint1);
        curvePointService.save(curvePoint2);
        
        // When
        List<CurvePoint> result = curvePointService.findAll();
        
        // Then
        assertThat(result).hasSize(initialCount + 2);
        assertThat(result).extracting(CurvePoint::getValue)
                         .contains(2.5, 3.0);
    }
    
    @Test
    void findById_WithValidId_ShouldReturnCurvePoint() {
        // Given
        CurvePoint savedCurvePoint = curvePointService.save(validCurvePoint);
        
        // When
        Optional<CurvePoint> result = curvePointService.findById(savedCurvePoint.getId());
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCurveId()).isEqualTo(1);
        assertThat(result.get().getTerm()).isEqualTo(1.0);
        assertThat(result.get().getValue()).isEqualTo(2.5);
    }
    
    @Test
    void save_WithValidCurvePoint_ShouldReturnSaved() {
        // When
        CurvePoint result = curvePointService.save(validCurvePoint);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCurveId()).isEqualTo(1);
        assertThat(result.getTerm()).isEqualTo(1.0);
        assertThat(result.getValue()).isEqualTo(2.5);
    }
    
    @Test
    void save_WithNewCurvePoint_ShouldSetCreationDate() {
        // Given
        LocalDateTime beforeSave = LocalDateTime.now();
        
        // When
        CurvePoint result = curvePointService.save(validCurvePoint);
        
        // Then
        assertThat(result.getCreationDate()).isNotNull();
        assertThat(result.getCreationDate()).isAfterOrEqualTo(beforeSave);
    }
    
    @Test
    void save_WithNewCurvePoint_ShouldSetAsOfDateIfNull() {
        // Given
        validCurvePoint.setAsOfDate(null);
        LocalDateTime beforeSave = LocalDateTime.now();
        
        // When
        CurvePoint result = curvePointService.save(validCurvePoint);
        
        // Then
        assertThat(result.getAsOfDate()).isNotNull();
        assertThat(result.getAsOfDate()).isAfterOrEqualTo(beforeSave);
    }
    
    @Test
    void deleteById_WithValidId_ShouldDelete() {
        // Given
        CurvePoint savedCurvePoint = curvePointService.save(validCurvePoint);
        Integer id = savedCurvePoint.getId();
        
        // When
        curvePointService.deleteById(id);
        
        // Then
        Optional<CurvePoint> result = curvePointService.findById(id);
        assertThat(result).isEmpty();
    }
    
    @Test
    void save_WithValidCurveId_ShouldPass() {
        // Given
        validCurvePoint.setCurveId(100);
        
        // When
        CurvePoint result = curvePointService.save(validCurvePoint);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCurveId()).isEqualTo(100);
    }
    
    @Test
    void save_WithValidTerm_ShouldPass() {
        // Given
        validCurvePoint.setTerm(10.5);
        
        // When
        CurvePoint result = curvePointService.save(validCurvePoint);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTerm()).isEqualTo(10.5);
    }
    
    @Test
    void save_WithValidValue_ShouldPass() {
        // Given
        validCurvePoint.setValue(5.75);
        
        // When
        CurvePoint result = curvePointService.save(validCurvePoint);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(5.75);
    }
    
    @Test
    void save_WithZeroTerm_ShouldPass() {
        // Given
        validCurvePoint.setTerm(0.0);
        
        // When
        CurvePoint result = curvePointService.save(validCurvePoint);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTerm()).isEqualTo(0.0);
    }
    
    @Test
    void save_WithNegativeValue_ShouldPass() {
        // Given - Taux négatifs autorisés
        validCurvePoint.setValue(-0.5);
        
        // When
        CurvePoint result = curvePointService.save(validCurvePoint);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(-0.5);
    }

    // Exception Tests
    
    @Test
    void save_WithNullCurvePoint_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> curvePointService.save(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("CurvePoint cannot be null");
    }
    
    @Test
    void save_WithNullCurveId_ShouldThrowException() {
        // Given
        validCurvePoint.setCurveId(null);
        
        // When & Then
        assertThatThrownBy(() -> curvePointService.save(validCurvePoint))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Curve ID is required and must be positive");
    }
    
    @Test
    void save_WithNegativeCurveId_ShouldThrowException() {
        // Given
        validCurvePoint.setCurveId(-1);
        
        // When & Then
        assertThatThrownBy(() -> curvePointService.save(validCurvePoint))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Curve ID is required and must be positive");
    }
    
    @Test
    void save_WithZeroCurveId_ShouldThrowException() {
        // Given
        validCurvePoint.setCurveId(0);
        
        // When & Then
        assertThatThrownBy(() -> curvePointService.save(validCurvePoint))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Curve ID is required and must be positive");
    }
    
    @Test
    void save_WithNullTerm_ShouldThrowException() {
        // Given
        validCurvePoint.setTerm(null);
        
        // When & Then
        assertThatThrownBy(() -> curvePointService.save(validCurvePoint))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Term is required and must be positive or zero");
    }
    
    @Test
    void save_WithNegativeTerm_ShouldThrowException() {
        // Given
        validCurvePoint.setTerm(-1.0);
        
        // When & Then
        assertThatThrownBy(() -> curvePointService.save(validCurvePoint))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Term is required and must be positive or zero");
    }
    
    @Test
    void save_WithNullValue_ShouldThrowException() {
        // Given
        validCurvePoint.setValue(null);
        
        // When & Then
        assertThatThrownBy(() -> curvePointService.save(validCurvePoint))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Value is required");
    }

    // Additional Edge Case Tests
    
    @Test
    void findById_WithNullId_ShouldReturnEmpty() {
        // When
        Optional<CurvePoint> result = curvePointService.findById(null);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void findById_WithNegativeId_ShouldReturnEmpty() {
        // When
        Optional<CurvePoint> result = curvePointService.findById(-1);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        // When
        Optional<CurvePoint> result = curvePointService.findById(99999);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void deleteById_WithNullId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> curvePointService.deleteById(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid ID for deletion");
    }
    
    @Test
    void deleteById_WithNegativeId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> curvePointService.deleteById(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid ID for deletion");
    }
    
    @Test
    void deleteById_WithNonExistentId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> curvePointService.deleteById(99999))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("CurvePoint not found with id: 99999");
    }
    
    @Test
    void existsById_WithValidId_ShouldReturnTrue() {
        // Given
        CurvePoint savedCurvePoint = curvePointService.save(validCurvePoint);
        
        // When
        boolean result = curvePointService.existsById(savedCurvePoint.getId());
        
        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    void existsById_WithNullId_ShouldReturnFalse() {
        // When
        boolean result = curvePointService.existsById(null);
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    void existsById_WithNegativeId_ShouldReturnFalse() {
        // When
        boolean result = curvePointService.existsById(-1);
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    void existsById_WithNonExistentId_ShouldReturnFalse() {
        // When
        boolean result = curvePointService.existsById(99999);
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    void save_WithCompleteValidData_ShouldSaveAllFields() {
        // Given
        CurvePoint completeCurvePoint = new CurvePoint(5, 2.5, 3.75);
        completeCurvePoint.setAsOfDate(LocalDateTime.of(2025, 1, 1, 12, 0));
        
        // When
        CurvePoint result = curvePointService.save(completeCurvePoint);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCurveId()).isEqualTo(5);
        assertThat(result.getTerm()).isEqualTo(2.5);
        assertThat(result.getValue()).isEqualTo(3.75);
        assertThat(result.getAsOfDate()).isEqualTo(LocalDateTime.of(2025, 1, 1, 12, 0));
        assertThat(result.getCreationDate()).isNotNull();
    }
    
    @Test
    void save_WithExistingCurvePoint_ShouldUpdate() {
        // Given
        CurvePoint savedCurvePoint = curvePointService.save(validCurvePoint);
        savedCurvePoint.setValue(4.0);
        
        // When
        CurvePoint result = curvePointService.save(savedCurvePoint);
        
        // Then
        assertThat(result.getId()).isEqualTo(savedCurvePoint.getId());
        assertThat(result.getValue()).isEqualTo(4.0);
    }
}
