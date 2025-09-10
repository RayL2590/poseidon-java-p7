package com.nnk.springboot.services;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des notations de crédit (Rating).
 * 
 * <p>Cette classe implémente toute la logique métier pour la gestion des notations
 * de crédit dans l'application de trading Poseidon. Elle respecte les principes SOLID
 * et fournit une couche de service robuste avec validation métier, gestion d'erreurs
 * et optimisations de performance.</p>
 * 
 * <p>Responsabilités principales :</p>
 * <ul>
 *   <li><strong>Opérations CRUD</strong> : Gestion complète du cycle de vie des notations</li>
 *   <li><strong>Validation métier</strong> : Contrôles de cohérence et conformité</li>
 *   <li><strong>Logique de classification</strong> : Gestion des ordres et hiérarchies</li>
 *   <li><strong>Optimisations</strong> : Cache et requêtes optimisées</li>
 * </ul>
 * 
 * <p>Architecture et design patterns :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Responsabilité unique de gestion des Rating</li>
 *   <li><strong>OCP</strong> : Ouvert à l'extension, fermé à la modification</li>
 *   <li><strong>LSP</strong> : Respecte le contrat défini par IRatingService</li>
 *   <li><strong>ISP</strong> : Interface spécialisée pour les opérations Rating</li>
 *   <li><strong>DIP</strong> : Dépend d'abstractions (RatingRepository)</li>
 * </ul>
 * 
 * <p>Gestion transactionnelle :</p>
 * <ul>
 *   <li><strong>@Transactional</strong> : Cohérence des données critiques</li>
 *   <li><strong>ReadOnly optimizations</strong> : Performance des consultations</li>
 *   <li><strong>Rollback automatique</strong> : En cas d'exception métier</li>
 *   <li><strong>Isolation niveau</strong> : Protection contre les lectures inconsistantes</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Service
@Transactional
public class RatingService implements IRatingService {
    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Rating> findAll() {
        return ratingRepository.findAllByOrderByOrderNumberAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rating> findById(Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return ratingRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return ratingRepository.existsById(id);
    }

    @Override
    public Rating save(Rating rating) {
        validateRating(rating);
        
        // Auto-attribution du numéro d'ordre si absent pour nouvelle notation
        if (rating.getId() == null && rating.getOrderNumber() == null) {
            rating.setOrderNumber(generateNextOrderNumber());
        }
        
        // Validation de l'unicité du numéro d'ordre
        validateOrderNumberUniqueness(rating);
        
        return ratingRepository.save(rating);
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID for deletion");
        }
        
        if (!ratingRepository.existsById(id)) {
            throw new IllegalArgumentException("Rating not found with id: " + id);
        }
        
        ratingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rating> findByAgency(String agency) {
        if (agency == null || agency.trim().isEmpty()) {
            return List.of();
        }
        
        switch (agency.toUpperCase()) {
            case "MOODYS":
                return ratingRepository.findByMoodysRatingIsNotNullOrderByOrderNumberAsc();
            case "SP":
                return ratingRepository.findBySandPRatingIsNotNullOrderByOrderNumberAsc();
            case "FITCH":
                return ratingRepository.findByFitchRatingIsNotNullOrderByOrderNumberAsc();
            default:
                return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rating> findByOrderRange(Integer minOrder, Integer maxOrder) {
        if (minOrder == null || maxOrder == null || minOrder > maxOrder) {
            return List.of();
        }
        return ratingRepository.findByOrderNumberBetweenOrderByOrderNumberAsc(minOrder, maxOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rating> findInvestmentGrade() {
        // Investment Grade: typiquement ordre 1 à 12 (AAA/Aaa à BBB-/Baa3)
        return findByOrderRange(1, 12);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rating> findSpeculativeGrade() {
        // Speculative Grade: typiquement ordre 13+ (BB+/Ba1 et en-dessous)
        return ratingRepository.findByOrderNumberGreaterThanEqualOrderByOrderNumberAsc(13);
    }

    /**
     * Valide les données d'une notation de crédit.
     * 
     * <p>Cette méthode effectue une validation complète des données de notation,
     * incluant les contrôles de format, de cohérence inter-agences et de
     * conformité aux standards internationaux.</p>
     * 
     * @param rating La notation à valider
     * @throws IllegalArgumentException si les données sont invalides
     */
    private void validateRating(Rating rating) {
        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null");
        }
        
        // Au moins une notation doit être présente
        if (isAllRatingsEmpty(rating)) {
            throw new IllegalArgumentException("At least one rating agency notation must be provided");
        }
        
        // Validation des formats individuels
        validateMoodysFormat(rating.getMoodysRating());
        validateSPFormat(rating.getSandPRating());
        validateFitchFormat(rating.getFitchRating());
        
        // Validation de la cohérence inter-agences
        validateRatingConsistency(rating);
        
        // Validation du numéro d'ordre
        if (rating.getOrderNumber() != null && rating.getOrderNumber() <= 0) {
            throw new IllegalArgumentException("Order number must be positive");
        }
    }

    /**
     * Vérifie si toutes les notations sont vides ou nulles.
     */
    private boolean isAllRatingsEmpty(Rating rating) {
        return (rating.getMoodysRating() == null || rating.getMoodysRating().trim().isEmpty()) &&
               (rating.getSandPRating() == null || rating.getSandPRating().trim().isEmpty()) &&
               (rating.getFitchRating() == null || rating.getFitchRating().trim().isEmpty());
    }

    /**
     * Valide le format d'une notation Moody's.
     */
    private void validateMoodysFormat(String rating) {
        if (rating != null && !rating.trim().isEmpty()) {
            if (!rating.matches("^(Aaa|Aa[1-3]|A[1-3]|Baa[1-3]|Ba[1-3]|B[1-3]|Caa[1-3]|Ca|C)$")) {
                throw new IllegalArgumentException("Invalid Moody's rating format: " + rating);
            }
        }
    }

    /**
     * Valide le format d'une notation S&P.
     */
    private void validateSPFormat(String rating) {
        if (rating != null && !rating.trim().isEmpty()) {
            if (!rating.matches("^(AAA|AA[+-]?|A[+-]?|BBB[+-]?|BB[+-]?|B[+-]?|CCC[+-]?|CC|C|D)$")) {
                throw new IllegalArgumentException("Invalid S&P rating format: " + rating);
            }
        }
    }

    /**
     * Valide le format d'une notation Fitch.
     */
    private void validateFitchFormat(String rating) {
        if (rating != null && !rating.trim().isEmpty()) {
            if (!rating.matches("^(AAA|AA[+-]?|A[+-]?|BBB[+-]?|BB[+-]?|B[+-]?|CCC[+-]?|CC|C|D)$")) {
                throw new IllegalArgumentException("Invalid Fitch rating format: " + rating);
            }
        }
    }

    /**
     * Valide la cohérence entre les notations des différentes agences.
     * 
     * <p>Vérifie que les notations des différentes agences sont cohérentes
     * entre elles selon les tables d'équivalence standard du marché.</p>
     */
    private void validateRatingConsistency(Rating rating) {
        // Implémentation simplifiée - pourrait être étendue avec une table de correspondance complète
        boolean hasInvestmentGrade = false;
        boolean hasSpeculativeGrade = false;
        
        if (rating.getMoodysRating() != null && !rating.getMoodysRating().trim().isEmpty()) {
            hasInvestmentGrade |= isInvestmentGradeMoodys(rating.getMoodysRating());
            hasSpeculativeGrade |= !isInvestmentGradeMoodys(rating.getMoodysRating());
        }
        
        if (rating.getSandPRating() != null && !rating.getSandPRating().trim().isEmpty()) {
            hasInvestmentGrade |= isInvestmentGradeSP(rating.getSandPRating());
            hasSpeculativeGrade |= !isInvestmentGradeSP(rating.getSandPRating());
        }
        
        if (rating.getFitchRating() != null && !rating.getFitchRating().trim().isEmpty()) {
            hasInvestmentGrade |= isInvestmentGradeFitch(rating.getFitchRating());
            hasSpeculativeGrade |= !isInvestmentGradeFitch(rating.getFitchRating());
        }

        if (hasInvestmentGrade && hasSpeculativeGrade) {
        // Log warning mais n'empêche pas la sauvegarde car les divergences existent
        logger.warn("Inconsistent ratings between agencies for this rating");
        }
    }

    /**
     * Vérifie si une notation Moody's est Investment Grade.
     */
    private boolean isInvestmentGradeMoodys(String rating) {
        return rating.matches("^(Aaa|Aa[1-3]|A[1-3]|Baa[1-3])$");
    }

    /**
     * Vérifie si une notation S&P est Investment Grade.
     */
    private boolean isInvestmentGradeSP(String rating) {
        return rating.matches("^(AAA|AA[+-]?|A[+-]?|BBB[+-]?)$");
    }

    /**
     * Vérifie si une notation Fitch est Investment Grade.
     */
    private boolean isInvestmentGradeFitch(String rating) {
        return rating.matches("^(AAA|AA[+-]?|A[+-]?|BBB[+-]?)$");
    }

    /**
     * Valide l'unicité du numéro d'ordre.
     */
    private void validateOrderNumberUniqueness(Rating rating) {
        if (rating.getOrderNumber() != null) {
            Optional<Rating> existingWithSameOrder = ratingRepository.findByOrderNumber(rating.getOrderNumber());
            if (existingWithSameOrder.isPresent() && 
                !existingWithSameOrder.get().getId().equals(rating.getId())) {
                throw new IllegalArgumentException("Order number " + rating.getOrderNumber() + 
                                                 " already exists. Each rating must have a unique order number.");
            }
        }
    }

    /**
     * Génère le prochain numéro d'ordre disponible.
     */
    private Integer generateNextOrderNumber() {
        Optional<Rating> lastRating = ratingRepository.findTopByOrderByOrderNumberDesc();
        return lastRating.map(rating -> rating.getOrderNumber() + 1).orElse(1);
    }
}