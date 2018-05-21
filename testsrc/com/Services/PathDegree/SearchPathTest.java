package com.Services.PathDegree;

import com.Model.Adult;
import com.Model.Exceptions.*;
import com.Model.RelationType;
import com.Model.Relationship;
import com.Model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchPathTest {

    private SearchPath classUnderTest;


    @BeforeEach
    void setUp() {
        Adult user3 = new Adult("Charly", 18, "", "", "M", "Vic");
        Adult user2 = new Adult("Bravo", 18, "", "", "M", "Vic");
        Adult user1 = new Adult("Alpha", 18, "", "", "M", "Vic");

        classUnderTest = new SearchPath(user1, user3);

        try {
            user1.addRelation(new Relationship(RelationType.FRIEND, user2));
            user2.addRelation(new Relationship(RelationType.FRIEND, user3));
        } catch (NotToBeColleaguesException | NotToBeFriendsException | NoAvailableException |
                NotToBeCoupledException | TooYoungException | NotToBeClassmastesException e) {
            e.printStackTrace();
        }
    }

    @Test
    void searchPath() {
        String expected = "Degree of Connection: 2\nAlpha -> Bravo -> Charly";
        String actual = classUnderTest.searchPath();
        Assertions.assertEquals(expected, actual, "Expected path not working " + actual);
    }
}