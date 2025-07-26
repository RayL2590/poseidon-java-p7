package com.nnk.springboot.mapper;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.dto.BidListDTO;

public class BidListMapper {

    public static BidListDTO toDTO(BidList entity) {
        if (entity == null) return null;
        BidListDTO dto = new BidListDTO();
        dto.setBidListId(entity.getBidListId());
        dto.setAccount(entity.getAccount());
        dto.setType(entity.getType());
        dto.setBidQuantity(entity.getBidQuantity());
        dto.setAskQuantity(entity.getAskQuantity());
        dto.setBid(entity.getBid());
        dto.setAsk(entity.getAsk());
        dto.setBenchmark(entity.getBenchmark());
        dto.setCommentary(entity.getCommentary());
        dto.setSecurity(entity.getSecurity());
        dto.setStatus(entity.getStatus());
        dto.setTrader(entity.getTrader());
        dto.setBook(entity.getBook());
        dto.setCreationName(entity.getCreationName());
        dto.setRevisionName(entity.getRevisionName());
        dto.setDealName(entity.getDealName());
        dto.setDealType(entity.getDealType());
        dto.setSourceListId(entity.getSourceListId());
        dto.setSide(entity.getSide());
        return dto;
    }

    public static BidList toEntity(BidListDTO dto) {
        if (dto == null) return null;
        BidList entity = new BidList();
        entity.setBidListId(dto.getBidListId());
        entity.setAccount(dto.getAccount());
        entity.setType(dto.getType());
        entity.setBidQuantity(dto.getBidQuantity());
        entity.setAskQuantity(dto.getAskQuantity());
        entity.setBid(dto.getBid());
        entity.setAsk(dto.getAsk());
        entity.setBenchmark(dto.getBenchmark());
        entity.setCommentary(dto.getCommentary());
        entity.setSecurity(dto.getSecurity());
        entity.setStatus(dto.getStatus());
        entity.setTrader(dto.getTrader());
        entity.setBook(dto.getBook());
        entity.setCreationName(dto.getCreationName());
        entity.setRevisionName(dto.getRevisionName());
        entity.setDealName(dto.getDealName());
        entity.setDealType(dto.getDealType());
        entity.setSourceListId(dto.getSourceListId());
        entity.setSide(dto.getSide());
        return entity;
    }
}