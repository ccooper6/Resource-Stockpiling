package seng201.team0.gui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class starts the javaFX application window
 * @author seng201 teaching team
 */
public class MenuWindow extends Application {
    private static ReadOnlyDoubleProperty width;
    private static ReadOnlyDoubleProperty height;
    /**
     * Opens the gui with the fxml content specified in resources/fxml/fx_wrapper.fxml
     * @param primaryStage The current fxml stage, handled by javaFX Application class
     * @throws IOException if there is an issue loading fxml file
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/fx_wrapper.fxml"));
        Parent root = baseLoader.load();
        GameWrapper gameWrapper = baseLoader.getController();
        primaryStage.setTitle("Group 15 Game");
        Scene scene = new Scene(root, 1200, 675);
        width = primaryStage.widthProperty();
        height = primaryStage.heightProperty();
        primaryStage.setScene(scene);
        primaryStage.show();
        gameWrapper.init(primaryStage);
    }
    public static ReadOnlyDoubleProperty getWidth() {
        return width;
    }
    public static ReadOnlyDoubleProperty getHeight() {
        return height;
    }

    /**
     * Launches the FXML application, this must be called from another class (in this cass App.java) otherwise JavaFX
     * errors out and does not run
     * @param args command line arguments
     */
    public static void launchWrapper(String [] args) {
        launch(args);
    }

}
