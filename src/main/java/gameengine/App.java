package gameengine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(new Group(), 720, 540);
        setRoot("main_menu");

        stage.setTitle("Game engine");
        stage.setScene(scene);
        stage.setMinWidth(720);
        stage.setMinHeight(540);
        stage.show();
    }

    @Override
    public void stop() {
        Renderer.stop();
    }

    static Object setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/" + fxml + ".fxml"));
        scene.setRoot(fxmlLoader.load());
        return fxmlLoader.getController();
    }

    public static void main(String[] args) {
        launch();
    }
}