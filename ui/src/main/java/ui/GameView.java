package ui;

import engine.Engine;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static engine.core.GameStateManager.GameState;

public class GameView {
	private Engine engine;
	private String levelName;

	@FXML
	private Pane canvasParent;
	@FXML
	private VBox pauseMenu;
	@FXML
	private Label pauseMenuLevelName;
	@FXML
	private VBox gameOverMenu;
	@FXML
	private VBox levelCompleteMenu;

	@FXML
	private void buttonPauseGame() {
		App.getEngine().setPaused(true);
	}

	@FXML
	private void buttonResumeGame() {
		App.getEngine().setPaused(false);
	}

	@FXML
	private void buttonMainMenu() throws IOException {
		App.getEngine().saveLevel(levelName);
		App.getEngine().shutdown();
		App.setRoot("main_menu");
	}

	@FXML
	private void buttonTryAgain() {
		App.getEngine().loadLevel(levelName); // Reload level
	}

	@FXML
	public void initialize() {
		engine = App.getEngine();

		EventHandler<KeyEvent> escapeKeyHandler = event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				engine.setPaused(!engine.isPaused());
				engine.getInputHandler().clear();
				event.consume();
			}
		};
		canvasParent.sceneProperty().addListener((observable, oldScene, newScene) -> {
			if (oldScene != null) oldScene.removeEventFilter(KeyEvent.KEY_PRESSED, escapeKeyHandler);
			if (newScene != null) newScene.addEventFilter(KeyEvent.KEY_PRESSED, escapeKeyHandler);
		});
	}

	public void loadLevel(String name) {
		engine.init(canvasParent, 60);
		engine.loadLevel(name);

		levelName = name;
		pauseMenuLevelName.setText(name);

		engine.getGameStateManager().addListener((GameState state) -> {
			pauseMenu.setVisible(false);
			gameOverMenu.setVisible(false);
			levelCompleteMenu.setVisible(false);
			switch (state) {
				case PAUSED:
					pauseMenu.setVisible(true);
					break;
				case GAME_OVER:
					gameOverMenu.setVisible(true);
					break;
				case LEVEL_COMPLETE:
					levelCompleteMenu.setVisible(true);
					break;
				default:
					break;
			}
		});
	}
}
