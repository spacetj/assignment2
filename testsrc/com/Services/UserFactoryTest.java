package com.Services;

import com.Model.Adult;
import com.Model.Infant;
import com.Model.User;
import com.Model.YoungAdult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    @Test
    void getUser() {
        User user1 = UserFactory.getUser("Alpha", 18, "","", "M", "Vic");
        assertTrue(user1 instanceof Adult, "User is supposed to be an instance of adult");

        assertTrue(UserFactory.isAdult.test(user1), "isAdult predicate not functioning as expected.");

        User user2 = UserFactory.getUser("Delta", 18, "","", "M", "Vic");

        User user3 = UserFactory.getUser("Bravo", 15, "","", user1, user2,"M", "Vic");
        assertTrue(user3 instanceof YoungAdult, "User is supposed to be an instance of Young Adult");

        assertTrue(UserFactory.isYoungAdult.test(user3), "isYoungAdult is not functioning as expected.");

        User user4 = UserFactory.getUser("Charlie", 2, "","", user1, user2, "M", "Vic");
        assertTrue(user4 instanceof Infant, "User is supposed to be an instance of Infant");

        assertTrue(UserFactory.isInfant.test(user4), "isInfant is not working as expected.");
    }
}