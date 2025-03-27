package gameengine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import gameengine.gameobjects.GameObject;
import gameengine.gameobjects.Player;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class GameView {
    private Renderer renderer;
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    @FXML
    private Pane canvas;

    @FXML
    private void clickMainMenu() throws IOException {
        Renderer.stop();
        App.setRoot("main_menu");
    }

    public void loadLvl(String path) {
        renderer = new Renderer(canvas, 64, 60, this::update);
        renderer.loadLevel(path);

        canvas.getScene().setOnKeyPressed(event -> pressedKeys.add(event.getCode()));
        canvas.getScene().setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));
    }

    public void update(ArrayList<GameObject> gameObjects) {
        Player player = (Player) gameObjects.stream().filter(go -> go instanceof Player).findFirst().orElse(null);
        if (player != null && player.rendered) {
            if (pressedKeys.contains(KeyCode.W)) player.moveY(-10); // Move up
            if (pressedKeys.contains(KeyCode.A)) player.moveX(-10); // Move left
            if (pressedKeys.contains(KeyCode.S)) player.moveY(10); // Move down
            if (pressedKeys.contains(KeyCode.D)) player.moveX(10); // Move right
        }
    }
}
