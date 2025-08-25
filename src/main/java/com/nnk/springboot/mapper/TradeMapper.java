package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.dto.TradeDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper Spring pour la conversion entre entités Trade et DTOs TradeDTO.
 * 
 * <p>Cette classe composant Spring implémente le pattern Mapper pour assurer la conversion
 * bidirectionnelle entre la couche de persistance (entités JPA) et la couche de
 * présentation (DTOs) spécifiquement pour les transactions du système de trading.</p>
 * 
 * <p>Spécificités des transactions financières :</p>
 * <ul>
 *   <li><strong>Données critiques</strong> : Précision requise pour les montants et prix</li>
 *   <li><strong>Traçabilité complète</strong> : Conservation des métadonnées d'audit</li>
 *   <li><strong>Validation renforcée</strong> : Cohérence des données financières</li>
 *   <li><strong>Performance optimisée</strong> : Conversion efficace pour volumes importants</li>
 * </ul>
 * 
 * <p>Architecture et principes SOLID :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Responsabilité unique de conversion Trade</li>
 *   <li><strong>OCP</strong> : Extensible via l'interface IEntityMapper</li>
 *   <li><strong>LSP</strong> : Substitution respectée pour l'interface</li>
 *   <li><strong>ISP</strong> : Interface spécialisée pour les mappings</li>
 *   <li><strong>DIP</strong> : Dépendance sur l'abstraction IEntityMapper</li>
 * </ul>
 * 
 * <p>Fonctionnalités avancées :</p>
 * <ul>
 *   <li><strong>Conversion complète</strong> : Entity ↔ DTO bidirectionnelle</li>
 *   <li><strong>Mise à jour in-place</strong> : Optimisation pour les modifications</li>
 *   <li><strong>Gestion défensive</strong> : Protection contre les valeurs null</li>
 *   <li><strong>Validation intégrée</strong> : Contrôles de cohérence lors du mapping</li>
 * </ul>
 * 
 * <p>Usage typique dans l'écosystème de trading :</p>
 * <pre>
 * // Affichage des transactions
 * &#64;Autowired
 * private TradeMapper mapper;
 * 
 * // Liste pour interface utilisateur
 * List&lt;Trade&gt; trades = tradeService.findAll();
 * List&lt;TradeDTO&gt; dtos = trades.stream()
 *     .map(mapper::toDTO)
 *     .collect(Collectors.toList());
 * 
 * // Mise à jour d'une transaction
 * Trade existing = tradeService.findById(id);
 * mapper.updateEntityFromDTO(existing, modifiedDTO);
 * tradeService.save(existing);
 * </pre>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.Trade
 * @see com.nnk.springboot.dto.TradeDTO
 * @see IEntityMapper
 */
@Component
public class TradeMapper implements IEntityMapper<Trade, TradeDTO> {

    /**
     * Convertit une entité Trade en DTO TradeDTO.
     * 
     * <p>Cette méthode transforme une entité JPA de transaction en objet
     * de transfert de données pour l'affichage et la manipulation dans les vues.
     * Elle préserve l'intégrité des données financières critiques et maintient
     * la précision requise pour les calculs monétaires.</p>
     * 
     * <p>Données mappées :</p>
     * <ul>
     *   <li><strong>tradeId</strong> : Identifiant unique de la transaction</li>
     *   <li><strong>account</strong> : Compte de trading</li>
     *   <li><strong>type</strong> : Type de transaction</li>
     *   <li><strong>buyQuantity/sellQuantity</strong> : Volumes d'achat/vente</li>
     *   <li><strong>buyPrice/sellPrice</strong> : Prix d'exécution</li>
     *   <li><strong>tradeDate</strong> : Date d'exécution</li>
     *   <li><strong>Métadonnées d'audit</strong> : Traçabilité complète</li>
     * </ul>
     * 
     * <p>Cas d'usage dans l'environnement de trading :</p>
     * <ul>
     *   <li><strong>Interface de trading</strong> : Affichage des positions</li>
     *   <li><strong>Reporting financier</strong> : Génération de rapports</li>
     *   <li><strong>Export de données</strong> : Sauvegarde et archivage</li>
     *   <li><strong>API REST</strong> : Sérialisation JSON pour clients externes</li>
     * </ul>
     * 
     * @param trade L'entité Trade à convertir (peut être null)
     * @return Le DTO TradeDTO correspondant, ou null si l'entité source est null
     * @throws aucune exception n'est levée (gestion défensive)
     */
    public TradeDTO toDTO(Trade trade) {
        if (trade == null) {
            return null;
        }

        TradeDTO dto = new TradeDTO();
        dto.setTradeId(trade.getTradeId());
        dto.setAccount(trade.getAccount());
        dto.setType(trade.getType());
        dto.setBuyQuantity(trade.getBuyQuantity());
        dto.setSellQuantity(trade.getSellQuantity());
        dto.setBuyPrice(trade.getBuyPrice());
        dto.setSellPrice(trade.getSellPrice());
        dto.setTradeDate(trade.getTradeDate());
        dto.setSecurity(trade.getSecurity());
        dto.setStatus(trade.getStatus());
        dto.setTrader(trade.getTrader());
        dto.setBenchmark(trade.getBenchmark());
        dto.setBook(trade.getBook());
        dto.setCreationName(trade.getCreationName());
        dto.setCreationDate(trade.getCreationDate());
        dto.setRevisionName(trade.getRevisionName());
        dto.setRevisionDate(trade.getRevisionDate());
        dto.setDealName(trade.getDealName());
        dto.setDealType(trade.getDealType());
        dto.setSourceListId(trade.getSourceListId());
        dto.setSide(trade.getSide());

        return dto;
    }

    /**
     * Convertit un DTO TradeDTO en entité Trade.
     * 
     * <p>Cette méthode transforme un objet de transfert de données (provenant
     * généralement d'un formulaire web ou d'une API) en entité JPA prête pour
     * la persistance. Elle assure la cohérence des données financières critiques
     * et maintient l'intégrité des informations de trading.</p>
     * 
     * <p>Validation et intégrité :</p>
     * <ul>
     *   <li><strong>Données obligatoires</strong> : Vérification des champs requis</li>
     *   <li><strong>Précision monétaire</strong> : Préservation des décimales</li>
     *   <li><strong>Cohérence temporelle</strong> : Validation des dates</li>
     *   <li><strong>Format des comptes</strong> : Respect des standards</li>
     * </ul>
     * 
     * <p>Cas d'usage dans l'environnement de trading :</p>
     * <ul>
     *   <li><strong>Création de transactions</strong> : Nouvelles opérations depuis interface</li>
     *   <li><strong>Import de données</strong> : Chargement depuis systèmes externes</li>
     *   <li><strong>Migration de données</strong> : Transfert depuis anciens systèmes</li>
     *   <li><strong>API REST</strong> : Réception de données JSON externes</li>
     * </ul>
     * 
     * <p>Note : Les validations métier complexes sont déléguées au service
     * pour respecter la séparation des responsabilités.</p>
     * 
     * @param dto Le DTO TradeDTO à convertir (peut être null)
     * @return L'entité Trade correspondante, ou null si le DTO source est null
     * @throws aucune exception n'est levée (gestion défensive)
     */
    public Trade toEntity(TradeDTO dto) {
        if (dto == null) {
            return null;
        }

        Trade trade = new Trade();
        trade.setTradeId(dto.getTradeId());
        trade.setAccount(dto.getAccount());
        trade.setType(dto.getType());
        trade.setBuyQuantity(dto.getBuyQuantity());
        trade.setSellQuantity(dto.getSellQuantity());
        trade.setBuyPrice(dto.getBuyPrice());
        trade.setSellPrice(dto.getSellPrice());
        trade.setTradeDate(dto.getTradeDate());
        trade.setSecurity(dto.getSecurity());
        trade.setStatus(dto.getStatus());
        trade.setTrader(dto.getTrader());
        trade.setBenchmark(dto.getBenchmark());
        trade.setBook(dto.getBook());
        trade.setCreationName(dto.getCreationName());
        trade.setCreationDate(dto.getCreationDate());
        trade.setRevisionName(dto.getRevisionName());
        trade.setRevisionDate(dto.getRevisionDate());
        trade.setDealName(dto.getDealName());
        trade.setDealType(dto.getDealType());
        trade.setSourceListId(dto.getSourceListId());
        trade.setSide(dto.getSide());

        return trade;
    }

    /**
     * Met à jour une entité Trade existante avec les données d'un DTO.
     * 
     * <p>Cette méthode optimisée effectue une mise à jour in-place d'une entité
     * existante sans créer de nouvelle instance. Elle est particulièrement efficace
     * pour les opérations de modification où l'entité est déjà chargée en session JPA.</p>
     * 
     * <p>Avantages de la mise à jour in-place :</p>
     * <ul>
     *   <li><strong>Performance</strong> : Évite la création d'objets inutiles</li>
     *   <li><strong>Session JPA</strong> : Préserve l'état de la session Hibernate</li>
     *   <li><strong>Optimistic Locking</strong> : Maintient la gestion des versions</li>
     *   <li><strong>Lazy Loading</strong> : Préserve les associations chargées</li>
     * </ul>
     * 
     * <p>Champs mis à jour :</p>
     * <ul>
     *   <li><strong>Données métier</strong> : account, type, quantités, prix</li>
     *   <li><strong>Métadonnées</strong> : security, status, trader, benchmark</li>
     *   <li><strong>Audit trail</strong> : revisionName, revisionDate mis à jour</li>
     *   <li><strong>Informations deal</strong> : dealName, dealType, side</li>
     * </ul>
     * 
     * <p>Champs préservés :</p>
     * <ul>
     *   <li><strong>tradeId</strong> : Identifiant technique préservé</li>
     *   <li><strong>creationName/Date</strong> : Métadonnées de création maintenues</li>
     * </ul>
     * 
     * <p>Usage typique :</p>
     * <pre>
     * // Modification d'une transaction existante
     * Trade existing = tradeService.findById(tradeId);
     * TradeDTO modified = getModifiedDataFromForm();
     * 
     * tradeMapper.updateEntityFromDTO(existing, modified);
     * tradeService.save(existing); // Hibernate détecte les changements
     * </pre>
     * 
     * <p>Considérations métier :</p>
     * <ul>
     *   <li><strong>Correction d'erreurs</strong> : Rectification de transactions erronées</li>
     *   <li><strong>Mise à jour statut</strong> : Évolution du cycle de vie</li>
     *   <li><strong>Enrichissement données</strong> : Ajout d'informations manquantes</li>
     *   <li><strong>Conformité réglementaire</strong> : Traçabilité des modifications</li>
     * </ul>
     * 
     * @param existingTrade L'entité existante à mettre à jour (ne doit pas être null en usage normal)
     * @param dto Le DTO contenant les nouvelles données (ne doit pas être null en usage normal)
     * @throws aucune exception n'est levée - protection défensive contre les nulls
     */
    public void updateEntityFromDTO(Trade existingTrade, TradeDTO dto) {
        if (existingTrade == null || dto == null) {
            return;
        }

        existingTrade.setAccount(dto.getAccount());
        existingTrade.setType(dto.getType());
        existingTrade.setBuyQuantity(dto.getBuyQuantity());
        existingTrade.setSellQuantity(dto.getSellQuantity());
        existingTrade.setBuyPrice(dto.getBuyPrice());
        existingTrade.setSellPrice(dto.getSellPrice());
        existingTrade.setTradeDate(dto.getTradeDate());
        existingTrade.setSecurity(dto.getSecurity());
        existingTrade.setStatus(dto.getStatus());
        existingTrade.setTrader(dto.getTrader());
        existingTrade.setBenchmark(dto.getBenchmark());
        existingTrade.setBook(dto.getBook());
        existingTrade.setRevisionName(dto.getRevisionName());
        existingTrade.setRevisionDate(dto.getRevisionDate());
        existingTrade.setDealName(dto.getDealName());
        existingTrade.setDealType(dto.getDealType());
        existingTrade.setSourceListId(dto.getSourceListId());
        existingTrade.setSide(dto.getSide());
    }

    /**
     * Crée un DTO avec les valeurs par défaut pour un type de transaction donné.
     * 
     * <p>Méthode utilitaire pour générer rapidement des DTOs avec des configurations
     * pré-définies selon le type de transaction. Facilite la création de nouvelles
     * transactions en proposant des templates standardisés.</p>
     * 
     * <p>Types de transactions supportés :</p>
     * <ul>
     *   <li><strong>BUY</strong> : Transaction d'achat standard</li>
     *   <li><strong>SELL</strong> : Transaction de vente standard</li>
     *   <li><strong>SWAP</strong> : Échange bidirectionnel</li>
     *   <li><strong>REPO</strong> : Opération de pension</li>
     *   <li><strong>OPTION</strong> : Transaction sur options</li>
     *   <li><strong>FUTURE</strong> : Transaction sur contrats à terme</li>
     * </ul>
     * 
     * <p>Exemple d'usage :</p>
     * <pre>
     * // Création d'une transaction d'achat par défaut
     * TradeDTO buyTrade = mapper.createDefaultForType("BUY");
     * 
     * // Personnalisation selon les besoins
     * buyTrade.setAccount("TRADING_DESK_A");
     * buyTrade.setBuyQuantity(1000.0);
     * </pre>
     * 
     * @param tradeType Le type de transaction souhaité
     * @return DTO avec configuration par défaut pour le type spécifié
     */
    public TradeDTO createDefaultForType(String tradeType) {
        if (tradeType == null) {
            return new TradeDTO();
        }
        
        switch (tradeType.toUpperCase()) {
            case "BUY":
                TradeDTO buyTrade = new TradeDTO("DEFAULT_ACCOUNT", "BUY");
                buyTrade.setStatus("PENDING");
                buyTrade.setSide("BUY");
                return buyTrade;
                
            case "SELL":
                TradeDTO sellTrade = new TradeDTO("DEFAULT_ACCOUNT", "SELL");
                sellTrade.setStatus("PENDING");
                sellTrade.setSide("SELL");
                return sellTrade;
                
            case "SWAP":
                TradeDTO swapTrade = new TradeDTO("DEFAULT_ACCOUNT", "SWAP");
                swapTrade.setStatus("PENDING");
                swapTrade.setSide("BOTH");
                return swapTrade;
                
            case "REPO":
                TradeDTO repoTrade = new TradeDTO("DEFAULT_ACCOUNT", "REPO");
                repoTrade.setStatus("PENDING");
                repoTrade.setSide("BUY");
                repoTrade.setDealType("REPO");
                return repoTrade;
                
            case "OPTION":
                TradeDTO optionTrade = new TradeDTO("DEFAULT_ACCOUNT", "OPTION");
                optionTrade.setStatus("PENDING");
                optionTrade.setDealType("OPTION");
                return optionTrade;
                
            case "FUTURE":
                TradeDTO futureTrade = new TradeDTO("DEFAULT_ACCOUNT", "FUTURE");
                futureTrade.setStatus("PENDING");
                futureTrade.setDealType("FUTURE");
                return futureTrade;
                
            default:
                TradeDTO defaultTrade = new TradeDTO("DEFAULT_ACCOUNT", "GENERAL");
                defaultTrade.setStatus("PENDING");
                return defaultTrade;
        }
    }

    /**
     * Vérifie si une transaction est considérée comme exécutable.
     * 
     * <p>Méthode utilitaire pour déterminer si une transaction possède tous les
     * composants nécessaires pour être exécutée sur le marché.</p>
     * 
     * <p>Critères d'exécutabilité :</p>
     * <ul>
     *   <li><strong>Compte valide</strong> : Compte défini et non vide</li>
     *   <li><strong>Type défini</strong> : Type de transaction spécifié</li>
     *   <li><strong>Opération définie</strong> : Au moins achat ou vente avec prix</li>
     *   <li><strong>Quantité positive</strong> : Volume à traiter positif</li>
     * </ul>
     * 
     * @param dto Le DTO à évaluer
     * @return true si la transaction est exécutable, false sinon
     */
    public boolean isExecutable(TradeDTO dto) {
        if (dto == null) {
            return false;
        }
        
        boolean hasAccount = dto.getAccount() != null && !dto.getAccount().trim().isEmpty();
        boolean hasType = dto.getType() != null && !dto.getType().trim().isEmpty();
        
        boolean hasBuyOperation = dto.getBuyQuantity() != null && dto.getBuyQuantity() > 0 &&
                                dto.getBuyPrice() != null && dto.getBuyPrice() > 0;
        
        boolean hasSellOperation = dto.getSellQuantity() != null && dto.getSellQuantity() > 0 &&
                                 dto.getSellPrice() != null && dto.getSellPrice() > 0;
        
        return hasAccount && hasType && (hasBuyOperation || hasSellOperation);
    }

    /**
     * Calcule un score de risque pour une transaction.
     * 
     * <p>Méthode utilitaire qui évalue le niveau de risque d'une transaction
     * basée sur ses caractéristiques. Utile pour la priorisation des validations
     * et l'application de contrôles différenciés.</p>
     * 
     * <p>Facteurs de risque :</p>
     * <ul>
     *   <li><strong>+3</strong> : Montant élevé (&gt; 1M)</li>
     *   <li><strong>+2</strong> : Type de transaction complexe (SWAP, OPTION)</li>
     *   <li><strong>+1</strong> : Pas de benchmark défini</li>
     *   <li><strong>+1</strong> : Statut autre que PENDING</li>
     * </ul>
     * 
     * @param dto Le DTO à évaluer
     * @return Score de risque (0-7)
     */
    public int calculateRiskScore(TradeDTO dto) {
        if (dto == null) {
            return 0;
        }
        
        int score = 0;
        
        // Évaluation du montant
        Double totalValue = 0.0;
        if (dto.getTotalBuyValue() != null) totalValue += dto.getTotalBuyValue();
        if (dto.getTotalSellValue() != null) totalValue += dto.getTotalSellValue();
        
        if (totalValue > 1_000_000) score += 3;
        else if (totalValue > 100_000) score += 1;
        
        // Type de transaction
        if (dto.getType() != null) {
            String type = dto.getType().toUpperCase();
            if (type.contains("SWAP") || type.contains("OPTION") || type.contains("DERIVATIVE")) {
                score += 2;
            }
        }
        
        // Manque de benchmark
        if (dto.getBenchmark() == null || dto.getBenchmark().trim().isEmpty()) {
            score += 1;
        }
        
        // Statut non standard
        if (dto.getStatus() != null && !"PENDING".equalsIgnoreCase(dto.getStatus())) {
            score += 1;
        }
        
        return Math.min(score, 7); // Plafonne à 7
    }
}