package gameengine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Renderer implements Runnable {
    private static Thread RENDER_THREAD;
    private int FPS;
    private double deltaTime = 0;
    private UpdateListener updateListener;

    private Pane canvas;
    private Timer resizeTimer;
    private final int RESIZE_DELAY = 200;

    private JSONObject json;
    private double tileWidth = 0, tileHeight = 0;
    private ArrayList<Rectangle> tiles = new ArrayList<>();

    public Renderer(Pane canvas, int FPS, UpdateListener updateListener) {
        this.canvas = canvas;
        this.FPS = FPS;
        this.updateListener = updateListener;
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
        if (RENDER_THREAD != null) RENDER_THREAD.interrupt();
    }

    public void loadLevel(String path) {
        try {
            canvas.widthProperty().addListener((obs, oldVal, newVal) -> rescale());
            canvas.heightProperty().addListener((obs, oldVal, newVal) -> rescale());

            String content = Files.readString(Path.of(App.class.getResource(path).toURI()));
            json = new JSONObject(content);

            // create all gameObjects in advance
            // stop thread if already running
            RENDER_THREAD = new Thread(this);
            RENDER_THREAD.start();
        } catch (IOException | URISyntaxException e) {
            System.err.println(e);
        }
    }

    private void rescale() {
        if (resizeTimer != null) resizeTimer.cancel();
        resizeTimer = new Timer();
        resizeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                tileWidth = canvas.getWidth() / json.getJSONObject("map").getInt("cols");
                tileHeight = canvas.getHeight() / json.getJSONObject("map").getInt("rows");
                // set shouldScale to true so it wont change scale every rendering cycle
            }
        }, RESIZE_DELAY);
    }

    private void render() {
        JSONObject map = json.getJSONObject("map");
        for (int i = 0; i < map.getInt("rows"); i++) {
            JSONArray row = map.getJSONArray("tiles").getJSONArray(i);
            for (int j = 0; j < map.getInt("cols"); j++) {
                if (tiles.size() - 1 >= i * row.length() + j) {
                    Rectangle tile = tiles.get(i * row.length() + j);
                    tile.setWidth(tileWidth);
                    tile.setHeight(tileHeight);
                    tile.setX(j * tileWidth);
                    tile.setY(i * tileHeight);
                } else {
                    // only render on their actual values
                    int value = row.getInt(j);
                    Rectangle tile = new Rectangle(tileWidth, tileHeight);
                    tile.setFill(value == 0 ? Color.BLACK : Color.BLUE);
                    tiles.add(tile);
                    Platform.runLater(() -> canvas.getChildren().add(tile));
                }
            }
        }
    }
}
