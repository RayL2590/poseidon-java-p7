package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.dto.RatingDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper Spring pour la conversion entre entités Rating et DTOs RatingDTO.
 * 
 * <p>Cette classe composant Spring implémente le pattern Mapper pour assurer la conversion
 * bidirectionnelle entre la couche de persistance (entités JPA) et la couche de
 * présentation (DTOs) spécifiquement pour les notations de crédit financières.</p>
 * 
 * <p>Spécificités des notations de crédit :</p>
 * <ul>
 *   <li><strong>Données critiques</strong> : Précision requise pour l'analyse de risque</li>
 *   <li><strong>Standards internationaux</strong> : Respect des échelles officielles</li>
 *   <li><strong>Cohérence inter-agences</strong> : Validation des équivalences</li>
 *   <li><strong>Classification ordonnée</strong> : Gestion des hiérarchies de qualité</li>
 * </ul>
 * 
 * <p>Architecture et principes SOLID :</p>
 * <ul>
 *   <li><strong>SRP</strong> : Responsabilité unique de conversion Rating</li>
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
 * // Affichage des notations
 * &#64;Autowired
 * private RatingMapper mapper;
 * 
 * // Liste pour interface utilisateur
 * List&lt;Rating&gt; ratings = ratingService.findAll();
 * List&lt;RatingDTO&gt; dtos = ratings.stream()
 *     .map(mapper::toDTO)
 *     .collect(Collectors.toList());
 * 
 * // Mise à jour d'une notation
 * Rating existing = ratingService.findById(id);
 * mapper.updateEntityFromDTO(existing, modifiedDTO);
 * ratingService.save(existing);
 * </pre>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see com.nnk.springboot.domain.Rating
 * @see com.nnk.springboot.dto.RatingDTO
 * @see IEntityMapper
 */
@Component
public class RatingMapper implements IEntityMapper<Rating, RatingDTO> {

    /**
     * Convertit une entité Rating en DTO RatingDTO.
     * 
     * <p>Cette méthode transforme une entité JPA de notation de crédit en objet
     * de transfert de données pour l'affichage et la manipulation dans les vues.
     * Elle préserve l'intégrité des données de notation critiques pour l'analyse
     * de risque financier.</p>
     * 
     * <p>Données mappées :</p>
     * <ul>
     *   <li><strong>id</strong> : Identifiant unique de la notation</li>
     *   <li><strong>moodysRating</strong> : Notation selon l'échelle Moody's</li>
     *   <li><strong>sandPRating</strong> : Notation selon l'échelle S&P</li>
     *   <li><strong>fitchRating</strong> : Notation selon l'échelle Fitch</li>
     *   <li><strong>orderNumber</strong> : Numéro d'ordre pour classification</li>
     * </ul>
     * 
     * <p>Cas d'usage financiers :</p>
     * <ul>
     *   <li><strong>Affichage de grilles</strong> : Tableaux de notation par émetteur</li>
     *   <li><strong>Export de données</strong> : Rapports et fichiers de référence</li>
     *   <li><strong>API REST</strong> : Sérialisation JSON pour clients externes</li>
     *   <li><strong>Analyse comparative</strong> : Comparaison inter-agences</li>
     * </ul>
     * 
     * @param rating L'entité Rating à convertir (peut être null)
     * @return Le DTO RatingDTO correspondant, ou null si l'entité source est null
     * @throws aucune exception n'est levée (gestion défensive)
     */
    public RatingDTO toDTO(Rating rating) {
        if (rating == null) {
            return null;
        }

        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setMoodysRating(rating.getMoodysRating());
        dto.setSandPRating(rating.getSandPRating());
        dto.setFitchRating(rating.getFitchRating());
        dto.setOrderNumber(rating.getOrderNumber());

        return dto;
    }

    /**
     * Convertit un DTO RatingDTO en entité Rating.
     * 
     * <p>Cette méthode transforme un objet de transfert de données (provenant
     * généralement d'un formulaire web ou d'une API) en entité JPA prête pour
     * la persistance. Elle assure la cohérence des données de notation critiques
     * pour la gestion des risques financiers.</p>
     * 
     * <p>Validation et intégrité :</p>
     * <ul>
     *   <li><strong>Standards de notation</strong> : Respect des échelles officielles</li>
     *   <li><strong>Cohérence inter-agences</strong> : Vérification des équivalences</li>
     *   <li><strong>Classification ordonnée</strong> : Numéros d'ordre valides</li>
     *   <li><strong>Validation déléguée</strong> : Bean Validation effectuée côté DTO</li>
     * </ul>
     * 
     * <p>Cas d'usage financiers :</p>
     * <ul>
     *   <li><strong>Création de notations</strong> : Nouvelles évaluations depuis interface</li>
     *   <li><strong>Import de données</strong> : Chargement depuis agences de notation</li>
     *   <li><strong>Mise à jour périodique</strong> : Révisions de notations existantes</li>
     *   <li><strong>API REST</strong> : Réception de données JSON externes</li>
     * </ul>
     * 
     * <p>Note : Les métadonnées d'audit ne sont pas mappées car elles sont gérées
     * automatiquement par la logique métier et les listeners JPA.</p>
     * 
     * @param dto Le DTO RatingDTO à convertir (peut être null)
     * @return L'entité Rating correspondante, ou null si le DTO source est null
     * @throws aucune exception n'est levée (gestion défensive)
     */
    public Rating toEntity(RatingDTO dto) {
        if (dto == null) {
            return null;
        }

        Rating rating = new Rating();
        rating.setId(dto.getId());
        rating.setMoodysRating(dto.getMoodysRating());
        rating.setSandPRating(dto.getSandPRating());
        rating.setFitchRating(dto.getFitchRating());
        rating.setOrderNumber(dto.getOrderNumber());

        return rating;
    }

    /**
     * Met à jour une entité Rating existante avec les données d'un DTO.
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
     *   <li><strong>moodysRating</strong> : Nouvelle notation Moody's</li>
     *   <li><strong>sandPRating</strong> : Nouvelle notation S&P</li>
     *   <li><strong>fitchRating</strong> : Nouvelle notation Fitch</li>
     *   <li><strong>orderNumber</strong> : Nouveau numéro d'ordre</li>
     * </ul>
     * 
     * <p>Champs préservés :</p>
     * <ul>
     *   <li><strong>id</strong> : Identifiant technique préservé</li>
     *   <li><strong>Métadonnées d'audit</strong> : Horodatages de création maintenus</li>
     * </ul>
     * 
     * <p>Usage typique :</p>
     * <pre>
     * // Modification d'une notation existante
     * Rating existing = ratingService.findById(ratingId);
     * RatingDTO modified = getModifiedDataFromForm();
     * 
     * ratingMapper.updateEntityFromDTO(existing, modified);
     * ratingService.save(existing); // Hibernate détecte les changements
     * </pre>
     * 
     * <p>Considérations métier :</p>
     * <ul>
     *   <li><strong>Révision de notation</strong> : Mise à jour suite à réanalyse</li>
     *   <li><strong>Correction d'erreurs</strong> : Rectification de saisies erronées</li>
     *   <li><strong>Harmonisation</strong> : Ajustement pour cohérence inter-agences</li>
     *   <li><strong>Reclassification</strong> : Modification du numéro d'ordre</li>
     * </ul>
     * 
     * @param existingRating L'entité existante à mettre à jour (ne doit pas être null en usage normal)
     * @param dto Le DTO contenant les nouvelles données (ne doit pas être null en usage normal)
     * @throws aucune exception n'est levée - protection défensive contre les nulls
     */
    public void updateEntityFromDTO(Rating existingRating, RatingDTO dto) {
        if (existingRating == null || dto == null) {
            return;
        }

        existingRating.setMoodysRating(dto.getMoodysRating());
        existingRating.setSandPRating(dto.getSandPRating());
        existingRating.setFitchRating(dto.getFitchRating());
        existingRating.setOrderNumber(dto.getOrderNumber());
    }

    /**
     * Crée un DTO avec les notations par défaut pour un niveau de qualité donné.
     * 
     * <p>Méthode utilitaire pour générer rapidement des DTOs avec des notations
     * équivalentes cohérentes entre agences, basées sur les tables de correspondance
     * standard du marché financier.</p>
     * 
     * <p>Niveaux supportés :</p>
     * <ul>
     *   <li><strong>"PRIME"</strong> : AAA/Aaa - Qualité maximale</li>
     *   <li><strong>"HIGH_INVESTMENT"</strong> : AA/Aa2 - Très haute qualité</li>
     *   <li><strong>"INVESTMENT"</strong> : A/A2 - Qualité investment grade</li>
     *   <li><strong>"LOWER_INVESTMENT"</strong> : BBB/Baa2 - Investment grade minimum</li>
     *   <li><strong>"SPECULATIVE"</strong> : BB/Ba2 - Spéculatif</li>
     *   <li><strong>"HIGH_YIELD"</strong> : B/B2 - Haut rendement</li>
     * </ul>
     * 
     * @param qualityLevel Le niveau de qualité souhaité
     * @return DTO avec notations équivalentes cohérentes
     */
    public RatingDTO createDefaultForQuality(String qualityLevel) {
        if (qualityLevel == null) {
            return new RatingDTO();
        }
        
        switch (qualityLevel.toUpperCase()) {
            case "PRIME":
                return new RatingDTO("Aaa", "AAA", "AAA", 1);
            case "HIGH_INVESTMENT":
                return new RatingDTO("Aa2", "AA", "AA", 4);
            case "INVESTMENT":
                return new RatingDTO("A2", "A", "A", 7);
            case "LOWER_INVESTMENT":
                return new RatingDTO("Baa2", "BBB", "BBB", 10);
            case "SPECULATIVE":
                return new RatingDTO("Ba2", "BB", "BB", 13);
            case "HIGH_YIELD":
                return new RatingDTO("B2", "B", "B", 16);
            default:
                return new RatingDTO();
        }
    }
}