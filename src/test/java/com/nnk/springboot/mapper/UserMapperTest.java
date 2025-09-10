package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;
    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setFullname("Test User");
        testUser.setRole("USER");
        
        testUserDTO = new UserDTO();
        testUserDTO.setId(1);
        testUserDTO.setUsername("testuser");
        testUserDTO.setPassword("password123");
        testUserDTO.setFullname("Test User");
        testUserDTO.setRole("USER");
    }

    @Test
    void toDTO_ShouldConvertUserToDTO() {
        UserDTO result = userMapper.toDTO(testUser);
        
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals("", result.getPassword()); // Le mot de passe doit être masqué
        assertEquals(testUser.getFullname(), result.getFullname());
        assertEquals(testUser.getRole(), result.getRole());
    }

    @Test
    void toDTO_ShouldThrowException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userMapper.toDTO(null)
        );
        assertEquals("L'entité User ne peut pas être null", exception.getMessage());
    }

    @Test
    void toEntity_ShouldConvertDTOToUser() {
        User result = userMapper.toEntity(testUserDTO);
        
        assertNotNull(result);
        assertEquals(testUserDTO.getId(), result.getId());
        assertEquals(testUserDTO.getUsername(), result.getUsername());
        assertEquals(testUserDTO.getPassword(), result.getPassword());
        assertEquals(testUserDTO.getFullname(), result.getFullname());
        assertEquals(testUserDTO.getRole(), result.getRole());
    }

    @Test
    void toEntity_ShouldThrowException_WhenDTOIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userMapper.toEntity(null)
        );
        assertEquals("Le DTO UserDTO ne peut pas être null", exception.getMessage());
    }

    @Test
    void toDTOForEdit_ShouldConvertWithDefaultPassword() {
        String defaultPassword = "defaultPass";
        
        UserDTO result = userMapper.toDTOForEdit(testUser, defaultPassword);
        
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(defaultPassword, result.getPassword());
        assertEquals(testUser.getFullname(), result.getFullname());
        assertEquals(testUser.getRole(), result.getRole());
    }

    @Test
    void toDTOForEdit_ShouldUseEmptyString_WhenDefaultPasswordIsNull() {
        UserDTO result = userMapper.toDTOForEdit(testUser, null);
        
        assertNotNull(result);
        assertEquals("", result.getPassword());
    }

    @Test
    void toDTOForEdit_ShouldThrowException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userMapper.toDTOForEdit(null, "defaultPass")
        );
        assertEquals("L'entité User ne peut pas être null", exception.getMessage());
    }

    @Test
    void updateEntityFromDTO_ShouldUpdateAllFields() {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername("updatedUser");
        updateDTO.setPassword("newPassword");
        updateDTO.setFullname("Updated User");
        updateDTO.setRole("ADMIN");
        
        userMapper.updateEntityFromDTO(testUser, updateDTO);
        
        assertEquals("updatedUser", testUser.getUsername());
        assertEquals("newPassword", testUser.getPassword());
        assertEquals("Updated User", testUser.getFullname());
        assertEquals("ADMIN", testUser.getRole());
    }

    @Test
    void updateEntityFromDTO_ShouldNotUpdateEmptyFields() {
        String originalUsername = testUser.getUsername();
        String originalPassword = testUser.getPassword();
        
        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername(""); // Une chaîne vide ne doit pas mettre à jour
        updateDTO.setPassword("   "); // Les espaces ne doivent pas mettre à jour
        updateDTO.setFullname("Updated User");
        updateDTO.setRole("ADMIN");
        
        userMapper.updateEntityFromDTO(testUser, updateDTO);
        
        assertEquals(originalUsername, testUser.getUsername());
        assertEquals(originalPassword, testUser.getPassword());
        assertEquals("Updated User", testUser.getFullname());
        assertEquals("ADMIN", testUser.getRole());
    }

    @Test
    void updateEntityFromDTO_ShouldNotUpdateNullFields() {
        String originalUsername = testUser.getUsername();
        String originalFullname = testUser.getFullname();
        
        UserDTO updateDTO = new UserDTO();
        updateDTO.setUsername(null);
        updateDTO.setPassword("newPassword");
        updateDTO.setFullname(null);
        updateDTO.setRole("ADMIN");
        
        userMapper.updateEntityFromDTO(testUser, updateDTO);
        
        assertEquals(originalUsername, testUser.getUsername());
        assertEquals(originalFullname, testUser.getFullname());
        assertEquals("newPassword", testUser.getPassword());
        assertEquals("ADMIN", testUser.getRole());
    }

    @Test
    void updateEntityFromDTO_ShouldThrowException_WhenEntityIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userMapper.updateEntityFromDTO(null, testUserDTO)
        );
        assertEquals("L'entité User ne peut pas être null", exception.getMessage());
    }

    @Test
    void updateEntityFromDTO_ShouldThrowException_WhenDTOIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userMapper.updateEntityFromDTO(testUser, null)
        );
        assertEquals("Le DTO UserDTO ne peut pas être null", exception.getMessage());
    }

    @Test
    void createEmptyDTO_ShouldReturnDTOWithDefaults() {
        UserDTO result = userMapper.createEmptyDTO();
        
        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("", result.getUsername());
        assertEquals("", result.getPassword());
        assertEquals("", result.getFullname());
        assertEquals("USER", result.getRole());
    }
}