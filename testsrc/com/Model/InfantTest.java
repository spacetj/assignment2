package com.Model;

import com.Model.Exceptions.*;
import com.Services.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InfantTest {

    private User parent1;
    private User parent2;
    private User userToTest;

    @BeforeEach
    void setUp() {
        parent1 = UserFactory.getUser("Alpha", 18, "","", "M", "Vic");
        parent2 = UserFactory.getUser("Delta", 18, "","", "M", "Vic");
        userToTest = UserFactory.getUser("Charlie", 2, "","", parent1, parent2, "M", "Vic");
    }

    @Test
    void addRelation() {
        boolean hasAddedRelation;
        String exceptionClass = "";

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.FRIEND, parent1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException
                | NoAvailableException | NotToBeClassmastesException e) {
            hasAddedRelation = false;
            exceptionClass = e.getClass().getName();
        }

        assertFalse(hasAddedRelation, "Children should not be able to add adults as friends");
        assertEquals("com.Model.Exceptions.TooYoungException", exceptionClass,
                "Should throw NotToBeFriendsException when trying to add an adult and an infant as friends");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.COPARENT, parent1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException | NotToBeClassmastesException
                | NotToBeCoupledException | NoAvailableException e) {
            hasAddedRelation = false;
            exceptionClass = e.getClass().getName();
        }

        assertFalse(hasAddedRelation, "Children should not be able to add adults as couple");
        assertEquals("com.Model.Exceptions.NotToBeCoupledException", exceptionClass,
                "Should throw NotToBeCoupledException when trying to add an adult and an Infant as a couple.");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.COLLEAGUES, parent1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException
                | NoAvailableException | NotToBeClassmastesException e) {
            hasAddedRelation = false;
            exceptionClass = e.getClass().getName();
        }

        assertFalse(hasAddedRelation, "Children should not be able to add adults as colleagues");
        assertEquals("com.Model.Exceptions.NotToBeColleaguesException", exceptionClass,
                "Should throw NotToBeColleaguesException when trying to add an adult and an infant as colleagues");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.CLASSMATES, parent1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException
                | NoAvailableException | NotToBeClassmastesException e) {
            hasAddedRelation = false;
            exceptionClass = e.getClass().getName();
        }

        assertFalse(hasAddedRelation, "Children should not be able to add adults as classmates");
        assertEquals("com.Model.Exceptions.NotToBeClassmastesException", exceptionClass,
                "Should throw NotToBeClassmastesException when trying to add an adult and an infant as colleagues");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.GUARDIAN, parent1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException | NotToBeClassmastesException
                | NotToBeCoupledException | NoAvailableException e) {
            hasAddedRelation = false;
        }

        assertTrue(hasAddedRelation, "Infants should be able to add adults as guardians.");

    }

    @Test
    void deleteRelation() {
        boolean hasDeletedRelation = false;

        try {
            hasDeletedRelation = userToTest.deleteRelation(new Relationship(RelationType.GUARDIAN, parent1));
        } catch (NoParentException e) {
            hasDeletedRelation = false;
        }

        assertFalse(hasDeletedRelation, "Infant should not be able to delete guardian relationship");

    }

    @Test
    void eraseRelationWithUser() {
        boolean erasedAllRelations = false;

        userToTest.eraseRelationWithUser(parent1);

        erasedAllRelations = userToTest.getRelationships().stream().noneMatch(o -> o.getUser().getName().equalsIgnoreCase(parent1.getName()));

        assertFalse(erasedAllRelations, "Infants should not be able to delete relations with guardians");

    }
}