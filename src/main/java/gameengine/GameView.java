package gameengine;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class GameView implements UpdateListener {
    private Renderer renderer;

    @FXML
    private Pane canvas;

    @FXML
    private void clickMainMenu() throws IOException {
        Renderer.stop();
        App.setRoot("main_menu");
    }

    public void loadLvl(String path) {
        renderer = new Renderer(canvas, 64, 60, this);
        renderer.loadLevel(path);
    }

    @Override
    public void onUpdate(double deltaTime) {
        // System.out.println("Update: " + deltaTime);
    }
}
