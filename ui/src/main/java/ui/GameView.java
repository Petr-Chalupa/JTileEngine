package ui;

import java.io.IOException;

import engine.core.InputHandler;
import engine.core.LevelData;
import engine.core.Renderer;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GameView {
    private InputHandler inputHandler;
    private final LevelData levelData = new LevelData();

    @FXML
    private Pane canvasParent;
    @FXML
    private VBox pauseMenu;
    @FXML
    private Text pauseMenuLevelName;

    @FXML
    private void buttonPauseGame() {
        App.getRenderer().setPaused(true);
        pauseMenu.setVisible(true);
    }

    @FXML
    private void buttonResumeGame() {
        App.getRenderer().setPaused(false);
        pauseMenu.setVisible(false);
    }

    @FXML
    private void buttonMainMenu() throws IOException {
        App.getRenderer().stop();
        App.setRoot("main_menu");
    }

    public void loadLvl(String path) {
        levelData.loadFile(path);

        pauseMenuLevelName.setText(levelData.name);

        Renderer renderer = new Renderer(canvasParent, 60, levelData);
        App.setRenderer(renderer);
        renderer.start();

        inputHandler = new InputHandler(canvasParent.getScene());
        levelData.player.setInputHandler(inputHandler);

        inputHandler.setPressedCallback((event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                renderer.setPaused(!renderer.isPaused());
                pauseMenu.setVisible(renderer.isPaused());
                inputHandler.clearKeys();
            }
        });
    }
}
