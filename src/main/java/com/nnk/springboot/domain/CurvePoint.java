package com.nnk.springboot.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité JPA représentant un point de courbe financière dans le système de trading Poseidon.
 * 
 * <p>Cette entité modélise les points individuels qui constituent les courbes financières
 * utilisées dans le trading et l'analyse quantitative. Chaque point de courbe représente
 * une valeur à un terme donné (échéance) pour une courbe spécifique, comme les courbes
 * de taux d'intérêt, les courbes de rendement ou les courbes de volatilité.</p>
 * 
 * <p>Concepts financiers :</p>
 * <ul>
 *   <li><strong>Courbe financière</strong> : Représentation graphique de la relation entre
 *       le temps (terme) et une valeur financière (taux, prix, volatilité)</li>
 *   <li><strong>Terme</strong> : Échéance ou durée jusqu'à maturité (ex: 1 an, 5 ans)</li>
 *   <li><strong>Valeur</strong> : Taux, prix ou autre métrique financière au terme donné</li>
 * </ul>
 * 
 * <p>Caractéristiques techniques :</p>
 * <ul>
 *   <li>Entité JPA mappée sur la table "CurvePoint"</li>
 *   <li>Identifiant auto-généré avec stratégie IDENTITY</li>
 *   <li>Utilisation de Lombok pour la génération automatique des getters/setters</li>
 *   <li>Support des timestamps pour l'audit et la datation des données</li>
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
@Table(name = "CurvePoint")
public class CurvePoint {

    /** 
     * Identifiant unique du point de courbe.
     * Clé primaire auto-générée avec stratégie IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    /** 
     * Identifiant de la courbe à laquelle appartient ce point.
     * 
     * <p>Permet de regrouper plusieurs points appartenant à la même courbe financière.
     * Par exemple, tous les points d'une courbe de taux USD auront le même curveId.</p>
     */
    @Column(name = "CurveId")
    private Integer curveId;

    /** 
     * Date de référence (as of date) pour ce point de courbe.
     * 
     * <p>Représente la date à laquelle cette valeur de courbe était valide.
     * Essentiel pour l'historisation et la valorisation à une date donnée.
     * En finance, les courbes évoluent quotidiennement selon les conditions de marché.</p>
     */
    @Column(name = "asOfDate")
    private LocalDateTime asOfDate;

    /** 
     * Terme ou échéance de ce point de courbe.
     * 
     * <p>Représente la durée ou l'échéance en années (généralement) pour ce point.
     * Exemples : 0.25 (3 mois), 1.0 (1 an), 5.0 (5 ans), 10.0 (10 ans).
     * Plus le terme est long, plus l'échéance est éloignée.</p>
     */
    @Column(name = "term")
    private Double term;

    /** 
     * Valeur financière à ce terme spécifique.
     * 
     * <p>La valeur dépend du type de courbe :</p>
     * <ul>
     *   <li>Courbe de taux : taux d'intérêt en pourcentage (ex: 2.5 pour 2.5%)</li>
     *   <li>Courbe de rendement : rendement en pourcentage</li>
     *   <li>Courbe de volatilité : volatilité implicite en pourcentage</li>
     *   <li>Courbe de crédit : spread de crédit en points de base</li>
     * </ul>
     */
    @Column(name = "value")
    private Double value;

    /** 
     * Date et heure de création de cet enregistrement.
     * 
     * <p>Timestamp d'audit indiquant quand ce point de courbe a été
     * enregistré dans le système. Différent de asOfDate qui indique
     * la date de validité financière des données.</p>
     */
    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    /**
     * Constructeur de convenance pour les tests et la création rapide d'instances.
     * 
     * <p>Ce constructeur permet de créer un CurvePoint avec les champs essentiels
     * pour les tests unitaires ou la création rapide d'instances. Il initialise
     * les trois attributs fondamentaux qui définissent un point de courbe :
     * l'identifiant de courbe, le terme et la valeur.</p>
     * 
     * <p>Les autres champs (id, asOfDate, creationDate) peuvent être définis
     * séparément selon les besoins du contexte d'utilisation.</p>
     * 
     * @param curveId L'identifiant de la courbe à laquelle appartient ce point
     * @param term Le terme ou échéance de ce point (en années généralement)
     * @param value La valeur financière à ce terme spécifique
     */
    public CurvePoint(Integer curveId, Double term, Double value) {
        this.curveId = curveId;
        this.term = term;
        this.value = value;
    }
}
