package com.nnk.springboot.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entité JPA représentant une notation de crédit dans le système de trading Poseidon.
 * 
 * <p>Cette entité modélise les notations de crédit attribuées par les principales
 * agences de notation financière (Moody's, Standard & Poor's, Fitch). Elle centralise
 * les informations de risque crédit essentielles pour l'évaluation des instruments
 * financiers et la gestion des risques dans l'environnement de trading.</p>
 * 
 * <p>Agences de notation supportées :</p>
 * <ul>
 *   <li><strong>Moody's</strong> : Échelle Aaa à C (investissement à défaut)</li>
 *   <li><strong>Standard & Poor's</strong> : Échelle AAA à D (investissement à défaut)</li>
 *   <li><strong>Fitch</strong> : Échelle AAA à D (similaire à S&P)</li>
 * </ul>
 * 
 * <p>Caractéristiques techniques :</p>
 * <ul>
 *   <li>Entité JPA mappée sur la table "Rating"</li>
 *   <li>Identifiant auto-généré avec stratégie IDENTITY</li>
 *   <li>Utilisation de Lombok pour la génération automatique des getters/setters</li>
 *   <li>Numéro d'ordre pour classification et tri des notations</li>
 * </ul>
 * 
 * <p>Usage dans l'environnement financier :</p>
 * <ul>
 *   <li>Évaluation du risque de crédit des contreparties</li>
 *   <li>Calcul des exigences en capital réglementaire</li>
 *   <li>Stratégies de couverture et allocation d'actifs</li>
 *   <li>Reporting réglementaire et conformité</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rating")
public class Rating {

    /** 
     * Identifiant unique de la notation de crédit.
     * Clé primaire auto-générée avec stratégie IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    /** 
     * Notation de crédit selon l'échelle Moody's.
     * 
     * <p>Échelle Moody's (du meilleur au plus risqué) :</p>
     * <ul>
     *   <li><strong>Aaa</strong> : Qualité exceptionnelle, risque minimal</li>
     *   <li><strong>Aa1, Aa2, Aa3</strong> : Très haute qualité</li>
     *   <li><strong>A1, A2, A3</strong> : Qualité supérieure</li>
     *   <li><strong>Baa1, Baa2, Baa3</strong> : Qualité moyenne (investment grade)</li>
     *   <li><strong>Ba1, Ba2, Ba3</strong> : Éléments spéculatifs</li>
     *   <li><strong>B1, B2, B3</strong> : Spéculatif</li>
     *   <li><strong>Caa, Ca, C</strong> : Très spéculatif à défaut probable</li>
     * </ul>
     */
    @Column(name = "moodys_rating", length = 125)
    private String moodysRating;

    /** 
     * Notation de crédit selon l'échelle Standard & Poor's.
     * 
     * <p>Échelle S&P (du meilleur au plus risqué) :</p>
     * <ul>
     *   <li><strong>AAA</strong> : Capacité de paiement extrêmement forte</li>
     *   <li><strong>AA+, AA, AA-</strong> : Très forte capacité de paiement</li>
     *   <li><strong>A+, A, A-</strong> : Forte capacité de paiement</li>
     *   <li><strong>BBB+, BBB, BBB-</strong> : Capacité adéquate (investment grade)</li>
     *   <li><strong>BB+, BB, BB-</strong> : Moins vulnérable à court terme</li>
     *   <li><strong>B+, B, B-</strong> : Plus vulnérable</li>
     *   <li><strong>CCC, CC, C</strong> : Actuellement vulnérable</li>
     *   <li><strong>D</strong> : En défaut de paiement</li>
     * </ul>
     */
    @Column(name = "sand_p_rating", length = 125)
    private String sandPRating;

    /** 
     * Notation de crédit selon l'échelle Fitch.
     * 
     * <p>Échelle Fitch (similaire à S&P, du meilleur au plus risqué) :</p>
     * <ul>
     *   <li><strong>AAA</strong> : Qualité de crédit la plus élevée</li>
     *   <li><strong>AA+, AA, AA-</strong> : Très haute qualité de crédit</li>
     *   <li><strong>A+, A, A-</strong> : Haute qualité de crédit</li>
     *   <li><strong>BBB+, BBB, BBB-</strong> : Bonne qualité de crédit</li>
     *   <li><strong>BB+, BB, BB-</strong> : Qualité de crédit spéculative</li>
     *   <li><strong>B+, B, B-</strong> : Hautement spéculatif</li>
     *   <li><strong>CCC, CC, C</strong> : Risque de crédit substantiel</li>
     *   <li><strong>D</strong> : Défaut de paiement</li>
     * </ul>
     */
    @Column(name = "fitch_rating", length = 125)
    private String fitchRating;

    /** 
     * Numéro d'ordre pour la classification des notations.
     * 
     * <p>Permet d'établir un ordre numérique pour les notations afin de faciliter
     * les comparaisons, tris et calculs. Plus le numéro est faible, meilleure est
     * la notation. Utilisé pour les algorithmes de scoring et l'analyse quantitative.</p>
     * 
     * <p>Exemple d'ordonnancement :</p>
     * <ul>
     *   <li><strong>1</strong> : AAA/Aaa (meilleure notation)</li>
     *   <li><strong>2</strong> : AA+/Aa1</li>
     *   <li><strong>...</strong></li>
     *   <li><strong>21</strong> : D/C (défaut)</li>
     * </ul>
     */
    @Column(name = "order_number")
    private Integer orderNumber;

    /**
     * Constructeur de convenance pour les tests et la création rapide d'instances.
     * 
     * <p>Ce constructeur permet de créer un Rating avec les champs essentiels
     * pour les tests unitaires ou la création rapide d'instances. Il initialise
     * les trois notations principales des agences de référence.</p>
     * 
     * <p>Usage typique pour les tests :</p>
     * <pre>
     * // Notation investment grade
     * Rating investmentGrade = new Rating("A1", "A+", "A+");
     * 
     * // Notation spéculative
     * Rating speculative = new Rating("Ba2", "BB", "BB");
     * </pre>
     * 
     * @param moodysRating La notation Moody's
     * @param sandPRating La notation Standard & Poor's
     * @param fitchRating La notation Fitch
     */
    public Rating(String moodysRating, String sandPRating, String fitchRating) {
        this.moodysRating = moodysRating;
        this.sandPRating = sandPRating;
        this.fitchRating = fitchRating;
    }

    /**
     * Constructeur complet incluant le numéro d'ordre.
     * 
     * <p>Ce constructeur permet de créer un Rating complet avec toutes les
     * informations de notation et le numéro d'ordre pour la classification.</p>
     * 
     * @param moodysRating La notation Moody's
     * @param sandPRating La notation Standard & Poor's
     * @param fitchRating La notation Fitch
     * @param orderNumber Le numéro d'ordre pour la classification
     */
    public Rating(String moodysRating, String sandPRating, String fitchRating, Integer orderNumber) {
        this.moodysRating = moodysRating;
        this.sandPRating = sandPRating;
        this.fitchRating = fitchRating;
        this.orderNumber = orderNumber;
    }
}