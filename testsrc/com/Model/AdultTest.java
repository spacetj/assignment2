package com.Model;

import com.Model.Exceptions.*;
import com.Services.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdultTest {

    User userToTest;
    User usersCoParent;
    User child1;
    User child2;
    User friendUser;

    @BeforeEach
    void setUp() {
        userToTest = UserFactory.getUser("Alpha", 18, "","", "M", "Vic");
        usersCoParent = UserFactory.getUser("Delta", 18, "","", "M", "Vic");
        friendUser = UserFactory.getUser("Echo", 18, "","", "M", "Vic");
        try {
            friendUser.addRelation(new Relationship(RelationType.FRIEND, userToTest));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException | NoAvailableException | NotToBeClassmastesException e) {
            e.printStackTrace();
        }
        try {
            friendUser.addRelation(new Relationship(RelationType.COLLEAGUES, userToTest));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException | NoAvailableException | NotToBeClassmastesException e) {
            e.printStackTrace();
        }
        try {
            friendUser.addRelation(new Relationship(RelationType.CLASSMATES, userToTest));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException | NoAvailableException | NotToBeClassmastesException e) {
            e.printStackTrace();
        }
        child1 = UserFactory.getUser("Chalie", 12, "","", userToTest, usersCoParent, "M", "Vic");
        child2 = UserFactory.getUser("Bravo", 15, "","", userToTest, usersCoParent,"M", "Vic");
    }

    @Test
    void addRelation() {
        boolean hasAddedRelation;
        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.FRIEND, child1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException | NotToBeCoupledException
                | NoAvailableException | NotToBeClassmastesException e) {
            hasAddedRelation = false;
        }

        assertFalse(hasAddedRelation, "Adults should not add Young adult as a friend.");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.COPARENT, child1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException | NotToBeCoupledException
                | NoAvailableException | NotToBeClassmastesException e) {
            hasAddedRelation = false;
        }

        assertFalse(hasAddedRelation, "Adults should not add Young adult as a coparent.");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.CLASSMATES, child1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException | NotToBeCoupledException
                | NoAvailableException | NotToBeClassmastesException e) {
            hasAddedRelation = false;
        }

        assertTrue(hasAddedRelation, "Adults should be able to add young adult as a class mate.");

        try {
            hasAddedRelation = userToTest.addRelation(new Relationship(RelationType.COLLEAGUES, child1));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException | NotToBeCoupledException
                | NoAvailableException | NotToBeClassmastesException e) {
            hasAddedRelation = false;
        }

        assertFalse(hasAddedRelation, "Adults should not add Young adult as a colleagues.");

    }

    @Test
    void deleteRelation() {
        boolean hasDeletedRelation = false;
        try {
            hasDeletedRelation = userToTest.deleteRelation(new Relationship(RelationType.DEPENDANT, child1));
        } catch (NoParentException e) {
            hasDeletedRelation = false;
        }
        assertFalse(hasDeletedRelation, "Adult should not be able to delete dependant relations");

        try {
            hasDeletedRelation = userToTest.deleteRelation(new Relationship(RelationType.COPARENT, usersCoParent));
        } catch (NoParentException e) {
            hasDeletedRelation = false;
        }

        assertFalse(hasDeletedRelation, "Adult should not be able to delete Coparent relationship.");


        try {
            hasDeletedRelation = userToTest.deleteRelation(new Relationship(RelationType.FRIEND, friendUser));
        } catch (NoParentException e) {
            hasDeletedRelation = false;
        }

        assertTrue(hasDeletedRelation, "User should be able to delete friend relations");

        try {
            hasDeletedRelation = userToTest.deleteRelation(new Relationship(RelationType.COLLEAGUES, friendUser));
        } catch (NoParentException e) {
            hasDeletedRelation = false;
        }

        assertTrue(hasDeletedRelation, "User should be able to delete colleague relationship");

        try {
            hasDeletedRelation = userToTest.deleteRelation(new Relationship(RelationType.CLASSMATES, friendUser));
        } catch (NoParentException e) {
            hasDeletedRelation = false;
        }

        assertTrue(hasDeletedRelation, "User should be able to delete classmate relationship");

    }

    @Test
    void eraseRelationWithUser() {
        friendUser.eraseRelationWithUser(userToTest);
        boolean deletedRelationsWithUser = friendUser.getRelationships().stream().anyMatch(o -> o.getUser().getName().equalsIgnoreCase(userToTest.getName()));

        assertFalse(deletedRelationsWithUser, "Adult should be able to erase all relations with user");
    }

    @Test
    void canDeleteUser() {
        boolean canDeleteUser = false;

        try {
            canDeleteUser = userToTest.canDeleteUser();
        } catch (NoParentException e) {
            canDeleteUser = false;
        }

        assertFalse(canDeleteUser, "Adult should be able to delete user with a couple or dependent");

        try {
            canDeleteUser = friendUser.canDeleteUser();
        } catch (NoParentException e) {
            canDeleteUser = false;
        }

        assertTrue(canDeleteUser, "Adult should be able to delete user with no couple of dependant relation.");
    }


}