package gameengine.core;

import java.util.Timer;
import java.util.TimerTask;

import gameengine.core.gameobjects.GameObject;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

public class Renderer implements Runnable {
    private static Thread RENDER_THREAD;
    private int FPS;
    private int RESCALE_DELAY = 100;
    private Timer rescaleTimer;
    private volatile boolean isPaused = false;;
    private Pane parent;
    private Canvas canvas;
    private LevelData levelData;

    public Renderer(Pane parent, int FPS, LevelData levelData) {
        this.parent = parent;
        this.canvas = new Canvas();
        this.levelData = levelData;
        this.FPS = FPS;

        parent.getChildren().add(canvas);
        parent.widthProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(this::rescale));
        parent.heightProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(this::rescale));
    }

    public void rescale() {
        if (rescaleTimer != null) rescaleTimer.cancel();
        rescaleTimer = new Timer();
        rescaleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                levelData.tileSize = (int) Math.min(parent.getWidth() / levelData.cols,
                        parent.getHeight() / levelData.rows);
                canvas.setWidth(levelData.tileSize * levelData.cols);
                canvas.setHeight(levelData.tileSize * levelData.rows);
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
                update(deltaTime);
                Platform.runLater(this::render);
                deltaTime -= interval;
            }
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

    private void update(double deltaTime) {
        for (GameObject gameObject : levelData.gameObjects) {
            gameObject.update(deltaTime, levelData);
        }
    }

    private void render() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (GameObject gameObject : levelData.gameObjects) {
            gameObject.render(context, levelData);
        }
    }
}
