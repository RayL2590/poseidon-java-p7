package com.nnk.springboot.services;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des points de courbe financière
 * Respecte les principes SOLID :
 * - SRP : Une seule responsabilité (gestion des CurvePoint)
 * - OCP : Ouvert à l'extension, fermé à la modification
 * - LSP : Respecte le contrat défini par ICurvePointService
 * - ISP : Interface spécialisée pour CurvePoint
 * - DIP : Dépend d'abstractions (CurvePointRepository)
 */
@Service
@Transactional
public class CurvePointService implements ICurvePointService {

    @Autowired
    private CurvePointRepository curvePointRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CurvePoint> findAll() {
        return curvePointRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CurvePoint> findById(Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return curvePointRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return curvePointRepository.existsById(id);
    }

    @Override
    public CurvePoint save(CurvePoint curvePoint) {
        validateCurvePoint(curvePoint);
        
        // Ajout automatique des dates
        if (curvePoint.getId() == null) {
            curvePoint.setCreationDate(LocalDateTime.now());
            // Pour un nouveau point, asOfDate par défaut = maintenant si non spécifié
            if (curvePoint.getAsOfDate() == null) {
                curvePoint.setAsOfDate(LocalDateTime.now());
            }
        }
        
        return curvePointRepository.save(curvePoint);
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID for deletion");
        }
        
        if (!curvePointRepository.existsById(id)) {
            throw new IllegalArgumentException("CurvePoint not found with id: " + id);
        }
        
        curvePointRepository.deleteById(id);
    }

    /**
     * Valide les données d'un point de courbe
     * @param curvePoint le point de courbe à valider
     * @throws IllegalArgumentException si les données sont invalides
     */
    private void validateCurvePoint(CurvePoint curvePoint) {
        if (curvePoint == null) {
            throw new IllegalArgumentException("CurvePoint cannot be null");
        }
        
        if (curvePoint.getCurveId() == null || curvePoint.getCurveId() <= 0) {
            throw new IllegalArgumentException("Curve ID is required and must be positive");
        }
        
        if (curvePoint.getTerm() == null || curvePoint.getTerm() < 0) {
            throw new IllegalArgumentException("Term is required and must be positive or zero");
        }
        
        if (curvePoint.getValue() == null) {
            throw new IllegalArgumentException("Value is required");
        }
    }
}