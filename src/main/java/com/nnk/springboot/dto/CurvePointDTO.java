package com.nnk.springboot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * Data Transfer Object (DTO) pour l'entité CurvePoint.
 * 
 * <p>Cette classe DTO encapsule les données des points de courbe financière
 * pour la communication entre la couche de présentation et la couche métier.
 * Elle assure une validation rigoureuse des données saisies par l'utilisateur
 * pour les courbes utilisées dans l'analyse quantitative et le pricing.</p>
 * 
 * <p>Domaine d'application financier :</p>
 * <ul>
 *   <li><strong>Courbes de taux</strong> : Construction de courbes de rendement</li>
 *   <li><strong>Courbes de volatilité</strong> : Modélisation des surfaces de volatilité</li>
 *   <li><strong>Courbes de crédit</strong> : Spreads de crédit par échéance</li>
 *   <li><strong>Courbes forward</strong> : Taux forward par terme</li>
 * </ul>
 * 
 * <p>Caractéristiques de validation :</p>
 * <ul>
 *   <li><strong>Précision numérique</strong> : 4 décimales pour les calculs financiers</li>
 *   <li><strong>Cohérence métier</strong> : Termes positifs, IDs valides</li>
 *   <li><strong>Intégrité référentielle</strong> : Validation de l'ID de courbe</li>
 *   <li><strong>Messages explicites</strong> : Guidance claire pour l'utilisateur</li>
 * </ul>
 * 
 * <p>Exemple d'usage pour une courbe de taux :</p>
 * <pre>
 * // Point à 1 an avec taux 2.5%
 * CurvePointDTO ratePoint = new CurvePointDTO(1, 1.0, 2.5000);
 * 
 * // Point à 6 mois avec taux 2.25%
 * CurvePointDTO shortPoint = new CurvePointDTO(1, 0.5, 2.2500);
 * </pre>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.CurvePoint
 * @see com.nnk.springboot.mapper.CurvePointMapper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurvePointDTO {

    /** 
     * Identifiant unique du point de courbe.
     * 
     * <p>Utilisé pour l'identification lors des opérations de mise à jour
     * et de suppression. Reste null lors de la création d'un nouveau point.</p>
     */
    private Integer id;

    /** 
     * Identifiant de la courbe à laquelle appartient ce point.
     * 
     * <p>Référence obligatoire vers une courbe existante. Tous les points
     * partageant le même curveId constituent une courbe financière complète.
     * Doit être un entier positif valide.</p>
     * 
     * <p>Exemples d'usage :</p>
     * <ul>
     *   <li>Courbe ID 1 : Taux USD (tous termes)</li>
     *   <li>Courbe ID 2 : Taux EUR (tous termes)</li>
     *   <li>Courbe ID 3 : Volatilité implicite EUR/USD</li>
     * </ul>
     */
    @NotNull(message = "Curve ID is mandatory")
    @Min(value = 1, message = "Curve ID must be positive")
    private Integer curveId;

    /** 
     * Terme ou échéance de ce point de courbe en années.
     * 
     * <p>Représente la durée jusqu'à l'échéance pour ce point de la courbe.
     * Valeur obligatoire, positive ou nulle, avec une précision jusqu'à 4 décimales
     * pour permettre des calculs financiers précis.</p>
     * 
     * <p>Conventions et exemples :</p>
     * <ul>
     *   <li><strong>0.0833</strong> : 1 mois (1/12 année)</li>
     *   <li><strong>0.2500</strong> : 3 mois (trimestre)</li>
     *   <li><strong>0.5000</strong> : 6 mois (semestre)</li>
     *   <li><strong>1.0000</strong> : 1 an</li>
     *   <li><strong>2.0000</strong> : 2 ans</li>
     *   <li><strong>10.0000</strong> : 10 ans</li>
     *   <li><strong>30.0000</strong> : 30 ans</li>
     * </ul>
     * 
     * <p>La précision de 4 décimales permet de représenter des échéances
     * au jour près : 1 jour = 1/365 ≈ 0.0027 années.</p>
     */
    @NotNull(message = "Term is mandatory")
    @DecimalMin(value = "0.0", inclusive = true, message = "Term must be positive or zero")
    @Digits(integer = 10, fraction = 4, message = "Term must be a valid number with max 4 decimal places")
    private Double term;

    /** 
     * Valeur financière à ce terme spécifique.
     * 
     * <p>La valeur représente la métrique financière pour ce terme.
     * Le type de valeur dépend de la nature de la courbe :
     * précision de 4 décimales pour les calculs quantitatifs.</p>
     * 
     * <p>Types de valeurs selon le type de courbe :</p>
     * <ul>
     *   <li><strong>Courbes de taux</strong> : Taux d'intérêt en % (ex: 2.5000% pour 2,5%)</li>
     *   <li><strong>Courbes de rendement</strong> : Rendement en % (ex: 3.2500%)</li>
     *   <li><strong>Courbes de volatilité</strong> : Volatilité implicite en % (ex: 18.7500%)</li>
     *   <li><strong>Courbes de crédit</strong> : Spread en points de base (ex: 125.0000 bp)</li>
     *   <li><strong>Courbes de change</strong> : Taux de change (ex: 1.0850 EUR/USD)</li>
     *   <li><strong>Courbes forward</strong> : Taux forward en % (ex: 2.7500%)</li>
     * </ul>
     * 
     * <p>La validation permet des valeurs négatives car certaines courbes
     * peuvent avoir des valeurs négatives (taux négatifs, spreads négatifs).</p>
     */
    @NotNull(message = "Value is mandatory")
    @Digits(integer = 10, fraction = 4, message = "Value must be a valid number with max 4 decimal places")
    private Double value;

    /**
     * Constructeur de convenance pour la création rapide de points de courbe.
     * 
     * <p>Ce constructeur permet de créer efficacement un point de courbe
     * avec les trois attributs essentiels. Particulièrement utile pour :</p>
     * <ul>
     *   <li><strong>Tests unitaires</strong> : Création rapide de jeux de données</li>
     *   <li><strong>Import de données</strong> : Construction de courbes depuis fichiers</li>
     *   <li><strong>Calculs quantitatifs</strong> : Génération programmatique de points</li>
     *   <li><strong>Prototypage</strong> : Création rapide pour démonstrations</li>
     * </ul>
     * 
     * <p>Exemples d'usage pratique :</p>
     * <pre>
     * // Construction d'une courbe de taux USD
     * List&lt;CurvePointDTO&gt; usdRateCurve = Arrays.asList(
     *     new CurvePointDTO(1, 0.0833, 1.7500),  // 1M : 1.75%
     *     new CurvePointDTO(1, 0.2500, 2.0000),  // 3M : 2.00%
     *     new CurvePointDTO(1, 0.5000, 2.2500),  // 6M : 2.25%
     *     new CurvePointDTO(1, 1.0000, 2.5000),  // 1Y : 2.50%
     *     new CurvePointDTO(1, 2.0000, 2.7500),  // 2Y : 2.75%
     *     new CurvePointDTO(1, 5.0000, 3.0000),  // 5Y : 3.00%
     *     new CurvePointDTO(1, 10.0000, 3.2500)  // 10Y: 3.25%
     * );
     * 
     * // Point de volatilité implicite
     * CurvePointDTO volPoint = new CurvePointDTO(2, 0.25, 18.5000); // 3M vol: 18.5%
     * </pre>
     * 
     * @param curveId L'identifiant de la courbe à laquelle appartient ce point
     * @param term Le terme ou échéance en années (précision 4 décimales)
     * @param value La valeur financière à ce terme (taux, volatilité, spread, etc.)
     */
    public CurvePointDTO(Integer curveId, Double term, Double value) {
        this.curveId = curveId;
        this.term = term;
        this.value = value;
    }
}
