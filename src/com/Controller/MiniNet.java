package com.Controller;

import com.Model.States;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MiniNet extends Application {

    public final static String DIVIDER = "=========================";
    private static Menu menu;
    private static Scene activeScene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../View/mininet.fxml"));

        primaryStage.setTitle(menu.getTitle());

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
