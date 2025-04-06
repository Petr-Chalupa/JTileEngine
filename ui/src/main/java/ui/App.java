package ui;

import engine.core.Renderer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private static Scene scene;
    private static Renderer renderer;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(new Group(), 1024, 768);
        setRoot("main_menu");

        stage.setTitle("Game engine");
        stage.getIcons().add(new Image(App.class.getResource("img/logo.png").toExternalForm()));
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        if (renderer != null) renderer.stop();
        Platform.exit();
        System.exit(0);
    }

    public static Object setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/" + fxml + ".fxml"));
        scene.setRoot(fxmlLoader.load());
        return fxmlLoader.getController();
    }

    public static Renderer getRenderer() {
        return renderer;
    }

    public static void setRenderer(Renderer _renderer) {
        if (renderer != null) renderer.stop();
        renderer = _renderer;
    }

    public static void main(String[] args) {
        launch();
    }
}