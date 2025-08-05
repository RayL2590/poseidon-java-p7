package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidListDTO;

/**
 * Mapper statique pour la conversion entre entités BidList et DTOs BidListDTO.
 * 
 * <p>Cette classe utilitaire implémente le pattern Mapper pour assurer la conversion
 * bidirectionnelle entre la couche de persistance (entités JPA) et la couche de
 * présentation (DTOs) dans l'architecture de l'application de trading Poseidon.</p>
 * 
 * <p>Responsabilités du mapper :</p>
 * <ul>
 *   <li><strong>Conversion Entity → DTO</strong> : Pour l'affichage dans les vues</li>
 *   <li><strong>Conversion DTO → Entity</strong> : Pour la persistance en base</li>
 *   <li><strong>Gestion des valeurs null</strong> : Protection contre les NPE</li>
 *   <li><strong>Mapping complet</strong> : Tous les champs sont mappés</li>
 * </ul>
 * 
 * <p>Architecture et pattern :</p>
 * <ul>
 *   <li><strong>Stateless</strong> : Méthodes statiques sans état</li>
 *   <li><strong>Thread-safe</strong> : Utilisation sécurisée en environnement concurrent</li>
 *   <li><strong>Séparation des couches</strong> : Isole la logique de conversion</li>
 *   <li><strong>Réutilisabilité</strong> : Utilisable dans tous les services et contrôleurs</li>
 * </ul>
 * 
 * <p>Utilisation typique :</p>
 * <pre>
 * // Dans un contrôleur - Affichage
 * BidList entity = bidListService.findById(id);
 * BidListDTO dto = BidListMapper.toDTO(entity);
 * model.addAttribute("bidList", dto);
 * 
 * // Dans un contrôleur - Sauvegarde
 * BidList entity = BidListMapper.toEntity(bidListDTO);
 * bidListService.save(entity);
 * </pre>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.BidList
 * @see com.nnk.springboot.dto.BidListDTO
 */
public class BidListMapper {

    /**
     * Convertit une entité BidList en DTO BidListDTO.
     * 
     * <p>Cette méthode transforme une entité JPA en objet de transfert de données
     * pour l'affichage dans les vues. Elle copie tous les champs de l'entité
     * vers le DTO correspondant, y compris les champs d'audit et de métadonnées.</p>
     * 
     * <p>Cas d'usage principaux :</p>
     * <ul>
     *   <li><strong>Affichage en lecture seule</strong> : Listes, détails</li>
     *   <li><strong>Pré-remplissage de formulaires</strong> : Modification</li>
     *   <li><strong>Réponses API</strong> : Sérialisation JSON</li>
     *   <li><strong>Export de données</strong> : Rapports, fichiers</li>
     * </ul>
     * 
     * <p>Champs mappés :</p>
     * <ul>
     *   <li>Identifiant et données core : bidListId, account, type</li>
     *   <li>Données financières : bidQuantity, askQuantity, bid, ask</li>
     *   <li>Métadonnées trading : benchmark, security, status, trader, book</li>
     *   <li>Informations contextuelles : commentary, dealName, dealType, side</li>
     *   <li>Données d'audit : creationName, revisionName, sourceListId</li>
     * </ul>
     * 
     * @param entity L'entité BidList source à convertir (peut être null)
     * @return Le DTO BidListDTO correspondant, ou null si l'entité source est null
     * @throws aucune exception n'est levée (gestion defensive du null)
     */
    public static BidListDTO toDTO(BidList entity) {
        if (entity == null) return null;
        BidListDTO dto = new BidListDTO();
        dto.setBidListId(entity.getBidListId());
        dto.setAccount(entity.getAccount());
        dto.setType(entity.getType());
        dto.setBidQuantity(entity.getBidQuantity());
        dto.setAskQuantity(entity.getAskQuantity());
        dto.setBid(entity.getBid());
        dto.setAsk(entity.getAsk());
        dto.setBenchmark(entity.getBenchmark());
        dto.setCommentary(entity.getCommentary());
        dto.setSecurity(entity.getSecurity());
        dto.setStatus(entity.getStatus());
        dto.setTrader(entity.getTrader());
        dto.setBook(entity.getBook());
        dto.setCreationName(entity.getCreationName());
        dto.setRevisionName(entity.getRevisionName());
        dto.setDealName(entity.getDealName());
        dto.setDealType(entity.getDealType());
        dto.setSourceListId(entity.getSourceListId());
        dto.setSide(entity.getSide());
        return dto;
    }

    /**
     * Convertit un DTO BidListDTO en entité BidList.
     * 
     * <p>Cette méthode transforme un objet de transfert de données (généralement
     * provenant d'un formulaire web) en entité JPA prête pour la persistance.
     * Elle effectue un mapping complet de tous les champs du DTO vers l'entité.</p>
     * 
     * <p>Cas d'usage principaux :</p>
     * <ul>
     *   <li><strong>Création d'entités</strong> : Nouvelles offres depuis formulaires</li>
     *   <li><strong>Mise à jour d'entités</strong> : Modifications utilisateur</li>
     *   <li><strong>Import de données</strong> : Chargement en masse</li>
     *   <li><strong>API REST</strong> : Désérialisation JSON vers entité</li>
     * </ul>
     * 
     * <p>Comportement spécifique :</p>
     * <ul>
     *   <li><strong>Tous les champs mappés</strong> : Aucune donnée perdue</li>
     *   <li><strong>ID préservé</strong> : Pour les opérations de mise à jour</li>
     *   <li><strong>Validation déléguée</strong> : Bean Validation se fait côté DTO</li>
     *   <li><strong>Timestamps exclus</strong> : Gérés automatiquement par JPA</li>
     * </ul>
     * 
     * <p>Note importante : Cette méthode ne gère pas les timestamps automatiques
     * (creationDate, revisionDate, bidListDate) qui sont typiquement gérés par
     * des listeners JPA ou des triggers de base de données.</p>
     * 
     * @param dto Le DTO BidListDTO source à convertir (peut être null)
     * @return L'entité BidList correspondante, ou null si le DTO source est null
     * @throws aucune exception n'est levée (gestion defensive du null)
     */
    public static BidList toEntity(BidListDTO dto) {
        if (dto == null) return null;
        BidList entity = new BidList();
        entity.setBidListId(dto.getBidListId());
        entity.setAccount(dto.getAccount());
        entity.setType(dto.getType());
        entity.setBidQuantity(dto.getBidQuantity());
        entity.setAskQuantity(dto.getAskQuantity());
        entity.setBid(dto.getBid());
        entity.setAsk(dto.getAsk());
        entity.setBenchmark(dto.getBenchmark());
        entity.setCommentary(dto.getCommentary());
        entity.setSecurity(dto.getSecurity());
        entity.setStatus(dto.getStatus());
        entity.setTrader(dto.getTrader());
        entity.setBook(dto.getBook());
        entity.setCreationName(dto.getCreationName());
        entity.setRevisionName(dto.getRevisionName());
        entity.setDealName(dto.getDealName());
        entity.setDealType(dto.getDealType());
        entity.setSourceListId(dto.getSourceListId());
        entity.setSide(dto.getSide());
        return entity;
    }
}