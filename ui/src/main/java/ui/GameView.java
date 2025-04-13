package ui;

import engine.Engine;
import engine.utils.LevelLoader;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class GameView {

    @FXML
    private Pane canvasParent;
    @FXML
    private VBox pauseMenu;
    @FXML
    private Text pauseMenuLevelName;

    @FXML
    private void buttonPauseGame() {
        App.getEngine().setPaused(true);
        pauseMenu.setVisible(true);
    }

    @FXML
    private void buttonResumeGame() {
        App.getEngine().setPaused(false);
        pauseMenu.setVisible(false);
    }

    @FXML
    private void buttonMainMenu() throws IOException {
        App.getEngine().shutdown();
        App.setRoot("main_menu");
    }

    public void loadLvl(String path) {
        Engine engine = App.getEngine();
        engine.init(canvasParent);

        LevelLoader levelLoader = LevelLoader.getInstance();
        levelLoader.loadFile(path);

        pauseMenuLevelName.setText(levelLoader.name);

        levelLoader.inputHandler.addPressedCallback((event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                engine.setPaused(!engine.isPaused());
                pauseMenu.setVisible(engine.isPaused());
                levelLoader.inputHandler.clearKeys();
            }
        });
    }
}
