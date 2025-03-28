package gameengine.core;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import gameengine.core.gameobjects.GameObject;
import javafx.application.Platform;
import javafx.scene.layout.Pane;

public class Renderer implements Runnable {
    private static Thread RENDER_THREAD;
    private int FPS;
    private int RESCALE_DELAY = 100;
    private Timer rescaleTimer;
    private volatile boolean isPaused = false;;
    private Consumer<Double> updateCallback;
    private Pane canvas;
    private LevelData levelData;
    private int tileSize;
    private ArrayList<GameObject> gameObjects = new ArrayList<>();

    public Renderer(Pane canvas, int FPS, LevelData levelData, Consumer<Double> updateCallback) {
        this.canvas = canvas;
        this.levelData = levelData;
        this.FPS = FPS;
        this.updateCallback = updateCallback;

        canvas.widthProperty().addListener((obs, oldVal, newVal) -> rescale());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> rescale());
        rescale();
    }

    public ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    public void addGameObject(GameObject gameObject) {
        this.gameObjects.add(gameObject);
    }

    public void addGameObjects(ArrayList<GameObject> gameObjects) {
        this.gameObjects.clear();
        this.gameObjects.addAll(gameObjects);
    }

    public void removeGameObject(GameObject gameObject) {
        this.gameObjects.remove(gameObject);
    }

    public void rescale() {
        if (rescaleTimer != null) rescaleTimer.cancel();
        rescaleTimer = new Timer();
        rescaleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                tileSize = (int) Math.min(canvas.getWidth() / levelData.cols, canvas.getHeight() / levelData.rows);
            }
        }, RESCALE_DELAY);
    }

    @Override
    public void run() {
        double interval = 1.0 / FPS;
        long lastTime = System.nanoTime();
        double deltaTime = 0;

        while (RENDER_THREAD != null && !RENDER_THREAD.isInterrupted()) {
            if (isPaused) {
                try {
                    Thread.sleep(100);
                    continue;
                } catch (InterruptedException e) {
                    stop();
                }
            }

            long currentTime = System.nanoTime();
            deltaTime += (double) (currentTime - lastTime) / 1_000_000_000.0;
            lastTime = currentTime;

            if (deltaTime >= interval) {
                if (updateCallback != null) updateCallback.accept(deltaTime);
                deltaTime -= interval;
            }

            render();
        }
    }

    public void start() {
        if (RENDER_THREAD != null) return;
        RENDER_THREAD = new Thread(this);
        RENDER_THREAD.setDaemon(true);
        RENDER_THREAD.start();
        isPaused = false;
    }

    public void stop() {
        if (RENDER_THREAD == null) return;
        if (rescaleTimer != null) rescaleTimer.cancel();
        RENDER_THREAD.interrupt();
        RENDER_THREAD = null;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    private void render() {
        for (GameObject gameObject : gameObjects) {
            if (!gameObject.rendered) {
                Platform.runLater(() -> canvas.getChildren().add(gameObject.getSelf()));
                gameObject.rendered = true;
            }

            gameObject.getSelf().setViewOrder(gameObject.layer);
            gameObject.getSelf().setTranslateX(gameObject.posX * tileSize);
            gameObject.getSelf().setTranslateY(gameObject.posY * tileSize);
            gameObject.rescale(tileSize, tileSize);
        }
    }
}
