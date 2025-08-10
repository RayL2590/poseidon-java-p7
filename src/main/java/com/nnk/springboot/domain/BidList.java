package com.nnk.springboot.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité JPA représentant une liste d'offres (BidList) dans le système de trading Poseidon.
 * 
 * <p>Cette entité modélise les offres d'achat et de vente (bid/ask) dans le contexte
 * du trading financier. Elle contient toutes les informations nécessaires pour
 * gérer les transactions financières, incluant les quantités, les prix, les
 * métadonnées de suivi et les informations de trading.</p>
 * 
 * <p>Caractéristiques techniques :</p>
 * <ul>
 *   <li>Entité JPA mappée sur la table "BidList"</li>
 *   <li>Identifiant auto-généré avec stratégie IDENTITY</li>
 *   <li>Utilisation de Lombok pour la génération automatique des getters/setters</li>
 *   <li>Support des timestamps pour l'audit (création, révision)</li>
 * </ul>
 * 
 * <p>Domaine métier :</p>
 * <ul>
 *   <li>Gestion des offres d'achat (bid) et de vente (ask)</li>
 *   <li>Suivi des quantités et des prix</li>
 *   <li>Traçabilité des modifications avec horodatage</li>
 *   <li>Association avec les comptes et types de trading</li>
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
@Table(name = "bid_list")
public class BidList {

    /** 
     * Identifiant unique de la liste d'offres.
     * Clé primaire auto-générée avec stratégie IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BidListId")
    private Integer bidListId;

    /** 
     * Compte associé à cette offre.
     * Champ obligatoire identifiant le compte de trading.
     */
    @Column(name = "account", nullable = false, length = 30)
    private String account;

    /** 
     * Type de l'offre.
     * Champ obligatoire spécifiant la catégorie ou le type de transaction.
     */
    @Column(name = "type", nullable = false, length = 30)
    private String type;

    /** 
     * Quantité demandée pour l'offre d'achat (bid).
     * Représente le volume souhaité pour l'achat.
     */
    @Column(name = "bidQuantity")
    private Double bidQuantity;

    /** 
     * Quantité proposée pour l'offre de vente (ask).
     * Représente le volume disponible pour la vente.
     */
    @Column(name = "askQuantity")
    private Double askQuantity;

    /** 
     * Prix de l'offre d'achat (bid).
     * Prix maximum que l'acheteur est prêt à payer.
     */
    @Column(name = "bid")
    private Double bid;

    /** 
     * Prix de l'offre de vente (ask).
     * Prix minimum auquel le vendeur accepte de vendre.
     */
    @Column(name = "ask")
    private Double ask;

    /** 
     * Référence de benchmark pour cette offre.
     * Indicateur de référence utilisé pour évaluer la performance.
     */
    @Column(name = "benchmark", length = 125)
    private String benchmark;

    /** 
     * Date et heure de création de la liste d'offres.
     * Timestamp automatique lors de la création de l'offre.
     */
    @Column(name = "bidListDate")
    private LocalDateTime bidListDate;

    /** 
     * Commentaires additionnels sur cette offre.
     * Champ libre pour des notes ou observations.
     */
    @Column(name = "commentary", length = 125)
    private String commentary;

    /** 
     * Identifiant du titre ou de la sécurité financière.
     * Référence vers l'instrument financier concerné.
     */
    @Column(name = "security", length = 125)
    private String security;

    /** 
     * Statut actuel de l'offre.
     * État de traitement de l'offre (ex: active, closed, pending).
     */
    @Column(name = "status", length = 10)
    private String status;

    /** 
     * Identifiant du trader responsable de cette offre.
     * Référence vers la personne ayant créé ou géré l'offre.
     */
    @Column(name = "trader", length = 125)
    private String trader;

    /** 
     * Référence du livre de trading.
     * Identifiant du portefeuille ou du livre de positions.
     */
    @Column(name = "book", length = 125)
    private String book;

    /** 
     * Nom de la personne ayant créé l'enregistrement.
     * Champ d'audit pour tracer la création.
     */
    @Column(name = "creationName", length = 125)
    private String creationName;

    /** 
     * Date et heure de création de l'enregistrement.
     * Timestamp d'audit pour la création.
     */
    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    /** 
     * Nom de la personne ayant effectué la dernière révision.
     * Champ d'audit pour tracer les modifications.
     */
    @Column(name = "revisionName", length = 125)
    private String revisionName;

    /** 
     * Date et heure de la dernière révision.
     * Timestamp d'audit pour les modifications.
     */
    @Column(name = "revisionDate")
    private LocalDateTime revisionDate;

    /** 
     * Nom de l'accord ou du deal associé.
     * Référence vers une transaction ou un accord spécifique.
     */
    @Column(name = "dealName", length = 125)
    private String dealName;

    /** 
     * Type de deal ou d'accord.
     * Catégorisation du type de transaction.
     */
    @Column(name = "dealType", length = 125)
    private String dealType;

    /** 
     * Identifiant de la liste source.
     * Référence vers une liste d'origine si applicable.
     */
    @Column(name = "sourceListId", length = 125)
    private String sourceListId;

    /** 
     * Côté de la transaction (achat/vente).
     * Indique si c'est une position d'achat ou de vente.
     */
    @Column(name = "side", length = 125)
    private String side;

    /**
     * Méthode utilitaire pour récupérer l'identifiant de l'entité.
     * 
     * <p>Cette méthode fournit un accès uniforme à l'identifiant de l'entité,
     * facilitant l'utilisation dans les interfaces génériques ou les frameworks
     * qui attendent une méthode getId() standard.</p>
     * 
     * @return L'identifiant unique de la BidList (bidListId)
     */
    public Integer getId() {
        return bidListId;
    }
    
    /**
     * Constructeur de convenance pour les tests et la création rapide d'instances.
     * 
     * <p>Ce constructeur permet de créer une BidList avec les champs essentiels
     * pour les tests unitaires ou la création rapide d'instances avec les
     * données minimales requises.</p>
     * 
     * @param account Le compte associé à l'offre
     * @param type Le type de l'offre
     * @param bidQuantity La quantité demandée pour l'offre d'achat
     */
    public BidList(String account, String type, Double bidQuantity) {
        this.account = account;
        this.type = type;
        this.bidQuantity = bidQuantity;
    }
}