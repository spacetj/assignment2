package com.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    // Active Line
    @FXML
    Rectangle homePageActive;

    @FXML
    Rectangle userProfileActive;

    @FXML
    Rectangle addUserActive;

    // Active Pane
    @FXML
    AnchorPane mainMenuPane;

    @FXML
    AnchorPane addUserPane;

    @FXML
    AnchorPane userProfilePane;




    //Icons
    @FXML
    ImageView mainMenuIcon;

    @FXML
    ImageView addUserIcon;

    @FXML
    ImageView userProfileIcon;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainMenuIcon.setImage(new Image(new File("assets/home_page.png").toURI().toString()));
        addUserIcon.setImage(new Image(new File("assets/add_user.png").toURI().toString()));
        userProfileIcon.setImage(new Image(new File("assets/user_profile.png").toURI().toString()));
    }

    public void mainMenuClicked(){
        addUserActive.setVisible(false);
        addUserPane.setVisible(false);
        userProfileActive.setVisible(false);
        userProfilePane.setVisible(false);
        homePageActive.setVisible(true);
        mainMenuPane.setVisible(true);
    }

    public void addUserClicked(){
        mainMenuPane.setVisible(false);
        addUserPane.setVisible(true);
        homePageActive.setVisible(false);
        userProfilePane.setVisible(false);
        addUserActive.setVisible(true);
        userProfileActive.setVisible(false);
    }

    public void userProfileClicked(){
        mainMenuPane.setVisible(false);
        addUserPane.setVisible(false);
        homePageActive.setVisible(false);
        userProfilePane.setVisible(true);
        addUserActive.setVisible(false);
        userProfileActive.setVisible(true);
    }
}
