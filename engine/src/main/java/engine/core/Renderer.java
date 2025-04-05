package engine.core;

import java.util.List;
import java.util.stream.Collectors;

import engine.gameobjects.GameObject;
import javafx.application.Platform;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

public class Renderer implements Runnable {
    private static Thread RENDER_THREAD;
    private int FPS;
    private volatile boolean isPaused = false;;
    private Canvas canvas;
    private LevelData levelData;

    public Renderer(Pane parent, int FPS, LevelData levelData) {
        this.canvas = new Canvas();
        this.levelData = levelData;
        this.FPS = FPS;

        parent.getChildren().add(canvas);
        canvas.widthProperty().bind(parent.widthProperty());
        canvas.heightProperty().bind(parent.heightProperty());
    }

    @Override
    public void run() {
        double interval = 1.0 / FPS;
        long lastTime = System.nanoTime();
        double deltaTime = 0;

        while (RENDER_THREAD != null && !RENDER_THREAD.isInterrupted()) {
            if (isPaused) {
                try {
                    lastTime = System.nanoTime();
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

        // Calculate camera view bounds
        Bounds viewBounds = calculateViewBounds();

        // Sort game objects based on layer
        List<GameObject> sortedObjects = levelData.gameObjects.stream()
                .sorted((a, b) -> Integer.compare(a.layer, b.layer)).collect(Collectors.toList());

        // Render visible game objects
        for (GameObject gameObject : sortedObjects) {
            Bounds gameObjectBounds = new BoundingBox(gameObject.posX, gameObject.posY, gameObject.size,
                    gameObject.size);
            Bounds intersection = calculateVisibleIntersection(gameObjectBounds, viewBounds);
            if (intersection == null) continue;

            double spriteWidth = gameObject.getSprite().getWidth();
            double spriteHeight = gameObject.getSprite().getHeight();
            double sourceX = (intersection.getMinX() - gameObject.posX) * (spriteWidth / gameObject.size);
            double sourceY = (intersection.getMinY() - gameObject.posY) * (spriteHeight / gameObject.size);
            double sourceWidth = intersection.getWidth() * (spriteWidth / gameObject.size);
            double sourceHeight = intersection.getHeight() * (spriteHeight / gameObject.size);
            double screenX = gameObject.posX - viewBounds.getMinX();
            double screenY = gameObject.posY - viewBounds.getMinY();
            context.drawImage(gameObject.getSprite(), sourceX, sourceY, sourceWidth, sourceHeight, screenX, screenY,
                    gameObject.size, gameObject.size);
        }
    }

    private Bounds calculateViewBounds() {
        double viewWorldCenterX = levelData.player.posX + (levelData.player.size / 2.0);
        double viewWorldCenterY = levelData.player.posY + (levelData.player.size / 2.0);

        double viewCols = canvas.getWidth() / levelData.tileSize;
        double viewRows = canvas.getHeight() / levelData.tileSize;

        double viewLeft = viewWorldCenterX - (viewCols * levelData.tileSize) / 2.0;
        double viewRight = viewWorldCenterX + (viewCols * levelData.tileSize) / 2.0;
        double viewTop = viewWorldCenterY - (viewRows * levelData.tileSize) / 2.0;
        double viewBottom = viewWorldCenterY + (viewRows * levelData.tileSize) / 2.0;

        return new BoundingBox(viewLeft, viewTop, viewRight - viewLeft, viewBottom - viewTop);
    }

    private Bounds calculateVisibleIntersection(Bounds gameObjectBounds, Bounds viewBounds) {
        double minX = Math.max(gameObjectBounds.getMinX(), viewBounds.getMinX());
        double minY = Math.max(gameObjectBounds.getMinY(), viewBounds.getMinY());
        double maxX = Math.min(gameObjectBounds.getMaxX(), viewBounds.getMaxX());
        double maxY = Math.min(gameObjectBounds.getMaxY(), viewBounds.getMaxY());
        return (minX < maxX && minY < maxY) ? new BoundingBox(minX, minY, maxX - minX, maxY - minY) : null;
    }
}
