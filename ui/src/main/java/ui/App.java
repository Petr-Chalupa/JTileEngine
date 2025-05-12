package ui;

import engine.Engine;
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
	private static Engine engine;

	public static Object setRoot(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/" + fxml + ".fxml"));
		scene.setRoot(fxmlLoader.load());
		return fxmlLoader.getController();
	}

	public static Engine getEngine() {
		return engine;
	}

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) throws IOException {
		scene = new Scene(new Group(), 1024, 768);
		setRoot("main_menu");
		App.engine = Engine.getInstance();

		stage.setTitle("JTile Game Engine");
		stage.getIcons().add(new Image(App.class.getResource("img/logo.png").toExternalForm()));
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() {
		engine.shutdown();
		Platform.exit();
		System.exit(0);
	}
}