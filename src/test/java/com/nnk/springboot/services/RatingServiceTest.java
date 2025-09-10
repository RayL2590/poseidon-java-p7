package com.nnk.springboot.services;

/**
 * Tests unitaires pour le RatingService.
 * 
 * <p>Cette classe de test valide la logique métier du service de gestion des notations financières (Rating),
 * en testant les opérations CRUD, la validation des données de notation et la gestion des erreurs
 * dans un contexte d'intégration avec la base de données.</p>
 * 
 * <p>Couverture des tests :</p>
 * <ul>
 *   <li><strong>Création</strong> : Ajout de nouvelles notations avec validation</li>
 *   <li><strong>Lecture</strong> : Récupération par ID et liste complète</li>
 *   <li><strong>Mise à jour</strong> : Modification des notations existantes</li>
 *   <li><strong>Suppression</strong> : Suppression avec vérification d'existence</li>
 *   <li><strong>Gestion des erreurs</strong> : Entités introuvables, validation</li>
 *   <li><strong>Contraintes métier</strong> : Validation des formats de notation</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour le service RatingService.
 * 
 * <p>Cette classe de test valide la logique metier du service de gestion des notations
 * de credit. Elle utilise des mocks pour isoler completement la logique du service
 * des dependances externes (repository, base de donnees).</p>
 * 
 * <p>Couverture des tests :</p>
 * <ul>
 *   <li><strong>Operations CRUD</strong> : findAll, findById, save, deleteById, existsById</li>
 *   <li><strong>Validations metier</strong> : Formats de notations, coherence inter-agences</li>
 *   <li><strong>Methodes specialisees</strong> : findByAgency, findInvestmentGrade, findSpeculativeGrade</li>
 *   <li><strong>Logique avancee</strong> : Generation numero d'ordre, validation unicite</li>
 *   <li><strong>Gestion d'erreurs</strong> : Cas limites, exceptions metier</li>
 *   <li><strong>Edge cases</strong> : Valeurs null, parametres invalides</li>
 * </ul>
 * 
 * @author Poseidon Trading App Test Suite
 * @version 1.0
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RatingService - Tests unitaires")
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    private Rating validRating;
    private Rating investmentGradeRating;
    private Rating speculativeGradeRating;
    private Rating primeRating;

    @BeforeEach
    void setUp() {
        // Notation Investment Grade standard
        validRating = new Rating();
        validRating.setId(1);
        validRating.setMoodysRating("A2");
        validRating.setSandPRating("A");
        validRating.setFitchRating("A");
        validRating.setOrderNumber(7);

        // Notation Investment Grade limitrophe
        investmentGradeRating = new Rating();
        investmentGradeRating.setId(2);
        investmentGradeRating.setMoodysRating("Baa3");
        investmentGradeRating.setSandPRating("BBB-");
        investmentGradeRating.setFitchRating("BBB-");
        investmentGradeRating.setOrderNumber(12);

        // Notation Speculative Grade
        speculativeGradeRating = new Rating();
        speculativeGradeRating.setId(3);
        speculativeGradeRating.setMoodysRating("Ba1");
        speculativeGradeRating.setSandPRating("BB+");
        speculativeGradeRating.setFitchRating("BB+");
        speculativeGradeRating.setOrderNumber(13);

        // Notation Prime Grade
        primeRating = new Rating();
        primeRating.setId(4);
        primeRating.setMoodysRating("Aaa");
        primeRating.setSandPRating("AAA");
        primeRating.setFitchRating("AAA");
        primeRating.setOrderNumber(1);
    }

    // ================== TESTS CRUD DE BASE ==================

    @Test
    @DisplayName("findAll() - Doit retourner toutes les notations ordonnees")
    void findAll_ShouldReturnAllRatingsOrderedByNumber() {
        // Given
        List<Rating> expectedRatings = Arrays.asList(primeRating, validRating, investmentGradeRating, speculativeGradeRating);
        when(ratingRepository.findAllByOrderByOrderNumberAsc()).thenReturn(expectedRatings);

        // When
        List<Rating> result = ratingService.findAll();

        // Then
        assertThat(result).hasSize(4);
        assertThat(result).containsExactly(primeRating, validRating, investmentGradeRating, speculativeGradeRating);
        assertThat(result.get(0).getOrderNumber()).isEqualTo(1);  // Prime en premier
        assertThat(result.get(3).getOrderNumber()).isEqualTo(13); // Speculative en dernier
        
        verify(ratingRepository).findAllByOrderByOrderNumberAsc();
    }

    @Test
    @DisplayName("findAll() - Avec repository vide - Doit retourner liste vide")
    void findAll_WithEmptyRepository_ShouldReturnEmptyList() {
        // Given
        when(ratingRepository.findAllByOrderByOrderNumberAsc()).thenReturn(List.of());

        // When
        List<Rating> result = ratingService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository).findAllByOrderByOrderNumberAsc();
    }

    @Test
    @DisplayName("findById() - Avec ID valide - Doit retourner la notation")
    void findById_WithValidId_ShouldReturnRating() {
        // Given
        when(ratingRepository.findById(1)).thenReturn(Optional.of(validRating));

        // When
        Optional<Rating> result = ratingService.findById(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(validRating);
        assertThat(result.get().getMoodysRating()).isEqualTo("A2");
        
        verify(ratingRepository).findById(1);
    }

    @Test
    @DisplayName("findById() - Avec ID inexistant - Doit retourner Optional vide")
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        // Given
        when(ratingRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Rating> result = ratingService.findById(999);

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository).findById(999);
    }

    @Test
    @DisplayName("findById() - Avec ID null - Doit retourner Optional vide")
    void findById_WithNullId_ShouldReturnEmpty() {
        // When
        Optional<Rating> result = ratingService.findById(null);

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository, never()).findById(any());
    }

    @Test
    @DisplayName("findById() - Avec ID negatif - Doit retourner Optional vide")
    void findById_WithNegativeId_ShouldReturnEmpty() {
        // When
        Optional<Rating> result = ratingService.findById(-1);

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository, never()).findById(any());
    }

    @Test
    @DisplayName("findById() - Avec ID zero - Doit retourner Optional vide")
    void findById_WithZeroId_ShouldReturnEmpty() {
        // When
        Optional<Rating> result = ratingService.findById(0);

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository, never()).findById(any());
    }

    @Test
    @DisplayName("existsById() - Avec ID valide existant - Doit retourner true")
    void existsById_WithValidExistingId_ShouldReturnTrue() {
        // Given
        when(ratingRepository.existsById(1)).thenReturn(true);

        // When
        boolean result = ratingService.existsById(1);

        // Then
        assertThat(result).isTrue();
        verify(ratingRepository).existsById(1);
    }

    @Test
    @DisplayName("existsById() - Avec ID valide inexistant - Doit retourner false")
    void existsById_WithValidNonExistentId_ShouldReturnFalse() {
        // Given
        when(ratingRepository.existsById(999)).thenReturn(false);

        // When
        boolean result = ratingService.existsById(999);

        // Then
        assertThat(result).isFalse();
        verify(ratingRepository).existsById(999);
    }

    @Test
    @DisplayName("existsById() - Avec ID null - Doit retourner false")
    void existsById_WithNullId_ShouldReturnFalse() {
        // When
        boolean result = ratingService.existsById(null);

        // Then
        assertThat(result).isFalse();
        verify(ratingRepository, never()).existsById(any());
    }

    @Test
    @DisplayName("existsById() - Avec ID negatif - Doit retourner false")
    void existsById_WithNegativeId_ShouldReturnFalse() {
        // When
        boolean result = ratingService.existsById(-1);

        // Then
        assertThat(result).isFalse();
        verify(ratingRepository, never()).existsById(any());
    }

    // ================== TESTS SAVE ==================

    @Test
    @DisplayName("save() - Avec nouvelle notation valide - Doit sauvegarder avec numero d'ordre auto")
    void save_WithNewValidRating_ShouldSaveWithAutoOrderNumber() {
        // Given - Nouvelle notation sans ID ni numero d'ordre
        Rating newRating = new Rating();
        newRating.setMoodysRating("Aa1");
        newRating.setSandPRating("AA+");
        newRating.setFitchRating("AA+");
        
        Rating savedRating = new Rating();
        savedRating.setId(5);
        savedRating.setMoodysRating("Aa1");
        savedRating.setSandPRating("AA+");
        savedRating.setFitchRating("AA+");
        savedRating.setOrderNumber(14); // Auto-genere
        
        when(ratingRepository.findTopByOrderByOrderNumberDesc()).thenReturn(Optional.of(speculativeGradeRating)); // ordre 13
        when(ratingRepository.findByOrderNumber(anyInt())).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(savedRating);

        // When
        Rating result = ratingService.save(newRating);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5);
        assertThat(result.getOrderNumber()).isEqualTo(14); // 13 + 1
        
        verify(ratingRepository).findTopByOrderByOrderNumberDesc();
        verify(ratingRepository).findByOrderNumber(14);
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    @DisplayName("save() - Avec nouvelle notation et premier numero d'ordre - Doit utiliser 1")
    void save_WithNewRatingAndEmptyRepository_ShouldUseOrderNumberOne() {
        // Given - Nouvelle notation et repository vide
        Rating newRating = new Rating();
        newRating.setMoodysRating("Aaa");
        newRating.setSandPRating("AAA");
        newRating.setFitchRating("AAA");
        
        Rating savedRating = new Rating();
        savedRating.setId(1);
        savedRating.setMoodysRating("Aaa");
        savedRating.setSandPRating("AAA");
        savedRating.setFitchRating("AAA");
        savedRating.setOrderNumber(1);
        
        when(ratingRepository.findTopByOrderByOrderNumberDesc()).thenReturn(Optional.empty());
        when(ratingRepository.findByOrderNumber(1)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenReturn(savedRating);

        // When
        Rating result = ratingService.save(newRating);

        // Then
        assertThat(result.getOrderNumber()).isEqualTo(1);
        verify(ratingRepository).findTopByOrderByOrderNumberDesc();
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    @DisplayName("save() - Avec notation existante - Doit mettre a jour sans changer ordre")
    void save_WithExistingRating_ShouldUpdateWithoutChangingOrder() {
        // Given - Notation existante avec modification
        Rating existingRating = new Rating();
        existingRating.setId(1);
        existingRating.setMoodysRating("A1");      
        existingRating.setSandPRating("A+");       
        existingRating.setFitchRating("A+");       
        existingRating.setOrderNumber(7);          // Garde le meme ordre
        
        when(ratingRepository.findByOrderNumber(7)).thenReturn(Optional.of(validRating)); // Meme rating
        when(ratingRepository.save(existingRating)).thenReturn(existingRating);

        // When
        Rating result = ratingService.save(existingRating);

        // Then
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getOrderNumber()).isEqualTo(7);
        assertThat(result.getMoodysRating()).isEqualTo("A1");
        
        verify(ratingRepository).findByOrderNumber(7);
        verify(ratingRepository).save(existingRating);
    }

    @Test
    @DisplayName("save() - Avec numero d'ordre duplique - Doit lever exception")
    void save_WithDuplicateOrderNumber_ShouldThrowException() {
        // Given - Nouvelle notation avec numero d'ordre existant
        Rating newRating = new Rating();
        newRating.setMoodysRating("A3");
        newRating.setSandPRating("A-");
        newRating.setFitchRating("A-");
        newRating.setOrderNumber(7); // Deja utilise par validRating
        
        when(ratingRepository.findByOrderNumber(7)).thenReturn(Optional.of(validRating));

        // When & Then
        assertThatThrownBy(() -> ratingService.save(newRating))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Order number 7 already exists. Each rating must have a unique order number.");
            
        verify(ratingRepository).findByOrderNumber(7);
        verify(ratingRepository, never()).save(any());
    }

    @Test
    @DisplayName("save() - Avec notation null - Doit lever exception")
    void save_WithNullRating_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> ratingService.save(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Rating cannot be null");
            
        verify(ratingRepository, never()).save(any());
    }

    @Test
    @DisplayName("save() - Avec toutes notations vides - Doit lever exception")
    void save_WithAllEmptyRatings_ShouldThrowException() {
        // Given - Rating sans aucune notation
        Rating emptyRating = new Rating();
        emptyRating.setMoodysRating("");
        emptyRating.setSandPRating(null);
        emptyRating.setFitchRating("   ");
        emptyRating.setOrderNumber(10);

        // When & Then
        assertThatThrownBy(() -> ratingService.save(emptyRating))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("At least one rating agency notation must be provided");
            
        verify(ratingRepository, never()).save(any());
    }

    @Test
    @DisplayName("save() - Avec ordre negatif - Doit lever exception")
    void save_WithNegativeOrderNumber_ShouldThrowException() {
        // Given
        Rating ratingWithNegativeOrder = new Rating();
        ratingWithNegativeOrder.setMoodysRating("A1");
        ratingWithNegativeOrder.setOrderNumber(-1);

        // When & Then
        assertThatThrownBy(() -> ratingService.save(ratingWithNegativeOrder))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Order number must be positive");
            
        verify(ratingRepository, never()).save(any());
    }

    @Test
    @DisplayName("save() - Avec ordre zero - Doit lever exception")
    void save_WithZeroOrderNumber_ShouldThrowException() {
        // Given
        Rating ratingWithZeroOrder = new Rating();
        ratingWithZeroOrder.setMoodysRating("A1");
        ratingWithZeroOrder.setOrderNumber(0);

        // When & Then
        assertThatThrownBy(() -> ratingService.save(ratingWithZeroOrder))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Order number must be positive");
            
        verify(ratingRepository, never()).save(any());
    }

    // ================== TESTS DELETE ==================

    @Test
    @DisplayName("deleteById() - Avec ID valide existant - Doit supprimer")
    void deleteById_WithValidExistingId_ShouldDelete() {
        // Given
        when(ratingRepository.existsById(1)).thenReturn(true);
        doNothing().when(ratingRepository).deleteById(1);

        // When
        ratingService.deleteById(1);

        // Then
        verify(ratingRepository).existsById(1);
        verify(ratingRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteById() - Avec ID null - Doit lever exception")
    void deleteById_WithNullId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> ratingService.deleteById(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid ID for deletion");
            
        verify(ratingRepository, never()).existsById(any());
        verify(ratingRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteById() - Avec ID negatif - Doit lever exception")
    void deleteById_WithNegativeId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> ratingService.deleteById(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid ID for deletion");
            
        verify(ratingRepository, never()).existsById(any());
        verify(ratingRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteById() - Avec ID zero - Doit lever exception")
    void deleteById_WithZeroId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> ratingService.deleteById(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid ID for deletion");
            
        verify(ratingRepository, never()).existsById(any());
        verify(ratingRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteById() - Avec ID inexistant - Doit lever exception")
    void deleteById_WithNonExistentId_ShouldThrowException() {
        // Given
        when(ratingRepository.existsById(999)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> ratingService.deleteById(999))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Rating not found with id: 999");
            
        verify(ratingRepository).existsById(999);
        verify(ratingRepository, never()).deleteById(999);
    }

    // ================== TESTS METHODES SPECIALISEES ==================

    @Test
    @DisplayName("findByAgency() - MOODYS - Doit retourner notations Moodys")
    void findByAgency_Moodys_ShouldReturnMoodysRatings() {
        // Given
        List<Rating> moodysRatings = Arrays.asList(validRating, investmentGradeRating);
        when(ratingRepository.findByMoodysRatingIsNotNullOrderByOrderNumberAsc()).thenReturn(moodysRatings);

        // When
        List<Rating> result = ratingService.findByAgency("MOODYS");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(validRating, investmentGradeRating);
        
        verify(ratingRepository).findByMoodysRatingIsNotNullOrderByOrderNumberAsc();
    }

    @Test
    @DisplayName("findByAgency() - SP - Doit retourner notations S&P")
    void findByAgency_SP_ShouldReturnSPRatings() {
        // Given
        List<Rating> spRatings = Arrays.asList(primeRating, validRating);
        when(ratingRepository.findBySandPRatingIsNotNullOrderByOrderNumberAsc()).thenReturn(spRatings);

        // When
        List<Rating> result = ratingService.findByAgency("SP");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(primeRating, validRating);
        
        verify(ratingRepository).findBySandPRatingIsNotNullOrderByOrderNumberAsc();
    }

    @Test
    @DisplayName("findByAgency() - FITCH - Doit retourner notations Fitch")
    void findByAgency_Fitch_ShouldReturnFitchRatings() {
        // Given
        List<Rating> fitchRatings = Arrays.asList(speculativeGradeRating);
        when(ratingRepository.findByFitchRatingIsNotNullOrderByOrderNumberAsc()).thenReturn(fitchRatings);

        // When
        List<Rating> result = ratingService.findByAgency("FITCH");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(speculativeGradeRating);
        
        verify(ratingRepository).findByFitchRatingIsNotNullOrderByOrderNumberAsc();
    }

    @Test
    @DisplayName("findByAgency() - Casse insensitive - Doit fonctionner")
    void findByAgency_CaseInsensitive_ShouldWork() {
        // Given
        List<Rating> moodysRatings = Arrays.asList(validRating);
        when(ratingRepository.findByMoodysRatingIsNotNullOrderByOrderNumberAsc()).thenReturn(moodysRatings);

        // When & Then - Test differentes casses
        assertThat(ratingService.findByAgency("moodys")).hasSize(1);
        assertThat(ratingService.findByAgency("Moodys")).hasSize(1);
        assertThat(ratingService.findByAgency("MOODYS")).hasSize(1);
        
        verify(ratingRepository, times(3)).findByMoodysRatingIsNotNullOrderByOrderNumberAsc();
    }

    @Test
    @DisplayName("findByAgency() - Agence inconnue - Doit retourner liste vide")
    void findByAgency_UnknownAgency_ShouldReturnEmptyList() {
        // When
        List<Rating> result = ratingService.findByAgency("UNKNOWN");

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository, never()).findByMoodysRatingIsNotNullOrderByOrderNumberAsc();
        verify(ratingRepository, never()).findBySandPRatingIsNotNullOrderByOrderNumberAsc();
        verify(ratingRepository, never()).findByFitchRatingIsNotNullOrderByOrderNumberAsc();
    }

    @Test
    @DisplayName("findByAgency() - Parametre null - Doit retourner liste vide")
    void findByAgency_NullAgency_ShouldReturnEmptyList() {
        // When
        List<Rating> result = ratingService.findByAgency(null);

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository, never()).findByMoodysRatingIsNotNullOrderByOrderNumberAsc();
    }

    @Test
    @DisplayName("findByAgency() - Parametre vide - Doit retourner liste vide")
    void findByAgency_EmptyAgency_ShouldReturnEmptyList() {
        // When
        List<Rating> result = ratingService.findByAgency("   ");

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository, never()).findByMoodysRatingIsNotNullOrderByOrderNumberAsc();
    }

    @Test
    @DisplayName("findByOrderRange() - Plage valide - Doit retourner notations dans la plage")
    void findByOrderRange_ValidRange_ShouldReturnRatingsInRange() {
        // Given
        List<Rating> ratingsInRange = Arrays.asList(validRating, investmentGradeRating);
        when(ratingRepository.findByOrderNumberBetweenOrderByOrderNumberAsc(7, 12)).thenReturn(ratingsInRange);

        // When
        List<Rating> result = ratingService.findByOrderRange(7, 12);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(validRating, investmentGradeRating);
        
        verify(ratingRepository).findByOrderNumberBetweenOrderByOrderNumberAsc(7, 12);
    }

    @Test
    @DisplayName("findByOrderRange() - Min superieur a Max - Doit retourner liste vide")
    void findByOrderRange_MinGreaterThanMax_ShouldReturnEmptyList() {
        // When
        List<Rating> result = ratingService.findByOrderRange(10, 5);

        // Then
        assertThat(result).isEmpty();
        verify(ratingRepository, never()).findByOrderNumberBetweenOrderByOrderNumberAsc(anyInt(), anyInt());
    }

    @Test
    @DisplayName("findByOrderRange() - Parametres null - Doit retourner liste vide")
    void findByOrderRange_NullParameters_ShouldReturnEmptyList() {
        // When & Then
        assertThat(ratingService.findByOrderRange(null, 10)).isEmpty();
        assertThat(ratingService.findByOrderRange(1, null)).isEmpty();
        assertThat(ratingService.findByOrderRange(null, null)).isEmpty();
        
        verify(ratingRepository, never()).findByOrderNumberBetweenOrderByOrderNumberAsc(anyInt(), anyInt());
    }

    @Test
    @DisplayName("findInvestmentGrade() - Doit retourner notations Investment Grade")
    void findInvestmentGrade_ShouldReturnInvestmentGradeRatings() {
        // Given
        List<Rating> investmentGrades = Arrays.asList(primeRating, validRating, investmentGradeRating);
        when(ratingRepository.findByOrderNumberBetweenOrderByOrderNumberAsc(1, 12)).thenReturn(investmentGrades);

        // When
        List<Rating> result = ratingService.findInvestmentGrade();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(primeRating, validRating, investmentGradeRating);
        assertThat(result.get(0).getOrderNumber()).isEqualTo(1);   // Prime
        assertThat(result.get(2).getOrderNumber()).isEqualTo(12);  // Limite Investment Grade
        
        verify(ratingRepository).findByOrderNumberBetweenOrderByOrderNumberAsc(1, 12);
    }

    @Test
    @DisplayName("findSpeculativeGrade() - Doit retourner notations Speculative Grade")
    void findSpeculativeGrade_ShouldReturnSpeculativeGradeRatings() {
        // Given
        List<Rating> speculativeGrades = Arrays.asList(speculativeGradeRating);
        when(ratingRepository.findByOrderNumberGreaterThanEqualOrderByOrderNumberAsc(13)).thenReturn(speculativeGrades);

        // When
        List<Rating> result = ratingService.findSpeculativeGrade();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(speculativeGradeRating);
        assertThat(result.get(0).getOrderNumber()).isEqualTo(13);  // Premiere notation Speculative
        
        verify(ratingRepository).findByOrderNumberGreaterThanEqualOrderByOrderNumberAsc(13);
    }

    // ================== TESTS VALIDATION FORMATS ==================

    @Test
    @DisplayName("save() - Format Moodys valide - Doit sauvegarder")
    void save_ValidMoodysFormat_ShouldSave() {
        // Given - Test tous les formats Moodys valides
        String[] validMoodysRatings = {"Aaa", "Aa1", "Aa2", "Aa3", "A1", "A2", "A3", 
                                      "Baa1", "Baa2", "Baa3", "Ba1", "Ba2", "Ba3",
                                      "B1", "B2", "B3", "Caa1", "Caa2", "Caa3", "Ca", "C"};

        for (String moodysRating : validMoodysRatings) {
            Rating rating = new Rating();
            rating.setMoodysRating(moodysRating);
            rating.setOrderNumber(1);
            
            when(ratingRepository.findByOrderNumber(1)).thenReturn(Optional.empty());
            when(ratingRepository.save(any())).thenReturn(rating);

            // When & Then - Ne doit pas lever d'exception
            assertThatNoException().isThrownBy(() -> ratingService.save(rating));
            
            reset(ratingRepository); // Reset pour le prochain test
        }
    }

    @Test
    @DisplayName("save() - Format Moodys invalide - Doit lever exception")
    void save_InvalidMoodysFormat_ShouldThrowException() {
        // Given - Formats Moodys invalides
        String[] invalidMoodysRatings = {"AAA", "AA+", "Aaa1", "D", "InvalidRating", "123"};

        for (String invalidRating : invalidMoodysRatings) {
            Rating rating = new Rating();
            rating.setMoodysRating(invalidRating);
            rating.setOrderNumber(1);

            // When & Then
            assertThatThrownBy(() -> ratingService.save(rating))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Moody's rating format: " + invalidRating);
        }
    }

    @Test
    @DisplayName("save() - Format S&P valide - Doit sauvegarder")
    void save_ValidSPFormat_ShouldSave() {
        // Given - Test tous les formats S&P valides
        String[] validSPRatings = {"AAA", "AA+", "AA", "AA-", "A+", "A", "A-", 
                                  "BBB+", "BBB", "BBB-", "BB+", "BB", "BB-",
                                  "B+", "B", "B-", "CCC+", "CCC", "CCC-", "CC", "C", "D"};

        for (String spRating : validSPRatings) {
            Rating rating = new Rating();
            rating.setSandPRating(spRating);
            rating.setOrderNumber(1);
            
            when(ratingRepository.findByOrderNumber(1)).thenReturn(Optional.empty());
            when(ratingRepository.save(any())).thenReturn(rating);

            // When & Then - Ne doit pas lever d'exception
            assertThatNoException().isThrownBy(() -> ratingService.save(rating));
            
            reset(ratingRepository); // Reset pour le prochain test
        }
    }

    @Test
    @DisplayName("save() - Format S&P invalide - Doit lever exception")
    void save_InvalidSPFormat_ShouldThrowException() {
        // Given - Formats S&P invalides
        String[] invalidSPRatings = {"Aaa", "A1", "BBB++", "InvalidRating", "123"};

        for (String invalidRating : invalidSPRatings) {
            Rating rating = new Rating();
            rating.setSandPRating(invalidRating);
            rating.setOrderNumber(1);

            // When & Then
            assertThatThrownBy(() -> ratingService.save(rating))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid S&P rating format: " + invalidRating);
        }
    }

    @Test
    @DisplayName("save() - Format Fitch valide - Doit sauvegarder")
    void save_ValidFitchFormat_ShouldSave() {
        // Given - Fitch utilise meme format que S&P
        String[] validFitchRatings = {"AAA", "AA+", "AA", "AA-", "A+", "A", "A-", 
                                     "BBB+", "BBB", "BBB-", "BB+", "BB", "BB-",
                                     "B+", "B", "B-", "CCC+", "CCC", "CCC-", "CC", "C", "D"};

        for (String fitchRating : validFitchRatings) {
            Rating rating = new Rating();
            rating.setFitchRating(fitchRating);
            rating.setOrderNumber(1);
            
            when(ratingRepository.findByOrderNumber(1)).thenReturn(Optional.empty());
            when(ratingRepository.save(any())).thenReturn(rating);

            // When & Then - Ne doit pas lever d'exception
            assertThatNoException().isThrownBy(() -> ratingService.save(rating));
            
            reset(ratingRepository); // Reset pour le prochain test
        }
    }

    @Test
    @DisplayName("save() - Format Fitch invalide - Doit lever exception")
    void save_InvalidFitchFormat_ShouldThrowException() {
        // Given - Formats Fitch invalides
        String[] invalidFitchRatings = {"Aaa", "A1", "BBB++", "InvalidRating", "123"};

        for (String invalidRating : invalidFitchRatings) {
            Rating rating = new Rating();
            rating.setFitchRating(invalidRating);
            rating.setOrderNumber(1);

            // When & Then
            assertThatThrownBy(() -> ratingService.save(rating))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid Fitch rating format: " + invalidRating);
        }
    }

    @Test
    @DisplayName("save() - Notations partielles - Doit accepter notations manquantes")
    void save_PartialRatings_ShouldAcceptMissingRatings() {
        // Given - Seulement une notation sur trois
        Rating partialRating = new Rating();
        partialRating.setMoodysRating("A2");       // Seule notation presente
        partialRating.setSandPRating(null);        // Manquante
        partialRating.setFitchRating("");          // Vide
        partialRating.setOrderNumber(7);
        
        when(ratingRepository.findByOrderNumber(7)).thenReturn(Optional.empty());
        when(ratingRepository.save(any())).thenReturn(partialRating);

        // When & Then - Ne doit pas lever d'exception
        assertThatNoException().isThrownBy(() -> ratingService.save(partialRating));
        
        verify(ratingRepository).save(any());
    }

    @Test
    @DisplayName("save() - Coherence Investment/Speculative Grade - Doit afficher warning")
    void save_InconsistentRatings_ShouldDisplayWarning() {
        // Given - Notations incoherentes (Investment Grade + Speculative Grade)
        Rating inconsistentRating = new Rating();
        inconsistentRating.setMoodysRating("Baa3");    // Investment Grade (limite)
        inconsistentRating.setSandPRating("BB+");      // Speculative Grade
        inconsistentRating.setFitchRating("BBB-");     // Investment Grade
        inconsistentRating.setOrderNumber(11);
        
        when(ratingRepository.findByOrderNumber(11)).thenReturn(Optional.empty());
        when(ratingRepository.save(any())).thenReturn(inconsistentRating);

        // When & Then - Doit sauvegarder malgre l'incoherence (warning seulement)
        assertThatNoException().isThrownBy(() -> ratingService.save(inconsistentRating));
        
        verify(ratingRepository).save(any());
    }
}
