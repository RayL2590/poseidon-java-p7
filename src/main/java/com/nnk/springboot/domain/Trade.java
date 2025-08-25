package com.nnk.springboot.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité JPA représentant une transaction (Trade) dans le système de trading Poseidon.
 * 
 * <p>Cette entité modélise les transactions financières réalisées dans le contexte
 * du trading. Elle contient toutes les informations nécessaires pour gérer les
 * transactions d'achat et de vente, incluant les quantités, les prix, les métadonnées
 * de suivi et les informations de trading.</p>
 * 
 * <p>Caractéristiques techniques :</p>
 * <ul>
 *   <li>Entité JPA mappée sur la table "trade"</li>
 *   <li>Identifiant auto-généré avec stratégie IDENTITY</li>
 *   <li>Utilisation de Lombok pour la génération automatique des getters/setters</li>
 *   <li>Support des timestamps pour l'audit (création, révision)</li>
 * </ul>
 * 
 * <p>Domaine métier :</p>
 * <ul>
 *   <li>Gestion des transactions d'achat et de vente</li>
 *   <li>Suivi des quantités et des prix d'exécution</li>
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
@Table(name = "trade")
public class Trade {

    /** 
     * Identifiant unique de la transaction.
     * Clé primaire auto-générée avec stratégie IDENTITY.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TradeId")
    private Integer tradeId;

    /** 
     * Compte associé à cette transaction.
     * Champ obligatoire identifiant le compte de trading.
     */
    @Column(name = "account", nullable = false, length = 30)
    private String account;

    /** 
     * Type de la transaction.
     * Champ obligatoire spécifiant la catégorie ou le type de transaction.
     */
    @Column(name = "type", nullable = false, length = 30)
    private String type;

    /** 
     * Quantité d'achat pour cette transaction.
     * Représente le volume acheté.
     */
    @Column(name = "buyQuantity")
    private Double buyQuantity;

    /** 
     * Quantité de vente pour cette transaction.
     * Représente le volume vendu.
     */
    @Column(name = "sellQuantity")
    private Double sellQuantity;

    /** 
     * Prix d'achat pour cette transaction.
     * Prix unitaire d'exécution pour les achats.
     */
    @Column(name = "buyPrice")
    private Double buyPrice;

    /** 
     * Prix de vente pour cette transaction.
     * Prix unitaire d'exécution pour les ventes.
     */
    @Column(name = "sellPrice")
    private Double sellPrice;

    /** 
     * Date et heure de la transaction.
     * Timestamp d'exécution de la transaction.
     */
    @Column(name = "tradeDate")
    private LocalDateTime tradeDate;

    /** 
     * Titre ou sécurité financière concernée.
     * Identifiant du produit financier traité.
     */
    @Column(name = "security", length = 125)
    private String security;

    /** 
     * Statut de la transaction.
     * État actuel de la transaction (ex: PENDING, EXECUTED, CANCELLED).
     */
    @Column(name = "status", length = 10)
    private String status;

    /** 
     * Identifiant du trader responsable.
     * Nom ou ID du trader ayant effectué la transaction.
     */
    @Column(name = "trader", length = 125)
    private String trader;

    /** 
     * Référence de benchmark utilisée.
     * Indice ou référence utilisée pour cette transaction.
     */
    @Column(name = "benchmark", length = 125)
    private String benchmark;

    /** 
     * Livre de trading associé.
     * Portefeuille ou livre dans lequel la transaction est enregistrée.
     */
    @Column(name = "book", length = 125)
    private String book;

    /** 
     * Nom de l'utilisateur ayant créé l'enregistrement.
     * Utilisé pour l'audit et la traçabilité.
     */
    @Column(name = "creationName", length = 125)
    private String creationName;

    /** 
     * Date et heure de création de l'enregistrement.
     * Timestamp de création automatique.
     */
    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    /** 
     * Nom de l'utilisateur ayant effectué la dernière révision.
     * Utilisé pour l'audit des modifications.
     */
    @Column(name = "revisionName", length = 125)
    private String revisionName;

    /** 
     * Date et heure de la dernière révision.
     * Timestamp de modification automatique.
     */
    @Column(name = "revisionDate")
    private LocalDateTime revisionDate;

    /** 
     * Nom de l'opération ou du deal.
     * Identifiant métier de l'opération.
     */
    @Column(name = "dealName", length = 125)
    private String dealName;

    /** 
     * Type de deal ou d'opération.
     * Classification métier de l'opération.
     */
    @Column(name = "dealType", length = 125)
    private String dealType;

    /** 
     * Identifiant de la source ou liste source.
     * Référence vers la source de données origine.
     */
    @Column(name = "sourceListId", length = 125)
    private String sourceListId;

    /** 
     * Côté de la transaction (Buy/Sell).
     * Indique le sens de la transaction.
     */
    @Column(name = "side", length = 125)
    private String side;

    /**
     * Constructeur de convenance pour les tests et la création rapide d'instances.
     * 
     * @param account Le compte associé
     * @param type Le type de transaction
     */
    public Trade(String account, String type) {
        this.account = account;
        this.type = type;
    }

    /**
     * Constructeur avec les champs essentiels pour une transaction.
     * 
     * @param account Le compte associé
     * @param type Le type de transaction
     * @param buyQuantity La quantité d'achat
     * @param sellQuantity La quantité de vente
     */
    public Trade(String account, String type, Double buyQuantity, Double sellQuantity) {
        this.account = account;
        this.type = type;
        this.buyQuantity = buyQuantity;
        this.sellQuantity = sellQuantity;
    }
}
