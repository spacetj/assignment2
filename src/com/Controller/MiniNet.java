package com.Controller;

import com.Model.States;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MiniNet extends Application {

    public final static String DIVIDER = "=========================";
    private static Menu menu;
    private static Queue<String> notifications = new LinkedList<>();

    @FXML
    Rectangle notificationBox;

    @FXML
    Text notificationText;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../View/mininet.fxml"));
        primaryStage.setTitle(menu.getTitle());
        List<Double> offsets = new ArrayList<>();
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        root.setOnMouseDragEntered(e -> {
            offsets.add(e.getSceneX());
            offsets.add(e.getSceneY());
        });

        root.setOnMouseDragged(e -> {
            primaryStage.setX(offsets.get(0));
            primaryStage.setY(offsets.get(1));
        });

        Scene mainScene = new Scene(root, 500, 400);
        mainScene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        menu = new AddUserMenu();
        menu.getStore().getUsers().forEach(o -> {
            System.out.println("Name: "+o.getName());
            o.getFriends().forEach(frnd -> System.out.println("         "+frnd.getUser().getName()+" "+frnd.getRelation()));
        });
        launch(args);
    }



    /**
     * Switches the menu option.
     * @param state One of the states enum.
     */
    public static void switchState(States state){
        switch (state){
            case ADD_USER:
                menu = new AddUserMenu();
                break;
            case MAIN_MENU:
                menu = new MainMenu();
                break;
            case SELECT_USER:
                menu = new UserMenu();
                break;
        }
    }

    /**
     * Print the top menu panel of the application.
     */
    public static void printHeader() {
        System.out.println("Welcome to MiniNet, the next generation social media. Command line interfaces is where its at.\n");
        System.out.println("Useful Shortcuts:");
        System.out.println("1. To exit the application at any time: "+ Menu.EXIT);
        System.out.println("2. To go back a screen: "+ Menu.BACK);
        System.out.println("3. To view these help shortcuts: "+ Menu.HELP);
        System.out.println(DIVIDER);
        System.out.println();
    }

    /**
     * Exits the application with a done message.
     */
    public static void exit(){
        System.out.println("\nExiting now...");
        System.out.println("Thank you for using MiniNet.\n");
        Platform.exit();
    }

    /**
     * Checks if the input is an integer.
     * @param value
     * @return
     */
    public static boolean isInputInt(String value) {
        try{
            Integer.parseInt(value);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
}
