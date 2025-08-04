package com.nnk.springboot.services;

import com.nnk.springboot.domain.CurvePoint;
import java.util.List;
import java.util.Optional;

/**
 * Interface pour le service de gestion des CurvePoint
 * Respecte le principe ISP (Interface Segregation Principle)
 */
public interface ICurvePointService {
    
    /**
     * Récupère tous les points de courbe
     * @return Liste de tous les points de courbe
     */
    List<CurvePoint> findAll();
    
    /**
     * Récupère un point de courbe par son ID
     * @param id l'ID du point de courbe
     * @return Optional contenant le point de courbe ou vide si non trouvé
     */
    Optional<CurvePoint> findById(Integer id);
    
    /**
     * Vérifie l'existence d'un point de courbe par son ID
     * @param id l'ID à vérifier
     * @return true si existe, false sinon
     */
    boolean existsById(Integer id);
    
    /**
     * Sauvegarde un point de courbe (création ou mise à jour)
     * @param curvePoint le point de courbe à sauvegarder
     * @return le point de courbe sauvegardé
     * @throws IllegalArgumentException si les données sont invalides
     */
    CurvePoint save(CurvePoint curvePoint);
    
    /**
     * Supprime un point de courbe par son ID
     * @param id l'ID du point de courbe à supprimer
     * @throws IllegalArgumentException si l'ID est invalide ou n'existe pas
     */
    void deleteById(Integer id);
}