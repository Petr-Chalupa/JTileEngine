package engine.core;

import engine.gameobjects.GameObject;
import engine.gameobjects.Player;
import engine.utils.LevelLoader;
import javafx.application.Platform;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.stream.Collectors;

public class Renderer implements Runnable {
    private static Thread RENDER_THREAD;
    private volatile boolean isPaused = false;
    private Canvas canvas;
    private double FPS;
    private LevelLoader levelLoader;

    public Renderer(Pane target, double FPS) {
        this.canvas = new Canvas();
        this.levelLoader = LevelLoader.getInstance();
        this.FPS = FPS;

        target.getChildren().add(canvas);
        canvas.widthProperty().bind(target.widthProperty());
        canvas.heightProperty().bind(target.heightProperty());
    }

    public void setFPS(double FPS) {
        this.FPS = FPS;
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
        for (GameObject gameObject : levelLoader.getGameObjects()) {
            gameObject.update(deltaTime);
        }
    }

    public void render() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Calculate camera view bounds
        Bounds viewBounds = calculateViewBounds();

        // Sort game objects based on layer
        List<GameObject> sortedObjects = levelLoader.getGameObjects()
                .stream()
                .sorted((a, b) -> Integer.compare(a.getLayer(), b.getLayer()))
                .collect(Collectors.toList());

        // Render visible game objects
        for (GameObject gameObject : sortedObjects) {
            Bounds intersection = getVisibleIntersection(gameObject.getBounds(), viewBounds);
            if (intersection == null) {
                gameObject.setRendered(false);
                continue;
            }

            double goPosX = gameObject.getPosX();
            double goPosY = gameObject.getPosY();
            double goSize = gameObject.getSize();
            double spriteWidth = gameObject.getSprite().getWidth();
            double spriteHeight = gameObject.getSprite().getHeight();
            double sourceX = (intersection.getMinX() - goPosX) * (spriteWidth / goSize);
            double sourceY = (intersection.getMinY() - goPosY) * (spriteHeight / goSize);
            double sourceWidth = intersection.getWidth() * (spriteWidth / goSize);
            double sourceHeight = intersection.getHeight() * (spriteHeight / goSize);
            double destX = goPosX - viewBounds.getMinX();
            double destY = goPosY - viewBounds.getMinY();
            gameObject.render(context, sourceX, sourceY, sourceWidth, sourceHeight, destX, destY, goSize, goSize);
            gameObject.setRendered(true);
        }
    }

    private Bounds calculateViewBounds() {
        Player player = levelLoader.getPlayer();
        double tileSize = levelLoader.getTileSize();

        double viewWorldCenterX = player.getPosX() + (player.getSize() / 2.0);
        double viewWorldCenterY = player.getPosY() + (player.getSize() / 2.0);

        double viewCols = canvas.getWidth() / tileSize;
        double viewRows = canvas.getHeight() / tileSize;

        double viewLeft = viewWorldCenterX - (viewCols * tileSize) / 2.0;
        double viewRight = viewWorldCenterX + (viewCols * tileSize) / 2.0;
        double viewTop = viewWorldCenterY - (viewRows * tileSize) / 2.0;
        double viewBottom = viewWorldCenterY + (viewRows * tileSize) / 2.0;

        return new BoundingBox(viewLeft, viewTop, viewRight - viewLeft, viewBottom - viewTop);
    }

    private Bounds getVisibleIntersection(Bounds gameObjectBounds, Bounds viewBounds) {
        double minX = Math.max(gameObjectBounds.getMinX(), viewBounds.getMinX());
        double minY = Math.max(gameObjectBounds.getMinY(), viewBounds.getMinY());
        double maxX = Math.min(gameObjectBounds.getMaxX(), viewBounds.getMaxX());
        double maxY = Math.min(gameObjectBounds.getMaxY(), viewBounds.getMaxY());
        return (minX < maxX && minY < maxY) ? new BoundingBox(minX, minY, maxX - minX, maxY - minY) : null;
    }
}
