package gameengine.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import gameengine.App;
import gameengine.core.LevelData;
import gameengine.core.Renderer;
import gameengine.core.gameobjects.Player;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class GameView {
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final LevelData levelData = new LevelData();

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
        levelData.loadFile(path);

        pauseMenuLevelName.setText(levelData.name);

        Renderer renderer = new Renderer(canvasParent, 60, levelData, this::update);
        App.setRenderer(renderer);
        renderer.start();

        canvasParent.getScene().setOnKeyPressed(event -> {
            pressedKeys.add(event.getCode());
            if (event.getCode() == KeyCode.ESCAPE) {
                renderer.setPaused(!renderer.isPaused());
                pauseMenu.setVisible(renderer.isPaused());
            }
        });
        canvasParent.getScene().setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));
    }

    public void update(double deltaTime) {
        Player player = levelData.player;
        if (pressedKeys.contains(KeyCode.W)) player.moveY(-player.speed * deltaTime); // Up
        if (pressedKeys.contains(KeyCode.A)) player.moveX(-player.speed * deltaTime); // Left
        if (pressedKeys.contains(KeyCode.S)) player.moveY(player.speed * deltaTime); // Down
        if (pressedKeys.contains(KeyCode.D)) player.moveX(player.speed * deltaTime); // Right
    }
}
