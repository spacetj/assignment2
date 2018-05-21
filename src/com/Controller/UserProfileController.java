package com.Controller;

import com.MiniNet;
import com.Model.Exceptions.*;
import com.Model.RelationType;
import com.Model.Relationship;
import com.Model.User;
import com.Services.UserFactory;
import com.Services.UserStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.Services.UserStore.ASSETS_FOLDER;

/**
 * UserProfileController has the menu options once a user is selected.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class UserProfileController implements Initializable {

    @FXML
    ChoiceBox<String> selectUserDropdown, addRelationUserDropDown, addRelationDropDown,
            deleteRelationUserDropdown;

    @FXML
    Text userNameText, userAgeText, userGenderText, userStatusText, userStateText, addRelationStatusText,
            deleteRelationStatusText;

    @FXML
    TextField selectedUserGenderField, selectedUserProfilePicField, selectedUserstatusField, selectedUserStateField;

    @FXML
    ImageView userProfPicImage, addRelationStatusImage, deleteRelationStatusImage;

    @FXML
    HBox userInformationBox;

    @FXML
    TableView<Relationship> selectedUserTableView;

    @FXML
    TableColumn<User,String> userACol, relationshipCol;

    @FXML
    Tab showRelationsTab, addRelationTab, deleteRelationTab, updateProfileTab;

    @FXML
    VBox addRelationStatusBox, deleteRelationStatusBox;

    private UserStore store;
    private ObservableList<Relationship> selectedUserRelationships = FXCollections.observableArrayList();

    public UserProfileController() {
        store = UserStore.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fillDropdown();
    }

    public void fillDropdown() {
        selectUserDropdown.getItems().remove(0, selectUserDropdown.getItems().size());
        selectUserDropdown.getItems().addAll(
                store.getUsers().stream().map(User::getName).collect(Collectors.toList())
        );
    }

    /**
     * Helps select the user profile that you would like to view.
     * It populates user information such as name, age, gender, profile picture, state and status
     */
    public void setSelectedUser() {
        store.setSelectedUser(selectUserDropdown.getValue());
        if(store.getSelectedUser().isPresent()){
            User user = store.getSelectedUser().get();
            userNameText.setText(user.getName());
            userAgeText.setText(user.getAge().toString());
            userGenderText.setText(user.getGender());
            userStatusText.setText(user.getStatus());
            userStateText.setText(user.getState());
            enableTabs(user);

            File f = new File(ASSETS_FOLDER+user.getProfilePicture());
            Image i;

            if(f.exists() && !f.isDirectory()){
                i = new Image(f.toURI().toString());
            } else {
                i = MiniNet.getImageFromPath("image_not_found.png");
            }

            userProfPicImage.setImage(i);
            userInformationBox.setVisible(true);
        }
    }

    /**
     * This function gets executed when Add Relation button is clicked in user_profile.fxml.
     *
     * It attempts to add the user and displays a status image and text.
     */
    public void addRelation() {
        RelationType rel = RelationType.getRelation(addRelationDropDown.getValue());
        Optional<User> usr = store.getUsers().stream().filter(o -> o.getName().equalsIgnoreCase(addRelationUserDropDown.getValue())).findAny();
        boolean addRelationStatus = false;
        String statusText = "";
        String statusImage;

        if (store.getSelectedUser().isPresent() && rel != null && usr.isPresent()) {
            Relationship relationship = new Relationship(rel, usr.get());
            try {
                addRelationStatus = store.getSelectedUser().get().addRelation(relationship);
            } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeClassmastesException | NoAvailableException | NotToBeColleaguesException e) {
                addRelationStatus = false;
                statusText = e.getMessage();
                System.out.println("Error: Could not add relation: " + e.getMessage());
            }

            if (addRelationStatus) {
                statusText = store.getSelectedUser().get().getName() + " has added " + usr.get().getName() + " as a " + RelationType.getString(rel);
                statusImage = "success.png";
            } else {
                statusText = "Could not add "+usr.get().getName()+" as a "+RelationType.getString(rel)+": "+statusText;
                statusImage = "failed.png";
            }

        } else {
            statusText = "Invalid Request. Missing Information.";
            statusImage = "failed.png";
        }

        addRelationStatusText.setText(statusText);
        addRelationStatusImage.setImage(MiniNet.getImageFromPath(statusImage));
        addRelationStatusBox.setVisible(true);
    }

    /**
     * This function is invoked when the delete relation button is clicked on the user_profile.fxml
     *
     * Depending on the status of the deletion, it will display the te3xt and status image.
     */
    public void deleteRelation() {
        Optional<Relationship> relationship = Optional.empty();
        String statusText = "";
        String statusImage;
        String[] userAndRelation = deleteRelationUserDropdown.getValue().split(" - ");

        // Locate the relationship
        if (store.getSelectedUser().isPresent() && userAndRelation.length == 2) {
            relationship = store.getSelectedUser().get().getRelationships().stream().filter(o ->
                    o.getUser().getName().equalsIgnoreCase(userAndRelation[0])).filter(o -> RelationType.valueOf(userAndRelation[1]) == o.getRelation()).findAny();
        }
        boolean deleteRelationStatus = false;

        if (relationship.isPresent() && store.getSelectedUser().isPresent()) {
            try {
                deleteRelationStatus = store.getSelectedUser().get().deleteRelation(relationship.get());
            } catch (NoParentException e) {
                deleteRelationStatus = false;
                statusText = e.getMessage();
                System.out.println("Error: Could not delete relation: " + e.getMessage());
            }

            if (deleteRelationStatus) {
                statusText = store.getSelectedUser().get().getName() + " has deleted " + relationship.get().getUser().getName();
                statusImage = "success.png";
            } else {
                statusText = "Could not delete relation: "+statusText;
                statusImage = "failed.png";
            }
        } else {
            statusText = "Could not process request.";
            statusImage = "failed.png";
        }

        deleteRelationStatusText.setText(statusText);
        deleteRelationStatusImage.setImage(MiniNet.getImageFromPath(statusImage));
        deleteRelationStatusBox.setVisible(true);

    }

    /**
     * When a user has been selected, this function is called to execute the related user functionality.
     *
     * It enables tabs such as add relation, delete relation and other actions that are associated with the user.
     * @param user
     */
    private void enableTabs(User user) {
        showRelationsTab.setDisable(false);
        addRelationTab.setDisable(false);
        deleteRelationTab.setDisable(false);
        updateProfileTab.setDisable(false);
        deleteRelationStatusBox.setVisible(false);
        addRelationStatusBox.setVisible(false);

        populateDropdowns(user);

        setUserRelationships(user);

        setProfileInformation(user);
    }

    /**
     * Populates dropdowns such as addRelationDropDown and deleteRelationDropDown.
     * @param user
     */
    private void populateDropdowns(User user) {
        addRelationDropDown.getItems().remove(0, addRelationDropDown.getItems().size());
        addRelationUserDropDown.getItems().remove(0, addRelationUserDropDown.getItems().size());
        deleteRelationUserDropdown.getItems().remove(0, deleteRelationUserDropdown.getItems().size());
        addRelationUserDropDown.getItems().addAll(
                store.getUsers().stream()
                        .filter(o -> !o.getName().equalsIgnoreCase(user.getName()))
                        .map(User::getName).collect(Collectors.toList())
        );
        deleteRelationUserDropdown.getItems().addAll(
                user.getRelationships().stream()
                        .map(o -> o.getUser().getName()+" - "+o.getRelation()).collect(Collectors.toList())
        );

        addRelationDropDown.getItems().addAll(
                Arrays.asList(RelationType.relations)
        );
    }

    public void resetUserRelationshipTable(){
        if(store.getSelectedUser().isPresent()){
            setUserRelationships(store.getSelectedUser().get());
            populateDropdowns(store.getSelectedUser().get());
        }
    }

    /**
     * Displays the relationships that the user has including sibling relations.
     * @param user
     */
    private void setUserRelationships(User user) {
        selectedUserRelationships.remove(0, selectedUserRelationships.size());
        selectedUserRelationships.addAll(user.getRelationships());

        // Add siblings if YoungAdult or Infant
        if(UserFactory.isYoungAdult.test(user) || UserFactory.isInfant.test(user)){
            user.getSiblings().forEach(o -> {
                if(selectedUserRelationships.stream().map(usr -> usr.getUser().getName()).noneMatch(usr -> usr.equalsIgnoreCase(o.getUser().getName()))){
                    selectedUserRelationships.add(o);
                }
            });
        }

        userACol.setCellValueFactory(new PropertyValueFactory<>("username"));
        relationshipCol.setCellValueFactory(new PropertyValueFactory<>("relation"));
        selectedUserTableView.setItems(selectedUserRelationships);
    }

    /**
     * Sets profile information that is updatable.
     * @param selectedUser
     */
    private void setProfileInformation(User selectedUser) {
        selectedUserGenderField.setText(selectedUser.getGender());
        selectedUserProfilePicField.setText(selectedUser.getProfilePicture());
        selectedUserStateField.setText(selectedUser.getState());
        selectedUserstatusField.setText(selectedUser.getStatus());
    }

    /**
     * Update changable user information such as gender, profile picture, status and state.
     */
    public void updateUser() {
        if(store.getSelectedUser().isPresent()){
            store.getSelectedUser().get().setGender(selectedUserGenderField.getText());
            store.getSelectedUser().get().setProfilePicture(selectedUserProfilePicField.getText());
            store.getSelectedUser().get().setStatus(selectedUserstatusField.getText());
            store.getSelectedUser().get().setState(selectedUserStateField.getText());
        }

        store.updateUser(store.getSelectedUser().get());
    }
}