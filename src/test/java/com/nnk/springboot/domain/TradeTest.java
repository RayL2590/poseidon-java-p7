package com.nnk.springboot.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TradeTest {

    @Test
    void noArgsConstructor_ShouldCreateEmptyTrade() {
        Trade trade = new Trade();
        
        assertNotNull(trade);
        assertNull(trade.getTradeId());
        assertNull(trade.getAccount());
        assertNull(trade.getType());
        assertNull(trade.getBuyQuantity());
        assertNull(trade.getSellQuantity());
        assertNull(trade.getBuyPrice());
        assertNull(trade.getSellPrice());
        assertNull(trade.getTradeDate());
        assertNull(trade.getSecurity());
        assertNull(trade.getStatus());
        assertNull(trade.getTrader());
        assertNull(trade.getBenchmark());
        assertNull(trade.getBook());
        assertNull(trade.getCreationName());
        assertNull(trade.getCreationDate());
        assertNull(trade.getRevisionName());
        assertNull(trade.getRevisionDate());
        assertNull(trade.getDealName());
        assertNull(trade.getDealType());
        assertNull(trade.getSourceListId());
        assertNull(trade.getSide());
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        Integer tradeId = 1;
        String account = "TEST_ACCOUNT";
        String type = "BUY";
        Double buyQuantity = 100.0;
        Double sellQuantity = 50.0;
        Double buyPrice = 25.5;
        Double sellPrice = 26.0;
        LocalDateTime tradeDate = LocalDateTime.now();
        String security = "AAPL";
        String status = "EXECUTED";
        String trader = "John Doe";
        String benchmark = "S&P500";
        String book = "EQUITY_BOOK";
        String creationName = "system";
        LocalDateTime creationDate = LocalDateTime.now().minusDays(1);
        String revisionName = "admin";
        LocalDateTime revisionDate = LocalDateTime.now();
        String dealName = "DEAL_001";
        String dealType = "SPOT";
        String sourceListId = "SRC_123";
        String side = "BUY";

        Trade trade = new Trade(tradeId, account, type, buyQuantity, sellQuantity, buyPrice, sellPrice,
                tradeDate, security, status, trader, benchmark, book, creationName, creationDate,
                revisionName, revisionDate, dealName, dealType, sourceListId, side);

        assertEquals(tradeId, trade.getTradeId());
        assertEquals(account, trade.getAccount());
        assertEquals(type, trade.getType());
        assertEquals(buyQuantity, trade.getBuyQuantity());
        assertEquals(sellQuantity, trade.getSellQuantity());
        assertEquals(buyPrice, trade.getBuyPrice());
        assertEquals(sellPrice, trade.getSellPrice());
        assertEquals(tradeDate, trade.getTradeDate());
        assertEquals(security, trade.getSecurity());
        assertEquals(status, trade.getStatus());
        assertEquals(trader, trade.getTrader());
        assertEquals(benchmark, trade.getBenchmark());
        assertEquals(book, trade.getBook());
        assertEquals(creationName, trade.getCreationName());
        assertEquals(creationDate, trade.getCreationDate());
        assertEquals(revisionName, trade.getRevisionName());
        assertEquals(revisionDate, trade.getRevisionDate());
        assertEquals(dealName, trade.getDealName());
        assertEquals(dealType, trade.getDealType());
        assertEquals(sourceListId, trade.getSourceListId());
        assertEquals(side, trade.getSide());
    }

    @Test
    void twoArgsConstructor_ShouldSetAccountAndType() {
        String account = "TEST_ACCOUNT";
        String type = "SELL";

        Trade trade = new Trade(account, type);

        assertEquals(account, trade.getAccount());
        assertEquals(type, trade.getType());
        assertNull(trade.getTradeId());
        assertNull(trade.getBuyQuantity());
        assertNull(trade.getSellQuantity());
    }

    @Test
    void fourArgsConstructor_ShouldSetEssentialFields() {
        String account = "TRADING_ACC";
        String type = "SWAP";
        Double buyQuantity = 200.0;
        Double sellQuantity = 150.0;

        Trade trade = new Trade(account, type, buyQuantity, sellQuantity);

        assertEquals(account, trade.getAccount());
        assertEquals(type, trade.getType());
        assertEquals(buyQuantity, trade.getBuyQuantity());
        assertEquals(sellQuantity, trade.getSellQuantity());
        assertNull(trade.getTradeId());
        assertNull(trade.getBuyPrice());
        assertNull(trade.getSellPrice());
    }

    @Test
    void setters_ShouldUpdateFields() {
        Trade trade = new Trade();
        
        trade.setTradeId(5);
        trade.setAccount("UPDATED_ACC");
        trade.setType("OPTION");
        trade.setBuyQuantity(300.0);
        trade.setSellQuantity(250.0);
        trade.setBuyPrice(15.75);
        trade.setSellPrice(16.25);
        LocalDateTime now = LocalDateTime.now();
        trade.setTradeDate(now);
        trade.setSecurity("GOOGL");
        trade.setStatus("PENDING");
        trade.setTrader("Jane Smith");
        trade.setBenchmark("NASDAQ");
        trade.setBook("TECH_BOOK");
        trade.setCreationName("user1");
        trade.setCreationDate(now);
        trade.setRevisionName("user2");
        trade.setRevisionDate(now);
        trade.setDealName("DEAL_002");
        trade.setDealType("FORWARD");
        trade.setSourceListId("SRC_456");
        trade.setSide("SELL");

        assertEquals(5, trade.getTradeId());
        assertEquals("UPDATED_ACC", trade.getAccount());
        assertEquals("OPTION", trade.getType());
        assertEquals(300.0, trade.getBuyQuantity());
        assertEquals(250.0, trade.getSellQuantity());
        assertEquals(15.75, trade.getBuyPrice());
        assertEquals(16.25, trade.getSellPrice());
        assertEquals(now, trade.getTradeDate());
        assertEquals("GOOGL", trade.getSecurity());
        assertEquals("PENDING", trade.getStatus());
        assertEquals("Jane Smith", trade.getTrader());
        assertEquals("NASDAQ", trade.getBenchmark());
        assertEquals("TECH_BOOK", trade.getBook());
        assertEquals("user1", trade.getCreationName());
        assertEquals(now, trade.getCreationDate());
        assertEquals("user2", trade.getRevisionName());
        assertEquals(now, trade.getRevisionDate());
        assertEquals("DEAL_002", trade.getDealName());
        assertEquals("FORWARD", trade.getDealType());
        assertEquals("SRC_456", trade.getSourceListId());
        assertEquals("SELL", trade.getSide());
    }

    @Test
    void twoArgsConstructor_WithNullValues() {
        Trade trade = new Trade(null, null);
        
        assertNull(trade.getAccount());
        assertNull(trade.getType());
        assertNull(trade.getTradeId());
    }

    @Test
    void fourArgsConstructor_WithNullValues() {
        Trade trade = new Trade(null, null, null, null);
        
        assertNull(trade.getAccount());
        assertNull(trade.getType());
        assertNull(trade.getBuyQuantity());
        assertNull(trade.getSellQuantity());
    }

    @Test
    void setters_WithNullValues() {
        Trade trade = new Trade("test", "test");
        
        trade.setTradeId(null);
        trade.setAccount(null);
        trade.setType(null);
        trade.setBuyQuantity(null);
        trade.setSellQuantity(null);
        trade.setBuyPrice(null);
        trade.setSellPrice(null);
        trade.setTradeDate(null);
        trade.setSecurity(null);
        trade.setStatus(null);
        trade.setTrader(null);
        trade.setBenchmark(null);
        trade.setBook(null);
        trade.setCreationName(null);
        trade.setCreationDate(null);
        trade.setRevisionName(null);
        trade.setRevisionDate(null);
        trade.setDealName(null);
        trade.setDealType(null);
        trade.setSourceListId(null);
        trade.setSide(null);

        assertNull(trade.getTradeId());
        assertNull(trade.getAccount());
        assertNull(trade.getType());
        assertNull(trade.getBuyQuantity());
        assertNull(trade.getSellQuantity());
        assertNull(trade.getBuyPrice());
        assertNull(trade.getSellPrice());
        assertNull(trade.getTradeDate());
        assertNull(trade.getSecurity());
        assertNull(trade.getStatus());
        assertNull(trade.getTrader());
        assertNull(trade.getBenchmark());
        assertNull(trade.getBook());
        assertNull(trade.getCreationName());
        assertNull(trade.getCreationDate());
        assertNull(trade.getRevisionName());
        assertNull(trade.getRevisionDate());
        assertNull(trade.getDealName());
        assertNull(trade.getDealType());
        assertNull(trade.getSourceListId());
        assertNull(trade.getSide());
    }

    @Test
    void setters_WithEmptyStrings() {
        Trade trade = new Trade();
        
        trade.setAccount("");
        trade.setType("");
        trade.setSecurity("");
        trade.setStatus("");
        trade.setTrader("");
        trade.setBenchmark("");
        trade.setBook("");
        trade.setCreationName("");
        trade.setRevisionName("");
        trade.setDealName("");
        trade.setDealType("");
        trade.setSourceListId("");
        trade.setSide("");

        assertEquals("", trade.getAccount());
        assertEquals("", trade.getType());
        assertEquals("", trade.getSecurity());
        assertEquals("", trade.getStatus());
        assertEquals("", trade.getTrader());
        assertEquals("", trade.getBenchmark());
        assertEquals("", trade.getBook());
        assertEquals("", trade.getCreationName());
        assertEquals("", trade.getRevisionName());
        assertEquals("", trade.getDealName());
        assertEquals("", trade.getDealType());
        assertEquals("", trade.getSourceListId());
        assertEquals("", trade.getSide());
    }

    @Test
    void buyTransaction_ShouldBeCreatedCorrectly() {
        Trade buyTrade = new Trade("EQUITY_ACC", "BUY", 100.0, 0.0);
        buyTrade.setBuyPrice(50.0);
        buyTrade.setSide("BUY");
        buyTrade.setStatus("EXECUTED");

        assertEquals("EQUITY_ACC", buyTrade.getAccount());
        assertEquals("BUY", buyTrade.getType());
        assertEquals(100.0, buyTrade.getBuyQuantity());
        assertEquals(0.0, buyTrade.getSellQuantity());
        assertEquals(50.0, buyTrade.getBuyPrice());
        assertEquals("BUY", buyTrade.getSide());
        assertEquals("EXECUTED", buyTrade.getStatus());
    }

    @Test
    void sellTransaction_ShouldBeCreatedCorrectly() {
        Trade sellTrade = new Trade("BOND_ACC", "SELL", 0.0, 75.0);
        sellTrade.setSellPrice(45.5);
        sellTrade.setSide("SELL");
        sellTrade.setStatus("PENDING");

        assertEquals("BOND_ACC", sellTrade.getAccount());
        assertEquals("SELL", sellTrade.getType());
        assertEquals(0.0, sellTrade.getBuyQuantity());
        assertEquals(75.0, sellTrade.getSellQuantity());
        assertEquals(45.5, sellTrade.getSellPrice());
        assertEquals("SELL", sellTrade.getSide());
        assertEquals("PENDING", sellTrade.getStatus());
    }
}