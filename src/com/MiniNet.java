package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import static com.Services.UserStore.ASSETS_FOLDER;

/**
 * MiniNet is the main class which instantiages the scene and the stage.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class MiniNet extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("./View/mininet.fxml"));
        primaryStage.setTitle("MiniNet");
        Scene mainScene = new Scene(root);
        mainScene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Gets the image from the assets folder.
     * @param path file URL
     * @return Returns an image from the path.
     */
    public static Image getImageFromPath(String path){
        return new Image(new File(ASSETS_FOLDER+path).toURI().toString());
    }
}
