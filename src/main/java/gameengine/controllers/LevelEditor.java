package gameengine.controllers;

import java.io.IOException;

import gameengine.App;
import gameengine.core.LevelData;
import gameengine.core.Renderer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

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

        Renderer renderer = new Renderer(canvasParent, 10, levelData, null);
        App.setRenderer(renderer);
        renderer.start();
    }
}
