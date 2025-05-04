package ui;

import engine.Engine;
import engine.utils.LevelLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class LevelEditor {

	@FXML
	private Pane canvasParent;

	@FXML
	private void clickMainMenu() throws IOException {
		App.getEngine().shutdown();
		App.setRoot("main_menu");
	}

	public void loadLevel(String path) {
		Engine engine = App.getEngine();
		engine.init(canvasParent, 10);

		LevelLoader levelLoader = LevelLoader.getInstance();
		levelLoader.loadLevel(path);
	}
}
