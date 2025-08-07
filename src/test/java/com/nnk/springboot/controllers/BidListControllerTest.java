package com.nnk.springboot.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidListDTO;
import com.nnk.springboot.services.IBidListService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BidListController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class BidListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBidListService bidListService;
    
    private BidList bidListEntity;
    private BidListDTO bidListDTO;
    private List<BidList> bidListEntities;

    @BeforeEach
    void setUp() {
        bidListEntity = new BidList("TestAccount", "TestType", 100.0);
        bidListEntity.setBidListId(1);
        
        bidListDTO = new BidListDTO("TestAccount", "TestType", 100.0);
        bidListDTO.setBidListId(1);
        
        bidListEntities = Arrays.asList(
            new BidList("Account1", "Type1", 100.0),
            new BidList("Account2", "Type2", 200.0)
        );
        bidListEntities.get(0).setBidListId(1);
        bidListEntities.get(1).setBidListId(2);
    }

    // Happy Path Tests
    
    @Test
    void home_ShouldReturnListView() throws Exception {
        // Given
        when(bidListService.findAll()).thenReturn(bidListEntities);
        
        // When & Then
        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"))
                .andExpect(model().attributeExists("bidLists"))
                .andExpect(model().attribute("bidLists", org.hamcrest.Matchers.hasSize(2)));
        
        verify(bidListService).findAll();
    }
    
    @Test
    void home_WithSuccessParam_ShouldAddSuccessMessage() throws Exception {
        // Given
        when(bidListService.findAll()).thenReturn(bidListEntities);
        
        // When & Then
        mockMvc.perform(get("/bidList/list").param("success", "created"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"))
                .andExpect(model().attribute("successMessage", "BidList created successfully"));
        
        mockMvc.perform(get("/bidList/list").param("success", "updated"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("successMessage", "BidList updated successfully"));
        
        mockMvc.perform(get("/bidList/list").param("success", "deleted"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("successMessage", "BidList deleted successfully"));
    }
    
    @Test
    void home_WithErrorParam_ShouldAddErrorMessage() throws Exception {
        // Given
        when(bidListService.findAll()).thenReturn(bidListEntities);
        
        // When & Then
        mockMvc.perform(get("/bidList/list").param("error", "notfound"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"))
                .andExpect(model().attribute("errorMessage", "BidList not found"));
        
        mockMvc.perform(get("/bidList/list").param("error", "invalid"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", "Invalid ID provided"));
        
        mockMvc.perform(get("/bidList/list").param("error", "unexpected"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", "An unexpected error occurred"));
    }
    
    @Test
    void home_WithServiceException_ShouldHandleError() throws Exception {
        // Given
        when(bidListService.findAll()).thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("Error loading data")));
    }
    
    @Test
    void addBidForm_ShouldReturnAddView() throws Exception {
        // When & Then
        mockMvc.perform(get("/bidList/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeExists("bidListDTO"))
                .andExpect(model().attribute("bidListDTO", org.hamcrest.Matchers.isA(BidListDTO.class)));
    }
    
    @Test
    void showUpdateForm_WithValidId_ShouldReturnUpdateView() throws Exception {
        // Given
        when(bidListService.findById(1)).thenReturn(Optional.of(bidListEntity));
        
        // When & Then
        mockMvc.perform(get("/bidList/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeExists("bidListDTO"))
                .andExpect(model().attribute("bidListDTO", org.hamcrest.Matchers.hasProperty("account", org.hamcrest.Matchers.is("TestAccount"))));
        
        verify(bidListService).findById(1);
    }
    
    @Test
    void showUpdateForm_WithInvalidId_ShouldRedirectToList() throws Exception {
        // When & Then
        mockMvc.perform(get("/bidList/update/0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list?error=invalid"));
        
        verify(bidListService, never()).findById(anyInt());
    }
    
    @Test
    void showUpdateForm_WithNonExistentId_ShouldRedirectWithError() throws Exception {
        // Given
        when(bidListService.findById(999)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/bidList/update/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list?error=notfound"));
        
        verify(bidListService).findById(999);
    }
    
    @Test
    void validate_WithValidDTO_ShouldRedirectToList() throws Exception {
        // Given
        when(bidListService.save(any(BidList.class))).thenReturn(bidListEntity);
        
        // When & Then
        mockMvc.perform(post("/bidList/validate")
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list?success=created"));
        
        verify(bidListService).save(any(BidList.class));
    }
    
    @Test
    void validate_WithValidationErrors_ShouldReturnAddView() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/bidList/validate")
                .param("account", "")
                .param("type", "")
                .param("bidQuantity", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeExists("bidListDTO"))
                .andExpect(model().hasErrors());
        
        verify(bidListService, never()).save(any(BidList.class));
    }
    
    @Test
    void validate_WithServiceException_ShouldReturnAddViewWithError() throws Exception {
        // Given
        when(bidListService.save(any(BidList.class))).thenThrow(new IllegalArgumentException("Invalid data"));
        
        // When & Then
        mockMvc.perform(post("/bidList/validate")
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("Validation error")));
    }
    
    @Test
    void updateBid_WithValidData_ShouldRedirectToList() throws Exception {
        // Given
        when(bidListService.existsById(1)).thenReturn(true);
        when(bidListService.save(any(BidList.class))).thenReturn(bidListEntity);
        
        // When & Then
        mockMvc.perform(post("/bidList/update/1")
                .param("account", "UpdatedAccount")
                .param("type", "UpdatedType")
                .param("bidQuantity", "150.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list?success=updated"));
        
        verify(bidListService).existsById(1);
        verify(bidListService).save(any(BidList.class));
    }
    
    @Test
    void updateBid_WithValidationErrors_ShouldReturnUpdateView() throws Exception {
        // When & Then - Missing required fields
        mockMvc.perform(post("/bidList/update/1")
                .param("account", "")
                .param("type", "")
                .param("bidQuantity", "150.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeExists("bidListDTO"))
                .andExpect(model().hasErrors());
        
        verify(bidListService, never()).save(any(BidList.class));
    }
    
    @Test
    void updateBid_WithNonExistentId_ShouldReturnUpdateViewWithError() throws Exception {
        // Given
        when(bidListService.existsById(999)).thenReturn(false);
        
        // When & Then
        mockMvc.perform(post("/bidList/update/999")
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("BidList not found with ID")));
        
        verify(bidListService).existsById(999);
        verify(bidListService, never()).save(any(BidList.class));
    }
    
    @Test
    void deleteBid_WithValidId_ShouldRedirectWithSuccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/bidList/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list?success=deleted"));
        
        verify(bidListService).deleteById(1);
    }
    
    @Test
    void deleteBid_WithInvalidId_ShouldRedirectWithError() throws Exception {
        // When & Then
        mockMvc.perform(get("/bidList/delete/0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list?error=notfound"));
        
        verify(bidListService, never()).deleteById(anyInt());
    }
    
    @Test
    void deleteBid_WithServiceException_ShouldRedirectWithError() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("BidList not found")).when(bidListService).deleteById(999);
        
        // When & Then
        mockMvc.perform(get("/bidList/delete/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list?error=notfound"));
        
        verify(bidListService).deleteById(999);
    }
    
    @Test
    void handleGenericException_ShouldReturnErrorView() throws Exception {
        // Given
        when(bidListService.findAll()).thenThrow(new RuntimeException("Unexpected error"));
        
        // When & Then
        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("Error loading data")));
    }

    // Additional Edge Case Tests
    
    @Test
    void validate_WithNegativeBidQuantity_ShouldReturnAddViewWithErrors() throws Exception {
        // When & Then
        mockMvc.perform(post("/bidList/validate")
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "-100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().hasErrors());
        
        verify(bidListService, never()).save(any(BidList.class));
    }
    
    @Test
    void validate_WithTooLongAccount_ShouldReturnAddViewWithErrors() throws Exception {
        // When & Then
        String longAccount = "a".repeat(31); // More than 30 characters
        mockMvc.perform(post("/bidList/validate")
                .param("account", longAccount)
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/add"))
                .andExpect(model().hasErrors());
        
        verify(bidListService, never()).save(any(BidList.class));
    }
    
    @Test
    void updateBid_WithInvalidIdInPath_ShouldReturnUpdateViewWithError() throws Exception {
        // When & Then
        mockMvc.perform(post("/bidList/update/0")
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", org.hamcrest.Matchers.containsString("Invalid ID")));
        
        verify(bidListService, never()).save(any(BidList.class));
    }
    
    @Test
    void showUpdateForm_WithServiceException_ShouldRedirectWithError() throws Exception {
        // Given
        when(bidListService.findById(1)).thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        mockMvc.perform(get("/bidList/update/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list?error=unexpected"));
        
        verify(bidListService).findById(1);
    }
    
    @Test
    void updateBid_WithServiceException_ShouldReturnUpdateViewWithError() throws Exception {
        // Given
        when(bidListService.existsById(1)).thenReturn(true);
        when(bidListService.save(any(BidList.class))).thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        mockMvc.perform(post("/bidList/update/1")
                .param("account", "TestAccount")
                .param("type", "TestType")
                .param("bidQuantity", "100.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/update"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "An unexpected error occurred. Please try again."));
    }
    
    @Test
    void deleteBid_WithUnexpectedServiceException_ShouldRedirectWithError() throws Exception {
        // Given
        doThrow(new RuntimeException("Database connection failed")).when(bidListService).deleteById(1);
        
        // When & Then
        mockMvc.perform(get("/bidList/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list?error=unexpected"));
        
        verify(bidListService).deleteById(1);
    }
    
    @Test
    void home_WithBothSuccessAndErrorParams_ShouldPrioritizeError() throws Exception {
        // Given
        when(bidListService.findAll()).thenReturn(bidListEntities);
        
        // When & Then
        mockMvc.perform(get("/bidList/list")
                .param("success", "created")
                .param("error", "notfound"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"))
                .andExpect(model().attribute("errorMessage", "BidList not found"))
                .andExpect(model().attribute("successMessage", "BidList created successfully"));
    }
}
