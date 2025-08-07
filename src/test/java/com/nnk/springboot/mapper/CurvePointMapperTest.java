package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.dto.CurvePointDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour le CurvePointMapper.
 * 
 * <p>Cette classe de test valide la conversion bidirectionnelle entre les entités CurvePoint
 * et les DTOs CurvePointDTO, ainsi que la fonctionnalité de mise à jour in-place.
 * Elle vérifie le mapping correct de tous les champs, la gestion des valeurs null,
 * et l'intégrité des données financières critiques pour les courbes quantitatives.</p>
 * 
 * <p>Couverture des tests :</p>
 * <ul>
 *   <li><strong>Mapping complet</strong> : Tous les champs entity ↔ DTO</li>
 *   <li><strong>Gestion des nulls</strong> : Protection contre les NPE</li>
 *   <li><strong>Mise à jour in-place</strong> : Optimisation pour modifications existantes</li>
 *   <li><strong>Précision financière</strong> : Validation des valeurs à 4 décimales</li>
 *   <li><strong>Données scientifiques</strong> : Gestion de la notation scientifique</li>
 *   <li><strong>Edge cases</strong> : Valeurs limites et cas particuliers</li>
 * </ul>
 * 
 * <p>Scénarios financiers testés :</p>
 * <ul>
 *   <li><strong>Courbes de taux</strong> : Taux d'intérêt standard</li>
 *   <li><strong>Courbes de volatilité</strong> : Volatilités implicites</li>
 *   <li><strong>Courbes de crédit</strong> : Spreads de crédit</li>
 *   <li><strong>Courbes exotiques</strong> : Valeurs négatives, très petites/grandes</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */
@DisplayName("CurvePointMapper - Tests de conversion Entity/DTO et mise à jour")
class CurvePointMapperTest {

    private CurvePointMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CurvePointMapper();
    }

    @Test
    @DisplayName("toDTO() - Avec CurvePoint valide - Doit mapper correctement")
    void toDTO_WithValidCurvePoint_ShouldMapCorrectly() {
        // Given - Création d'une entité CurvePoint complète pour courbe de taux
        CurvePoint curvePoint = new CurvePoint();
        curvePoint.setId(1);
        curvePoint.setCurveId(101);
        curvePoint.setAsOfDate(LocalDateTime.of(2024, 8, 7, 14, 30));
        curvePoint.setTerm(2.5000); // 2 ans et 6 mois
        curvePoint.setValue(3.2500); // Taux de 3.25%
        curvePoint.setCreationDate(LocalDateTime.of(2024, 8, 7, 9, 0));

        // When - Conversion vers DTO
        CurvePointDTO result = mapper.toDTO(curvePoint);

        // Then - Vérification du mapping des champs essentiels
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getCurveId()).isEqualTo(101);
        assertThat(result.getTerm()).isEqualTo(2.5000);
        assertThat(result.getValue()).isEqualTo(3.2500);
    }

    @Test
    @DisplayName("toDTO() - Avec CurvePoint null - Doit retourner null")
    void toDTO_WithNullCurvePoint_ShouldReturnNull() {
        // Given
        CurvePoint curvePoint = null;

        // When
        CurvePointDTO result = mapper.toDTO(curvePoint);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toEntity() - Avec DTO valide - Doit mapper correctement")
    void toEntity_WithValidDTO_ShouldMapCorrectly() {
        // Given - Création d'un DTO CurvePointDTO pour courbe de volatilité
        CurvePointDTO dto = new CurvePointDTO();
        dto.setId(2);
        dto.setCurveId(202);
        dto.setTerm(0.2500); // 3 mois
        dto.setValue(18.7500); // Volatilité de 18.75%

        // When - Conversion vers entité
        CurvePoint result = mapper.toEntity(dto);

        // Then - Vérification du mapping des champs
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getCurveId()).isEqualTo(202);
        assertThat(result.getTerm()).isEqualTo(0.2500);
        assertThat(result.getValue()).isEqualTo(18.7500);
        
        // Vérification que les timestamps ne sont pas mappés (gérés par la logique métier)
        assertThat(result.getAsOfDate()).isNull();
        assertThat(result.getCreationDate()).isNull();
    }

    @Test
    @DisplayName("toEntity() - Avec DTO null - Doit retourner null")
    void toEntity_WithNullDTO_ShouldReturnNull() {
        // Given
        CurvePointDTO dto = null;

        // When
        CurvePoint result = mapper.toEntity(dto);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("updateEntityFromDTO() - Doit mettre à jour les champs")
    void updateEntityFromDTO_ShouldUpdateFields() {
        // Given - Entité existante avec des valeurs initiales
        CurvePoint existingEntity = new CurvePoint();
        existingEntity.setId(5);
        existingEntity.setCurveId(301);
        existingEntity.setAsOfDate(LocalDateTime.of(2024, 8, 6, 10, 0));
        existingEntity.setTerm(1.0000); // 1 an
        existingEntity.setValue(2.5000); // Taux initial de 2.5%
        existingEntity.setCreationDate(LocalDateTime.of(2024, 8, 6, 8, 0));

        // DTO avec nouvelles valeurs pour mise à jour
        CurvePointDTO updateDTO = new CurvePointDTO();
        updateDTO.setId(5); // Même ID (sera ignoré dans l'update)
        updateDTO.setCurveId(302); // Nouvelle courbe
        updateDTO.setTerm(1.5000); // Nouveau terme : 1 an et 6 mois
        updateDTO.setValue(2.7500); // Nouveau taux : 2.75%

        // When - Mise à jour de l'entité existante
        mapper.updateEntityFromDTO(existingEntity, updateDTO);

        // Then - Vérification de la mise à jour des champs modifiables
        assertThat(existingEntity.getCurveId()).isEqualTo(302);
        assertThat(existingEntity.getTerm()).isEqualTo(1.5000);
        assertThat(existingEntity.getValue()).isEqualTo(2.7500);
    }

    @Test
    @DisplayName("updateEntityFromDTO() - Doit préserver l'ID et les timestamps")
    void updateEntityFromDTO_ShouldPreserveId() {
        // Given - Entité existante avec timestamps et ID
        CurvePoint existingEntity = new CurvePoint();
        existingEntity.setId(10);
        existingEntity.setCurveId(401);
        existingEntity.setAsOfDate(LocalDateTime.of(2024, 8, 5, 16, 45));
        existingEntity.setTerm(5.0000);
        existingEntity.setValue(3.5000);
        existingEntity.setCreationDate(LocalDateTime.of(2024, 8, 5, 9, 30));

        // DTO avec un ID différent (ne devrait pas changer l'ID de l'entité)
        CurvePointDTO updateDTO = new CurvePointDTO();
        updateDTO.setId(999); // ID différent
        updateDTO.setCurveId(402);
        updateDTO.setTerm(6.0000);
        updateDTO.setValue(3.7500);

        // Sauvegarde des valeurs originales
        Integer originalId = existingEntity.getId();
        LocalDateTime originalAsOfDate = existingEntity.getAsOfDate();
        LocalDateTime originalCreationDate = existingEntity.getCreationDate();

        // When
        mapper.updateEntityFromDTO(existingEntity, updateDTO);

        // Then - L'ID et les timestamps doivent être préservés
        assertThat(existingEntity.getId()).isEqualTo(originalId);
        assertThat(existingEntity.getAsOfDate()).isEqualTo(originalAsOfDate);
        assertThat(existingEntity.getCreationDate()).isEqualTo(originalCreationDate);
        
        // Mais les autres champs doivent être mis à jour
        assertThat(existingEntity.getCurveId()).isEqualTo(402);
        assertThat(existingEntity.getTerm()).isEqualTo(6.0000);
        assertThat(existingEntity.getValue()).isEqualTo(3.7500);
    }

    @Test
    @DisplayName("updateEntityFromDTO() - Avec valeurs null - Doit gérer correctement")
    void updateEntityFromDTO_WithNullValues_ShouldHandle() {
        // Given - Entité existante
        CurvePoint existingEntity = new CurvePoint();
        existingEntity.setId(15);
        existingEntity.setCurveId(501);
        existingEntity.setTerm(3.0000);
        existingEntity.setValue(4.0000);

        // Test avec entité null
        mapper.updateEntityFromDTO(null, new CurvePointDTO());
        // Ne devrait pas lever d'exception

        // Test avec DTO null
        mapper.updateEntityFromDTO(existingEntity, null);
        // Ne devrait pas lever d'exception et l'entité reste inchangée
        assertThat(existingEntity.getCurveId()).isEqualTo(501);
        assertThat(existingEntity.getTerm()).isEqualTo(3.0000);
        assertThat(existingEntity.getValue()).isEqualTo(4.0000);

        // Test avec les deux null
        mapper.updateEntityFromDTO(null, null);
        // Ne devrait pas lever d'exception
    }

    @Test
    @DisplayName("Mapping - Doit préserver la précision financière")
    void mapping_ShouldPreserveFinancialPrecision() {
        // Given - Valeurs financières avec précision maximale (4 décimales)
        CurvePoint entity = new CurvePoint();
        entity.setId(20);
        entity.setCurveId(601);
        entity.setTerm(0.0833); // 1 mois (1/12)
        entity.setValue(9999.9999); // Valeur avec précision maximale

        // When - Conversion round-trip : Entity → DTO → Entity
        CurvePointDTO dto = mapper.toDTO(entity);
        CurvePoint resultEntity = mapper.toEntity(dto);

        // Then - Vérification de la précision financière
        assertThat(dto.getTerm()).isEqualTo(0.0833);
        assertThat(dto.getValue()).isEqualTo(9999.9999);
        
        assertThat(resultEntity.getTerm()).isEqualTo(0.0833);
        assertThat(resultEntity.getValue()).isEqualTo(9999.9999);

        // Test avec des valeurs de trading réalistes
        entity.setTerm(2.7500); // 2 ans 9 mois
        entity.setValue(3.1234); // Taux précis

        dto = mapper.toDTO(entity);
        assertThat(dto.getTerm()).isEqualTo(2.7500);
        assertThat(dto.getValue()).isEqualTo(3.1234);
    }

    @Test
    @DisplayName("Mapping - Avec notation scientifique - Doit gérer correctement")
    void mapping_WithScientificNotation_ShouldHandleCorrectly() {
        // Given - Valeurs en notation scientifique (très petites et très grandes)
        CurvePoint entity = new CurvePoint();
        entity.setId(25);
        entity.setCurveId(701);
        
        // Valeurs extrêmes qui peuvent apparaître en finance
        entity.setTerm(1.0E-4); // Très petit terme (environ 1 heure en années)
        entity.setValue(-1.5E2); // Taux négatif important (-150.0)

        // When - Conversion round-trip
        CurvePointDTO dto = mapper.toDTO(entity);
        CurvePoint resultEntity = mapper.toEntity(dto);

        // Then - Vérification de la préservation des valeurs scientifiques
        assertThat(dto.getTerm()).isEqualTo(1.0E-4);
        assertThat(dto.getValue()).isEqualTo(-1.5E2);
        
        assertThat(resultEntity.getTerm()).isEqualTo(1.0E-4);
        assertThat(resultEntity.getValue()).isEqualTo(-1.5E2);

        // Test avec très grandes valeurs
        entity.setTerm(1.0E4); // Très long terme
        entity.setValue(9.99E3); // Très grande valeur

        dto = mapper.toDTO(entity);
        resultEntity = mapper.toEntity(dto);

        assertThat(dto.getTerm()).isEqualTo(1.0E4);
        assertThat(dto.getValue()).isEqualTo(9.99E3);
        assertThat(resultEntity.getTerm()).isEqualTo(1.0E4);
        assertThat(resultEntity.getValue()).isEqualTo(9.99E3);
    }

    @Test
    @DisplayName("Conversion round-trip - Doit préserver toutes les données")
    void roundTripConversion_ShouldPreserveAllData() {
        // Given - Entité source avec données financières complexes
        CurvePoint originalEntity = new CurvePoint();
        originalEntity.setId(30);
        originalEntity.setCurveId(801);
        originalEntity.setAsOfDate(LocalDateTime.of(2024, 8, 7, 16, 0));
        originalEntity.setTerm(7.5000); // 7 ans et 6 mois
        originalEntity.setValue(-0.5000); // Taux négatif (réaliste en EUR)
        originalEntity.setCreationDate(LocalDateTime.of(2024, 8, 7, 15, 45));

        // When - Conversion round-trip : Entity → DTO → Entity
        CurvePointDTO intermediateDTO = mapper.toDTO(originalEntity);
        CurvePoint finalEntity = mapper.toEntity(intermediateDTO);

        // Then - Vérification de la conservation des données mappées
        assertThat(finalEntity.getId()).isEqualTo(originalEntity.getId());
        assertThat(finalEntity.getCurveId()).isEqualTo(originalEntity.getCurveId());
        assertThat(finalEntity.getTerm()).isEqualTo(originalEntity.getTerm());
        assertThat(finalEntity.getValue()).isEqualTo(originalEntity.getValue());
        
        // Les timestamps ne sont pas mappés dans toEntity()
        assertThat(finalEntity.getAsOfDate()).isNull();
        assertThat(finalEntity.getCreationDate()).isNull();
    }

    @Test
    @DisplayName("Mapping des valeurs limites - Doit gérer les cas extrêmes")
    void mapping_WithBoundaryValues_ShouldHandleEdgeCases() {
        // Given - Valeurs aux limites financières
        CurvePoint entity = new CurvePoint();
        entity.setId(Integer.MAX_VALUE);
        entity.setCurveId(Integer.MAX_VALUE);
        entity.setTerm(0.0); // Terme zéro (overnight)
        entity.setValue(Double.MAX_VALUE); // Valeur maximale théorique

        // When - Conversion round-trip
        CurvePointDTO dto = mapper.toDTO(entity);
        CurvePoint result = mapper.toEntity(dto);

        // Then - Vérification des valeurs limites
        assertThat(result.getId()).isEqualTo(Integer.MAX_VALUE);
        assertThat(result.getCurveId()).isEqualTo(Integer.MAX_VALUE);
        assertThat(result.getTerm()).isEqualTo(0.0);
        assertThat(result.getValue()).isEqualTo(Double.MAX_VALUE);

        // Test avec valeurs négatives (spreads de crédit, taux négatifs)
        entity.setTerm(0.0001); // Terme très court
        entity.setValue(-999.9999); // Valeur très négative

        dto = mapper.toDTO(entity);
        result = mapper.toEntity(dto);

        assertThat(result.getTerm()).isEqualTo(0.0001);
        assertThat(result.getValue()).isEqualTo(-999.9999);
    }

    @Test
    @DisplayName("Mapping scénarios financiers - Différents types de courbes")
    void mapping_FinancialScenarios_DifferentCurveTypes() {
        // Test 1: Courbe de taux EUR (avec taux négatifs)
        CurvePoint eurRatePoint = new CurvePoint(1, 0.5000, -0.7500);
        CurvePointDTO eurDTO = mapper.toDTO(eurRatePoint);
        assertThat(eurDTO.getCurveId()).isEqualTo(1);
        assertThat(eurDTO.getTerm()).isEqualTo(0.5000); // 6 mois
        assertThat(eurDTO.getValue()).isEqualTo(-0.7500); // -0.75% (réaliste)

        // Test 2: Courbe de volatilité
        CurvePoint volPoint = new CurvePoint(2, 1.0000, 25.5000);
        CurvePointDTO volDTO = mapper.toDTO(volPoint);
        assertThat(volDTO.getCurveId()).isEqualTo(2);
        assertThat(volDTO.getTerm()).isEqualTo(1.0000); // 1 an
        assertThat(volDTO.getValue()).isEqualTo(25.5000); // 25.5% de volatilité

        // Test 3: Courbe de crédit (spreads en bp)
        CurvePoint creditPoint = new CurvePoint(3, 5.0000, 125.0000);
        CurvePointDTO creditDTO = mapper.toDTO(creditPoint);
        assertThat(creditDTO.getCurveId()).isEqualTo(3);
        assertThat(creditDTO.getTerm()).isEqualTo(5.0000); // 5 ans
        assertThat(creditDTO.getValue()).isEqualTo(125.0000); // 125 bp de spread

        // Test 4: Courbe forward très long terme
        CurvePoint forwardPoint = new CurvePoint(4, 30.0000, 4.5000);
        CurvePointDTO forwardDTO = mapper.toDTO(forwardPoint);
        assertThat(forwardDTO.getCurveId()).isEqualTo(4);
        assertThat(forwardDTO.getTerm()).isEqualTo(30.0000); // 30 ans
        assertThat(forwardDTO.getValue()).isEqualTo(4.5000); // 4.5% forward
    }

    @Test
    @DisplayName("Update avec scénarios métier - Modifications réalistes")
    void updateEntityFromDTO_BusinessScenarios_RealisticModifications() {
        // Given - Point de courbe EUR existant (taux de marché)
        CurvePoint existingPoint = new CurvePoint();
        existingPoint.setId(50);
        existingPoint.setCurveId(100); // Courbe EUR
        existingPoint.setAsOfDate(LocalDateTime.of(2024, 8, 6, 17, 0));
        existingPoint.setTerm(2.0000); // 2 ans
        existingPoint.setValue(-0.5000); // Taux négatif initial
        existingPoint.setCreationDate(LocalDateTime.of(2024, 8, 6, 9, 0));

        // When - Mise à jour suite à un mouvement de marché
        CurvePointDTO marketUpdate = new CurvePointDTO();
        marketUpdate.setCurveId(100); // Même courbe
        marketUpdate.setTerm(2.0000); // Même terme
        marketUpdate.setValue(-0.2500); // Nouveau taux : -0.25% (hausse de 25 bp)

        mapper.updateEntityFromDTO(existingPoint, marketUpdate);

        // Then - Vérification de la mise à jour de marché
        assertThat(existingPoint.getCurveId()).isEqualTo(100);
        assertThat(existingPoint.getTerm()).isEqualTo(2.0000);
        assertThat(existingPoint.getValue()).isEqualTo(-0.2500);
        
        // Les données d'audit doivent être préservées
        assertThat(existingPoint.getId()).isEqualTo(50);
        assertThat(existingPoint.getAsOfDate()).isEqualTo(LocalDateTime.of(2024, 8, 6, 17, 0));
        assertThat(existingPoint.getCreationDate()).isEqualTo(LocalDateTime.of(2024, 8, 6, 9, 0));

        // Scenario 2: Changement de courbe (recalibration)
        CurvePointDTO recalibration = new CurvePointDTO();
        recalibration.setCurveId(101); // Nouvelle courbe calibrée
        recalibration.setTerm(2.1000); // Terme légèrement ajusté
        recalibration.setValue(-0.1500); // Nouvelle valeur calibrée

        mapper.updateEntityFromDTO(existingPoint, recalibration);

        assertThat(existingPoint.getCurveId()).isEqualTo(101);
        assertThat(existingPoint.getTerm()).isEqualTo(2.1000);
        assertThat(existingPoint.getValue()).isEqualTo(-0.1500);
    }
}
