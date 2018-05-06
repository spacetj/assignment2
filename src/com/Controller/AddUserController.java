package com.Controller;


import com.MiniNet;
import com.Model.States;
import com.Model.RelationType;
import com.Model.User;
import com.Services.UserFactory;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.MiniNet.getStore;

/**
 * AddUserController is the menu used to add a new user.
 *
 * @version 1.0.0 22nd March 2018
 * @author Tejas Cherukara
 */
public class AddUserController implements Initializable {

    public AddUserController() {
    }

    /**
     * Add User menu options
     */
    public Scene getMenu() {
        GridPane pane = new GridPane();

        Label nameLabel = new Label("Name");
        TextField nameField = new TextField();
        Label ageLabel = new Label("Age");
        TextField ageField = new TextField();
        pane.setAlignment(Pos.TOP_CENTER);
        pane.add(nameLabel, 1,1);
        pane.add(nameField, 2,1);
        pane.add(ageLabel, 1,1);
        pane.add(ageField, 2,1);
        return new Scene(pane, 500, 500);

//        System.out.println("\nAdd User");
//        System.out.println(MiniNet.DIVIDER);
    }

    /**
     * Parse add user inputs.
     *
     * @param input
     */
    public void doAction(Scanner input) {

        String name;
        do {
            System.out.print("Enter name (must be unique): ");
            name = input.nextLine();
        } while (Objects.equals(name, "") || Objects.equals(name, " ") || !getStore().uniqueName(name));

        String age;
        do {
            System.out.print("Enter age (must be number): ");
            age = input.nextLine();
        } while (!MiniNet.isInputInt(age) || Integer.parseInt(age) < 0);

        Integer ageInt = Integer.parseInt(age);

        System.out.print("Enter profile pic (Optional): ");
        String profilePicture = input.nextLine();

        System.out.print("Enter status (Optional): ");
        String status = input.nextLine();

        if (ageInt >= UserFactory.YOUNG_ADULT) {
            getStore().addUser(UserFactory.getUser(name, ageInt, profilePicture, status, null, null));
        } else {
            getYoungAdult(input, name, ageInt, profilePicture, status);
        }
        MiniNet.switchState(States.MAIN_MENU);
    }

    public String getTitle() {
        return "Add New User";
    }

    /**
     * Get parent 1 and parent 2 for Young adult users.
     *
     * @param input          Scanner to get parent 1 and 2 name
     * @param name           Name of the new Young Adult / infant.
     * @param ageInt         Age of the new Young adult / infant.
     * @param profilePicture profile picture of the new Young adult / infant.
     * @param status         status of the new Young adult / infant.
     */
    private void getYoungAdult(Scanner input, String name, Integer ageInt, String profilePicture, String status) {
        // If age is less than 16, they need to choose 2 parents.
        List<User> adults = getStore().getUsers().stream().filter(UserFactory.isAdult).collect(Collectors.toList());
        if (adults.size() >= 2) {
            IntStream.range(0, adults.size()).mapToObj(i -> (i + 1) + ". " + getStore().getUsers().get(i).getName()).forEach(System.out::println);

            Optional<User> guardianOne, guardianTwo;
            String guardianOneName, guardianTwoName;

            do {
                System.out.print("Enter name of guardian 1: ");
                guardianOneName = input.nextLine();
                guardianOne = getStore().getUserWithName(guardianOneName);
            } while (!guardianOne.isPresent());

            if (!guardianOne.isPresent()) {
            }

            do {//Check if the parent 1 already has a coparent assciated with them.
                if (guardianOne.isPresent() && guardianOne.get().getFriends().stream()
                        .anyMatch(o -> o.getRelation() == RelationType.COPARENT)) {
                    guardianTwo = checkForCoParent(guardianOne.get());
                    guardianTwoName = guardianTwo.get().getName();

                } else {
                    System.out.print("Enter name of guardian 2 (Cannot be same user as guardian 1): ");
                    guardianTwoName = input.nextLine();
                    guardianTwo = getStore().getUserWithName(guardianTwoName);
                }
            } while (!guardianTwo.isPresent() || guardianTwo.get() == guardianOne.get());

            if (guardianTwo.isPresent()) {
                getStore().addUser(
                        UserFactory.getUser(name, ageInt, profilePicture, status, guardianOne.get(), guardianTwo.get(), null, null)
                );
            } else {
                System.out.println("\n\n Error creating user.\n\n");
            }
        } else {
            System.out.println("\nNeed at least 2 adults to add a young adult.\n");
        }
    }

    /**
     * Check if a person already has a coparent relationship.
     *
     * @param guardianOne user to check the coparent relationship for.
     * @return Optional user to see if a coparent exists.
     */
    private Optional<User> checkForCoParent(User guardianOne) {
        Optional<User> guardianTwo;
        guardianTwo = Optional.ofNullable(guardianOne
                .getFriends().stream()
                .filter(o -> o.getRelation() == RelationType.COPARENT)
                .findFirst().get().getUser());
        System.out.println("\n\n" + guardianOne.getName() + "'s partner is " + guardianTwo.get().getName());
        System.out.println(guardianTwo.get().getName() + " is automatically selected as second guardian.");
        return guardianTwo;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

