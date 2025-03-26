package gameengine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import gameengine.gameobjects.GameObject;
import gameengine.gameobjects.Player;
import gameengine.gameobjects.Tile;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Renderer implements Runnable {
    private static Thread RENDER_THREAD;
    private int FPS;
    private double deltaTime = 0;
    private UpdateListener updateListener;

    private Pane canvas;

    private JSONObject json;
    private int tileRows = 12;
    private int tileCols = 16;
    private int tileSize;
    private ArrayList<GameObject> gameObjects = new ArrayList<>();

    public Renderer(Pane canvas, int tileSize, int FPS, UpdateListener updateListener) {
        this.canvas = canvas;
        this.tileSize = tileSize;
        this.FPS = FPS;
        this.updateListener = updateListener;

        canvas.setPrefSize(tileCols * tileSize, tileRows * tileSize);
    }

    @Override
    public void run() {
        double interval = 1_000_000_000 / FPS;
        long lastTime = System.nanoTime();

        while (RENDER_THREAD != null && !RENDER_THREAD.isInterrupted()) {
            long currentTime = System.nanoTime();
            deltaTime += (currentTime - lastTime) / interval;
            lastTime = currentTime;

            while (deltaTime >= 1) {
                if (updateListener != null) updateListener.onUpdate(deltaTime);
                deltaTime--;
            }

            render();
        }
    }

    public static void stop() {
        if (RENDER_THREAD == null) return;
        RENDER_THREAD.interrupt();
        RENDER_THREAD = null;
    }

    public void start() {
        if (RENDER_THREAD != null) return;
        RENDER_THREAD = new Thread(this);
        RENDER_THREAD.start();
    }

    public void loadLevel(String path) {
        // stop if already rendering
        stop();

        try {
            String content = Files.readString(Path.of(App.class.getResource(path).toURI()));
            json = new JSONObject(content);
        } catch (IOException | URISyntaxException e) {
            System.err.println(e);
        }

        // initialize game objects
        gameObjects.clear();
        JSONArray jsonTiles = json.getJSONObject("map").getJSONArray("tiles");
        for (int i = 0; i < tileRows * tileCols; i++) {
            Color color = jsonTiles.getInt(i) == 0 ? Color.BLACK : Color.BLUE;
            gameObjects.add(new Tile((i % tileCols) * tileSize, (i / tileCols) * tileSize, tileSize, color, true));
        }
        gameObjects.add(new Player(2 * tileSize, 2 * tileSize, tileSize));

        // start rendering
        start();
    }

    private void render() {
        for (GameObject gameObject : gameObjects) {
            if (!gameObject.rendered) {
                Platform.runLater(() -> canvas.getChildren().add(gameObject.getSelf()));
                gameObject.rendered = true;
            }
        }
    }
}
