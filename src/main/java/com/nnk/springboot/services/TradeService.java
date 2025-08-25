package com.nnk.springboot.services;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Implémentation du service de gestion des transactions (Trade).
 * 
 * <p>Cette classe implémente toute la logique métier pour la gestion des transactions
 * financières dans l'application de trading Poseidon. Elle respecte les principes SOLID
 * et fournit une couche de service robuste avec validation métier, gestion d'erreurs
 * et optimisations de performance.</p>
 * 
 * <p>Responsabilités principales :</p>
 * <ul>
 *   <li><strong>Opérations CRUD</strong> : Gestion complète du cycle de vie des transactions</li>
 *   <li><strong>Validation métier</strong> : Contrôles de cohérence et conformité financière</li>
 *   <li><strong>Contrôles de risque</strong> : Validation des limites et seuils</li>
 *   <li><strong>Optimisations</strong> : Cache et requêtes optimisées</li>
 * </ul>
 * 
 * <p>Architecture et design patterns :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Responsabilité unique de gestion des Trade</li>
 *   <li><strong>OCP</strong> : Ouvert à l'extension, fermé à la modification</li>
 *   <li><strong>LSP</strong> : Respecte le contrat défini par ITradeService</li>
 *   <li><strong>ISP</strong> : Interface spécialisée pour les opérations Trade</li>
 *   <li><strong>DIP</strong> : Dépend d'abstractions (TradeRepository)</li>
 * </ul>
 * 
 * <p>Gestion transactionnelle :</p>
 * <ul>
 *   <li><strong>@Transactional</strong> : Cohérence des données financières critiques</li>
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
public class TradeService implements ITradeService {

    @Autowired
    private TradeRepository tradeRepository;
    
    // Pattern pour la validation des comptes (lettres majuscules, chiffres, underscores, tirets)
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("^[A-Z0-9][A-Z0-9_\\-]*$");
    
    // Pattern pour la validation des types de transaction
    private static final Pattern TYPE_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]*$");

    @Override
    @Transactional(readOnly = true)
    public List<Trade> findAll() {
        return tradeRepository.findAllByOrderByTradeDateDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trade> findById(Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return tradeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trade> findByAccount(String account) {
        if (account == null || account.trim().isEmpty()) {
            return List.of();
        }
        return tradeRepository.findByAccountOrderByTradeDateDesc(account.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trade> findByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return List.of();
        }
        return tradeRepository.findByTypeOrderByTradeDateDesc(type.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return tradeRepository.existsById(id);
    }

    @Override
    public Trade save(Trade trade) {
        validateTradeInternal(trade);
        
        // Normalisation des données
        normalizeTradeData(trade);
        
        // Gestion des timestamps
        LocalDateTime now = LocalDateTime.now();
        if (trade.getTradeId() == null) {
            // Nouvelle transaction
            if (trade.getCreationDate() == null) {
                trade.setCreationDate(now);
            }
            if (trade.getTradeDate() == null) {
                trade.setTradeDate(now);
            }
        } else {
            // Mise à jour
            trade.setRevisionDate(now);
        }
        
        return tradeRepository.save(trade);
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID for deletion");
        }
        
        if (!tradeRepository.existsById(id)) {
            throw new IllegalArgumentException("Trade not found with id: " + id);
        }
        
        tradeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trade> findByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return List.of();
        }
        return tradeRepository.findByStatusOrderByTradeDateDesc(status.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trade> findByTrader(String trader) {
        if (trader == null || trader.trim().isEmpty()) {
            return List.of();
        }
        return tradeRepository.findByTraderOrderByTradeDateDesc(trader.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trade> findByTradeDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return List.of();
        }
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        
        return tradeRepository.findByTradeDateBetweenOrderByTradeDateDesc(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateTotalValueByAccount(String account) {
        if (account == null || account.trim().isEmpty()) {
            return null;
        }
        
        String cleanAccount = account.trim();
        Double buyValue = tradeRepository.sumBuyValueByAccount(cleanAccount);
        Double sellValue = tradeRepository.sumSellValueByAccount(cleanAccount);
        
        // Calcul de la valeur nette (ventes - achats)
        double totalBuy = buyValue != null ? buyValue : 0.0;
        double totalSell = sellValue != null ? sellValue : 0.0;
        
        return totalSell - totalBuy;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateTrade(Trade trade) {
        try {
            validateTradeInternal(trade);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trade> findRecentTrades(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return tradeRepository.findRecentTrades(PageRequest.of(0, limit));
    }

    /**
     * Valide les données d'une transaction.
     * 
     * <p>Cette méthode effectue une validation complète des données de la transaction,
     * incluant les contrôles de format, de cohérence métier et de sécurité financière.</p>
     * 
     * @param trade La transaction à valider
     * @throws IllegalArgumentException si les données sont invalides
     */
    private void validateTradeInternal(Trade trade) {
        if (trade == null) {
            throw new IllegalArgumentException("Trade cannot be null");
        }
        
        // Validation des champs obligatoires
        validateAccount(trade.getAccount());
        validateType(trade.getType());
        
        // Validation des montants
        validateAmounts(trade);
        
        // Validation des dates
        validateDates(trade);
        
        // Validation des champs de longueur
        validateStringLengths(trade);
        
        // Validation métier
        validateBusinessRules(trade);
    }

    /**
     * Valide le compte de la transaction.
     */
    private void validateAccount(String account) {
        if (account == null || account.trim().isEmpty()) {
            throw new IllegalArgumentException("Account cannot be null or empty");
        }
        
        String trimmedAccount = account.trim();
        
        if (trimmedAccount.length() > 30) {
            throw new IllegalArgumentException("Account cannot exceed 30 characters");
        }
        
        if (!ACCOUNT_PATTERN.matcher(trimmedAccount).matches()) {
            throw new IllegalArgumentException(
                "Account must start with alphanumeric character and contain only uppercase letters, digits, underscores, and hyphens");
        }
    }

    /**
     * Valide le type de transaction.
     */
    private void validateType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        
        String trimmedType = type.trim();
        
        if (trimmedType.length() > 30) {
            throw new IllegalArgumentException("Type cannot exceed 30 characters");
        }
        
        if (!TYPE_PATTERN.matcher(trimmedType).matches()) {
            throw new IllegalArgumentException(
                "Type must start with uppercase letter and contain only uppercase letters, digits, and underscores");
        }
    }

    /**
     * Valide les montants et quantités.
     */
    private void validateAmounts(Trade trade) {
        // Au moins une opération (achat ou vente) doit être définie
        boolean hasBuyOperation = trade.getBuyQuantity() != null && trade.getBuyQuantity() > 0;
        boolean hasSellOperation = trade.getSellQuantity() != null && trade.getSellQuantity() > 0;
        
        if (!hasBuyOperation && !hasSellOperation) {
            throw new IllegalArgumentException("Trade must have at least one operation (buy or sell) with positive quantity");
        }
        
        // Validation des quantités d'achat
        if (trade.getBuyQuantity() != null) {
            if (trade.getBuyQuantity() <= 0) {
                throw new IllegalArgumentException("Buy quantity must be positive");
            }
            if (trade.getBuyPrice() == null || trade.getBuyPrice() <= 0) {
                throw new IllegalArgumentException("Buy price must be positive when buy quantity is specified");
            }
        }
        
        // Validation des quantités de vente
        if (trade.getSellQuantity() != null) {
            if (trade.getSellQuantity() <= 0) {
                throw new IllegalArgumentException("Sell quantity must be positive");
            }
            if (trade.getSellPrice() == null || trade.getSellPrice() <= 0) {
                throw new IllegalArgumentException("Sell price must be positive when sell quantity is specified");
            }
        }
        
        // Validation des prix seuls (sans quantité correspondante)
        if (trade.getBuyPrice() != null && trade.getBuyQuantity() == null) {
            throw new IllegalArgumentException("Buy price cannot be specified without buy quantity");
        }
        
        if (trade.getSellPrice() != null && trade.getSellQuantity() == null) {
            throw new IllegalArgumentException("Sell price cannot be specified without sell quantity");
        }
    }

    /**
     * Valide les dates de la transaction.
     */
    private void validateDates(Trade trade) {
        LocalDateTime now = LocalDateTime.now();
        
        // La date de transaction ne peut pas être dans un futur lointain
        if (trade.getTradeDate() != null && trade.getTradeDate().isAfter(now.plusDays(1))) {
            throw new IllegalArgumentException("Trade date cannot be more than 1 day in the future");
        }
        
        // La date de création ne peut pas être dans le futur
        if (trade.getCreationDate() != null && trade.getCreationDate().isAfter(now.plusMinutes(5))) {
            throw new IllegalArgumentException("Creation date cannot be in the future");
        }
        
        // La date de révision ne peut pas être antérieure à la date de création
        if (trade.getCreationDate() != null && trade.getRevisionDate() != null) {
            if (trade.getRevisionDate().isBefore(trade.getCreationDate())) {
                throw new IllegalArgumentException("Revision date cannot be before creation date");
            }
        }
    }

    /**
     * Valide les longueurs des champs texte.
     */
    private void validateStringLengths(Trade trade) {
        validateStringLength(trade.getSecurity(), 125, "Security");
        validateStringLength(trade.getStatus(), 10, "Status");
        validateStringLength(trade.getTrader(), 125, "Trader");
        validateStringLength(trade.getBenchmark(), 125, "Benchmark");
        validateStringLength(trade.getBook(), 125, "Book");
        validateStringLength(trade.getCreationName(), 125, "Creation name");
        validateStringLength(trade.getRevisionName(), 125, "Revision name");
        validateStringLength(trade.getDealName(), 125, "Deal name");
        validateStringLength(trade.getDealType(), 125, "Deal type");
        validateStringLength(trade.getSourceListId(), 125, "Source list ID");
        validateStringLength(trade.getSide(), 125, "Side");
    }

    /**
     * Valide la longueur d'un champ texte.
     */
    private void validateStringLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot exceed " + maxLength + " characters");
        }
    }

    /**
     * Valide les règles métier spécifiques.
     */
    private void validateBusinessRules(Trade trade) {
        // Validation cohérence side vs opérations
        if (trade.getSide() != null) {
            String side = trade.getSide().toUpperCase();
            boolean hasBuy = trade.getBuyQuantity() != null && trade.getBuyQuantity() > 0;
            boolean hasSell = trade.getSellQuantity() != null && trade.getSellQuantity() > 0;
            
            if ("BUY".equals(side) && !hasBuy) {
                throw new IllegalArgumentException("Side is BUY but no buy operation is defined");
            }
            
            if ("SELL".equals(side) && !hasSell) {
                throw new IllegalArgumentException("Side is SELL but no sell operation is defined");
            }
        }
        
        // Validation des statuts standards
        if (trade.getStatus() != null) {
            String status = trade.getStatus().toUpperCase();
            List<String> validStatuses = List.of("PENDING", "EXECUTED", "CANCELLED", "FAILED", "SETTLED");
            if (!validStatuses.contains(status)) {
                // Log warning mais ne rejette pas - permet l'extensibilité
                System.out.println("Warning: Non-standard status '" + status + "' used in trade");
            }
        }
        
        // Validation des montants excessifs (contrôle de risque basique)
        validateRiskLimits(trade);
    }

    /**
     * Valide les limites de risque basiques.
     */
    private void validateRiskLimits(Trade trade) {
        double maxSingleTradeValue = 10_000_000.0; // 10M limit par défaut
        
        if (trade.getBuyQuantity() != null && trade.getBuyPrice() != null) {
            double buyValue = trade.getBuyQuantity() * trade.getBuyPrice();
            if (buyValue > maxSingleTradeValue) {
                throw new IllegalArgumentException("Buy trade value exceeds maximum allowed limit of " + maxSingleTradeValue);
            }
        }
        
        if (trade.getSellQuantity() != null && trade.getSellPrice() != null) {
            double sellValue = trade.getSellQuantity() * trade.getSellPrice();
            if (sellValue > maxSingleTradeValue) {
                throw new IllegalArgumentException("Sell trade value exceeds maximum allowed limit of " + maxSingleTradeValue);
            }
        }
    }

    /**
     * Normalise les données de la transaction avant sauvegarde.
     */
    private void normalizeTradeData(Trade trade) {
        if (trade.getAccount() != null) {
            trade.setAccount(trade.getAccount().trim().toUpperCase());
        }
        
        if (trade.getType() != null) {
            trade.setType(trade.getType().trim().toUpperCase());
        }
        
        if (trade.getStatus() != null) {
            trade.setStatus(trade.getStatus().trim().toUpperCase());
            if (trade.getStatus().isEmpty()) {
                trade.setStatus(null);
            }
        }
        
        if (trade.getSecurity() != null) {
            trade.setSecurity(trade.getSecurity().trim());
            if (trade.getSecurity().isEmpty()) {
                trade.setSecurity(null);
            }
        }
        
        if (trade.getTrader() != null) {
            trade.setTrader(trade.getTrader().trim());
            if (trade.getTrader().isEmpty()) {
                trade.setTrader(null);
            }
        }
        
        if (trade.getBenchmark() != null) {
            trade.setBenchmark(trade.getBenchmark().trim());
            if (trade.getBenchmark().isEmpty()) {
                trade.setBenchmark(null);
            }
        }
        
        if (trade.getBook() != null) {
            trade.setBook(trade.getBook().trim());
            if (trade.getBook().isEmpty()) {
                trade.setBook(null);
            }
        }
        
        if (trade.getCreationName() != null) {
            trade.setCreationName(trade.getCreationName().trim());
            if (trade.getCreationName().isEmpty()) {
                trade.setCreationName(null);
            }
        }
        
        if (trade.getRevisionName() != null) {
            trade.setRevisionName(trade.getRevisionName().trim());
            if (trade.getRevisionName().isEmpty()) {
                trade.setRevisionName(null);
            }
        }
        
        if (trade.getDealName() != null) {
            trade.setDealName(trade.getDealName().trim());
            if (trade.getDealName().isEmpty()) {
                trade.setDealName(null);
            }
        }
        
        if (trade.getDealType() != null) {
            trade.setDealType(trade.getDealType().trim());
            if (trade.getDealType().isEmpty()) {
                trade.setDealType(null);
            }
        }
        
        if (trade.getSourceListId() != null) {
            trade.setSourceListId(trade.getSourceListId().trim());
            if (trade.getSourceListId().isEmpty()) {
                trade.setSourceListId(null);
            }
        }
        
        if (trade.getSide() != null) {
            trade.setSide(trade.getSide().trim().toUpperCase());
            if (trade.getSide().isEmpty()) {
                trade.setSide(null);
            }
        }
    }
}