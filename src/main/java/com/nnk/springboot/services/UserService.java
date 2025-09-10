package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.mapper.UserMapper;
import com.nnk.springboot.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for User management operations.
 */
@Service
@Transactional
public class UserService implements IUserService {

    /** Logger pour la traçabilité des opérations utilisateur */
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /** Repository pour l'accès aux données des utilisateurs */
    @Autowired
    private UserRepository userRepository;

    /** Encodeur de mots de passe injecté par Spring Security */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Mapper pour la conversion Entity/DTO */
    @Autowired
    private UserMapper userMapper;

    /**
     * Finds all users.
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        logger.debug("Récupération de tous les utilisateurs");
        List<User> users = userRepository.findAll();
        logger.info("Récupération de {} utilisateurs", users.size());
        return users;
    }

    /**
     * Finds a user by ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Integer id) {
        logger.debug("Recherche utilisateur avec ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.debug("Utilisateur trouvé avec ID: {}", id);
        } else {
            logger.warn("Aucun utilisateur trouvé avec ID: {}", id);
        }
        return user;
    }

    /**
     * Finds a user by username.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        logger.debug("Recherche utilisateur avec username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            logger.debug("Utilisateur trouvé avec username: {}", username);
        } else {
            logger.warn("Aucun utilisateur trouvé avec username: {}", username);
        }
        return user;
    }

    /**
     * Creates a new user with validation and password encryption.
     */
    @Override
    public User save(User user) {
        logger.info("Tentative de création d'utilisateur: {}", user != null ? user.getUsername() : "null");
        
        // Validation des données d'entrée
        validateUserForCreation(user);
        
        // Vérification d'unicité du nom d'utilisateur
        if (!isUsernameAvailable(user.getUsername())) {
            logger.warn("Tentative de création avec username existant: {}", user.getUsername());
            throw new IllegalArgumentException("Le nom d'utilisateur '" + user.getUsername() + "' existe déjà");
        }
        
        // Chiffrement sécurisé du mot de passe
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        logger.debug("Mot de passe chiffré pour utilisateur: {}", user.getUsername());
        
        // Sauvegarde transactionnelle
        User savedUser = userRepository.save(user);
        logger.info("Utilisateur créé avec succès: {} (ID: {})", savedUser.getUsername(), savedUser.getId());
        
        return savedUser;
    }

    /**
     * Met à jour un utilisateur existant avec gestion sécurisée.
     * 
     * <p>Cette méthode gère la mise à jour complète d'un utilisateur existant,
     * incluant la validation de l'existence, le re-chiffrement du mot de passe
     * si nécessaire, et la vérification d'unicité du nom d'utilisateur.</p>
     * 
     * <p>Processus de mise à jour :</p>
     * <ol>
     *   <li><strong>Validation existence</strong> : Vérification utilisateur existant</li>
     *   <li><strong>Contrôle d'unicité</strong> : Username unique (excluant l'utilisateur actuel)</li>
     *   <li><strong>Gestion mot de passe</strong> : Re-chiffrement si modifié</li>
     *   <li><strong>Mise à jour transactionnelle</strong> : Persistence avec rollback</li>
     *   <li><strong>Audit des changements</strong> : Logging des modifications</li>
     * </ol>
     * 
     * @param id L'identifiant de l'utilisateur à mettre à jour
     * @param user Les nouvelles données de l'utilisateur
     * @return L'utilisateur mis à jour
     * @throws IllegalArgumentException Si l'utilisateur n'existe pas ou données invalides
     */
    @Override
    public User update(Integer id, User user) {
        logger.info("Tentative de mise à jour utilisateur ID: {}", id);
        
        // Validation de l'existence de l'utilisateur
        User existingUser = findById(id)
            .orElseThrow(() -> {
                logger.error("Tentative de mise à jour d'un utilisateur inexistant: {}", id);
                return new IllegalArgumentException("Utilisateur avec ID " + id + " non trouvé");
            });
        
        // Validation des données de mise à jour
        validateUserForUpdate(user);
        
        // Vérification d'unicité du nom d'utilisateur (excluant l'utilisateur actuel)
        if (!isUsernameAvailable(user.getUsername(), id)) {
            logger.warn("Tentative de mise à jour avec username existant: {} pour ID: {}", user.getUsername(), id);
            throw new IllegalArgumentException("Le nom d'utilisateur '" + user.getUsername() + "' existe déjà");
        }
        
        // Gestion sécurisée du mot de passe
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        logger.debug("Mot de passe re-chiffré pour utilisateur ID: {}", id);
        
        // Mise à jour avec conservation de l'ID
        user.setId(id);
        User updatedUser = userRepository.save(user);
        logger.info("Utilisateur mis à jour avec succès: {} (ID: {})", updatedUser.getUsername(), updatedUser.getId());
        
        return updatedUser;
    }

    /**
     * Supprime un utilisateur par son identifiant.
     * 
     * <p>Cette méthode effectue la suppression sécurisée d'un utilisateur après
     * validation de son existence. La suppression est transactionnelle avec
     * rollback automatique en cas d'erreur.</p>
     * 
     * @param id L'identifiant de l'utilisateur à supprimer
     * @throws IllegalArgumentException Si l'utilisateur n'existe pas
     */
    @Override
    public void deleteById(Integer id) {
        logger.info("Tentative de suppression utilisateur ID: {}", id);
        
        // Validation de l'existence avant suppression
        User existingUser = findById(id)
            .orElseThrow(() -> {
                logger.error("Tentative de suppression d'un utilisateur inexistant: {}", id);
                return new IllegalArgumentException("Utilisateur avec ID " + id + " non trouvé");
            });
        
        // Suppression transactionnelle
        userRepository.deleteById(id);
        logger.info("Utilisateur supprimé avec succès: {} (ID: {})", existingUser.getUsername(), id);
    }

    /**
     * Vérifie si un nom d'utilisateur est disponible (unique) dans le système.
     * 
     * @param username Le nom d'utilisateur à vérifier
     * @return true si disponible, false si déjà utilisé
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        boolean available = !userRepository.findByUsername(username).isPresent();
        logger.debug("Vérification unicité username '{}': {}", username, available ? "disponible" : "déjà utilisé");
        return available;
    }

    /**
     * Vérifie si un nom d'utilisateur est disponible en excluant un utilisateur donné.
     * 
     * <p>Utilisée lors des mises à jour pour permettre à un utilisateur de conserver
     * son nom d'utilisateur actuel tout en vérifiant l'unicité par rapport aux autres.</p>
     * 
     * @param username Le nom d'utilisateur à vérifier
     * @param userId L'ID de l'utilisateur à exclure de la vérification
     * @return true si disponible, false si utilisé par un autre utilisateur
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username, Integer userId) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        boolean available = !existingUser.isPresent() || existingUser.get().getId().equals(userId);
        logger.debug("Vérification unicité username '{}' (excluant ID {}): {}", 
                    username, userId, available ? "disponible" : "déjà utilisé");
        return available;
    }

    /**
     * Valide les données d'un utilisateur pour la création.
     * 
     * @param user L'utilisateur à valider
     * @throws IllegalArgumentException Si les données sont invalides
     */
    private void validateUserForCreation(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être null");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        if (user.getFullname() == null || user.getFullname().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom complet est obligatoire");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Le rôle est obligatoire");
        }
    }

    /**
     * Valide les données d'un utilisateur pour la mise à jour.
     * 
     * @param user L'utilisateur à valider
     * @throws IllegalArgumentException Si les données sont invalides
     */
    private void validateUserForUpdate(User user) {
        validateUserForCreation(user); // Même validations que pour la création
    }

    // DTO Methods - Implementation for SOLID compliance

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAllAsDTO() {
        logger.debug("Récupération de tous les utilisateurs en DTO");
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("Récupération de {} utilisateurs en DTO", userDTOs.size());
        return userDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> findByIdAsDTO(Integer id) {
        logger.debug("Recherche utilisateur DTO avec ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.debug("Utilisateur trouvé et converti en DTO avec ID: {}", id);
            return Optional.of(userMapper.toDTO(user.get()));
        } else {
            logger.warn("Aucun utilisateur trouvé avec ID: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public UserDTO saveFromDTO(UserDTO userDTO) {
        logger.info("Tentative de création d'utilisateur depuis DTO: {}", userDTO.getUsername());
        
        // Conversion DTO vers Entity via mapper
        User user = userMapper.toEntity(userDTO);
        
        // Utilisation de la méthode existante de validation et sauvegarde
        User savedUser = save(user);
        
        // Conversion de l'entité sauvée en DTO pour le retour
        UserDTO savedDTO = userMapper.toDTO(savedUser);
        logger.info("Utilisateur créé avec succès depuis DTO: {} (ID: {})", savedDTO.getUsername(), savedDTO.getId());
        
        return savedDTO;
    }

    @Override
    public UserDTO updateFromDTO(Integer id, UserDTO userDTO) {
        logger.info("Tentative de mise à jour utilisateur depuis DTO ID: {}", id);
        
        // Conversion DTO vers Entity via mapper
        User user = userMapper.toEntity(userDTO);
        
        // Utilisation de la méthode existante de validation et mise à jour
        User updatedUser = update(id, user);
        
        // Conversion de l'entité mise à jour en DTO pour le retour
        UserDTO updatedDTO = userMapper.toDTO(updatedUser);
        logger.info("Utilisateur mis à jour avec succès depuis DTO: {} (ID: {})", updatedDTO.getUsername(), updatedDTO.getId());
        
        return updatedDTO;
    }
}