package com.nnk.springboot.services;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BidListService {

    @Autowired
    private BidListRepository bidListRepository;

    @Transactional(readOnly = true)
    public List<BidList> findAll() {
        return bidListRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<BidList> findById(Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return bidListRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        return bidListRepository.existsById(id);
    }

    public BidList save(BidList bidList) {
        validateBidList(bidList);
        
        // Ajout automatique des dates
        if (bidList.getBidListId() == null) {
            bidList.setCreationDate(new Timestamp(System.currentTimeMillis()));
        } else {
            bidList.setRevisionDate(new Timestamp(System.currentTimeMillis()));
        }
        
        return bidListRepository.save(bidList);
    }

    public void deleteById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID for deletion");
        }
        
        if (!bidListRepository.existsById(id)) {
            throw new IllegalArgumentException("BidList not found with id: " + id);
        }
        
        bidListRepository.deleteById(id);
    }

    private void validateBidList(BidList bidList) {
        if (bidList == null) {
            throw new IllegalArgumentException("BidList cannot be null");
        }
        
        if (bidList.getAccount() == null || bidList.getAccount().trim().isEmpty()) {
            throw new IllegalArgumentException("Account is required");
        }
        
        if (bidList.getType() == null || bidList.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Type is required");
        }
        
        // Validation des champs numériques
        if (bidList.getBidQuantity() != null && bidList.getBidQuantity() < 0) {
            throw new IllegalArgumentException("Bid quantity cannot be negative");
        }
    }
}