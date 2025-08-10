package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.dto.RatingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires pour le RatingMapper.
 * 
 * <p>Cette classe de test valide la conversion bidirectionnelle entre les entites Rating
 * et les DTOs RatingDTO, en verifiant le mapping correct de tous les champs,
 * la gestion des valeurs null, et les fonctionnalites specifiques aux notations financieres.</p>
 * 
 * <p>Couverture des tests :</p>
 * <ul>
 *   <li><strong>Mapping complet</strong> : Tous les champs entity ↔ DTO</li>
 *   <li><strong>Gestion des nulls</strong> : Protection contre les NPE</li>
 *   <li><strong>Conversion round-trip</strong> : Integrite des donnees</li>
 *   <li><strong>Notations financieres</strong> : Validation des formats d'agences</li>
 *   <li><strong>Methodes specifiques</strong> : updateEntityFromDTO, createDefaultForQuality</li>
 *   <li><strong>Investment Grade</strong> : Coherence des classifications</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */
@DisplayName("RatingMapper - Tests de conversion Entity/DTO")
class RatingMapperTest {

    private RatingMapper ratingMapper;

    @BeforeEach
    void setUp() {
        ratingMapper = new RatingMapper();
    }

    // ================== TESTS toDTO() ==================

    @Test
    @DisplayName("toDTO() - Avec entite valide - Doit mapper tous les champs")
    void toDTO_WithValidEntity_ShouldMapAllFields() {
        // Given - Creation d'une entite Rating complete avec notations Investment Grade
        Rating entity = new Rating();
        entity.setId(1);
        entity.setMoodysRating("Aa2");
        entity.setSandPRating("AA");
        entity.setFitchRating("AA");
        entity.setOrderNumber(4);

        // When - Conversion vers DTO
        RatingDTO result = ratingMapper.toDTO(entity);

        // Then - Verification du mapping complet
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getMoodysRating()).isEqualTo("Aa2");
        assertThat(result.getSandPRating()).isEqualTo("AA");
        assertThat(result.getFitchRating()).isEqualTo("AA");
        assertThat(result.getOrderNumber()).isEqualTo(4);
    }

    @Test
    @DisplayName("toDTO() - Avec notation Prime Grade - Doit mapper correctement")
    void toDTO_WithPrimeGradeRating_ShouldMapCorrectly() {
        // Given - Notation de qualite maximale (AAA/Aaa)
        Rating entity = new Rating();
        entity.setId(2);
        entity.setMoodysRating("Aaa");
        entity.setSandPRating("AAA");
        entity.setFitchRating("AAA");
        entity.setOrderNumber(1);

        // When
        RatingDTO result = ratingMapper.toDTO(entity);

        // Then - Verification de la notation prime
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("Aaa");
        assertThat(result.getSandPRating()).isEqualTo("AAA");
        assertThat(result.getFitchRating()).isEqualTo("AAA");
        assertThat(result.getOrderNumber()).isEqualTo(1);
        // Verification que c'est Investment Grade
        assertThat(result.isInvestmentGrade()).isTrue();
    }

    @Test
    @DisplayName("toDTO() - Avec notation Speculative Grade - Doit mapper correctement")
    void toDTO_WithSpeculativeGradeRating_ShouldMapCorrectly() {
        // Given - Notation speculative (BB/Ba)
        Rating entity = new Rating();
        entity.setId(3);
        entity.setMoodysRating("Ba2");
        entity.setSandPRating("BB");
        entity.setFitchRating("BB");
        entity.setOrderNumber(13);

        // When
        RatingDTO result = ratingMapper.toDTO(entity);

        // Then - Verification de la notation speculative
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("Ba2");
        assertThat(result.getSandPRating()).isEqualTo("BB");
        assertThat(result.getFitchRating()).isEqualTo("BB");
        assertThat(result.getOrderNumber()).isEqualTo(13);
        // Verification que ce n'est pas Investment Grade
        assertThat(result.isInvestmentGrade()).isFalse();
    }

    @Test
    @DisplayName("toDTO() - Avec notation limitrophe Investment Grade - Doit mapper correctement")
    void toDTO_WithBorderlineInvestmentGrade_ShouldMapCorrectly() {
        // Given - Notation a la frontiere Investment Grade (BBB-/Baa3)
        Rating entity = new Rating();
        entity.setId(4);
        entity.setMoodysRating("Baa3");  // Derniere notation Investment Grade Moody's
        entity.setSandPRating("BBB-");   // Derniere notation Investment Grade S&P
        entity.setFitchRating("BBB-");   // Derniere notation Investment Grade Fitch
        entity.setOrderNumber(10);

        // When
        RatingDTO result = ratingMapper.toDTO(entity);

        // Then - Verification de la notation limitrophe
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("Baa3");
        assertThat(result.getSandPRating()).isEqualTo("BBB-");
        assertThat(result.getFitchRating()).isEqualTo("BBB-");
        assertThat(result.getOrderNumber()).isEqualTo(10);
        // Verification que c'est encore Investment Grade
        assertThat(result.isInvestmentGrade()).isTrue();
    }

    @Test
    @DisplayName("toDTO() - Avec notation de defaut - Doit mapper correctement")
    void toDTO_WithDefaultRating_ShouldMapCorrectly() {
        // Given - Notation de defaut
        Rating entity = new Rating();
        entity.setId(5);
        entity.setMoodysRating("C");   // Defaut Moody's
        entity.setSandPRating("D");    // Defaut S&P
        entity.setFitchRating("D");    // Defaut Fitch
        entity.setOrderNumber(21);

        // When
        RatingDTO result = ratingMapper.toDTO(entity);

        // Then - Verification de la notation de defaut
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("C");
        assertThat(result.getSandPRating()).isEqualTo("D");
        assertThat(result.getFitchRating()).isEqualTo("D");
        assertThat(result.getOrderNumber()).isEqualTo(21);
        assertThat(result.isInvestmentGrade()).isFalse();
    }

    @Test
    @DisplayName("toDTO() - Avec notations partielles - Doit mapper correctement")
    void toDTO_WithPartialRatings_ShouldMapCorrectly() {
        // Given - Seulement certaines agences ont donne une notation (cas realiste)
        Rating entity = new Rating();
        entity.setId(6);
        entity.setMoodysRating("A2");
        entity.setSandPRating("A");
        entity.setFitchRating(null);  // Pas de notation Fitch
        entity.setOrderNumber(7);

        // When
        RatingDTO result = ratingMapper.toDTO(entity);

        // Then - Verification des notations partielles
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("A2");
        assertThat(result.getSandPRating()).isEqualTo("A");
        assertThat(result.getFitchRating()).isNull();
        assertThat(result.getOrderNumber()).isEqualTo(7);
        // Reste Investment Grade malgre Fitch manquant
        assertThat(result.isInvestmentGrade()).isTrue();
    }

    @Test
    @DisplayName("toDTO() - Avec entite null - Doit retourner null")
    void toDTO_WithNullEntity_ShouldReturnNull() {
        // Given
        Rating entity = null;

        // When
        RatingDTO result = ratingMapper.toDTO(entity);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toDTO() - Avec champs null dans l'entite - Doit gerer les valeurs null")
    void toDTO_WithNullFieldsInEntity_ShouldHandleNullValues() {
        // Given - Entite avec seulement ID et order number
        Rating entity = new Rating();
        entity.setId(7);
        entity.setMoodysRating(null);
        entity.setSandPRating(null);
        entity.setFitchRating(null);
        entity.setOrderNumber(15);

        // When
        RatingDTO result = ratingMapper.toDTO(entity);

        // Then - Verification que les null sont preserves
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(7);
        assertThat(result.getMoodysRating()).isNull();
        assertThat(result.getSandPRating()).isNull();
        assertThat(result.getFitchRating()).isNull();
        assertThat(result.getOrderNumber()).isEqualTo(15);
        assertThat(result.isInvestmentGrade()).isFalse();
    }

    // ================== TESTS toEntity() ==================

    @Test
    @DisplayName("toEntity() - Avec DTO valide - Doit mapper tous les champs")
    void toEntity_WithValidDTO_ShouldMapAllFields() {
        // Given - Creation d'un DTO RatingDTO complet
        RatingDTO dto = new RatingDTO();
        dto.setId(8);
        dto.setMoodysRating("A3");
        dto.setSandPRating("A-");
        dto.setFitchRating("A-");
        dto.setOrderNumber(9);

        // When - Conversion vers entite
        Rating result = ratingMapper.toEntity(dto);

        // Then - Verification du mapping complet
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(8);
        assertThat(result.getMoodysRating()).isEqualTo("A3");
        assertThat(result.getSandPRating()).isEqualTo("A-");
        assertThat(result.getFitchRating()).isEqualTo("A-");
        assertThat(result.getOrderNumber()).isEqualTo(9);
    }

    @Test
    @DisplayName("toEntity() - Avec DTO High Yield - Doit mapper correctement")
    void toEntity_WithHighYieldDTO_ShouldMapCorrectly() {
        // Given - DTO avec notation High Yield (B/B2)
        RatingDTO dto = new RatingDTO();
        dto.setId(9);
        dto.setMoodysRating("B2");
        dto.setSandPRating("B");
        dto.setFitchRating("B");
        dto.setOrderNumber(16);

        // When
        Rating result = ratingMapper.toEntity(dto);

        // Then - Verification de la notation High Yield
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("B2");
        assertThat(result.getSandPRating()).isEqualTo("B");
        assertThat(result.getFitchRating()).isEqualTo("B");
        assertThat(result.getOrderNumber()).isEqualTo(16);
    }

    @Test
    @DisplayName("toEntity() - Avec DTO null - Doit retourner null")
    void toEntity_WithNullDTO_ShouldReturnNull() {
        // Given
        RatingDTO dto = null;

        // When
        Rating result = ratingMapper.toEntity(dto);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toEntity() - Avec champs null dans le DTO - Doit gerer les valeurs null")
    void toEntity_WithNullFieldsInDTO_ShouldHandleNullValues() {
        // Given - DTO avec seulement ID
        RatingDTO dto = new RatingDTO();
        dto.setId(10);
        dto.setMoodysRating(null);
        dto.setSandPRating(null);
        dto.setFitchRating(null);
        dto.setOrderNumber(null);

        // When
        Rating result = ratingMapper.toEntity(dto);

        // Then - Verification que les null sont preserves
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getMoodysRating()).isNull();
        assertThat(result.getSandPRating()).isNull();
        assertThat(result.getFitchRating()).isNull();
        assertThat(result.getOrderNumber()).isNull();
    }

    // ================== TESTS updateEntityFromDTO() ==================

    @Test
    @DisplayName("updateEntityFromDTO() - Avec donnees valides - Doit mettre a jour tous les champs")
    void updateEntityFromDTO_WithValidData_ShouldUpdateAllFields() {
        // Given - Entite existante avec anciennes notations
        Rating existingEntity = new Rating();
        existingEntity.setId(11);
        existingEntity.setMoodysRating("Aa1");      // Ancienne notation
        existingEntity.setSandPRating("AA+");       // Ancienne notation
        existingEntity.setFitchRating("AA+");       // Ancienne notation
        existingEntity.setOrderNumber(3);           // Ancien ordre

        // DTO avec nouvelles notations (degradation)
        RatingDTO updateDTO = new RatingDTO();
        updateDTO.setId(11);
        updateDTO.setMoodysRating("A2");            // Degradation
        updateDTO.setSandPRating("A");              // Degradation
        updateDTO.setFitchRating("A");              // Degradation
        updateDTO.setOrderNumber(7);                // Nouvel ordre

        // When - Mise a jour in-place
        ratingMapper.updateEntityFromDTO(existingEntity, updateDTO);

        // Then - Verification que seuls les champs metier sont mis a jour
        assertThat(existingEntity.getId()).isEqualTo(11);           // ID preserve
        assertThat(existingEntity.getMoodysRating()).isEqualTo("A2");      // Mis a jour
        assertThat(existingEntity.getSandPRating()).isEqualTo("A");        // Mis a jour
        assertThat(existingEntity.getFitchRating()).isEqualTo("A");        // Mis a jour
        assertThat(existingEntity.getOrderNumber()).isEqualTo(7);          // Mis a jour
    }

    @Test
    @DisplayName("updateEntityFromDTO() - Avec amelioration de notation - Doit mettre a jour correctement")
    void updateEntityFromDTO_WithRatingUpgrade_ShouldUpdateCorrectly() {
        // Given - Entite avec notation speculative
        Rating existingEntity = new Rating();
        existingEntity.setId(12);
        existingEntity.setMoodysRating("Ba1");      // Speculative Grade
        existingEntity.setSandPRating("BB+");       // Speculative Grade
        existingEntity.setFitchRating("BB+");       // Speculative Grade
        existingEntity.setOrderNumber(11);

        // DTO avec amelioration vers Investment Grade
        RatingDTO upgradeDTO = new RatingDTO();
        upgradeDTO.setId(12);
        upgradeDTO.setMoodysRating("Baa3");         // Amelioration vers Investment Grade
        upgradeDTO.setSandPRating("BBB-");          // Amelioration vers Investment Grade
        upgradeDTO.setFitchRating("BBB-");          // Amelioration vers Investment Grade
        upgradeDTO.setOrderNumber(10);              // Meilleur ordre

        // When
        ratingMapper.updateEntityFromDTO(existingEntity, upgradeDTO);

        // Then - Verification de l'amelioration
        assertThat(existingEntity.getMoodysRating()).isEqualTo("Baa3");
        assertThat(existingEntity.getSandPRating()).isEqualTo("BBB-");
        assertThat(existingEntity.getFitchRating()).isEqualTo("BBB-");
        assertThat(existingEntity.getOrderNumber()).isEqualTo(10);
    }

    @Test
    @DisplayName("updateEntityFromDTO() - Avec valeurs null dans DTO - Doit accepter les null")
    void updateEntityFromDTO_WithNullValuesInDTO_ShouldAcceptNulls() {
        // Given - Entite existante avec notations
        Rating existingEntity = new Rating();
        existingEntity.setId(13);
        existingEntity.setMoodysRating("A1");
        existingEntity.setSandPRating("A+");
        existingEntity.setFitchRating("A+");
        existingEntity.setOrderNumber(5);

        // DTO avec certaines notations supprimees (null)
        RatingDTO updateDTO = new RatingDTO();
        updateDTO.setId(13);
        updateDTO.setMoodysRating("A2");            // Mise a jour
        updateDTO.setSandPRating(null);             // Suppression notation S&P
        updateDTO.setFitchRating("A");              // Mise a jour
        updateDTO.setOrderNumber(6);                // Mise a jour

        // When
        ratingMapper.updateEntityFromDTO(existingEntity, updateDTO);

        // Then - Verification que les null sont acceptes
        assertThat(existingEntity.getId()).isEqualTo(13);
        assertThat(existingEntity.getMoodysRating()).isEqualTo("A2");
        assertThat(existingEntity.getSandPRating()).isNull();          // Accepte null
        assertThat(existingEntity.getFitchRating()).isEqualTo("A");
        assertThat(existingEntity.getOrderNumber()).isEqualTo(6);
    }

    @Test
    @DisplayName("updateEntityFromDTO() - Avec entite null - Ne doit pas lever d'exception")
    void updateEntityFromDTO_WithNullEntity_ShouldNotThrowException() {
        // Given
        Rating nullEntity = null;
        RatingDTO dto = new RatingDTO();
        dto.setMoodysRating("Aaa");

        // When & Then - Ne doit pas lever d'exception
        assertThatNoException().isThrownBy(() -> 
            ratingMapper.updateEntityFromDTO(nullEntity, dto)
        );
    }

    @Test
    @DisplayName("updateEntityFromDTO() - Avec DTO null - Ne doit pas lever d'exception")
    void updateEntityFromDTO_WithNullDTO_ShouldNotThrowException() {
        // Given
        Rating entity = new Rating();
        entity.setMoodysRating("Aa1");
        RatingDTO nullDTO = null;

        // When & Then - Ne doit pas lever d'exception ni modifier l'entite
        String originalRating = entity.getMoodysRating();
        assertThatNoException().isThrownBy(() -> 
            ratingMapper.updateEntityFromDTO(entity, nullDTO)
        );
        assertThat(entity.getMoodysRating()).isEqualTo(originalRating);
    }

    // ================== TESTS createDefaultForQuality() ==================

    @Test
    @DisplayName("createDefaultForQuality() - PRIME - Doit creer notation AAA/Aaa")
    void createDefaultForQuality_Prime_ShouldCreateTripleA() {
        // When
        RatingDTO result = ratingMapper.createDefaultForQuality("PRIME");

        // Then - Verification de la notation Prime
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("Aaa");
        assertThat(result.getSandPRating()).isEqualTo("AAA");
        assertThat(result.getFitchRating()).isEqualTo("AAA");
        assertThat(result.getOrderNumber()).isEqualTo(1);
        assertThat(result.isInvestmentGrade()).isTrue();
    }

    @Test
    @DisplayName("createDefaultForQuality() - HIGH_INVESTMENT - Doit creer notation AA/Aa2")
    void createDefaultForQuality_HighInvestment_ShouldCreateDoubleA() {
        // When
        RatingDTO result = ratingMapper.createDefaultForQuality("HIGH_INVESTMENT");

        // Then - Verification de la notation High Investment
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("Aa2");
        assertThat(result.getSandPRating()).isEqualTo("AA");
        assertThat(result.getFitchRating()).isEqualTo("AA");
        assertThat(result.getOrderNumber()).isEqualTo(4);
        assertThat(result.isInvestmentGrade()).isTrue();
    }

    @Test
    @DisplayName("createDefaultForQuality() - INVESTMENT - Doit creer notation A/A2")
    void createDefaultForQuality_Investment_ShouldCreateSingleA() {
        // When
        RatingDTO result = ratingMapper.createDefaultForQuality("INVESTMENT");

        // Then - Verification de la notation Investment
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("A2");
        assertThat(result.getSandPRating()).isEqualTo("A");
        assertThat(result.getFitchRating()).isEqualTo("A");
        assertThat(result.getOrderNumber()).isEqualTo(7);
        assertThat(result.isInvestmentGrade()).isTrue();
    }

    @Test
    @DisplayName("createDefaultForQuality() - LOWER_INVESTMENT - Doit creer notation BBB/Baa2")
    void createDefaultForQuality_LowerInvestment_ShouldCreateTripleB() {
        // When
        RatingDTO result = ratingMapper.createDefaultForQuality("LOWER_INVESTMENT");

        // Then - Verification de la notation Lower Investment
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("Baa2");
        assertThat(result.getSandPRating()).isEqualTo("BBB");
        assertThat(result.getFitchRating()).isEqualTo("BBB");
        assertThat(result.getOrderNumber()).isEqualTo(10);
        assertThat(result.isInvestmentGrade()).isTrue();
    }

    @Test
    @DisplayName("createDefaultForQuality() - SPECULATIVE - Doit creer notation BB/Ba2")
    void createDefaultForQuality_Speculative_ShouldCreateDoubleB() {
        // When
        RatingDTO result = ratingMapper.createDefaultForQuality("SPECULATIVE");

        // Then - Verification de la notation Speculative
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("Ba2");
        assertThat(result.getSandPRating()).isEqualTo("BB");
        assertThat(result.getFitchRating()).isEqualTo("BB");
        assertThat(result.getOrderNumber()).isEqualTo(13);
        assertThat(result.isInvestmentGrade()).isFalse();
    }

    @Test
    @DisplayName("createDefaultForQuality() - HIGH_YIELD - Doit creer notation B/B2")
    void createDefaultForQuality_HighYield_ShouldCreateSingleB() {
        // When
        RatingDTO result = ratingMapper.createDefaultForQuality("HIGH_YIELD");

        // Then - Verification de la notation High Yield
        assertThat(result).isNotNull();
        assertThat(result.getMoodysRating()).isEqualTo("B2");
        assertThat(result.getSandPRating()).isEqualTo("B");
        assertThat(result.getFitchRating()).isEqualTo("B");
        assertThat(result.getOrderNumber()).isEqualTo(16);
        assertThat(result.isInvestmentGrade()).isFalse();
    }

    @Test
    @DisplayName("createDefaultForQuality() - Avec casse differente - Doit fonctionner (case insensitive)")
    void createDefaultForQuality_WithDifferentCase_ShouldWorkCaseInsensitive() {
        // When & Then - Test avec differentes casses
        RatingDTO prime1 = ratingMapper.createDefaultForQuality("prime");
        RatingDTO prime2 = ratingMapper.createDefaultForQuality("Prime");
        RatingDTO prime3 = ratingMapper.createDefaultForQuality("PRIME");

        // Verification que toutes donnent le meme resultat
        assertThat(prime1.getMoodysRating()).isEqualTo("Aaa");
        assertThat(prime2.getMoodysRating()).isEqualTo("Aaa");
        assertThat(prime3.getMoodysRating()).isEqualTo("Aaa");
    }

    @Test
    @DisplayName("createDefaultForQuality() - Avec niveau inconnu - Doit retourner DTO vide")
    void createDefaultForQuality_WithUnknownLevel_ShouldReturnEmptyDTO() {
        // When
        RatingDTO result = ratingMapper.createDefaultForQuality("UNKNOWN_LEVEL");

        // Then - Verification du DTO vide
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getMoodysRating()).isNull();
        assertThat(result.getSandPRating()).isNull();
        assertThat(result.getFitchRating()).isNull();
        assertThat(result.getOrderNumber()).isNull();
    }

    @Test
    @DisplayName("createDefaultForQuality() - Avec null - Doit retourner DTO vide")
    void createDefaultForQuality_WithNull_ShouldReturnEmptyDTO() {
        // When & Then - Ne doit pas lever d'exception
        assertThatNoException().isThrownBy(() -> {
            RatingDTO result = ratingMapper.createDefaultForQuality(null);
            assertThat(result).isNotNull();
            assertThat(result.getMoodysRating()).isNull();
        });
    }

    // ================== TESTS CONVERSIONS ROUND-TRIP ==================

    @Test
    @DisplayName("Conversion round-trip - Entity→DTO→Entity - Doit preserver toutes les donnees")
    void roundTripConversion_EntityToDTOToEntity_ShouldPreserveAllData() {
        // Given - Entite source avec notation complexe mixte
        Rating originalEntity = new Rating();
        originalEntity.setId(42);
        originalEntity.setMoodysRating("Baa1");     // Investment Grade limite
        originalEntity.setSandPRating("BBB+");      // Investment Grade limite
        originalEntity.setFitchRating("BB+");       // Speculative Grade (notation mixte realiste)
        originalEntity.setOrderNumber(11);

        // When - Conversion round-trip : Entity → DTO → Entity
        RatingDTO intermediateDTO = ratingMapper.toDTO(originalEntity);
        Rating finalEntity = ratingMapper.toEntity(intermediateDTO);

        // Then - Verification de la conservation parfaite des donnees
        assertThat(finalEntity).isNotNull();
        assertThat(finalEntity.getId()).isEqualTo(originalEntity.getId());
        assertThat(finalEntity.getMoodysRating()).isEqualTo(originalEntity.getMoodysRating());
        assertThat(finalEntity.getSandPRating()).isEqualTo(originalEntity.getSandPRating());
        assertThat(finalEntity.getFitchRating()).isEqualTo(originalEntity.getFitchRating());
        assertThat(finalEntity.getOrderNumber()).isEqualTo(originalEntity.getOrderNumber());
        
        // Verification coherence metier : reste Investment Grade grace a Moody's et S&P
        assertThat(intermediateDTO.isInvestmentGrade()).isTrue();
    }

    @Test
    @DisplayName("Conversion round-trip - DTO→Entity→DTO - Doit preserver toutes les donnees")
    void roundTripConversion_DTOToEntityToDTO_ShouldPreserveAllData() {
        // Given - DTO source avec notation de defaut partiel
        RatingDTO originalDTO = new RatingDTO();
        originalDTO.setId(99);
        originalDTO.setMoodysRating("Ca");          // Tres speculatif Moody's
        originalDTO.setSandPRating("CCC-");         // Substantiellement risque S&P
        originalDTO.setFitchRating("D");            // Defaut Fitch
        originalDTO.setOrderNumber(20);

        // When - Conversion round-trip : DTO → Entity → DTO
        Rating intermediateEntity = ratingMapper.toEntity(originalDTO);
        RatingDTO finalDTO = ratingMapper.toDTO(intermediateEntity);

        // Then - Verification de la conservation parfaite des donnees
        assertThat(finalDTO).isNotNull();
        assertThat(finalDTO.getId()).isEqualTo(originalDTO.getId());
        assertThat(finalDTO.getMoodysRating()).isEqualTo(originalDTO.getMoodysRating());
        assertThat(finalDTO.getSandPRating()).isEqualTo(originalDTO.getSandPRating());
        assertThat(finalDTO.getFitchRating()).isEqualTo(originalDTO.getFitchRating());
        assertThat(finalDTO.getOrderNumber()).isEqualTo(originalDTO.getOrderNumber());
        
        // Verification coherence metier : notation tres speculative
        assertThat(finalDTO.isInvestmentGrade()).isFalse();
    }

    @Test
    @DisplayName("Round-trip avec mise a jour - Entity→DTO→update→Entity - Doit integrer les changements")
    void roundTripWithUpdate_EntityToDTOUpdateToEntity_ShouldIntegrateChanges() {
        // Given - Entite initiale avec notation moyenne
        Rating originalEntity = new Rating();
        originalEntity.setId(50);
        originalEntity.setMoodysRating("Baa2");
        originalEntity.setSandPRating("BBB");
        originalEntity.setFitchRating("BBB");
        originalEntity.setOrderNumber(9);

        // When - Cycle complet avec mise a jour
        RatingDTO dto = ratingMapper.toDTO(originalEntity);
        
        // Simulation d'une degradation via l'interface utilisateur
        dto.setMoodysRating("Ba1");         // Degradation vers Speculative
        dto.setSandPRating("BB+");          // Degradation vers Speculative
        dto.setFitchRating("BB+");          // Degradation vers Speculative
        dto.setOrderNumber(11);             // Nouvel ordre (moins bon)
        
        // Mise a jour de l'entite existante
        ratingMapper.updateEntityFromDTO(originalEntity, dto);

        // Then - Verification de l'integration des changements
        assertThat(originalEntity.getId()).isEqualTo(50);           // ID preserve
        assertThat(originalEntity.getMoodysRating()).isEqualTo("Ba1");      // Mis a jour
        assertThat(originalEntity.getSandPRating()).isEqualTo("BB+");       // Mis a jour
        assertThat(originalEntity.getFitchRating()).isEqualTo("BB+");       // Mis a jour
        assertThat(originalEntity.getOrderNumber()).isEqualTo(11);          // Mis a jour
        
        // Verification que la notation n'est plus Investment Grade apres degradation
        RatingDTO updatedDTO = ratingMapper.toDTO(originalEntity);
        assertThat(updatedDTO.isInvestmentGrade()).isFalse();
    }

    // ================== TESTS CAS LIMITES ET VALEURS EXTREMES ==================

    @Test
    @DisplayName("Mapping des valeurs limites - Doit gerer les cas extremes")
    void mapping_WithBoundaryValues_ShouldHandleEdgeCases() {
        // Given - Valeurs aux limites
        Rating entity = new Rating();
        entity.setId(Integer.MAX_VALUE);             // Valeur max pour ID
        entity.setMoodysRating("C");                 // Notation minimale Moody's
        entity.setSandPRating("D");                  // Notation minimale S&P
        entity.setFitchRating("D");                  // Notation minimale Fitch
        entity.setOrderNumber(Integer.MAX_VALUE);    // Valeur max pour ordre

        // When - Conversion round-trip
        RatingDTO dto = ratingMapper.toDTO(entity);
        Rating result = ratingMapper.toEntity(dto);

        // Then - Verification des valeurs limites
        assertThat(result.getId()).isEqualTo(Integer.MAX_VALUE);
        assertThat(result.getMoodysRating()).isEqualTo("C");
        assertThat(result.getSandPRating()).isEqualTo("D");
        assertThat(result.getFitchRating()).isEqualTo("D");
        assertThat(result.getOrderNumber()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("Mapping avec notations courtes - Doit preserver les notations d'un caractere")
    void mapping_WithShortRatings_ShouldPreserveSingleCharacterRatings() {
        // Given - Notations les plus courtes possibles
        Rating entity = new Rating();
        entity.setId(1);
        entity.setMoodysRating("C");        // 1 caractere
        entity.setSandPRating("D");         // 1 caractere
        entity.setFitchRating("C");         // 1 caractere
        entity.setOrderNumber(1);

        // When - Conversion round-trip
        RatingDTO dto = ratingMapper.toDTO(entity);
        Rating result = ratingMapper.toEntity(dto);

        // Then - Verification des notations courtes
        assertThat(result.getMoodysRating()).isEqualTo("C");
        assertThat(result.getSandPRating()).isEqualTo("D");
        assertThat(result.getFitchRating()).isEqualTo("C");
    }

    @Test
    @DisplayName("Mapping avec notations longues - Doit preserver les notations complexes")
    void mapping_WithLongRatings_ShouldPreserveComplexRatings() {
        // Given - Notations les plus longues dans les standards
        Rating entity = new Rating();
        entity.setId(2);
        entity.setMoodysRating("Caa3");     // 4 caracteres (notation Moody's complexe)
        entity.setSandPRating("CCC-");      // 4 caracteres (notation S&P avec modificateur)
        entity.setFitchRating("CCC+");      // 4 caracteres (notation Fitch avec modificateur)
        entity.setOrderNumber(19);

        // When - Conversion round-trip
        RatingDTO dto = ratingMapper.toDTO(entity);
        Rating result = ratingMapper.toEntity(dto);

        // Then - Verification des notations longues
        assertThat(result.getMoodysRating()).isEqualTo("Caa3");
        assertThat(result.getSandPRating()).isEqualTo("CCC-");
        assertThat(result.getFitchRating()).isEqualTo("CCC+");
        assertThat(result.getOrderNumber()).isEqualTo(19);
    }

    @Test
    @DisplayName("Coherence Investment Grade - Verification des classifications financieres")
    void investmentGradeCoherence_ShouldValidateFinancialClassifications() {
        // Test de coherence entre les differents niveaux de qualite
        
        // Investment Grade - Niveau Prime
        RatingDTO prime = ratingMapper.createDefaultForQuality("PRIME");
        assertThat(prime.isInvestmentGrade()).isTrue();
        assertThat(prime.getOrderNumber()).isEqualTo(1);
        
        // Investment Grade - Niveau minimum
        RatingDTO lowerInv = ratingMapper.createDefaultForQuality("LOWER_INVESTMENT");
        assertThat(lowerInv.isInvestmentGrade()).isTrue();
        assertThat(lowerInv.getOrderNumber()).isGreaterThan(prime.getOrderNumber());
        
        // Speculative Grade
        RatingDTO spec = ratingMapper.createDefaultForQuality("SPECULATIVE");
        assertThat(spec.isInvestmentGrade()).isFalse();
        assertThat(spec.getOrderNumber()).isGreaterThan(lowerInv.getOrderNumber());
        
        // High Yield (plus risque)
        RatingDTO highYield = ratingMapper.createDefaultForQuality("HIGH_YIELD");
        assertThat(highYield.isInvestmentGrade()).isFalse();
        assertThat(highYield.getOrderNumber()).isGreaterThan(spec.getOrderNumber());
    }

    @Test
    @DisplayName("Test de performance - Conversions multiples - Doit etre efficace")
    void performanceTest_MultipleConversions_ShouldBeEfficient() {
        // Given - Preparation des donnees de test
        Rating testEntity = new Rating("Aa2", "AA", "AA", 4);
        testEntity.setId(100);

        long startTime = System.currentTimeMillis();

        // When - Execution de nombreuses conversions
        for (int i = 0; i < 1000; i++) {
            RatingDTO dto = ratingMapper.toDTO(testEntity);
            Rating entity = ratingMapper.toEntity(dto);
            ratingMapper.updateEntityFromDTO(testEntity, dto);
        }

        long executionTime = System.currentTimeMillis() - startTime;

        // Then - Verification que les performances sont acceptables (< 100ms pour 1000 operations)
        assertThat(executionTime).isLessThan(100L);
        System.out.println("Temps d'execution pour 1000 conversions: " + executionTime + "ms");
    }

    @Test
    @DisplayName("Integrite des donnees financieres - Verification complete")
    void financialDataIntegrity_CompleteVerification() {
        // Test d'integrite complete avec tous les cas de notations reels
        
        String[] moodysRatings = {"Aaa", "Aa1", "Aa2", "Aa3", "A1", "A2", "A3", 
                                 "Baa1", "Baa2", "Baa3", "Ba1", "Ba2", "Ba3", 
                                 "B1", "B2", "B3", "Caa1", "Caa2", "Caa3", "Ca", "C"};
        
        String[] spRatings = {"AAA", "AA+", "AA", "AA-", "A+", "A", "A-", 
                             "BBB+", "BBB", "BBB-", "BB+", "BB", "BB-", 
                             "B+", "B", "B-", "CCC+", "CCC", "CCC-", "CC", "C", "D"};

        // Verification que toutes les combinaisons sont supportees
        for (int i = 0; i < Math.min(moodysRatings.length, spRatings.length); i++) {
            Rating entity = new Rating();
            entity.setId(i + 200);
            entity.setMoodysRating(moodysRatings[i]);
            entity.setSandPRating(spRatings[i]);
            entity.setFitchRating(spRatings[i]);  // Fitch = S&P
            entity.setOrderNumber(i + 1);

            // Test de conversion
            RatingDTO dto = ratingMapper.toDTO(entity);
            Rating result = ratingMapper.toEntity(dto);

            // Verification de l'integrite
            assertThat(result.getMoodysRating()).isEqualTo(moodysRatings[i]);
            assertThat(result.getSandPRating()).isEqualTo(spRatings[i]);
            assertThat(result.getFitchRating()).isEqualTo(spRatings[i]);
            assertThat(result.getOrderNumber()).isEqualTo(i + 1);
        }
    }
}
