package com.nnk.springboot.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleNameTest {

    @Test
    void noArgsConstructor_ShouldCreateEmptyRuleName() {
        RuleName ruleName = new RuleName();
        
        assertNotNull(ruleName);
        assertNull(ruleName.getId());
        assertNull(ruleName.getName());
        assertNull(ruleName.getDescription());
        assertNull(ruleName.getJson());
        assertNull(ruleName.getTemplate());
        assertNull(ruleName.getSqlStr());
        assertNull(ruleName.getSqlPart());
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        Integer id = 1;
        String name = "TestRule";
        String description = "Test Description";
        String json = "{\"test\": true}";
        String template = "Test Template";
        String sqlStr = "SELECT * FROM test";
        String sqlPart = "WHERE id = 1";

        RuleName ruleName = new RuleName(id, name, description, json, template, sqlStr, sqlPart);

        assertEquals(id, ruleName.getId());
        assertEquals(name, ruleName.getName());
        assertEquals(description, ruleName.getDescription());
        assertEquals(json, ruleName.getJson());
        assertEquals(template, ruleName.getTemplate());
        assertEquals(sqlStr, ruleName.getSqlStr());
        assertEquals(sqlPart, ruleName.getSqlPart());
    }

    @Test
    void convenienceConstructor_ShouldSetNameAndDescription() {
        String name = "TestRule";
        String description = "Test Description";

        RuleName ruleName = new RuleName(name, description);

        assertEquals(name, ruleName.getName());
        assertEquals(description, ruleName.getDescription());
        assertNull(ruleName.getId());
        assertNull(ruleName.getJson());
        assertNull(ruleName.getTemplate());
        assertNull(ruleName.getSqlStr());
        assertNull(ruleName.getSqlPart());
    }

    @Test
    void fullConfigConstructor_ShouldSetConfigurationFields() {
        String name = "TestRule";
        String description = "Test Description";
        String json = "{\"enabled\": true}";
        String template = "IF condition THEN action";

        RuleName ruleName = new RuleName(name, description, json, template);

        assertEquals(name, ruleName.getName());
        assertEquals(description, ruleName.getDescription());
        assertEquals(json, ruleName.getJson());
        assertEquals(template, ruleName.getTemplate());
        assertNull(ruleName.getId());
        assertNull(ruleName.getSqlStr());
        assertNull(ruleName.getSqlPart());
    }

    @Test
    void setters_ShouldUpdateFields() {
        RuleName ruleName = new RuleName();
        
        ruleName.setId(5);
        ruleName.setName("UpdatedRule");
        ruleName.setDescription("Updated Description");
        ruleName.setJson("{\"updated\": true}");
        ruleName.setTemplate("Updated Template");
        ruleName.setSqlStr("UPDATE test SET value = 1");
        ruleName.setSqlPart("ORDER BY id");

        assertEquals(5, ruleName.getId());
        assertEquals("UpdatedRule", ruleName.getName());
        assertEquals("Updated Description", ruleName.getDescription());
        assertEquals("{\"updated\": true}", ruleName.getJson());
        assertEquals("Updated Template", ruleName.getTemplate());
        assertEquals("UPDATE test SET value = 1", ruleName.getSqlStr());
        assertEquals("ORDER BY id", ruleName.getSqlPart());
    }

    @Test
    void getters_ShouldReturnCorrectValues() {
        RuleName ruleName = new RuleName();
        ruleName.setId(10);
        ruleName.setName("GetterTest");
        ruleName.setDescription("Getter Test Description");
        ruleName.setJson("{\"getter\": \"test\"}");
        ruleName.setTemplate("Getter Template");
        ruleName.setSqlStr("SELECT getter FROM test");
        ruleName.setSqlPart("LIMIT 1");

        assertEquals(10, ruleName.getId());
        assertEquals("GetterTest", ruleName.getName());
        assertEquals("Getter Test Description", ruleName.getDescription());
        assertEquals("{\"getter\": \"test\"}", ruleName.getJson());
        assertEquals("Getter Template", ruleName.getTemplate());
        assertEquals("SELECT getter FROM test", ruleName.getSqlStr());
        assertEquals("LIMIT 1", ruleName.getSqlPart());
    }

    @Test
    void convenienceConstructor_WithNullValues() {
        RuleName ruleName = new RuleName(null, null);
        
        assertNull(ruleName.getName());
        assertNull(ruleName.getDescription());
        assertNull(ruleName.getId());
    }

    @Test
    void fullConfigConstructor_WithNullValues() {
        RuleName ruleName = new RuleName(null, null, null, null);
        
        assertNull(ruleName.getName());
        assertNull(ruleName.getDescription());
        assertNull(ruleName.getJson());
        assertNull(ruleName.getTemplate());
    }

    @Test
    void setters_WithNullValues() {
        RuleName ruleName = new RuleName("test", "test");
        
        ruleName.setId(null);
        ruleName.setName(null);
        ruleName.setDescription(null);
        ruleName.setJson(null);
        ruleName.setTemplate(null);
        ruleName.setSqlStr(null);
        ruleName.setSqlPart(null);

        assertNull(ruleName.getId());
        assertNull(ruleName.getName());
        assertNull(ruleName.getDescription());
        assertNull(ruleName.getJson());
        assertNull(ruleName.getTemplate());
        assertNull(ruleName.getSqlStr());
        assertNull(ruleName.getSqlPart());
    }

    @Test
    void setters_WithEmptyStrings() {
        RuleName ruleName = new RuleName();
        
        ruleName.setName("");
        ruleName.setDescription("");
        ruleName.setJson("");
        ruleName.setTemplate("");
        ruleName.setSqlStr("");
        ruleName.setSqlPart("");

        assertEquals("", ruleName.getName());
        assertEquals("", ruleName.getDescription());
        assertEquals("", ruleName.getJson());
        assertEquals("", ruleName.getTemplate());
        assertEquals("", ruleName.getSqlStr());
        assertEquals("", ruleName.getSqlPart());
    }
}