package com.Controller;


import com.MiniNet;
import com.Model.Exceptions.NoSuchAgeException;
import com.Model.RelationType;
import com.Model.Relationship;
import com.Model.User;
import com.Services.StoreUtils;
import com.Services.UserFactory;
import com.Services.UserStore;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * AddUserController is the menu used to add a new user.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class AddUserController implements Initializable {

    private UserStore store;
    private SingleSelectionModel<Tab> selectionModel;

    @FXML
    Button addUserButton, addUserStep2Button;

    @FXML
    TabPane addUserTabPane;

    @FXML
    Tab addUserTab, selectParentTab, addStatusTab;

    @FXML
    TextField nameField, ageField, stateField, profilePicField, genderField, statusField;

    @FXML
    Text nameUniqueText, addUserStatusText, stepTwoStatusText;

    @FXML
    ImageView addUserStatusImage;

    @FXML
    ChoiceBox<String> parentADropDown, parentBDropDown;


    public AddUserController() {
        store = UserStore.getInstance();
    }

    /**
     * Implements the function needed for the controller to work.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        resetFields();

        // Adds an event listener to ensure that the name is unique.
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            isUniqueName(newValue);
        });

        // Adds an event listener to parent selection dropdown so parent B option is dependant on whether parent A has a coparent.
        parentADropDown.getSelectionModel().selectedIndexProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null && parentADropDown.getItems().size() > 0) {
                changeDropDownA(parentADropDown.getItems().get(newVal.intValue()));
            }
        });

    }

    /**
     * Reset the text fields that was populated by user input.
     */
    private void resetFields(){
        nameField.setText("");
        ageField.setText("");
        stateField.setText("");
        profilePicField.setText("");
        genderField.setText("");
        statusField.setText("");
        selectParentTab.setDisable(true);
        addStatusTab.setDisable(true);

        selectionModel = addUserTabPane.getSelectionModel();
        parentADropDown.getItems().remove(0, parentADropDown.getItems().size());
        parentADropDown.getItems()
                .addAll(store.getUsers().stream()
                        .filter(o -> UserFactory.isAdult.test(o))
                        .map(User::getName)
                        .collect(Collectors.toList())
                );
    }

    /**
     * The function gets invoked when the Add User button is clicked.
     */
    public void addUser() {
        //Ensure that the age is above 0 and below 115
        if(!nameField.getText().isEmpty() && !ageField.getText().isEmpty() && !genderField.getText().isEmpty() && !stateField.getText().isEmpty()){

            if(!UserFactory.isAgeValid.test(StoreUtils.parseInt(ageField.getText()))){
                try {
                    throw new NoSuchAgeException("No such age for user");
                } catch (NoSuchAgeException e) {
                    nameUniqueText.setText("No such age. Please enter a value between 0 and 150.");
                    nameUniqueText.setVisible(true);
                    return;
                }
            }

            if(Integer.parseInt(ageField.getText()) > UserFactory.YOUNG_ADULT) {
                // If adult, then create the user.
                addUserAndSelectStatus(nameField.getText(), ageField.getText(), profilePicField.getText(),
                        statusField.getText(), genderField.getText(), stateField.getText(), null,null);
            } else {
                // Else step 2: select parents.
                nameUniqueText.setVisible(false);
                stepTwoStatusText.setVisible(false);
                selectParentTab.setDisable(false);
                selectionModel.select(selectParentTab);
            }

        } else {
            nameUniqueText.setText("Name, age, state and gender must be filled.");
            nameUniqueText.setVisible(true);
        }
    }

    /**
     * Add the user and set the StatusText and the StatusImage.
     * @param name Name of the user to be added
     * @param age   Age of the user to be added
     * @param profilePic Profile picture URL of the user
     * @param statusText Status of the user
     * @param gender Gender of the user
     * @param state State of the user
     * @param parentA Guardian A in case the user is an Young Adult or Child
     * @param parentB Guardian B in case the user is an Young Adult or Child
     */
    private void addUserAndSelectStatus(String name, String age, String profilePic, String statusText, String gender, String state, User parentA, User parentB) {

        User u = UserFactory.getUser(name, Integer.parseInt(age), profilePic, statusText, parentA, parentB, gender, state);

        stepTwoStatusText.setVisible(false);

        if(store.getUsers().stream().anyMatch(o -> u.getName().equalsIgnoreCase(name))){
            addUserStatusImage.setImage(MiniNet.getImageFromPath("success.png"));
            addUserStatusText.setText("Successfully Added User");
        } else {
            addUserStatusImage.setImage(MiniNet.getImageFromPath("failed.png"));
            addUserStatusText.setText("Could not add User");
        }
        selectionModel.select(addStatusTab);
        resetFields();
    }

    /**
     * If the user is a Young Adult or Child, the parents need to be set before the adding said user.
     */
    public void addUserStep2() {
        Optional<User> parentA = store.getUsers().stream().filter(o -> o.getName().equalsIgnoreCase(parentADropDown.getValue())).findAny();
        Optional<User> parentB = store.getUsers().stream().filter(o -> o.getName().equalsIgnoreCase(parentBDropDown.getValue())).findAny();

        if(!nameField.getText().isEmpty() && !ageField.getText().isEmpty() && !genderField.getText().isEmpty() && !stateField.getText().isEmpty() && parentA.isPresent() && parentB.isPresent()){
            addUserAndSelectStatus(nameField.getText(), ageField.getText(), profilePicField.getText(), statusField.getText(), genderField.getText(),stateField.getText(), parentA.get(), parentB.get());
        } else {
            stepTwoStatusText.setText("Name, age, gender, state, parent A and parent B must be present");
            stepTwoStatusText.setVisible(true);
        }
    }

    /**
     * Checks if the user name is unique
     * @param name users name
     */
    private void isUniqueName(String name) {

        boolean uniqueName = store.uniqueName(name);

        if(!uniqueName){
            nameUniqueText.setText("Not a unique name.");
            nameUniqueText.setVisible(true);
            addUserButton.setDisable(true);
        } else {
            nameUniqueText.setVisible(false);
            addUserButton.setDisable(false);
        }
    }

    /**
     * Changes Guardian B according to Guardian A selection.
     * If Guardian A has a couple, then only that user can Guardian B.
     * Else only other adults not in a couple can be selected.
     * @param val
     */
    private void changeDropDownA(String val) {
        parentBDropDown.getItems().remove(0, parentBDropDown.getItems().size());
        User parentA = store.getUsers().stream().filter(o -> o.getName().equalsIgnoreCase(val)).findAny().get();
        Optional<User> parentB = parentA.getRelationships().stream().filter(o -> o.getRelation() == RelationType.COPARENT).map(Relationship::getUser).findAny();
        if(parentB.isPresent()){
            // If parent A has a coparent, only display that
            parentBDropDown.getItems().add(parentB.get().getName());
        } else {
            // Else display all users who is an adult and doesnt have a coparent
            parentBDropDown.getItems().addAll(
                    store.getUsers().stream()
                            .filter(o -> UserFactory.isAdult.test(o))
                            .filter(o -> o.getRelationships().stream().noneMatch(rel -> rel.getRelation() == RelationType.COPARENT))
                            .filter(o -> !o.getName().equals(parentA.getName()))
                            .map(User::getName)
                            .collect(Collectors.toList()));
        }
    }

}

