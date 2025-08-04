package com.nnk.springboot.mapper;

/**
 * Interface générique pour les mappers Entity <-> DTO
 * Respecte le principe ISP (Interface Segregation)
 * 
 * @param <E> Type de l'Entity
 * @param <D> Type du DTO
 */
public interface IEntityMapper<E, D> {
    
    /**
     * Convertit une entité en DTO
     * @param entity l'entité à convertir
     * @return le DTO correspondant ou null si l'entité est null
     */
    D toDTO(E entity);
    
    /**
     * Convertit un DTO en entité
     * @param dto le DTO à convertir
     * @return l'entité correspondante ou null si le DTO est null
     */
    E toEntity(D dto);
}