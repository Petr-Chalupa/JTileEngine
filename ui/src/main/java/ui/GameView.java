package ui;

import engine.core.InputHandler;
import engine.core.Renderer;
import engine.utils.LevelLoader;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class GameView {
    private InputHandler inputHandler;

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
        LevelLoader levelLoader = LevelLoader.getInstance();
        levelLoader.loadFile(path);

        pauseMenuLevelName.setText(levelLoader.name);

        Renderer renderer = new Renderer(canvasParent, 60);
        App.setRenderer(renderer);
        renderer.start();

        inputHandler = new InputHandler(canvasParent.getScene());
        levelLoader.player.setInputHandler(inputHandler);

        inputHandler.addPressedCallback((event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                renderer.setPaused(!renderer.isPaused());
                pauseMenu.setVisible(renderer.isPaused());
                inputHandler.clearKeys();
            }
        });
    }
}
