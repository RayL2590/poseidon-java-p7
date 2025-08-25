package com.nnk.springboot.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) pour l'entité Trade.
 * 
 * <p>Cette classe DTO encapsule les données des transactions pour la communication
 * entre la couche de présentation et la couche métier. Elle assure une validation
 * rigoureuse des transactions saisies par l'utilisateur et respecte les contraintes
 * de sécurité et de cohérence pour les opérations financières.</p>
 * 
 * <p>Validation des données financières :</p>
 * <ul>
 *   <li><strong>Comptes standardisés</strong> : Format et validité des comptes</li>
 *   <li><strong>Types de transactions</strong> : Validation des types autorisés</li>
 *   <li><strong>Quantités et prix</strong> : Validation des valeurs numériques</li>
 *   <li><strong>Données d'audit</strong> : Traçabilité des modifications</li>
 * </ul>
 * 
 * <p>Utilisation dans l'environnement de trading :</p>
 * <ul>
 *   <li><strong>Saisie de transactions</strong> : Formulaires de création/modification</li>
 *   <li><strong>Validation métier</strong> : Contrôle de cohérence des transactions</li>
 *   <li><strong>Interface utilisateur</strong> : Affichage et manipulation des données</li>
 *   <li><strong>API REST</strong> : Échange de données avec systèmes externes</li>
 * </ul>
 * 
 * <p>Types de transactions supportées :</p>
 * <ul>
 *   <li><strong>Achat</strong> : Transactions d'acquisition d'actifs</li>
 *   <li><strong>Vente</strong> : Transactions de cession d'actifs</li>
 *   <li><strong>Mixte</strong> : Transactions combinées achat/vente</li>
 *   <li><strong>Arbitrage</strong> : Transactions d'arbitrage sur différents marchés</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.Trade
 * @see com.nnk.springboot.mapper.TradeMapper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeDTO {

    /** 
     * Identifiant unique de la transaction.
     * 
     * <p>Utilisé pour l'identification lors des opérations de mise à jour
     * et de suppression. Reste null lors de la création d'une nouvelle transaction.</p>
     */
    private Integer tradeId;

    /** 
     * Compte associé à cette transaction.
     * 
     * <p>Identifiant du compte de trading, obligatoire pour toute transaction.
     * Doit respecter le format standardisé des comptes de l'organisation.</p>
     * 
     * <p>Format attendu : Code alphanumérique de maximum 30 caractères.</p>
     * 
     * <p>Exemples valides : "ACC001", "TRADING_DESK_A", "CLIENT_123"</p>
     */
    @NotBlank(message = "Account is required")
    @Size(max = 30, message = "Account must be less than 30 characters")
    @Pattern(regexp = "^[A-Z0-9][A-Z0-9_\\-]*$", 
             message = "Account must start with alphanumeric character and contain only uppercase letters, digits, underscores, and hyphens")
    private String account;

    /** 
     * Type de la transaction.
     * 
     * <p>Classification de la transaction selon les catégories métier définies.
     * Ce champ est obligatoire et permet de déterminer le traitement approprié
     * pour chaque type de transaction.</p>
     * 
     * <p>Types standards : BUY, SELL, SWAP, REPO, OPTION, FUTURE, etc.</p>
     */
    @NotBlank(message = "Type is required")
    @Size(max = 30, message = "Type must be less than 30 characters")
    private String type;

    /** 
     * Quantité d'achat pour cette transaction.
     * 
     * <p>Volume des actifs achetés dans cette transaction. Doit être un nombre
     * positif lorsque spécifié. Peut être null si la transaction ne concerne
     * que la vente.</p>
     * 
     * <p>Validation : Nombre positif avec maximum 2 décimales pour la précision.</p>
     */
    @DecimalMin(value = "0.01", message = "Buy quantity must be positive")
    @Digits(integer = 10, fraction = 2, message = "Buy quantity must have at most 2 decimal places")
    private Double buyQuantity;

    /** 
     * Quantité de vente pour cette transaction.
     * 
     * <p>Volume des actifs vendus dans cette transaction. Doit être un nombre
     * positif lorsque spécifié. Peut être null si la transaction ne concerne
     * que l'achat.</p>
     * 
     * <p>Validation : Nombre positif avec maximum 2 décimales pour la précision.</p>
     */
    @DecimalMin(value = "0.01", message = "Sell quantity must be positive")
    @Digits(integer = 10, fraction = 2, message = "Sell quantity must have at most 2 decimal places")
    private Double sellQuantity;

    /** 
     * Prix d'achat pour cette transaction.
     * 
     * <p>Prix unitaire d'exécution pour les achats. Doit être un nombre
     * positif lorsque spécifié. Utilisé avec buyQuantity pour calculer
     * la valeur totale de l'achat.</p>
     * 
     * <p>Validation : Nombre positif avec maximum 4 décimales pour la précision.</p>
     */
    @DecimalMin(value = "0.0001", message = "Buy price must be positive")
    @Digits(integer = 10, fraction = 4, message = "Buy price must have at most 4 decimal places")
    private Double buyPrice;

    /** 
     * Prix de vente pour cette transaction.
     * 
     * <p>Prix unitaire d'exécution pour les ventes. Doit être un nombre
     * positif lorsque spécifié. Utilisé avec sellQuantity pour calculer
     * la valeur totale de la vente.</p>
     * 
     * <p>Validation : Nombre positif avec maximum 4 décimales pour la précision.</p>
     */
    @DecimalMin(value = "0.0001", message = "Sell price must be positive")
    @Digits(integer = 10, fraction = 4, message = "Sell price must have at most 4 decimal places")
    private Double sellPrice;

    /** 
     * Date et heure de la transaction.
     * 
     * <p>Timestamp d'exécution de la transaction. Cette date peut être différente
     * de la date de création de l'enregistrement et représente le moment réel
     * de l'exécution sur le marché.</p>
     */
    private LocalDateTime tradeDate;

    /** 
     * Titre ou sécurité financière concernée.
     * 
     * <p>Identifiant du produit financier traité dans cette transaction.
     * Peut être un code ISIN, un ticker, ou tout autre identifiant standard.</p>
     * 
     * <p>Exemples : "AAPL", "FR0000120404", "EUR/USD"</p>
     */
    @Size(max = 125, message = "Security must be less than 125 characters")
    private String security;

    /** 
     * Statut de la transaction.
     * 
     * <p>État actuel de la transaction dans le cycle de vie du traitement.
     * États standards : PENDING, EXECUTED, CANCELLED, FAILED, SETTLED.</p>
     */
    @Size(max = 10, message = "Status must be less than 10 characters")
    private String status;

    /** 
     * Identifiant du trader responsable.
     * 
     * <p>Nom ou identifiant du trader ayant initié ou exécuté cette transaction.
     * Utilisé pour l'audit et la responsabilité des opérations.</p>
     */
    @Size(max = 125, message = "Trader must be less than 125 characters")
    private String trader;

    /** 
     * Référence de benchmark utilisée.
     * 
     * <p>Indice ou référence de marché utilisée comme base de comparaison
     * pour cette transaction. Aide à l'évaluation des performances.</p>
     */
    @Size(max = 125, message = "Benchmark must be less than 125 characters")
    private String benchmark;

    /** 
     * Livre de trading associé.
     * 
     * <p>Portefeuille ou livre dans lequel la transaction est enregistrée.
     * Permet la ségrégation et l'organisation des positions par stratégie
     * ou type de client.</p>
     */
    @Size(max = 125, message = "Book must be less than 125 characters")
    private String book;

    /** 
     * Nom de l'utilisateur ayant créé l'enregistrement.
     * 
     * <p>Utilisé pour l'audit et la traçabilité des saisies. Peut être
     * différent du trader si la saisie est effectuée par un back-office.</p>
     */
    @Size(max = 125, message = "Creation name must be less than 125 characters")
    private String creationName;

    /** 
     * Date et heure de création de l'enregistrement.
     * 
     * <p>Timestamp automatique de création de l'enregistrement dans le système.
     * Différent de tradeDate qui représente l'exécution réelle.</p>
     */
    private LocalDateTime creationDate;

    /** 
     * Nom de l'utilisateur ayant effectué la dernière révision.
     * 
     * <p>Utilisé pour l'audit des modifications. Permet de tracer qui a
     * modifié quoi et quand pour la conformité réglementaire.</p>
     */
    @Size(max = 125, message = "Revision name must be less than 125 characters")
    private String revisionName;

    /** 
     * Date et heure de la dernière révision.
     * 
     * <p>Timestamp automatique de la dernière modification de l'enregistrement.
     * Mis à jour automatiquement à chaque modification.</p>
     */
    private LocalDateTime revisionDate;

    /** 
     * Nom de l'opération ou du deal.
     * 
     * <p>Identifiant métier de l'opération globale dont fait partie cette
     * transaction. Permet de regrouper plusieurs transactions liées.</p>
     */
    @Size(max = 125, message = "Deal name must be less than 125 characters")
    private String dealName;

    /** 
     * Type de deal ou d'opération.
     * 
     * <p>Classification métier de l'opération globale. Permet de catégoriser
     * les transactions selon la stratégie ou le type d'opération.</p>
     */
    @Size(max = 125, message = "Deal type must be less than 125 characters")
    private String dealType;

    /** 
     * Identifiant de la source ou liste source.
     * 
     * <p>Référence vers la source de données origine, par exemple une
     * bid list ou une autre transaction parente. Permet la traçabilité
     * des flux de données.</p>
     */
    @Size(max = 125, message = "Source list ID must be less than 125 characters")
    private String sourceListId;

    /** 
     * Côté de la transaction (Buy/Sell).
     * 
     * <p>Indique le sens principal de la transaction. Valeurs standards :
     * BUY, SELL, BOTH (pour les transactions bidirectionnelles).</p>
     */
    @Size(max = 125, message = "Side must be less than 125 characters")
    private String side;

    /**
     * Constructeur de convenance pour la création rapide de transactions.
     * 
     * <p>Ce constructeur permet de créer rapidement un DTO avec les champs
     * essentiels (compte et type) pour les tests unitaires et la création
     * rapide d'instances. Les autres champs peuvent être ajoutés ultérieurement
     * selon les besoins spécifiques de la transaction.</p>
     * 
     * <p>Exemples d'usage :</p>
     * <pre>
     * // Transaction d'achat simple
     * TradeDTO buyTrade = new TradeDTO("ACC001", "BUY");
     * 
     * // Transaction de vente
     * TradeDTO sellTrade = new TradeDTO("ACC002", "SELL");
     * 
     * // Transaction de swap
     * TradeDTO swapTrade = new TradeDTO("ACC003", "SWAP");
     * </pre>
     * 
     * @param account Le compte associé à la transaction
     * @param type Le type de transaction
     */
    public TradeDTO(String account, String type) {
        this.account = account;
        this.type = type;
    }

    /**
     * Constructeur complet pour les transactions avec quantités.
     * 
     * <p>Ce constructeur permet de créer une transaction complète avec les
     * informations de base incluant les quantités d'achat et de vente.
     * Utilisé principalement lors de la création de transactions depuis
     * des systèmes externes ou des imports de données.</p>
     * 
     * <p>Exemple d'usage :</p>
     * <pre>
     * TradeDTO complexTrade = new TradeDTO(
     *     "TRADING_DESK_A",
     *     "BUY",
     *     1000.0,  // buyQuantity
     *     null     // sellQuantity
     * );
     * </pre>
     * 
     * @param account Le compte associé
     * @param type Le type de transaction
     * @param buyQuantity La quantité d'achat
     * @param sellQuantity La quantité de vente
     */
    public TradeDTO(String account, String type, Double buyQuantity, Double sellQuantity) {
        this.account = account;
        this.type = type;
        this.buyQuantity = buyQuantity;
        this.sellQuantity = sellQuantity;
    }

    /**
     * Vérifie si cette transaction a des quantités d'achat.
     * 
     * <p>Méthode utilitaire qui détermine si la transaction inclut des achats,
     * basé sur la présence d'une quantité d'achat positive.</p>
     * 
     * @return true si buyQuantity est présent et positif, false sinon
     */
    public boolean hasBuyTransaction() {
        return buyQuantity != null && buyQuantity > 0;
    }

    /**
     * Vérifie si cette transaction a des quantités de vente.
     * 
     * <p>Méthode utilitaire qui détermine si la transaction inclut des ventes,
     * basé sur la présence d'une quantité de vente positive.</p>
     * 
     * @return true si sellQuantity est présent et positif, false sinon
     */
    public boolean hasSellTransaction() {
        return sellQuantity != null && sellQuantity > 0;
    }

    /**
     * Calcule la valeur totale d'achat de la transaction.
     * 
     * <p>Méthode utilitaire qui calcule la valeur monétaire totale des achats
     * en multipliant la quantité par le prix d'achat.</p>
     * 
     * @return Valeur totale d'achat ou null si données insuffisantes
     */
    public Double getTotalBuyValue() {
        if (buyQuantity != null && buyPrice != null) {
            return buyQuantity * buyPrice;
        }
        return null;
    }

    /**
     * Calcule la valeur totale de vente de la transaction.
     * 
     * <p>Méthode utilitaire qui calcule la valeur monétaire totale des ventes
     * en multipliant la quantité par le prix de vente.</p>
     * 
     * @return Valeur totale de vente ou null si données insuffisantes
     */
    public Double getTotalSellValue() {
        if (sellQuantity != null && sellPrice != null) {
            return sellQuantity * sellPrice;
        }
        return null;
    }

    /**
     * Détermine si la transaction est complète pour l'exécution.
     * 
     * <p>Méthode utilitaire qui vérifie si la transaction possède tous les
     * éléments minimum requis pour être traitée : compte, type et au moins
     * une opération d'achat ou de vente avec les données correspondantes.</p>
     * 
     * @return true si la transaction est complète, false sinon
     */
    public boolean isComplete() {
        boolean hasRequiredFields = account != null && !account.trim().isEmpty() &&
                                  type != null && !type.trim().isEmpty();
        
        boolean hasBuyData = buyQuantity != null && buyQuantity > 0 && 
                           buyPrice != null && buyPrice > 0;
        
        boolean hasSellData = sellQuantity != null && sellQuantity > 0 && 
                            sellPrice != null && sellPrice > 0;
        
        return hasRequiredFields && (hasBuyData || hasSellData);
    }

    /**
     * Génère un résumé lisible de la transaction.
     * 
     * <p>Méthode utilitaire qui crée une description condensée de la transaction
     * incluant ses principales caractéristiques. Utile pour les logs,
     * les tooltips et les interfaces d'administration.</p>
     * 
     * @return Résumé descriptif de la transaction
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Trade: ").append(account != null ? account : "Unknown")
               .append(" - ").append(type != null ? type : "Unknown");
        
        if (hasBuyTransaction()) {
            summary.append(" [BUY: ").append(buyQuantity);
            if (buyPrice != null) {
                summary.append(" @ ").append(buyPrice);
            }
            summary.append("]");
        }
        
        if (hasSellTransaction()) {
            summary.append(" [SELL: ").append(sellQuantity);
            if (sellPrice != null) {
                summary.append(" @ ").append(sellPrice);
            }
            summary.append("]");
        }
        
        if (security != null && !security.trim().isEmpty()) {
            summary.append(" (").append(security).append(")");
        }
        
        return summary.toString();
    }
}