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
        engine.setFPS(10);
        engine.init(canvasParent);

        LevelLoader levelLoader = LevelLoader.getInstance();
        levelLoader.loadFile(path);
    }
}
