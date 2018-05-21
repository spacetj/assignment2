package com.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RelationTypeTest {

    @Test
    void getRelation() {
        assertEquals(RelationType.GUARDIAN, RelationType.getRelation("parent"));
        assertEquals(RelationType.COPARENT, RelationType.getRelation("couple"));
        assertEquals(RelationType.FRIEND, RelationType.getRelation("friends"));
        assertEquals(RelationType.CLASSMATES, RelationType.getRelation("classmates"));
        assertEquals(RelationType.COLLEAGUES, RelationType.getRelation("colleagues"));
    }

    @Test
    void getString() {
        assertEquals("parent", RelationType.getString(RelationType.GUARDIAN));
        assertEquals("couple", RelationType.getString(RelationType.COPARENT));
        assertEquals("friends", RelationType.getString(RelationType.FRIEND));
        assertEquals("classmates", RelationType.getString(RelationType.CLASSMATES));
        assertEquals("colleagues", RelationType.getString(RelationType.COLLEAGUES));
    }
}