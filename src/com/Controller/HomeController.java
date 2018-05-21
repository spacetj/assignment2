package com.Controller;

import com.MiniNet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * HomeController is the top template which has the MiniNet title and the icons.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class HomeController implements Initializable {

    @FXML
    Rectangle homePageActive, userProfileActive, addUserActive;

    @FXML
    AnchorPane mainMenuPane, addUserPane, userProfilePane;

    @FXML
    ImageView mainMenuIcon, addUserIcon, userProfileIcon;

    /**
     * Initializes the icon images.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainMenuIcon.setImage(MiniNet.getImageFromPath("home_page.png"));
        addUserIcon.setImage(MiniNet.getImageFromPath("add_user.png"));
        userProfileIcon.setImage(MiniNet.getImageFromPath("user_profile.png"));
    }

    /**
     * The method is invoked when the main menu icon is clicked.
     * It hides the other panes.
     */
    public void mainMenuClicked(){
        addUserActive.setVisible(false);
        addUserPane.setVisible(false);
        userProfileActive.setVisible(false);
        userProfilePane.setVisible(false);
        homePageActive.setVisible(true);
        mainMenuPane.setVisible(true);
    }

    /**
     * The method is invoked when the add user icon is clicked.
     * It hides the other panes.
     */
    public void addUserClicked(){
        mainMenuPane.setVisible(false);
        addUserPane.setVisible(true);
        homePageActive.setVisible(false);
        userProfilePane.setVisible(false);
        addUserActive.setVisible(true);
        userProfileActive.setVisible(false);
    }

    /**
     * The method is invoked when the user profile is clicked.
     * It hides the other panes.
     */
    public void userProfileClicked(){
        mainMenuPane.setVisible(false);
        addUserPane.setVisible(false);
        homePageActive.setVisible(false);
        userProfilePane.setVisible(true);
        addUserActive.setVisible(false);
        userProfileActive.setVisible(true);
    }
}
