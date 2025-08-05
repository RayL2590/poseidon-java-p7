package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.dto.CurvePointDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper Spring pour la conversion entre entités CurvePoint et DTOs CurvePointDTO.
 * 
 * <p>Cette classe composant Spring implémente le pattern Mapper pour assurer la conversion
 * bidirectionnelle entre la couche de persistance (entités JPA) et la couche de
 * présentation (DTOs) spécifiquement pour les points de courbes financières.</p>
 * 
 * <p>Spécificités des courbes financières :</p>
 * <ul>
 *   <li><strong>Données critiques</strong> : Précision requise pour les calculs quantitatifs</li>
 *   <li><strong>Intégrité référentielle</strong> : Validation des IDs de courbe</li>
 *   <li><strong>Cohérence temporelle</strong> : Gestion des termes et échéances</li>
 *   <li><strong>Performance</strong> : Optimisé pour le traitement de masse de points</li>
 * </ul>
 * 
 * <p>Architecture et principes SOLID :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Responsabilité unique de conversion CurvePoint</li>
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
 *   <li><strong>Injection Spring</strong> : Composant géré par le conteneur IoC</li>
 * </ul>
 * 
 * <p>Usage typique dans l'écosystème financier :</p>
 * <pre>
 * // Construction d'une courbe de taux
 * &#64;Autowired
 * private CurvePointMapper mapper;
 * 
 * // Affichage d'une courbe
 * List&lt;CurvePoint&gt; points = curveService.findByCurveId(1);
 * List&lt;CurvePointDTO&gt; dtos = points.stream()
 *     .map(mapper::toDTO)
 *     .collect(Collectors.toList());
 * 
 * // Mise à jour d'un point
 * CurvePoint existing = curveService.findById(id);
 * mapper.updateEntityFromDTO(existing, modifiedDTO);
 * curveService.save(existing);
 * </pre>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.CurvePoint
 * @see com.nnk.springboot.dto.CurvePointDTO
 * @see IEntityMapper
 */
@Component
public class CurvePointMapper implements IEntityMapper<CurvePoint, CurvePointDTO> {

    /**
     * Convertit une entité CurvePoint en DTO CurvePointDTO.
     * 
     * <p>Cette méthode transforme une entité JPA de point de courbe en objet
     * de transfert de données pour l'affichage et la manipulation dans les vues.
     * Elle préserve l'intégrité des données financières critiques.</p>
     * 
     * <p>Données mappées :</p>
     * <ul>
     *   <li><strong>id</strong> : Identifiant unique du point</li>
     *   <li><strong>curveId</strong> : Référence vers la courbe parent</li>
     *   <li><strong>term</strong> : Échéance en années (précision 4 décimales)</li>
     *   <li><strong>value</strong> : Valeur financière (taux, volatilité, etc.)</li>
     * </ul>
     * 
     * <p>Cas d'usage financiers :</p>
     * <ul>
     *   <li><strong>Affichage de courbes</strong> : Visualisation graphique</li>
     *   <li><strong>Export de données</strong> : Rapports et fichiers CSV/Excel</li>
     *   <li><strong>API REST</strong> : Sérialisation JSON pour clients externes</li>
     *   <li><strong>Validation utilisateur</strong> : Pré-remplissage de formulaires</li>
     * </ul>
     * 
     * @param curvePoint L'entité CurvePoint à convertir (peut être null)
     * @return Le DTO CurvePointDTO correspondant, ou null si l'entité source est null
     * @throws aucune exception n'est levée (gestion défensive)
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
     * Convertit un DTO CurvePointDTO en entité CurvePoint.
     * 
     * <p>Cette méthode transforme un objet de transfert de données (provenant
     * généralement d'un formulaire web ou d'une API) en entité JPA prête pour
     * la persistance. Elle assure la cohérence des données financières critiques.</p>
     * 
     * <p>Validation et intégrité :</p>
     * <ul>
     *   <li><strong>Précision numérique</strong> : Préservation des 4 décimales</li>
     *   <li><strong>Cohérence référentielle</strong> : CurveId valide requis</li>
     *   <li><strong>Intégrité temporelle</strong> : Term positif ou nul</li>
     *   <li><strong>Validation déléguée</strong> : Bean Validation effectuée côté DTO</li>
     * </ul>
     * 
     * <p>Cas d'usage financiers :</p>
     * <ul>
     *   <li><strong>Création de points</strong> : Nouveaux points depuis interface</li>
     *   <li><strong>Import de courbes</strong> : Chargement de données de marché</li>
     *   <li><strong>Calibration de modèles</strong> : Points calculés algorithmiquement</li>
     *   <li><strong>API REST</strong> : Réception de données JSON</li>
     * </ul>
     * 
     * <p>Note : Les timestamps (asOfDate, creationDate) ne sont pas mappés car
     * ils sont gérés automatiquement par la logique métier et les listeners JPA.</p>
     * 
     * @param dto Le DTO CurvePointDTO à convertir (peut être null)
     * @return L'entité CurvePoint correspondante, ou null si le DTO source est null
     * @throws aucune exception n'est levée (gestion défensive)
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
     * Met à jour une entité CurvePoint existante avec les données d'un DTO.
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
     *   <li><strong>curveId</strong> : Peut changer de courbe (avec validation métier)</li>
     *   <li><strong>term</strong> : Modification de l'échéance</li>
     *   <li><strong>value</strong> : Nouvelle valeur financière</li>
     * </ul>
     * 
     * <p>Champs préservés :</p>
     * <ul>
     *   <li><strong>id</strong> : Identifiant technique préservé</li>
     *   <li><strong>asOfDate</strong> : Date de référence maintenue</li>
     *   <li><strong>creationDate</strong> : Horodatage de création original</li>
     * </ul>
     * 
     * <p>Usage typique :</p>
     * <pre>
     * // Modification d'un point existant
     * CurvePoint existing = curveService.findById(pointId);
     * CurvePointDTO modified = getModifiedDataFromForm();
     * 
     * curvePointMapper.updateEntityFromDTO(existing, modified);
     * curveService.save(existing); // Hibernate détecte les changements
     * </pre>
     * 
     * @param existingCurvePoint L'entité existante à mettre à jour (ne doit pas être null en usage normal)
     * @param dto Le DTO contenant les nouvelles données (ne doit pas être null en usage normal)
     * @throws aucune exception n'est levée - protection défensive contre les nulls
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
