package com.nnk.springboot.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import com.nnk.springboot.domain.BidList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BidListServiceTest {

    @Autowired
    private BidListService bidListService;
    
    private BidList validBidList;
    
    @BeforeEach
    void setUp() {
        validBidList = new BidList("TestAccount", "TestType", 100.0);
        validBidList.setAskQuantity(90.0);
        validBidList.setBid(10.5);
        validBidList.setAsk(11.0);
    }

    // Happy Path Tests
    
    @Test
    void findAll_ShouldReturnAllBidLists() {
        // Given - Clear any existing data first
        List<BidList> existingBidLists = bidListService.findAll();
        int initialCount = existingBidLists.size();
        
        BidList bidList1 = new BidList("Account1", "Type1", 100.0);
        BidList bidList2 = new BidList("Account2", "Type2", 200.0);
        bidListService.save(bidList1);
        bidListService.save(bidList2);
        
        // When
        List<BidList> result = bidListService.findAll();
        
        // Then
        assertThat(result).hasSize(initialCount + 2);
        assertThat(result).extracting(BidList::getAccount)
                         .contains("Account1", "Account2");
    }
    
    @Test
    void findById_WithValidId_ShouldReturnBidList() {
        // Given
        BidList savedBidList = bidListService.save(validBidList);
        
        // When
        Optional<BidList> result = bidListService.findById(savedBidList.getBidListId());
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getAccount()).isEqualTo("TestAccount");
        assertThat(result.get().getType()).isEqualTo("TestType");
        assertThat(result.get().getBidQuantity()).isEqualTo(100.0);
    }
    
    @Test
    void save_WithValidBidList_ShouldReturnSavedBidList() {
        // When
        BidList result = bidListService.save(validBidList);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBidListId()).isNotNull();
        assertThat(result.getAccount()).isEqualTo("TestAccount");
        assertThat(result.getType()).isEqualTo("TestType");
        assertThat(result.getBidQuantity()).isEqualTo(100.0);
    }
    
    @Test
    void save_WithNewBidList_ShouldSetCreationDate() {
        // Given
        LocalDateTime beforeSave = LocalDateTime.now();
        
        // When
        BidList result = bidListService.save(validBidList);
        
        // Then
        assertThat(result.getCreationDate()).isNotNull();
        assertThat(result.getCreationDate()).isAfterOrEqualTo(beforeSave);
        assertThat(result.getRevisionDate()).isNull();
    }
    
    @Test
    void save_WithExistingBidList_ShouldSetRevisionDate() {
        // Given
        BidList savedBidList = bidListService.save(validBidList);
        savedBidList.setAccount("UpdatedAccount");
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // When
        BidList result = bidListService.save(savedBidList);
        
        // Then
        assertThat(result.getRevisionDate()).isNotNull();
        assertThat(result.getRevisionDate()).isAfterOrEqualTo(beforeUpdate);
        assertThat(result.getAccount()).isEqualTo("UpdatedAccount");
    }
    
    @Test
    void deleteById_WithValidId_ShouldDeleteBidList() {
        // Given
        BidList savedBidList = bidListService.save(validBidList);
        Integer id = savedBidList.getBidListId();
        
        // When
        bidListService.deleteById(id);
        
        // Then
        Optional<BidList> result = bidListService.findById(id);
        assertThat(result).isEmpty();
    }
    
    @Test
    void existsById_WithValidId_ShouldReturnTrue() {
        // Given
        BidList savedBidList = bidListService.save(validBidList);
        
        // When
        boolean result = bidListService.existsById(savedBidList.getBidListId());
        
        // Then
        assertThat(result).isTrue();
    }

    // Edge Cases Tests
    
    @Test
    void findById_WithNullId_ShouldReturnEmpty() {
        // When
        Optional<BidList> result = bidListService.findById(null);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void findById_WithNegativeId_ShouldReturnEmpty() {
        // When
        Optional<BidList> result = bidListService.findById(-1);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        // When
        Optional<BidList> result = bidListService.findById(99999);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void save_WithNullBidList_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> bidListService.save(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("BidList cannot be null");
    }
    
    @Test
    void save_WithNullAccount_ShouldThrowException() {
        // Given
        validBidList.setAccount(null);
        
        // When & Then
        assertThatThrownBy(() -> bidListService.save(validBidList))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Account is required");
    }
    
    @Test
    void save_WithEmptyAccount_ShouldThrowException() {
        // Given
        validBidList.setAccount("");
        
        // When & Then
        assertThatThrownBy(() -> bidListService.save(validBidList))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Account is required");
    }
    
    @Test
    void save_WithBlankAccount_ShouldThrowException() {
        // Given
        validBidList.setAccount("   ");
        
        // When & Then
        assertThatThrownBy(() -> bidListService.save(validBidList))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Account is required");
    }
    
    @Test
    void save_WithNullType_ShouldThrowException() {
        // Given
        validBidList.setType(null);
        
        // When & Then
        assertThatThrownBy(() -> bidListService.save(validBidList))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Type is required");
    }
    
    @Test
    void save_WithEmptyType_ShouldThrowException() {
        // Given
        validBidList.setType("");
        
        // When & Then
        assertThatThrownBy(() -> bidListService.save(validBidList))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Type is required");
    }
    
    @Test
    void save_WithBlankType_ShouldThrowException() {
        // Given
        validBidList.setType("   ");
        
        // When & Then
        assertThatThrownBy(() -> bidListService.save(validBidList))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Type is required");
    }
    
    @Test
    void save_WithNegativeBidQuantity_ShouldThrowException() {
        // Given
        validBidList.setBidQuantity(-10.0);
        
        // When & Then
        assertThatThrownBy(() -> bidListService.save(validBidList))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Bid quantity cannot be negative");
    }
    
    @Test
    void save_WithZeroBidQuantity_ShouldPass() {
        // Given
        validBidList.setBidQuantity(0.0);
        
        // When
        BidList result = bidListService.save(validBidList);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBidQuantity()).isEqualTo(0.0);
    }
    
    @Test
    void save_WithNullBidQuantity_ShouldPass() {
        // Given
        validBidList.setBidQuantity(null);
        
        // When
        BidList result = bidListService.save(validBidList);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBidQuantity()).isNull();
    }
    
    @Test
    void deleteById_WithNullId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> bidListService.deleteById(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid ID for deletion");
    }
    
    @Test
    void deleteById_WithNegativeId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> bidListService.deleteById(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid ID for deletion");
    }
    
    @Test
    void deleteById_WithZeroId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> bidListService.deleteById(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid ID for deletion");
    }
    
    @Test
    void deleteById_WithNonExistentId_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> bidListService.deleteById(99999))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("BidList not found with id: 99999");
    }
    
    @Test
    void existsById_WithNullId_ShouldReturnFalse() {
        // When
        boolean result = bidListService.existsById(null);
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    void existsById_WithNegativeId_ShouldReturnFalse() {
        // When
        boolean result = bidListService.existsById(-1);
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    void existsById_WithNonExistentId_ShouldReturnFalse() {
        // When
        boolean result = bidListService.existsById(99999);
        
        // Then
        assertThat(result).isFalse();
    }

    // Validation Métier Tests
    
    @Test
    void validateBidList_WithValidData_ShouldPass() {
        // Given - validBidList is already set up with valid data
        
        // When
        BidList result = bidListService.save(validBidList);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBidListId()).isNotNull();
    }
    
    @Test
    void save_WithCompleteValidData_ShouldSaveAllFields() {
        // Given
        BidList completeBidList = new BidList("TestAccount", "TestType", 100.0);
        completeBidList.setAskQuantity(90.0);
        completeBidList.setBid(10.5);
        completeBidList.setAsk(11.0);
        completeBidList.setBenchmark("EURIBOR");
        completeBidList.setCommentary("Test comment");
        completeBidList.setSecurity("BOND123");
        completeBidList.setStatus("ACTIVE");
        completeBidList.setTrader("John Doe");
        completeBidList.setBook("TRADING_BOOK_1");
        
        // When
        BidList result = bidListService.save(completeBidList);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccount()).isEqualTo("TestAccount");
        assertThat(result.getType()).isEqualTo("TestType");
        assertThat(result.getBidQuantity()).isEqualTo(100.0);
        assertThat(result.getAskQuantity()).isEqualTo(90.0);
        assertThat(result.getBid()).isEqualTo(10.5);
        assertThat(result.getAsk()).isEqualTo(11.0);
        assertThat(result.getBenchmark()).isEqualTo("EURIBOR");
        assertThat(result.getCommentary()).isEqualTo("Test comment");
        assertThat(result.getSecurity()).isEqualTo("BOND123");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getTrader()).isEqualTo("John Doe");
        assertThat(result.getBook()).isEqualTo("TRADING_BOOK_1");
    }
    
    @Test
    void save_WithMinimalRequiredData_ShouldPass() {
        // Given
        BidList minimalBidList = new BidList("Account", "Type", null);
        
        // When
        BidList result = bidListService.save(minimalBidList);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBidListId()).isNotNull();
        assertThat(result.getAccount()).isEqualTo("Account");
        assertThat(result.getType()).isEqualTo("Type");
    }
}