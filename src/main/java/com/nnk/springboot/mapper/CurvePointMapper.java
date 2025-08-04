package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.dto.CurvePointDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre CurvePoint et CurvePointDTO
 * Respecte le principe de responsabilité unique (SRP)
 */
@Component
public class CurvePointMapper implements IEntityMapper<CurvePoint, CurvePointDTO> {

    /**
     * Convertit une entité CurvePoint en DTO
     * @param curvePoint l'entité à convertir
     * @return le DTO correspondant ou null si l'entité est null
     */
    public CurvePointDTO toDTO(CurvePoint curvePoint) {
        if (curvePoint == null) {
            return null;
        }

        CurvePointDTO dto = new CurvePointDTO();
        dto.setId(curvePoint.getId());
        dto.setCurveId(curvePoint.getCurveId());
        dto.setTerm(curvePoint.getTerm());
        dto.setValue(curvePoint.getValue());

        return dto;
    }

    /**
     * Convertit un DTO en entité CurvePoint
     * @param dto le DTO à convertir
     * @return l'entité correspondante ou null si le DTO est null
     */
    public CurvePoint toEntity(CurvePointDTO dto) {
        if (dto == null) {
            return null;
        }

        CurvePoint curvePoint = new CurvePoint();
        curvePoint.setId(dto.getId());
        curvePoint.setCurveId(dto.getCurveId());
        curvePoint.setTerm(dto.getTerm());
        curvePoint.setValue(dto.getValue());

        return curvePoint;
    }

    /**
     * Met à jour une entité existante avec les données du DTO
     * @param existingCurvePoint l'entité existante à mettre à jour
     * @param dto le DTO contenant les nouvelles données
     */
    public void updateEntityFromDTO(CurvePoint existingCurvePoint, CurvePointDTO dto) {
        if (existingCurvePoint == null || dto == null) {
            return;
        }

        existingCurvePoint.setCurveId(dto.getCurveId());
        existingCurvePoint.setTerm(dto.getTerm());
        existingCurvePoint.setValue(dto.getValue());
    }
}
