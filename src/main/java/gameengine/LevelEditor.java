package gameengine;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class LevelEditor {
    private final LevelData levelData = new LevelData();

    @FXML
    private Pane canvas;

    @FXML
    private void clickMainMenu() throws IOException {
        App.getRenderer().stop();
        App.setRoot("main_menu");
    }

    public void loadLevel(String path) {
        levelData.parseFile(path, 48);

        Renderer renderer = new Renderer(canvas, 10, levelData.tileRows, levelData.tileCols, null);
        App.setRenderer(renderer);
        renderer.addGameObjects(levelData.gameObjects);
        renderer.start();
    }
}
