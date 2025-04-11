package ui;

import engine.core.Renderer;
import engine.utils.LevelLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class LevelEditor {

    @FXML
    private Pane canvasParent;

    @FXML
    private void clickMainMenu() throws IOException {
        App.getRenderer().stop();
        App.setRoot("main_menu");
    }

    public void loadLevel(String path) {
        LevelLoader levelLoader = LevelLoader.getInstance();
        levelLoader.loadFile(path);

        Renderer renderer = new Renderer(canvasParent, 10);
        App.setRenderer(renderer);
        renderer.start();
    }
}
