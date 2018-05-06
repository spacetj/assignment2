package com.Controller;

import com.MiniNet;
import com.Model.States;
import com.Model.User;
import javafx.fxml.Initializable;
import javafx.scene.Scene;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;

import static com.MiniNet.getStore;

/**
 * MainMenuController is the initial user interface that is displayed when MiniNet is run.
 *
 * @version 1.0.0 22nd March 2018
 * @author Tejas Cherukara
 */
public class MainMenuController implements Initializable {

    public MainMenuController() {}

    /**
     * Main menu options.
     */
    public Scene getMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. List all users");
        System.out.println("2. Add new user");
        System.out.println("3. Are these two friends?");
        System.out.println("4. Select a user");
        return null;
    }

    /**
     * Parses user actions for main menu.
     * @param input
     */
    public void doAction(Scanner input) {
        System.out.print("Select number from menu: ");
        String action = input.nextLine();
        if(MiniNet.isInputInt(action)){
            int actionInt = Integer.parseInt(action);
            switch(actionInt){
                case 1:
                    showUsers();
                    break;
                case 2:
                    MiniNet.switchState(States.ADD_USER);
                    break;
                case 3:
                    areTheseFriends(input);
                    break;
                case 4:
                    selectUser(input);
                    break;
                default:
                    break;
            }
        } else {
            System.out.println("Please input one of the options above.");
        }
    }

    public String getTitle() {
        return "Main Menu";
    }

    /**
     * Show all the users in the system.
     */
    private void showUsers() {
        if (getStore().getUsers().size() > 0) {
            getStore().getUsers().stream().filter(Objects::nonNull).forEach(user -> {
                System.out.println("\nName: "+user.getName());
                System.out.println("Age: "+user.getAge());

                if (!Objects.equals(user.getProfilePicture(), "")) {
                    System.out.println("Profile Picture: "+user.getProfilePicture());
                }

                if (!Objects.equals(user.getStatus(), "")) {
                    System.out.println("Status: "+user.getStatus()+"\n");
                }

            });
        } else {
            System.out.println("\nNo users currently registered.\n");
        }
    }

    /**
     * Selects a user and changes the menu.
     * @param input
     */
    private void selectUser(Scanner input) {
        getStore().displayUsers();
        boolean specialInput = false;

        Optional<User> user;
        String name;

        do {
            System.out.println("Name of the user (from list above):");
            name = input.nextLine();
            user = getStore().getUserWithName(name);

            if (name.endsWith("s")) {
                specialInput = true;
            }

        } while (!user.isPresent() || specialInput);

        if (user.isPresent()) {
            getStore().setSelectedUser(user.get());
            MiniNet.switchState(States.SELECT_USER);
        } else {
        }
    }

    /**
     * Checks if 2 users have a relation.
     * @param input
     */
    private void areTheseFriends(Scanner input){
        getStore().displayUsers();
        Optional<User> user1, user2;
        String userOne, userTwo;
        boolean specialInput = false;

        do{
            System.out.println("Enter the name of user:");
            userOne = input.nextLine();
            user1 = getStore().getUserWithName(userOne);
                specialInput = true;
        } while(!user1.isPresent() || specialInput);

        do{
            System.out.println("Enter the name of second user:");
            userTwo = input.nextLine();
            user2 = getStore().getUserWithName(userTwo);
                specialInput = true;
        } while(!user2.isPresent() || specialInput);

        if(specialInput){
        } else {
            if (user1.get().getUserRelation(user2.get()).isPresent()) {
                System.out.println(user2.get().getName()+" and "+user1.get().getName()+" has a relation.");
            } else {
                System.out.println("\n\nThey are not friends.\n");
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
