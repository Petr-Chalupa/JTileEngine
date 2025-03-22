package gameengine;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class LevelEditor {
    private Renderer renderer;

    @FXML
    private Pane canvas;

    @FXML
    private void clickMainMenu() throws IOException {
        Renderer.stop();
        App.setRoot("main_menu");
    }

    public void loadLevel(String path) {
        renderer = new Renderer(canvas, 10, null);
        renderer.loadLevel(path);
    }
}
