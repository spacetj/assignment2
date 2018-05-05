package com.Controller;

import com.Model.States;
import com.Model.User;
import javafx.scene.Scene;

import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

/**
 * MainMenu is the initial user interface that is displayed when MiniNet is run.
 *
 * @version 1.0.0 22nd March 2018
 * @author Tejas Cherukara
 */
public class MainMenu implements Menu {

    public MainMenu() {}

    /**
     * Main menu options.
     */
    @Override
    public Scene getMenu() {
        System.out.println("\nMain Menu:");
        System.out.println(MiniNet.DIVIDER);
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
    @Override
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
                    defaultAction(actionInt);
                    break;
            }
        } else {
            System.out.println("Please input one of the options above.");
        }
    }

    @Override
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

            if (isSpecialInput(name)) {
                specialInput = true;
            }

        } while (!user.isPresent() || specialInput);

        if (user.isPresent()) {
            getStore().setSelectedUser(user.get());
            MiniNet.switchState(States.SELECT_USER);
        } else {
            checkSpecialInput(name);
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
            if(isSpecialInput(userOne)){
                specialInput = true;
            }
        } while(!user1.isPresent() || specialInput);

        //Check if the back / exit / help input is given.
        if(specialInput){
            checkSpecialInput(userOne);
        }

        do{
            System.out.println("Enter the name of second user:");
            userTwo = input.nextLine();
            user2 = getStore().getUserWithName(userTwo);
            if(isSpecialInput(userTwo)){
                specialInput = true;
            }
        } while(!user2.isPresent() || specialInput);

        if(specialInput){
            checkSpecialInput(userOne);
        } else {
            if (user1.get().getUserRelation(user2.get()).isPresent()) {
                System.out.println(user2.get().getName()+" and "+user1.get().getName()+" has a relation.");
            } else {
                System.out.println("\n\nThey are not friends.\n");
            }
        }

    }
}
