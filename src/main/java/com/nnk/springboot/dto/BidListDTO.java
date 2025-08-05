package com.nnk.springboot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * Data Transfer Object (DTO) pour l'entité BidList.
 * 
 * <p>Cette classe DTO sert d'interface entre la couche de présentation (contrôleurs/vues)
 * et la couche métier. Elle encapsule les données des listes d'offres de trading
 * avec une validation complète des données d'entrée utilisateur.</p>
 * 
 * <p>Caractéristiques du pattern DTO :</p>
 * <ul>
 *   <li><strong>Séparation des responsabilités</strong> : Isole la logique de présentation</li>
 *   <li><strong>Validation robuste</strong> : Bean Validation avec messages personnalisés</li>
 *   <li><strong>Sécurité</strong> : Contrôle strict des données exposées aux vues</li>
 *   <li><strong>Flexibilité</strong> : Structure adaptée aux besoins de l'interface utilisateur</li>
 * </ul>
 * 
 * <p>Validation des données financières :</p>
 * <ul>
 *   <li>Champs obligatoires : account, type (essentiels pour le trading)</li>
 *   <li>Valeurs numériques : Positives ou nulles, format décimal contrôlé</li>
 *   <li>Longueurs de chaînes : Limitées selon les contraintes métier</li>
 *   <li>Messages d'erreur : Explicites pour guider l'utilisateur</li>
 * </ul>
 * 
 * <p>Usage typique :</p>
 * <pre>
 * // Création via formulaire web
 * BidListDTO dto = new BidListDTO();
 * dto.setAccount("TRADING_ACCOUNT_001");
 * dto.setType("EQUITY");
 * dto.setBidQuantity(1000.50);
 * 
 * // Validation automatique par Spring
 * if (bindingResult.hasErrors()) { ... }
 * </pre>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.BidList
 * @see com.nnk.springboot.mapper.BidListMapper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidListDTO {

    /** 
     * Identifiant unique de la liste d'offres.
     * 
     * <p>Utilisé pour l'identification lors des opérations de mise à jour
     * et de suppression. Null lors de la création d'une nouvelle offre.</p>
     */
    private Integer bidListId;

    /** 
     * Compte de trading associé à cette offre.
     * 
     * <p>Identifiant obligatoire du compte utilisé pour cette transaction.
     * Limité à 30 caractères pour respecter les contraintes de la base de données.</p>
     * 
     * <p>Exemples : "TRADING_ACCOUNT_001", "HEDGE_FUND_XYZ"</p>
     */
    @NotBlank(message = "Account is mandatory")
    @Size(max = 30, message = "Account must be less than 30 characters")
    private String account;

    /** 
     * Type de l'offre ou de l'instrument financier.
     * 
     * <p>Catégorisation obligatoire de l'offre pour le système de trading.
     * Limité à 30 caractères.</p>
     * 
     * <p>Exemples : "EQUITY", "BOND", "DERIVATIVE", "FOREX"</p>
     */
    @NotBlank(message = "Type is mandatory")
    @Size(max = 30, message = "Type must be less than 30 characters")
    private String type;

    /** 
     * Quantité demandée pour l'offre d'achat (bid).
     * 
     * <p>Volume que l'acheteur souhaite acquérir. Doit être positif ou zéro,
     * avec un maximum de 2 décimales pour la précision financière.</p>
     * 
     * <p>Exemple : 1500.75 (mille cinq cents unités et soixante-quinze centièmes)</p>
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Bid quantity must be positive or zero")
    @Digits(integer = 10, fraction = 2, message = "Bid quantity must be a valid number with max 2 decimal places")
    private Double bidQuantity;

    /** 
     * Quantité proposée pour l'offre de vente (ask).
     * 
     * <p>Volume que le vendeur propose à la vente. Doit être positif ou zéro,
     * avec un maximum de 2 décimales.</p>
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Ask quantity must be positive or zero")
    @Digits(integer = 10, fraction = 2, message = "Ask quantity must be a valid number with max 2 decimal places")
    private Double askQuantity;

    /** 
     * Prix de l'offre d'achat (bid).
     * 
     * <p>Prix maximum que l'acheteur est prêt à payer par unité.
     * Validation stricte : positif ou zéro, 2 décimales maximum.</p>
     * 
     * <p>Exemple : 125.50 (cent vingt-cinq dollars et cinquante cents)</p>
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Bid must be positive or zero")
    @Digits(integer = 10, fraction = 2, message = "Bid must be a valid number with max 2 decimal places")
    private Double bid;

    /** 
     * Prix de l'offre de vente (ask).
     * 
     * <p>Prix minimum auquel le vendeur accepte de vendre par unité.
     * Généralement supérieur au prix bid (écart bid-ask).</p>
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Ask must be positive or zero")
    @Digits(integer = 10, fraction = 2, message = "Ask must be a valid number with max 2 decimal places")
    private Double ask;

    /** 
     * Référence de benchmark pour l'évaluation de performance.
     * 
     * <p>Indicateur de référence utilisé pour mesurer la performance
     * de cette offre par rapport au marché.</p>
     * 
     * <p>Exemples : "S&P500", "NASDAQ", "EURIBOR"</p>
     */
    @Size(max = 125, message = "Benchmark must be less than 125 characters")
    private String benchmark;

    /** 
     * Commentaires ou notes additionnelles sur l'offre.
     * 
     * <p>Champ libre pour des observations, instructions spéciales
     * ou notes du trader concernant cette offre.</p>
     */
    @Size(max = 125, message = "Commentary must be less than 125 characters")
    private String commentary;

    /** 
     * Identifiant du titre ou de la sécurité financière.
     * 
     * <p>Référence vers l'instrument financier concerné par l'offre.
     * Peut être un code ISIN, un ticker ou un identifiant interne.</p>
     * 
     * <p>Exemples : "AAPL", "FR0000120271", "INTERNAL_BOND_001"</p>
     */
    @Size(max = 125, message = "Security must be less than 125 characters")
    private String security;

    /** 
     * Statut actuel de l'offre dans le workflow de trading.
     * 
     * <p>État de traitement court et codifié de l'offre.</p>
     * 
     * <p>Exemples : "ACTIVE", "CLOSED", "PENDING", "CANCELLED"</p>
     */
    @Size(max = 10, message = "Status must be less than 10 characters")
    private String status;

    /** 
     * Identifiant du trader responsable de cette offre.
     * 
     * <p>Référence vers la personne ayant créé ou géré l'offre
     * pour la traçabilité et la responsabilité.</p>
     */
    @Size(max = 125, message = "Trader must be less than 125 characters")
    private String trader;

    /** 
     * Référence du livre de trading ou portefeuille.
     * 
     * <p>Identifiant du book de positions auquel cette offre
     * est rattachée pour la gestion des risques.</p>
     */
    @Size(max = 125, message = "Book must be less than 125 characters")
    private String book;

    /** 
     * Nom de la personne ayant créé l'enregistrement.
     * 
     * <p>Champ d'audit pour la traçabilité de la création.
     * Différent du trader qui peut être responsable de l'offre.</p>
     */
    @Size(max = 125, message = "Creation name must be less than 125 characters")
    private String creationName;

    /** 
     * Nom de la personne ayant effectué la dernière révision.
     * 
     * <p>Champ d'audit pour tracer qui a modifié l'enregistrement
     * en dernier pour la conformité et la traçabilité.</p>
     */
    @Size(max = 125, message = "Revision name must be less than 125 characters")
    private String revisionName;

    /** 
     * Nom de l'accord ou du deal associé.
     * 
     * <p>Référence vers une transaction globale ou un accord
     * dont cette offre fait partie.</p>
     */
    @Size(max = 125, message = "Deal name must be less than 125 characters")
    private String dealName;

    /** 
     * Type de deal ou d'accord commercial.
     * 
     * <p>Catégorisation du type de transaction ou d'accord
     * pour le reporting et l'analyse.</p>
     * 
     * <p>Exemples : "SPOT", "FORWARD", "SWAP", "OPTION"</p>
     */
    @Size(max = 125, message = "Deal type must be less than 125 characters")
    private String dealType;

    /** 
     * Identifiant de la liste source d'origine.
     * 
     * <p>Référence vers une liste d'offres parent si cette
     * offre a été dérivée ou copiée d'une autre liste.</p>
     */
    @Size(max = 125, message = "Source list ID must be less than 125 characters")
    private String sourceListId;

    /** 
     * Côté de la transaction dans le trading.
     * 
     * <p>Indique la position de l'offre : achat, vente, ou neutre.
     * Important pour la gestion des risques et le P&L.</p>
     * 
     * <p>Exemples : "BUY", "SELL", "LONG", "SHORT"</p>
     */
    @Size(max = 125, message = "Side must be less than 125 characters")
    private String side;

    /**
     * Constructeur de convenance pour la création rapide d'offres simples.
     * 
     * <p>Ce constructeur permet de créer rapidement un DTO avec les champs
     * essentiels pour les tests unitaires, les prototypes ou la création
     * d'offres basiques avec les données minimales requises.</p>
     * 
     * <p>Les autres champs peuvent être définis ultérieurement via les setters
     * générés par Lombok selon les besoins spécifiques de l'offre.</p>
     * 
     * <p>Usage typique :</p>
     * <pre>
     * BidListDTO simpleOffer = new BidListDTO("ACCOUNT_001", "EQUITY", 1000.0);
     * simpleOffer.setBid(125.50);
     * simpleOffer.setAsk(125.75);
     * </pre>
     * 
     * @param account Le compte de trading associé à l'offre
     * @param type Le type d'instrument financier ou d'offre
     * @param bidQuantity La quantité demandée pour l'offre d'achat
     */
    public BidListDTO(String account, String type, Double bidQuantity) {
        this.account = account;
        this.type = type;
        this.bidQuantity = bidQuantity;
    }
}