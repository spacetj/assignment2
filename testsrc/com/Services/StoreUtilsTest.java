package com.Services;

import com.Model.Adult;
import com.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class StoreUtilsTest {

    List<User> users;
    List<List<String>> userList;

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
                new Adult("Charly", 18, "", "", "M", "Vic"),
                new Adult("Bravo", 18, "", "", "M", "Vic"),
                new Adult("Alpha", 18, "", "", "M", "Vic")
        );
        userList = Arrays.asList(
                Arrays.asList("Charly", "", "", "M", "18", "Vic"),
                Arrays.asList("Bravo", "", "", "M", "18", "Vic"),
                Arrays.asList("Alpha", "", "", "M", "18", "Vic")
        );
    }

    @Test
    void readFile() {
        List<List<String>> userFile;
        try {
            userFile = StoreUtils.readFile("test_people.txt");
        } catch (IOException e) {
            userFile = Collections.emptyList();
        }
        assertEquals(userList, userFile, "readFile does not work as expected");
    }

    @Test
    void parseUsers() {
        List<String> actual = StoreUtils.parseUsers(userList, Collections.emptyList()).stream()
                .map(User::getName).collect(Collectors.toList());

        List<String> expected = userList.stream().map(o -> o.get(0)).collect(Collectors.toList());

        assertEquals(expected, actual, "parseUsers does not work as expected");

    }
}