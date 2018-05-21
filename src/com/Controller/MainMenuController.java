package com.Controller;

import com.MiniNet;
import com.Model.Exceptions.NoParentException;
import com.Model.RelationType;
import com.Model.Relationship;
import com.Model.User;
import com.Services.PathDegree.SearchPath;
import com.Services.UserFactory;
import com.Services.UserStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MainMenuController is the initial user interface that is displayed when MiniNet is run.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class MainMenuController implements Initializable {

    @FXML
    TableView<User> userTable;

    @FXML
    TableColumn<User,String> nameCol, genderCol, statusCol, stateCol;

    @FXML
    TableColumn<User, Integer> ageCol;

    @FXML
    Text areTheyConnectedText, degreeOfFriendshipText, deleteUserStatusText;

    @FXML
    Label mainMenuLabel;

    @FXML
    ImageView deleteUserStatusImage;

    @FXML
    ChoiceBox<String> areTheyConnectedDropDown1, areTheyConnectedDropDown2, degreeOfFriendDropDown1,
            degreeOfFriendDropDown2, deleteUserDropDown;

    ObservableList<User> list = FXCollections.observableArrayList();
    UserStore store;

    public MainMenuController() {
        store = UserStore.getInstance();
    }

    /**
     * Initialises the are they connected dropdowns.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(!store.isActiveConnection()){
            mainMenuLabel.setText("Error: db.db not found in assets folder.");
        }
        areTheyConnectedDropDown();
    }

    /**
     * Displays all the users in the database.
     */
    public void displayAllUsers() {
        list.remove(0, list.size());
        list.addAll(store.getUsers());

        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        stateCol.setCellValueFactory(new PropertyValueFactory<>("residentialState"));
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));

        userTable.setItems(list);
    }

    /**
     * Sets up the are they connected drop down options.
     */
    public void areTheyConnectedDropDown() {
        setDropDown(areTheyConnectedText, areTheyConnectedDropDown1, areTheyConnectedDropDown2);
    }

    /**
     * Sets up the degree of friendship drop down options.
     */
    public void degreeOfFriendshipDropDown(){
        setDropDown(degreeOfFriendshipText, degreeOfFriendDropDown1, degreeOfFriendDropDown2);
    }

    /**
     * Populate the delete user drop down
     */
    public void deleteUserDropDown(){
        deleteUserStatusImage.setVisible(false);
        setDropDown(deleteUserStatusText, deleteUserDropDown, null);
    }

    /**
     * Sets dropdown with the name of the users.
     * @param text
     * @param dropDown1
     * @param dropDown2
     */
    private void setDropDown(Text text, ChoiceBox<String> dropDown1, ChoiceBox<String> dropDown2) {
        text.setVisible(false);
        dropDown1.getItems().remove(0, dropDown1.getItems().size());

        dropDown1.getItems().addAll(
                store.getUsers().stream().map(User::getName).collect(Collectors.toList())
        );

        if (dropDown2 != null) {
            dropDown2.getItems().remove(0, dropDown2.getItems().size());
            dropDown2.getItems().addAll(
                    store.getUsers().stream().map(User::getName).collect(Collectors.toList())
            );
        }
    }

    /**
     * The method is invoked when the are you connected drop down button is clicked.
     *
     * It checks if 2 users have a directly relationship including a sibling relationship.
     */
    public void areTheyConnected() {
        Optional<User> user1 = store.getUserWithName(areTheyConnectedDropDown1.getValue());
        Optional<User> user2 = store.getUserWithName(areTheyConnectedDropDown2.getValue());

        if (user1.isPresent() && user2.isPresent()) {
            List<Relationship> rel = user1.get().getRelationships().stream().filter(o -> o.getUser().getName() == user2.get().getName()).collect(Collectors.toList());
            // If young adult or infant, check and add siblings
            if(UserFactory.isYoungAdult.test(user1.get()) || UserFactory.isInfant.test(user1.get())){
                user1.get().getSiblings()
                        .forEach(o -> {
                            boolean existsAlready = rel.stream().anyMatch(relationship ->
                                    relationship.getUser().getName().equalsIgnoreCase(o.getUser().getName()) && o.getRelation() == relationship.getRelation());
                            if(!existsAlready) rel.add(o);
                        });
            }

            if (rel.isEmpty()) {
                // Check if relationship exists.
                System.out.println("Logger: No connection between "+user1.get().getName()+" and "+user2.get().getName());
                areTheyConnectedText.setText(user1.get().getName().equalsIgnoreCase(user2.get().getName()) ? "They are the one and the same" : "They are not connected.");
            } else {
                StringBuilder relationText = new StringBuilder(user1.get().getName() + " has the following relationships with " + user2.get().getName() + "\n");
                rel.forEach(o -> {
                    System.out.println("Logger: "+user1.get().getName()+" is connected to "+o.getUser().getName() + " as " + o.getRelation());
                    relationText.append("\t").append(RelationType.getString(o.getRelation())).append("\t");
                });
                areTheyConnectedText.setText(relationText.toString());
            }
            areTheyConnectedText.setVisible(true);
        } else {
            areTheyConnectedText.setText("Could not find users");
            areTheyConnectedText.setVisible(true);
        }
    }

    /**
     * Uses the SearchPath class to get the degree of friendship between 2 users.
     */
    public void degreeOfFriendship() {
        degreeOfFriendshipText.setVisible(false);
        Optional<User> user1 = store.getUsers().stream().filter(o -> o.getName().equalsIgnoreCase(degreeOfFriendDropDown1.getValue())).findAny();
        Optional<User> user2 = store.getUsers().stream().filter(o -> o.getName().equalsIgnoreCase(degreeOfFriendDropDown2.getValue())).findAny();

        if(user1.isPresent() && user2.isPresent()){
            SearchPath path = new SearchPath(user1.get(), user2.get());
            String degreePath = path.searchPath();
            System.out.println("Logger: "+degreePath);
            degreeOfFriendshipText.setText(degreePath);
            degreeOfFriendshipText.setVisible(true);
        }
    }

    public void deleteUser(){
        Optional<User> u = store.getUsers().stream().filter(o -> o.getName().equalsIgnoreCase(deleteUserDropDown.getValue())).findAny();
        String statusText = "";
        String statusImage;

        if(u.isPresent()){
            boolean canDelete;
            try {
                canDelete = u.get().canDeleteUser();
            } catch (NoParentException e) {
                canDelete = false;
                statusImage = e.getMessage();
            }

            if(canDelete){
                boolean deleteUser = u.get().getRelationships().stream().map(Relationship::getUser).allMatch(o -> o.eraseRelationWithUser(u.get()));
                if(deleteUser){
                    store.deleteUser(u.get());
                    statusText = "Deleted user"+ u.get().getName();
                    statusImage = "success.png";
                    deleteUserDropDown.getItems().remove(deleteUserDropDown.getValue());
                } else {
                    statusText = "Could not deleted user"+ u.get().getName();
                    statusImage = "failed.png";
                }

            } else {
                statusText = "Cannot delete user: " +statusText;
                statusImage = "failed.png";
            }
            deleteUserStatusText.setText(statusText);
            deleteUserStatusImage.setImage(MiniNet.getImageFromPath(statusImage));
            deleteUserStatusText.setVisible(true);
            deleteUserStatusImage.setVisible(true);
        }
    }

}
