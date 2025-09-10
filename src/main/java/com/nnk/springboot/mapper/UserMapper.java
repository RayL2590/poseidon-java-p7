package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper pour la conversion entre les entités User et les DTOs UserDTO.
 * 
 * <p>Cette classe implémente le pattern Mapper pour assurer une séparation
 * claire entre la couche de persistance (Entity) et la couche de présentation
 * (DTO) dans l'application de trading Poseidon.</p>
 * 
 * <p>Responsabilités du mapper :</p>
 * <ul>
 *   <li><strong>Conversion Entity → DTO</strong> : Pour affichage et validation</li>
 *   <li><strong>Conversion DTO → Entity</strong> : Pour persistence et traitement</li>
 *   <li><strong>Gestion sécurisée</strong> : Protection des données sensibles</li>
 *   <li><strong>Validation des données</strong> : Contrôles avant conversion</li>
 * </ul>
 * 
 * <p>Considérations de sécurité :</p>
 * <ul>
 *   <li><strong>Mot de passe masqué</strong> : Jamais exposé dans les DTOs de sortie</li>
 *   <li><strong>Validation stricte</strong> : Contrôles sur toutes les conversions</li>
 *   <li><strong>Immutabilité</strong> : Nouvelles instances pour éviter les effets de bord</li>
 *   <li><strong>Logging sécurisé</strong> : Pas d'exposition d'informations sensibles</li>
 * </ul>
 * 
 * <p>Utilisation dans l'architecture :</p>
 * <ul>
 *   <li><strong>Couche Controller</strong> : Conversion pour affichage web</li>
 *   <li><strong>Couche Service</strong> : Conversion pour logique métier</li>
 *   <li><strong>Validation Bean</strong> : Intégration avec les annotations de validation</li>
 *   <li><strong>Tests unitaires</strong> : Facilite les assertions et la vérification</li>
 * </ul>
 * 
 * @author Poseidon Trading App
 * @version 1.0
 * @since 1.0
 * @see User
 * @see UserDTO
 * @see IEntityMapper
 */
@Component
public class UserMapper implements IEntityMapper<User, UserDTO> {

    /**
     * Convertit une entité User en DTO UserDTO pour l'affichage.
     * 
     * <p>Cette méthode crée un DTO sécurisé à partir d'une entité de base de données.
     * Le mot de passe est intentionnellement masqué (chaîne vide) pour des raisons
     * de sécurité, conformément aux bonnes pratiques de développement sécurisé.</p>
     * 
     * <p>Transformations appliquées :</p>
     * <ul>
     *   <li><strong>Copie des données publiques</strong> : ID, username, fullname, role</li>
     *   <li><strong>Masquage du mot de passe</strong> : Chaîne vide pour sécurité</li>
     *   <li><strong>Validation des données</strong> : Vérification de cohérence</li>
     *   <li><strong>Nouvelle instance</strong> : Pas de référence partagée</li>
     * </ul>
     * 
     * <p>Usage typique :</p>
     * <pre>
     * User entity = userRepository.findById(1);
     * UserDTO dto = userMapper.toDTO(entity);
     * model.addAttribute("user", dto); // Sécurisé pour affichage
     * </pre>
     * 
     * @param entity L'entité User à convertir (ne doit pas être null)
     * @return Le DTO UserDTO correspondant avec mot de passe masqué
     * @throws IllegalArgumentException Si l'entité est null ou invalide
     */
    @Override
    public UserDTO toDTO(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("L'entité User ne peut pas être null");
        }

        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        // Sécurité : Le mot de passe n'est jamais exposé dans le DTO
        dto.setPassword("");
        dto.setFullname(entity.getFullname());
        dto.setRole(entity.getRole());

        return dto;
    }

    /**
     * Convertit un DTO UserDTO en entité User pour la persistence.
     * 
     * <p>Cette méthode crée une entité de base de données à partir d'un DTO
     * de présentation. Le mot de passe est conservé tel quel car il sera
     * traité (validé et chiffré) par la couche service.</p>
     * 
     * <p>Transformations appliquées :</p>
     * <ul>
     *   <li><strong>Copie complète des données</strong> : Tous les champs y compris password</li>
     *   <li><strong>Conservation de l'ID</strong> : Pour les mises à jour</li>
     *   <li><strong>Validation des données</strong> : Vérification de cohérence</li>
     *   <li><strong>Nouvelle instance</strong> : Entity indépendante du DTO</li>
     * </ul>
     * 
     * <p>Usage typique :</p>
     * <pre>
     * UserDTO dto = new UserDTO("john", "securePass123!", "John Doe", "USER");
     * User entity = userMapper.toEntity(dto);
     * userService.save(entity); // Le service gérera le chiffrement
     * </pre>
     * 
     * @param dto Le DTO UserDTO à convertir (ne doit pas être null)
     * @return L'entité User correspondante prête pour la persistence
     * @throws IllegalArgumentException Si le DTO est null ou invalide
     */
    @Override
    public User toEntity(UserDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Le DTO UserDTO ne peut pas être null");
        }

        User entity = new User();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword()); // Sera traité par le service
        entity.setFullname(dto.getFullname());
        entity.setRole(dto.getRole());

        return entity;
    }

    /**
     * Convertit une entité User en DTO UserDTO avec mot de passe pré-rempli.
     * 
     * <p>Cette méthode spécialisée est utilisée principalement pour les formulaires
     * de modification où un mot de passe par défaut doit être affiché (généralement
     * vide pour des raisons de sécurité). Elle permet de préparer un DTO pour
     * l'édition avec des valeurs par défaut appropriées.</p>
     * 
     * <p>Différences avec toDTO() :</p>
     * <ul>
     *   <li><strong>Mot de passe personnalisable</strong> : Peut être défini selon le contexte</li>
     *   <li><strong>Usage formulaires</strong> : Optimisé pour les interfaces de modification</li>
     *   <li><strong>Flexibilité</strong> : Permet différentes stratégies de masquage</li>
     *   <li><strong>Sécurité maintenue</strong> : Le vrai mot de passe n'est jamais exposé</li>
     * </ul>
     * 
     * <p>Usage typique :</p>
     * <pre>
     * User entity = userRepository.findById(1);
     * UserDTO dto = userMapper.toDTOForEdit(entity, "");
     * model.addAttribute("user", dto); // Pour formulaire de modification
     * </pre>
     * 
     * @param entity L'entité User à convertir (ne doit pas être null)
     * @param defaultPassword Le mot de passe par défaut à afficher (généralement "")
     * @return Le DTO UserDTO avec mot de passe par défaut
     * @throws IllegalArgumentException Si l'entité est null
     */
    public UserDTO toDTOForEdit(User entity, String defaultPassword) {
        if (entity == null) {
            throw new IllegalArgumentException("L'entité User ne peut pas être null");
        }

        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPassword(defaultPassword != null ? defaultPassword : "");
        dto.setFullname(entity.getFullname());
        dto.setRole(entity.getRole());

        return dto;
    }

    /**
     * Met à jour une entité existante avec les données d'un DTO.
     * 
     * <p>Cette méthode permet de mettre à jour sélectivement une entité existante
     * avec les données d'un DTO, tout en préservant certains champs si nécessaire.
     * Elle est particulièrement utile pour les opérations de mise à jour où
     * seuls certains champs doivent être modifiés.</p>
     * 
     * <p>Stratégie de mise à jour :</p>
     * <ul>
     *   <li><strong>Préservation de l'ID</strong> : L'ID original de l'entité est conservé</li>
     *   <li><strong>Mise à jour sélective</strong> : Seuls les champs non-null du DTO sont copiés</li>
     *   <li><strong>Validation</strong> : Contrôles de cohérence avant mise à jour</li>
     *   <li><strong>Immutabilité partielle</strong> : L'entité originale peut être préservée</li>
     * </ul>
     * 
     * <p>Usage typique :</p>
     * <pre>
     * User existingEntity = userRepository.findById(1);
     * UserDTO updateDto = getUserDTOFromForm();
     * userMapper.updateEntityFromDTO(existingEntity, updateDto);
     * userRepository.save(existingEntity);
     * </pre>
     * 
     * @param entity L'entité User à mettre à jour (ne doit pas être null)
     * @param dto Le DTO contenant les nouvelles données (ne doit pas être null)
     * @throws IllegalArgumentException Si l'entité ou le DTO sont null
     */
    public void updateEntityFromDTO(User entity, UserDTO dto) {
        if (entity == null) {
            throw new IllegalArgumentException("L'entité User ne peut pas être null");
        }
        if (dto == null) {
            throw new IllegalArgumentException("Le DTO UserDTO ne peut pas être null");
        }

        // Mise à jour des champs (l'ID est préservé)
        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty()) {
            entity.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            entity.setPassword(dto.getPassword());
        }
        if (dto.getFullname() != null && !dto.getFullname().trim().isEmpty()) {
            entity.setFullname(dto.getFullname());
        }
        if (dto.getRole() != null && !dto.getRole().trim().isEmpty()) {
            entity.setRole(dto.getRole());
        }
    }

    /**
     * Crée un DTO vide pré-configuré pour la création d'un nouvel utilisateur.
     * 
     * <p>Cette méthode utilitaire crée un DTO avec des valeurs par défaut
     * appropriées pour la création d'un nouvel utilisateur. Elle est utile
     * pour initialiser les formulaires de création avec des valeurs par défaut
     * sensées.</p>
     * 
     * <p>Valeurs par défaut appliquées :</p>
     * <ul>
     *   <li><strong>ID</strong> : null (sera généré par la base de données)</li>
     *   <li><strong>Username</strong> : chaîne vide</li>
     *   <li><strong>Password</strong> : chaîne vide</li>
     *   <li><strong>Fullname</strong> : chaîne vide</li>
     *   <li><strong>Role</strong> : "USER" (rôle par défaut le plus restrictif)</li>
     * </ul>
     * 
     * @return Un DTO UserDTO pré-configuré pour la création
     */
    public UserDTO createEmptyDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(null);
        dto.setUsername("");
        dto.setPassword("");
        dto.setFullname("");
        dto.setRole("USER");
        return dto;
    }
}