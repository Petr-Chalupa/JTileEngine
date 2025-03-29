package gameengine.core;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gameengine.core.gameobjects.GameObject;
import gameengine.core.gameobjects.Player;
import javafx.application.Platform;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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
                levelData.tileSize = (int) Math.min(parent.getWidth() / levelData.viewCols,
                        parent.getHeight() / levelData.viewRows);
                canvas.setWidth(levelData.tileSize * levelData.viewCols);
                canvas.setHeight(levelData.tileSize * levelData.viewRows);
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

    public void render() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Calculate camera position based on player position
        double playerSize = levelData.tileSize * levelData.player.scale;
        double viewWorldCenterX = levelData.player.posX * levelData.tileSize + (playerSize / 2);
        double viewWorldCenterY = levelData.player.posY * levelData.tileSize + (playerSize / 2);
        double viewScreenCenterX = (canvas.getWidth() - playerSize) / 2;
        double viewScreenCenterY = (canvas.getHeight() - playerSize) / 2;

        // Calculate camera view bounds
        double viewLeft = viewWorldCenterX - (levelData.viewCols * levelData.tileSize) / 2.0;
        double viewRight = viewWorldCenterX + (levelData.viewCols * levelData.tileSize) / 2.0;
        double viewTop = viewWorldCenterY - (levelData.viewRows * levelData.tileSize) / 2.0;
        double viewBottom = viewWorldCenterY + (levelData.viewRows * levelData.tileSize) / 2.0;
        Bounds viewBounds = new BoundingBox(viewLeft, viewTop, viewRight - viewLeft, viewBottom - viewTop);

        // Sort game objects based on layer
        List<GameObject> sortedObjects = levelData.gameObjects.stream()
                .sorted((a, b) -> Integer.compare(a.layer, b.layer)).collect(Collectors.toList());

        // Render visible game objects
        for (GameObject gameObject : sortedObjects) {
            double worldX = gameObject.posX * levelData.tileSize;
            double worldY = gameObject.posY * levelData.tileSize;
            double gameObjectSize = levelData.tileSize * gameObject.scale;

            Bounds objectBounds = new BoundingBox(worldX, worldY, gameObjectSize, gameObjectSize);
            Bounds intersection = calculateIntersection(objectBounds, viewBounds);
            if (intersection == null) continue;

            double sourceX = (intersection.getMinX() - worldX) / gameObject.scale;
            double sourceY = (intersection.getMinY() - worldY) / gameObject.scale;
            double sourceWidth = intersection.getWidth() / gameObject.scale;
            double sourceHeight = intersection.getHeight() / gameObject.scale;
            double screenX = intersection.getMinX() - viewBounds.getMinX() + viewScreenCenterX
                    - (levelData.viewCols * levelData.tileSize) / 2.0;
            double screenY = intersection.getMinY() - viewBounds.getMinY() + viewScreenCenterY
                    - (levelData.viewRows * levelData.tileSize) / 2.0;
            context.drawImage(gameObject.getSprite(), sourceX, sourceY, sourceWidth, sourceHeight, screenX, screenY,
                    levelData.tileSize * gameObject.scale, levelData.tileSize * gameObject.scale);
        }
    }

    private Bounds calculateIntersection(Bounds a, Bounds b) {
        double minX = Math.max(a.getMinX(), b.getMinX());
        double minY = Math.max(a.getMinY(), b.getMinY());
        double maxX = Math.min(a.getMaxX(), b.getMaxX());
        double maxY = Math.min(a.getMaxY(), b.getMaxY());
        return (minX < maxX && minY < maxY) ? new BoundingBox(minX, minY, maxX - minX, maxY - minY) : null;
    }
}
