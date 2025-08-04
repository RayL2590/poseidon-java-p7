package com.nnk.springboot.services;

import com.nnk.springboot.domain.BidList;
import java.util.List;
import java.util.Optional;

/**
 * Interface pour le service de gestion des BidList
 * Respecte le principe ISP (Interface Segregation Principle)
 * Contrat clair pour les opérations CRUD
 */
public interface IBidListService {
    
    /**
     * Récupère toutes les BidList
     * @return Liste de toutes les BidList
     */
    List<BidList> findAll();
    
    /**
     * Récupère une BidList par son ID
     * @param id l'ID de la BidList
     * @return Optional contenant la BidList ou vide si non trouvée
     */
    Optional<BidList> findById(Integer id);
    
    /**
     * Vérifie l'existence d'une BidList par son ID
     * @param id l'ID à vérifier
     * @return true si existe, false sinon
     */
    boolean existsById(Integer id);
    
    /**
     * Sauvegarde une BidList (création ou mise à jour)
     * @param bidList la BidList à sauvegarder
     * @return la BidList sauvegardée
     * @throws IllegalArgumentException si les données sont invalides
     */
    BidList save(BidList bidList);
    
    /**
     * Supprime une BidList par son ID
     * @param id l'ID de la BidList à supprimer
     * @throws IllegalArgumentException si l'ID est invalide ou n'existe pas
     */
    void deleteById(Integer id);
}