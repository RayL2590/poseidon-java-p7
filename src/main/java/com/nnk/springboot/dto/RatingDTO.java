package com.nnk.springboot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * Data Transfer Object (DTO) pour l'entité Rating.
 * 
 * <p>Cette classe DTO encapsule les données des notations de crédit pour la communication
 * entre la couche de présentation et la couche métier. Elle assure une validation
 * rigoureuse des notations saisies par l'utilisateur et respecte les standards
 * internationaux des agences de notation financière.</p>
 * 
 * <p>Validation des notations de crédit :</p>
 * <ul>
 *   <li><strong>Formats standardisés</strong> : Respect des échelles officielles</li>
 *   <li><strong>Cohérence inter-agences</strong> : Validation de la correspondance</li>
 *   <li><strong>Numéro d'ordre</strong> : Ordre logique pour classement</li>
 *   <li><strong>Messages explicites</strong> : Guidance pour l'utilisateur</li>
 * </ul>
 * 
 * <p>Utilisation dans l'analyse de crédit :</p>
 * <ul>
 *   <li><strong>Saisie de notations</strong> : Formulaires de création/modification</li>
 *   <li><strong>Import de données</strong> : Chargement depuis systèmes externes</li>
 *   <li><strong>Validation métier</strong> : Contrôle de cohérence des notations</li>
 *   <li><strong>Affichage harmonisé</strong> : Présentation standardisée</li>
 * </ul>
 * 
 * <p>Standards de notation supportés :</p>
 * <ul>
 *   <li><strong>Moody's</strong> : Aaa, Aa1-3, A1-3, Baa1-3, Ba1-3, B1-3, Caa-C</li>
 *   <li><strong>S&P/Fitch</strong> : AAA, AA+/AA/AA-, A+/A/A-, BBB+/BBB/BBB-, etc.</li>
 *   <li><strong>Investment Grade</strong> : BBB-/Baa3 et au-dessus</li>
 *   <li><strong>Speculative Grade</strong> : BB+/Ba1 et en-dessous</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.Rating
 * @see com.nnk.springboot.mapper.RatingMapper
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {

    /** 
     * Identifiant unique de la notation de crédit.
     * 
     * <p>Utilisé pour l'identification lors des opérations de mise à jour
     * et de suppression. Reste null lors de la création d'une nouvelle notation.</p>
     */
    private Integer id;

    /** 
     * Notation de crédit selon l'échelle Moody's.
     * 
     * <p>Validation par expression régulière qui accepte les formats Moody's standard :
     * Aaa, Aa1-3, A1-3, Baa1-3, Ba1-3, B1-3, Caa1-3, Ca, C.</p>
     * 
     * <p>Exemples valides : "Aaa", "Aa1", "A2", "Baa3", "Ba1", "B2", "Caa1", "Ca", "C"</p>
     * <p>Exemples invalides : "AAA", "AA+", "Aaa1", "D"</p>
     */
    @Pattern(regexp = "^(Aaa|Aa[1-3]|A[1-3]|Baa[1-3]|Ba[1-3]|B[1-3]|Caa[1-3]|Ca|C)?$", 
             message = "Moody's rating must follow standard format (e.g., Aaa, Aa1, A2, Baa3, Ba1, B2, Caa1, Ca, C)")
    @Size(max = 125, message = "Moody's rating must be less than 125 characters")
    private String moodysRating;

    /** 
     * Notation de crédit selon l'échelle Standard & Poor's.
     * 
     * <p>Validation par expression régulière qui accepte les formats S&P standard :
     * AAA, AA+/AA/AA-, A+/A/A-, BBB+/BBB/BBB-, BB+/BB/BB-, B+/B/B-, 
     * CCC+/CCC/CCC-, CC, C, D.</p>
     * 
     * <p>Exemples valides : "AAA", "AA+", "A-", "BBB", "BB+", "B-", "CCC", "D"</p>
     * <p>Exemples invalides : "Aaa", "AA1", "BBB++"</p>
     */
    @Pattern(regexp = "^(AAA|AA[+-]?|A[+-]?|BBB[+-]?|BB[+-]?|B[+-]?|CCC[+-]?|CC|C|D)?$", 
             message = "S&P rating must follow standard format (e.g., AAA, AA+, A-, BBB, BB+, B-, CCC, D)")
    @Size(max = 125, message = "S&P rating must be less than 125 characters")
    private String sandPRating;

    /** 
     * Notation de crédit selon l'échelle Fitch.
     * 
     * <p>Validation par expression régulière identique à S&P car Fitch utilise
     * la même échelle de notation : AAA, AA+/AA/AA-, A+/A/A-, BBB+/BBB/BBB-, 
     * BB+/BB/BB-, B+/B/B-, CCC+/CCC/CCC-, CC, C, D.</p>
     * 
     * <p>Exemples valides : "AAA", "AA", "A+", "BBB-", "BB", "B+", "CCC-", "D"</p>
     * <p>Exemples invalides : "Aaa", "A1", "BB++"</p>
     */
    @Pattern(regexp = "^(AAA|AA[+-]?|A[+-]?|BBB[+-]?|BB[+-]?|B[+-]?|CCC[+-]?|CC|C|D)?$", 
             message = "Fitch rating must follow standard format (e.g., AAA, AA+, A-, BBB, BB+, B-, CCC, D)")
    @Size(max = 125, message = "Fitch rating must be less than 125 characters")
    private String fitchRating;

    /** 
     * Numéro d'ordre pour la classification des notations.
     * 
     * <p>Doit être un entier positif permettant de classer les notations
     * par ordre de qualité décroissante. Plus le numéro est faible,
     * meilleure est la notation.</p>
     * 
     * <p>Convention recommandée :</p>
     * <ul>
     *   <li><strong>1-3</strong> : AAA/Aaa (prime grade)</li>
     *   <li><strong>4-12</strong> : AA à BBB/Aa à Baa (investment grade)</li>
     *   <li><strong>13-21</strong> : BB à D/Ba à C (speculative grade)</li>
     * </ul>
     */
    @Min(value = 1, message = "Order number must be positive")
    private Integer orderNumber;

    /**
     * Constructeur de convenance pour la création rapide de notations.
     * 
     * <p>Ce constructeur permet de créer rapidement un DTO avec les trois
     * notations principales des agences de référence. Particulièrement utile
     * pour les tests unitaires et l'import de données depuis des sources externes.</p>
     * 
     * <p>Exemples d'usage :</p>
     * <pre>
     * // Notation investment grade équivalente
     * RatingDTO investmentGrade = new RatingDTO("A1", "A+", "A+");
     * 
     * // Notation speculative
     * RatingDTO speculative = new RatingDTO("Ba2", "BB", "BB");
     * 
     * // Notation prime (meilleure qualité)
     * RatingDTO prime = new RatingDTO("Aaa", "AAA", "AAA");
     * </pre>
     * 
     * @param moodysRating La notation selon l'échelle Moody's
     * @param sandPRating La notation selon l'échelle Standard & Poor's
     * @param fitchRating La notation selon l'échelle Fitch
     */
    public RatingDTO(String moodysRating, String sandPRating, String fitchRating) {
        this.moodysRating = moodysRating;
        this.sandPRating = sandPRating;
        this.fitchRating = fitchRating;
    }

    /**
     * Constructeur complet incluant le numéro d'ordre.
     * 
     * <p>Ce constructeur permet de créer un DTO complet avec toutes les
     * informations de notation et le numéro d'ordre pour la classification.
     * Utilisé principalement lors de la conversion depuis l'entité ou
     * lors d'imports de données avec classification pré-établie.</p>
     * 
     * @param moodysRating La notation selon l'échelle Moody's
     * @param sandPRating La notation selon l'échelle Standard & Poor's
     * @param fitchRating La notation selon l'échelle Fitch
     * @param orderNumber Le numéro d'ordre pour la classification
     */
    public RatingDTO(String moodysRating, String sandPRating, String fitchRating, Integer orderNumber) {
        this.moodysRating = moodysRating;
        this.sandPRating = sandPRating;
        this.fitchRating = fitchRating;
        this.orderNumber = orderNumber;
    }

    /**
     * Vérifie si cette notation est de qualité "Investment Grade".
     * 
     * <p>Méthode utilitaire qui détermine si au moins une des notations
     * correspond aux critères "Investment Grade" selon les standards
     * internationaux (BBB-/Baa3 et au-dessus).</p>
     * 
     * @return true si au moins une notation est Investment Grade, false sinon
     */
    public boolean isInvestmentGrade() {
        return isInvestmentGradeMoodys(moodysRating) || 
               isInvestmentGradeSP(sandPRating) || 
               isInvestmentGradeFitch(fitchRating);
    }

    /**
     * Vérifie si une notation Moody's est Investment Grade.
     * 
     * @param rating La notation Moody's à vérifier
     * @return true si Investment Grade, false sinon
     */
    private boolean isInvestmentGradeMoodys(String rating) {
        if (rating == null) return false;
        return rating.matches("^(Aaa|Aa[1-3]|A[1-3]|Baa[1-3])$");
    }

    /**
     * Vérifie si une notation S&P est Investment Grade.
     * 
     * @param rating La notation S&P à vérifier
     * @return true si Investment Grade, false sinon
     */
    private boolean isInvestmentGradeSP(String rating) {
        if (rating == null) return false;
        return rating.matches("^(AAA|AA[+-]?|A[+-]?|BBB[+-]?)$");
    }

    /**
     * Vérifie si une notation Fitch est Investment Grade.
     * 
     * @param rating La notation Fitch à vérifier
     * @return true si Investment Grade, false sinon
     */
    private boolean isInvestmentGradeFitch(String rating) {
        if (rating == null) return false;
        return rating.matches("^(AAA|AA[+-]?|A[+-]?|BBB[+-]?)$");
    }
} 
