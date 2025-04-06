package ui;

import engine.core.LevelData;
import engine.core.Renderer;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class LevelEditor {
    private final LevelData levelData = new LevelData();

    @FXML
    private Pane canvasParent;

    @FXML
    private void clickMainMenu() throws IOException {
        App.getRenderer().stop();
        App.setRoot("main_menu");
    }

    public void loadLevel(String path) {
        levelData.loadFile(path);

        Renderer renderer = new Renderer(canvasParent, 10, levelData);
        App.setRenderer(renderer);
        renderer.start();
    }
}
