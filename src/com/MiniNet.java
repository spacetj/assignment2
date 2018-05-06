package com;

import com.Model.States;
import com.Services.DBStore;
import com.Services.UserStore;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;

public class MiniNet extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("./View/mininet.fxml"));
        primaryStage.setTitle("MiniNet");
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Scene mainScene = new Scene(root);
        mainScene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    public static void main(String[] args) {

//        getStore().getUsers().forEach(o -> {
//            System.out.println("Name: "+o.getName());
//            o.getFriends().forEach(frnd -> System.out.println("         "+frnd.getUser().getName()+" "+frnd.getRelation()));
//        });
        launch(args);
    }

    public static UserStore getStore(){
        try {
            return DBStore.getInstance();
        } catch (SQLException e) {
            System.out.println("Cannot get instance");
        }
        return null;
    }


    /**
     * Switches the menu option.
     * @param state One of the states enum.
     */
    public static void switchState(States state){
        switch (state){
            case ADD_USER:
                break;
            case MAIN_MENU:
                break;
            case SELECT_USER:
                break;
        }
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
