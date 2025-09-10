package com.nnk.springboot.services;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.mapper.UserMapper;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserService - Gestion sécurisée des utilisateurs.
 * 
 * <p>Cette classe teste l'ensemble des fonctionnalités du service utilisateur
 * avec un focus particulier sur les aspects de sécurité, validation et
 * chiffrement des mots de passe.</p>
 * 
 * <p>Domaines testés :</p>
 * <ul>
 *   <li><strong>CRUD Operations</strong> : Create, Read, Update, Delete</li>
 *   <li><strong>Sécurité</strong> : Chiffrement automatique des mots de passe</li>
 *   <li><strong>Validation</strong> : Contrôles métier et intégrité</li>
 *   <li><strong>Gestion d'erreurs</strong> : Exceptions appropriées</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User existingUser;
    private UserDTO testUserDTO;
    private UserDTO existingUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("TestPass123!");
        testUser.setFullname("Test User");
        testUser.setRole("USER");

        existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("existing");
        existingUser.setPassword("encoded_password");
        existingUser.setFullname("Existing User");
        existingUser.setRole("ADMIN");

        testUserDTO = new UserDTO();
        testUserDTO.setUsername("testuser");
        testUserDTO.setPassword("TestPass123!");
        testUserDTO.setFullname("Test User");
        testUserDTO.setRole("USER");

        existingUserDTO = new UserDTO();
        existingUserDTO.setId(1);
        existingUserDTO.setUsername("existing");
        existingUserDTO.setPassword(""); // Les DTOs doivent avoir des mots de passe masqués
        existingUserDTO.setFullname("Existing User");
        existingUserDTO.setRole("ADMIN");
    }

    // ================================
    // Tests FindAll
    // ================================

    @Test
    void findAll_ShouldReturnAllUsers() {
        // Given
        List<User> expectedUsers = Arrays.asList(testUser, existingUser);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // When
        List<User> result = userService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testUser, existingUser);
        verify(userRepository).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<User> result = userService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findAll();
    }

    // ================================
    // Tests FindById
    // ================================

    @Test
    void findById_ShouldReturnUserWhenExists() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));

        // When
        Optional<User> result = userService.findById(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(existingUser);
        verify(userRepository).findById(1);
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        // Given
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findById(99);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findById(99);
    }

    // ================================
    // Tests FindByUsername
    // ================================

    @Test
    void findByUsername_ShouldReturnUserWhenExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByUsername("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testUser);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findByUsername_ShouldReturnEmptyWhenNotExists() {
        // Given
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByUsername("unknown");

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findByUsername("unknown");
    }

    // ================================
    // Tests Save (Create)
    // ================================

    @Test
    void save_ShouldCreateUserWithEncodedPassword() {
        // Given
        String encodedPassword = "encoded_TestPass123!";
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("TestPass123!")).thenReturn(encodedPassword);
        
        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setUsername("testuser");
        savedUser.setPassword(encodedPassword);
        savedUser.setFullname("Test User");
        savedUser.setRole("USER");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.save(testUser);

        // Then
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.getFullname()).isEqualTo("Test User");
        assertThat(result.getRole()).isEqualTo("USER");

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).encode("TestPass123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void save_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThatThrownBy(() -> userService.save(testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le nom d'utilisateur 'testuser' existe déjà");

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void save_ShouldThrowException_WhenUserIsNull() {
        // When & Then
        assertThatThrownBy(() -> userService.save(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("L'utilisateur ne peut pas être null");
    }

    @Test
    void save_ShouldThrowException_WhenUsernameIsEmpty() {
        // Given
        testUser.setUsername("");

        // When & Then
        assertThatThrownBy(() -> userService.save(testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le nom d'utilisateur est obligatoire");
    }

    @Test
    void save_ShouldThrowException_WhenPasswordIsEmpty() {
        // Given
        testUser.setPassword("");

        // When & Then
        assertThatThrownBy(() -> userService.save(testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le mot de passe est obligatoire");
    }

    @Test
    void save_ShouldThrowException_WhenFullnameIsEmpty() {
        // Given
        testUser.setFullname("");

        // When & Then
        assertThatThrownBy(() -> userService.save(testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le nom complet est obligatoire");
    }

    @Test
    void save_ShouldThrowException_WhenRoleIsEmpty() {
        // Given
        testUser.setRole("");

        // When & Then
        assertThatThrownBy(() -> userService.save(testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le rôle est obligatoire");
    }

    // ================================
    // Tests Update
    // ================================

    @Test
    void update_ShouldUpdateUserWithEncodedPassword() {
        // Given
        Integer userId = 1;
        String encodedPassword = "encoded_NewPass123!";
        
        User updateData = new User();
        updateData.setUsername("updated_user");
        updateData.setPassword("NewPass123!");
        updateData.setFullname("Updated User");
        updateData.setRole("ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("updated_user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("NewPass123!")).thenReturn(encodedPassword);
        
        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setUsername("updated_user");
        savedUser.setPassword(encodedPassword);
        savedUser.setFullname("Updated User");
        savedUser.setRole("ADMIN");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.update(userId, updateData);

        // Then
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("updated_user");
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.getFullname()).isEqualTo("Updated User");
        assertThat(result.getRole()).isEqualTo("ADMIN");

        verify(userRepository).findById(userId);
        verify(userRepository).findByUsername("updated_user");
        verify(passwordEncoder).encode("NewPass123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_ShouldAllowSameUsername_WhenUpdatingExistingUser() {
        // Given
        Integer userId = 1;
        String encodedPassword = "encoded_NewPass123!";
        
        User updateData = new User();
        updateData.setUsername("existing"); // Même username
        updateData.setPassword("NewPass123!");
        updateData.setFullname("Updated User");
        updateData.setRole("ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("NewPass123!")).thenReturn(encodedPassword);
        
        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setUsername("existing");
        savedUser.setPassword(encodedPassword);
        savedUser.setFullname("Updated User");
        savedUser.setRole("ADMIN");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.update(userId, updateData);

        // Then
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("existing");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_ShouldThrowException_WhenUserNotFound() {
        // Given
        Integer userId = 99;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.update(userId, testUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Utilisateur avec ID 99 non trouvé");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_ShouldThrowException_WhenUsernameAlreadyTakenByOtherUser() {
        // Given
        Integer userId = 1;
        User otherUser = new User();
        otherUser.setId(2);
        otherUser.setUsername("taken_username");
        
        User updateData = new User();
        updateData.setUsername("taken_username");
        updateData.setPassword("NewPass123!");
        updateData.setFullname("Updated User");
        updateData.setRole("ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("taken_username")).thenReturn(Optional.of(otherUser));

        // When & Then
        assertThatThrownBy(() -> userService.update(userId, updateData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Le nom d'utilisateur 'taken_username' existe déjà");

        verify(userRepository).findById(userId);
        verify(userRepository).findByUsername("taken_username");
        verify(userRepository, never()).save(any(User.class));
    }

    // ================================
    // Tests DeleteById
    // ================================

    @Test
    void deleteById_ShouldDeleteUserWhenExists() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));

        // When
        userService.deleteById(1);

        // Then
        verify(userRepository).findById(1);
        verify(userRepository).deleteById(1);
    }

    @Test
    void deleteById_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteById(99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Utilisateur avec ID 99 non trouvé");

        verify(userRepository).findById(99);
        verify(userRepository, never()).deleteById(anyInt());
    }

    // ================================
    // Tests IsUsernameAvailable
    // ================================

    @Test
    void isUsernameAvailable_ShouldReturnTrue_WhenUsernameNotExists() {
        // Given
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        // When
        boolean result = userService.isUsernameAvailable("newuser");

        // Then
        assertThat(result).isTrue();
        verify(userRepository).findByUsername("newuser");
    }

    @Test
    void isUsernameAvailable_ShouldReturnFalse_WhenUsernameExists() {
        // Given
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existingUser));

        // When
        boolean result = userService.isUsernameAvailable("existing");

        // Then
        assertThat(result).isFalse();
        verify(userRepository).findByUsername("existing");
    }

    @Test
    void isUsernameAvailableWithUserId_ShouldReturnTrue_WhenUsernameNotExists() {
        // Given
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        // When
        boolean result = userService.isUsernameAvailable("newuser", 1);

        // Then
        assertThat(result).isTrue();
        verify(userRepository).findByUsername("newuser");
    }

    @Test
    void isUsernameAvailableWithUserId_ShouldReturnTrue_WhenUsernameOwnedBySameUser() {
        // Given
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existingUser));

        // When (existingUser a l'ID 1)
        boolean result = userService.isUsernameAvailable("existing", 1);

        // Then
        assertThat(result).isTrue();
        verify(userRepository).findByUsername("existing");
    }

    @Test
    void isUsernameAvailableWithUserId_ShouldReturnFalse_WhenUsernameOwnedByOtherUser() {
        // Given
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(existingUser));

        // Quand (existingUser a l'ID 1, on teste avec ID 2)
        boolean result = userService.isUsernameAvailable("existing", 2);

        // Then
        assertThat(result).isFalse();
        verify(userRepository).findByUsername("existing");
    }

    // ================================
    // Tests des méthodes DTO
    // ================================

    @Test
    void findAllAsDTO_ShouldReturnAllUsersAsDTO() {
        // Given
        List<User> users = Arrays.asList(testUser, existingUser);
        List<UserDTO> expectedDTOs = Arrays.asList(testUserDTO, existingUserDTO);
        
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);
        when(userMapper.toDTO(existingUser)).thenReturn(existingUserDTO);

        // When
        List<UserDTO> result = userService.findAllAsDTO();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(testUserDTO, existingUserDTO);
        verify(userRepository).findAll();
        verify(userMapper).toDTO(testUser);
        verify(userMapper).toDTO(existingUser);
    }

    @Test
    void findByIdAsDTO_ShouldReturnUserDTOWhenExists() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userMapper.toDTO(existingUser)).thenReturn(existingUserDTO);

        // When
        Optional<UserDTO> result = userService.findByIdAsDTO(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(existingUserDTO);
        verify(userRepository).findById(1);
        verify(userMapper).toDTO(existingUser);
    }

    @Test
    void findByIdAsDTO_ShouldReturnEmptyWhenNotExists() {
        // Given
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        // When
        Optional<UserDTO> result = userService.findByIdAsDTO(99);

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findById(99);
        verify(userMapper, never()).toDTO(any(User.class));
    }

    @Test
    void saveFromDTO_ShouldCreateUserFromDTO() {
        // Given
        String encodedPassword = "encoded_TestPass123!";
        when(userMapper.toEntity(testUserDTO)).thenReturn(testUser);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("TestPass123!")).thenReturn(encodedPassword);
        
        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setUsername("testuser");
        savedUser.setPassword(encodedPassword);
        savedUser.setFullname("Test User");
        savedUser.setRole("USER");
        
        UserDTO savedDTO = new UserDTO();
        savedDTO.setId(1);
        savedDTO.setUsername("testuser");
        savedDTO.setPassword(""); // Masked password
        savedDTO.setFullname("Test User");
        savedDTO.setRole("USER");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(savedDTO);

        // When
        UserDTO result = userService.saveFromDTO(testUserDTO);

        // Then
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getPassword()).isEmpty(); // Doit être masqué
        assertThat(result.getFullname()).isEqualTo("Test User");
        assertThat(result.getRole()).isEqualTo("USER");

        verify(userMapper).toEntity(testUserDTO);
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).encode("TestPass123!");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDTO(savedUser);
    }

    @Test
    void updateFromDTO_ShouldUpdateUserFromDTO() {
        // Given
        Integer userId = 1;
        String encodedPassword = "encoded_NewPass123!";
        
        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername("updated_user");
        updateDTO.setPassword("NewPass123!");
        updateDTO.setFullname("Updated User");
        updateDTO.setRole("ADMIN");
        
        User updateEntity = new User();
        updateEntity.setUsername("updated_user");
        updateEntity.setPassword("NewPass123!");
        updateEntity.setFullname("Updated User");
        updateEntity.setRole("ADMIN");

        when(userMapper.toEntity(updateDTO)).thenReturn(updateEntity);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("updated_user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("NewPass123!")).thenReturn(encodedPassword);
        
        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setUsername("updated_user");
        savedUser.setPassword(encodedPassword);
        savedUser.setFullname("Updated User");
        savedUser.setRole("ADMIN");
        
        UserDTO savedDTO = new UserDTO();
        savedDTO.setId(userId);
        savedDTO.setUsername("updated_user");
        savedDTO.setPassword(""); // Masked password
        savedDTO.setFullname("Updated User");
        savedDTO.setRole("ADMIN");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(savedDTO);

        // When
        UserDTO result = userService.updateFromDTO(userId, updateDTO);

        // Then
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("updated_user");
        assertThat(result.getPassword()).isEmpty(); // Doit être masqué
        assertThat(result.getFullname()).isEqualTo("Updated User");
        assertThat(result.getRole()).isEqualTo("ADMIN");

        verify(userMapper).toEntity(updateDTO);
        verify(userRepository).findById(userId);
        verify(userRepository).findByUsername("updated_user");
        verify(passwordEncoder).encode("NewPass123!");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDTO(savedUser);
    }
}