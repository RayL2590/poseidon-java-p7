package com.nnk.springboot.controllers;

import com.nnk.springboot.dto.UserDTO;
import com.nnk.springboot.services.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private IUserService userService;


    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUserDTO = new UserDTO();
        testUserDTO.setId(1);
        testUserDTO.setUsername("testuser");
        testUserDTO.setPassword("password123");
        testUserDTO.setFullname("Test User");
        testUserDTO.setRole("USER");
    }

    @Test
    void home_ShouldReturnUserListView() {
        when(userService.findAllAsDTO()).thenReturn(List.of(testUserDTO));

        String result = userController.home(model);

        assertEquals("user/list", result);
        verify(model).addAttribute(eq("users"), any(List.class));
        verify(userService).findAllAsDTO();
    }

    @Test
    void home_ShouldReturnErrorViewOnException() {
        when(userService.findAllAsDTO()).thenThrow(new RuntimeException("Database error"));

        String result = userController.home(model);

        assertEquals("error", result);
        verify(model).addAttribute("errorMessage", "Erreur lors du chargement des utilisateurs");
    }

    @Test
    void addUser_ShouldReturnAddViewWithEmptyUserDTO() {
        String result = userController.addUser(model);

        assertEquals("user/add", result);
        verify(model).addAttribute(eq("user"), any(UserDTO.class));
    }

    @Test
    void validate_ShouldReturnAddViewOnValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = userController.validate(testUserDTO, bindingResult, model, redirectAttributes);

        assertEquals("user/add", result);
        verify(userService, never()).saveFromDTO(any());
    }

    @Test
    void validate_ShouldRedirectToListOnSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.saveFromDTO(testUserDTO)).thenReturn(testUserDTO);

        String result = userController.validate(testUserDTO, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/user/list", result);
        verify(userService).saveFromDTO(testUserDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), 
            contains("Utilisateur 'testuser' créé avec succès"));
    }

    @Test
    void validate_ShouldReturnAddViewOnIllegalArgumentException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.saveFromDTO(testUserDTO)).thenThrow(new IllegalArgumentException("Username already exists"));

        String result = userController.validate(testUserDTO, bindingResult, model, redirectAttributes);

        assertEquals("user/add", result);
        verify(model).addAttribute("errorMessage", "Username already exists");
    }

    @Test
    void validate_ShouldReturnAddViewOnGenericException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.saveFromDTO(testUserDTO)).thenThrow(new RuntimeException("Database error"));

        String result = userController.validate(testUserDTO, bindingResult, model, redirectAttributes);

        assertEquals("user/add", result);
        verify(model).addAttribute("errorMessage", 
            "Erreur technique lors de la création de l'utilisateur");
    }

    @Test
    void showUpdateForm_ShouldReturnUpdateViewOnSuccess() {
        when(userService.findByIdAsDTO(1)).thenReturn(Optional.of(testUserDTO));

        String result = userController.showUpdateForm(1, model, redirectAttributes);

        assertEquals("user/update", result);
        verify(model).addAttribute("user", testUserDTO);
        verify(userService).findByIdAsDTO(1);
    }

    @Test
    void showUpdateForm_ShouldRedirectOnUserNotFound() {
        when(userService.findByIdAsDTO(999)).thenReturn(Optional.empty());

        String result = userController.showUpdateForm(999, model, redirectAttributes);

        assertEquals("redirect:/user/list", result);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), 
            contains("Utilisateur avec ID 999 non trouvé"));
    }

    @Test
    void showUpdateForm_ShouldRedirectOnGenericException() {
        when(userService.findByIdAsDTO(1)).thenThrow(new RuntimeException("Database error"));

        String result = userController.showUpdateForm(1, model, redirectAttributes);

        assertEquals("redirect:/user/list", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", 
            "Erreur technique lors du chargement de l'utilisateur");
    }

    @Test
    void updateUser_ShouldReturnUpdateViewOnValidationErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = userController.updateUser(1, testUserDTO, bindingResult, model, redirectAttributes);

        assertEquals("user/update", result);
        verify(userService, never()).updateFromDTO(anyInt(), any());
    }

    @Test
    void updateUser_ShouldRedirectToListOnSuccess() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.updateFromDTO(1, testUserDTO)).thenReturn(testUserDTO);

        String result = userController.updateUser(1, testUserDTO, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/user/list", result);
        verify(userService).updateFromDTO(1, testUserDTO);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), 
            contains("Utilisateur 'testuser' mis à jour avec succès"));
    }

    @Test
    void updateUser_ShouldReturnUpdateViewOnIllegalArgumentException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new IllegalArgumentException("User not found")).when(userService).updateFromDTO(1, testUserDTO);

        String result = userController.updateUser(1, testUserDTO, bindingResult, model, redirectAttributes);

        assertEquals("user/update", result);
        verify(model).addAttribute("errorMessage", "User not found");
    }

    @Test
    void updateUser_ShouldReturnUpdateViewOnGenericException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Database error")).when(userService).updateFromDTO(1, testUserDTO);

        String result = userController.updateUser(1, testUserDTO, bindingResult, model, redirectAttributes);

        assertEquals("user/update", result);
        verify(model).addAttribute("errorMessage", 
            "Erreur technique lors de la mise à jour de l'utilisateur");
    }

    @Test
    void deleteUser_ShouldRedirectWithSuccessMessage() {
        when(userService.findByIdAsDTO(1)).thenReturn(Optional.of(testUserDTO));
        doNothing().when(userService).deleteById(1);

        String result = userController.deleteUser(1, redirectAttributes);

        assertEquals("redirect:/user/list", result);
        verify(userService).findByIdAsDTO(1);
        verify(userService).deleteById(1);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), 
            contains("Utilisateur 'testuser' supprimé avec succès"));
    }

    @Test
    void deleteUser_ShouldRedirectWithErrorOnUserNotFound() {
        when(userService.findByIdAsDTO(999)).thenReturn(Optional.empty());

        String result = userController.deleteUser(999, redirectAttributes);

        assertEquals("redirect:/user/list", result);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), 
            contains("Utilisateur avec ID 999 non trouvé"));
        verify(userService, never()).deleteById(anyInt());
    }

    @Test
    void deleteUser_ShouldRedirectWithErrorOnGenericException() {
        when(userService.findByIdAsDTO(1)).thenReturn(Optional.of(testUserDTO));
        doThrow(new RuntimeException("Database error")).when(userService).deleteById(1);

        String result = userController.deleteUser(1, redirectAttributes);

        assertEquals("redirect:/user/list", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", 
            "Erreur technique lors de la suppression de l'utilisateur");
    }

    @Test
    void deleteUser_ShouldRedirectWithErrorOnFindByIdException() {
        when(userService.findByIdAsDTO(1)).thenThrow(new RuntimeException("Database error"));

        String result = userController.deleteUser(1, redirectAttributes);

        assertEquals("redirect:/user/list", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", 
            "Erreur technique lors de la suppression de l'utilisateur");
        verify(userService, never()).deleteById(anyInt());
    }

    @Test
    void home_ShouldHandleEmptyUserList() {
        when(userService.findAllAsDTO()).thenReturn(List.of());

        String result = userController.home(model);

        assertEquals("user/list", result);
        verify(model).addAttribute(eq("users"), eq(List.of()));
    }

    @Test
    void validate_ShouldHandleNullUserDTO() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.saveFromDTO(null)).thenThrow(new IllegalArgumentException("DTO cannot be null"));

        String result = userController.validate(null, bindingResult, model, redirectAttributes);

        assertEquals("user/add", result);
        verify(model).addAttribute("errorMessage", "DTO cannot be null");
    }

    @Test
    void showUpdateForm_ShouldHandleNullId() {
        when(userService.findByIdAsDTO(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        String result = userController.showUpdateForm(null, model, redirectAttributes);

        assertEquals("redirect:/user/list", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "ID cannot be null");
    }

    @Test
    void updateUser_ShouldHandleNullId() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new IllegalArgumentException("ID cannot be null")).when(userService).updateFromDTO(null, testUserDTO);

        String result = userController.updateUser(null, testUserDTO, bindingResult, model, redirectAttributes);

        assertEquals("user/update", result);
        verify(model).addAttribute("errorMessage", "ID cannot be null");
    }

    @Test
    void deleteUser_ShouldHandleNullId() {
        when(userService.findByIdAsDTO(null)).thenThrow(new IllegalArgumentException("ID cannot be null"));

        String result = userController.deleteUser(null, redirectAttributes);

        assertEquals("redirect:/user/list", result);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "ID cannot be null");
    }
}