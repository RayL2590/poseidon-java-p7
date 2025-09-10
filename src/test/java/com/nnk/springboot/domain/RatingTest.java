package com.nnk.springboot.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {

    @Test
    void noArgsConstructor_ShouldCreateEmptyRating() {
        Rating rating = new Rating();
        
        assertNotNull(rating);
        assertNull(rating.getId());
        assertNull(rating.getMoodysRating());
        assertNull(rating.getSandPRating());
        assertNull(rating.getFitchRating());
        assertNull(rating.getOrderNumber());
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        Integer id = 1;
        String moodysRating = "Aaa";
        String sandPRating = "AAA";
        String fitchRating = "AAA";
        Integer orderNumber = 1;

        Rating rating = new Rating(id, moodysRating, sandPRating, fitchRating, orderNumber);

        assertEquals(id, rating.getId());
        assertEquals(moodysRating, rating.getMoodysRating());
        assertEquals(sandPRating, rating.getSandPRating());
        assertEquals(fitchRating, rating.getFitchRating());
        assertEquals(orderNumber, rating.getOrderNumber());
    }

    @Test
    void threeArgsConstructor_ShouldSetRatingFields() {
        String moodysRating = "A1";
        String sandPRating = "A+";
        String fitchRating = "A+";

        Rating rating = new Rating(moodysRating, sandPRating, fitchRating);

        assertEquals(moodysRating, rating.getMoodysRating());
        assertEquals(sandPRating, rating.getSandPRating());
        assertEquals(fitchRating, rating.getFitchRating());
        assertNull(rating.getId());
        assertNull(rating.getOrderNumber());
    }

    @Test
    void fourArgsConstructor_ShouldSetRatingFieldsWithOrder() {
        String moodysRating = "Ba2";
        String sandPRating = "BB";
        String fitchRating = "BB";
        Integer orderNumber = 15;

        Rating rating = new Rating(moodysRating, sandPRating, fitchRating, orderNumber);

        assertEquals(moodysRating, rating.getMoodysRating());
        assertEquals(sandPRating, rating.getSandPRating());
        assertEquals(fitchRating, rating.getFitchRating());
        assertEquals(orderNumber, rating.getOrderNumber());
        assertNull(rating.getId());
    }

    @Test
    void setters_ShouldUpdateFields() {
        Rating rating = new Rating();
        
        rating.setId(5);
        rating.setMoodysRating("Baa1");
        rating.setSandPRating("BBB+");
        rating.setFitchRating("BBB+");
        rating.setOrderNumber(10);

        assertEquals(5, rating.getId());
        assertEquals("Baa1", rating.getMoodysRating());
        assertEquals("BBB+", rating.getSandPRating());
        assertEquals("BBB+", rating.getFitchRating());
        assertEquals(10, rating.getOrderNumber());
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        Rating rating = new Rating();
        rating.setId(10);
        rating.setMoodysRating("B1");
        rating.setSandPRating("B+");
        rating.setFitchRating("B+");
        rating.setOrderNumber(18);

        assertEquals(10, rating.getId());
        assertEquals("B1", rating.getMoodysRating());
        assertEquals("B+", rating.getSandPRating());
        assertEquals("B+", rating.getFitchRating());
        assertEquals(18, rating.getOrderNumber());
    }

    @Test
    void threeArgsConstructor_WithNullValues() {
        Rating rating = new Rating(null, null, null);
        
        assertNull(rating.getMoodysRating());
        assertNull(rating.getSandPRating());
        assertNull(rating.getFitchRating());
        assertNull(rating.getId());
        assertNull(rating.getOrderNumber());
    }

    @Test
    void fourArgsConstructor_WithNullValues() {
        Rating rating = new Rating(null, null, null, null);
        
        assertNull(rating.getMoodysRating());
        assertNull(rating.getSandPRating());
        assertNull(rating.getFitchRating());
        assertNull(rating.getOrderNumber());
        assertNull(rating.getId());
    }

    @Test
    void setters_WithNullValues() {
        Rating rating = new Rating("A", "A", "A");
        
        rating.setId(null);
        rating.setMoodysRating(null);
        rating.setSandPRating(null);
        rating.setFitchRating(null);
        rating.setOrderNumber(null);

        assertNull(rating.getId());
        assertNull(rating.getMoodysRating());
        assertNull(rating.getSandPRating());
        assertNull(rating.getFitchRating());
        assertNull(rating.getOrderNumber());
    }

    @Test
    void setters_WithEmptyStrings() {
        Rating rating = new Rating();
        
        rating.setMoodysRating("");
        rating.setSandPRating("");
        rating.setFitchRating("");

        assertEquals("", rating.getMoodysRating());
        assertEquals("", rating.getSandPRating());
        assertEquals("", rating.getFitchRating());
    }

    @Test
    void investmentGradeRatings_ShouldBeCreatedCorrectly() {
        // Test investment grade ratings
        Rating aaa = new Rating("Aaa", "AAA", "AAA", 1);
        Rating aa1 = new Rating("Aa1", "AA+", "AA+", 2);
        Rating a3 = new Rating("A3", "A-", "A-", 8);
        Rating bbb3 = new Rating("Baa3", "BBB-", "BBB-", 12);

        assertEquals("Aaa", aaa.getMoodysRating());
        assertEquals("AAA", aaa.getSandPRating());
        assertEquals("AAA", aaa.getFitchRating());
        assertEquals(1, aaa.getOrderNumber());

        assertEquals("Aa1", aa1.getMoodysRating());
        assertEquals("AA+", aa1.getSandPRating());
        assertEquals("AA+", aa1.getFitchRating());
        assertEquals(2, aa1.getOrderNumber());
    }

    @Test
    void speculativeRatings_ShouldBeCreatedCorrectly() {
        // Test speculative ratings
        Rating ba1 = new Rating("Ba1", "BB+", "BB+", 13);
        Rating b2 = new Rating("B2", "B", "B", 17);
        Rating c = new Rating("C", "D", "D", 21);

        assertEquals("Ba1", ba1.getMoodysRating());
        assertEquals("BB+", ba1.getSandPRating());
        assertEquals("BB+", ba1.getFitchRating());
        assertEquals(13, ba1.getOrderNumber());

        assertEquals("C", c.getMoodysRating());
        assertEquals("D", c.getSandPRating());
        assertEquals("D", c.getFitchRating());
        assertEquals(21, c.getOrderNumber());
    }
}