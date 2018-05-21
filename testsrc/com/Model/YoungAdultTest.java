package com.Model;

import com.Model.Exceptions.*;
import com.Services.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YoungAdultTest {

    private User parent1;
    private User parent2;
    private User youngAdultSameFamily;
    private User userToTest;
    private User diffFamParent1;
    private User diffFamParent2;
    private User youngAdultFromDiffFam;

    @BeforeEach
    void setUp() {
        parent1 = UserFactory.getUser("Alpha", 18, "","", "M", "Vic");
        parent2 = UserFactory.getUser("Delta", 18, "","", "M", "Vic");
        youngAdultSameFamily = UserFactory.getUser("Chalie", 12, "","", parent1, parent2, "M", "Vic");
        userToTest = UserFactory.getUser("Bravo", 15, "","", parent1, parent2,"M", "Vic");
        diffFamParent1 = UserFactory.getUser("Echo", 18, "","", "M", "Vic");
        diffFamParent2 = UserFactory.getUser("Foxtrot", 18, "","", "M", "Vic");
        youngAdultFromDiffFam = UserFactory.getUser("Bravo", 15, "","", diffFamParent1, diffFamParent2,"M", "Vic");
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

        assertFalse(hasAddedRelation, "Young Adults should not be able to add adults as friends");
        assertEquals("com.Model.Exceptions.NotToBeFriendsException", exceptionClass,
                "Should throw NotToBeFriendsException when trying to add an adult and an young adult as friends");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.FRIEND, youngAdultSameFamily));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException
                | NoAvailableException | NotToBeClassmastesException e) {
            hasAddedRelation = false;
            exceptionClass = e.getClass().getName();
        }

        assertFalse(hasAddedRelation, "Young Adults should not be able to add young adults from the same family.");
        assertEquals("com.Model.Exceptions.NotToBeFriendsException", exceptionClass,
                "Should throw NotToBeFriendsException when trying to add 2 young adults from the same family");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.FRIEND, youngAdultFromDiffFam));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException
                | NoAvailableException | NotToBeClassmastesException e) {
            hasAddedRelation = false;
            exceptionClass = e.getClass().getName();
        }

        assertTrue(hasAddedRelation, "Young Adults should be able to add other young adults as friends.");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.COPARENT, parent1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException | NotToBeClassmastesException
                | NotToBeCoupledException | NoAvailableException e) {
            hasAddedRelation = false;
            exceptionClass = e.getClass().getName();
        }

        assertFalse(hasAddedRelation, "Young Adults should not be able to add adults as couple");
        assertEquals("com.Model.Exceptions.NotToBeCoupledException", exceptionClass,
                "Should throw NotToBeCoupledException when trying to add an adult and a young adult as a couple.");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.COLLEAGUES, parent1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException
                | NoAvailableException | NotToBeClassmastesException e) {
            hasAddedRelation = false;
            exceptionClass = e.getClass().getName();
        }

        assertFalse(hasAddedRelation, "Young Adult should not be able to add adults as colleagues");
        assertEquals("com.Model.Exceptions.NotToBeColleaguesException", exceptionClass,
                "Should throw NotToBeColleaguesException when trying to add an adult and an young adult as colleagues");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.CLASSMATES, parent1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException
                | NoAvailableException | NotToBeClassmastesException e) {
            hasAddedRelation = false;
        }

        assertTrue(hasAddedRelation, "Young Adult should be able to add adults as classmates");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.GUARDIAN, parent1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException | NotToBeClassmastesException
                | NotToBeCoupledException | NoAvailableException e) {
            hasAddedRelation = false;
        }

        assertTrue(hasAddedRelation, "Young Adult should be able to add adults as guardians.");

    }

    @Test
    void deleteRelation() {
        boolean hasDeleteRelation = false;

        try {
            hasDeleteRelation = userToTest.deleteRelation(new Relationship(RelationType.GUARDIAN, parent1));
        } catch (NoParentException e) {
            hasDeleteRelation = false;
        }

        assertFalse(hasDeleteRelation, "Young adult should not be able to delete guardian relationship.");

        try {
            userToTest.addRelation(new Relationship(RelationType.FRIEND, youngAdultFromDiffFam));
            userToTest.addRelation(new Relationship(RelationType.CLASSMATES, youngAdultFromDiffFam));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException | NoAvailableException | NotToBeClassmastesException ignored) {}


        try {
            hasDeleteRelation = userToTest.deleteRelation(new Relationship(RelationType.FRIEND, youngAdultFromDiffFam));
        } catch (NoParentException e) {
            hasDeleteRelation = false;
        }

        assertTrue(hasDeleteRelation, "YoungAdult should be able to delete friendship relation.");

        try {
            hasDeleteRelation = userToTest.deleteRelation(new Relationship(RelationType.CLASSMATES, youngAdultFromDiffFam));
        } catch (NoParentException e) {
            hasDeleteRelation = false;
        }

        assertTrue(hasDeleteRelation, "YoungAdult should be able to delete classmate relation.");

    }

    @Test
    void eraseRelationWithUser() {
        try {
            userToTest.addRelation(new Relationship(RelationType.FRIEND, youngAdultFromDiffFam));
            userToTest.addRelation(new Relationship(RelationType.CLASSMATES, youngAdultFromDiffFam));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException | NoAvailableException | NotToBeClassmastesException ignored) {}

        userToTest.eraseRelationWithUser(youngAdultFromDiffFam);

        boolean deletedAllRelation = userToTest.getRelationships().stream().noneMatch(o -> o.getUser().getName().equalsIgnoreCase(youngAdultFromDiffFam.getName()));

        assertTrue(deletedAllRelation, "Young adult should be able to delete all relationships");
    }
}