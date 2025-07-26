package com.nnk.springboot.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.services.BidListService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BidListServiceTest {

    @Autowired
    private BidListService bidListService;

    @Test
    void testCreateBidList() {
        // ✅ Utilise le constructeur correct
        BidList bidList = new BidList("Test Account", "Test Type", 100.0);
        
        BidList saved = bidListService.save(bidList);
        
        assertThat(saved.getBidListId()).isNotNull();
        assertThat(saved.getAccount()).isEqualTo("Test Account");
        assertThat(saved.getType()).isEqualTo("Test Type");
        assertThat(saved.getBidQuantity()).isEqualTo(100.0);
    }
    
    @Test
    void testFindAll() {
        List<BidList> bidLists = bidListService.findAll();
        assertThat(bidLists).isNotNull();
    }
    
    @Test
    void testDeleteById() {
        BidList bidList = new BidList("Test", "Type", 50.0);
        BidList saved = bidListService.save(bidList);
        
        bidListService.deleteById(saved.getBidListId());
        
        Optional<BidList> found = bidListService.findById(saved.getBidListId());
        assertThat(found).isEmpty();
    }
}