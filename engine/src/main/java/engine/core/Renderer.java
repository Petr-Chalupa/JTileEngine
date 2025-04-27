package engine.core;

import engine.gameobjects.GameObject;
import engine.utils.LevelLoader;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.util.Comparator;
import java.util.List;

public class Renderer implements Runnable {
	private static Thread RENDER_THREAD;
	private final LevelLoader levelLoader;
	private final Canvas canvas;
	private final Camera camera;
	private volatile boolean isPaused = false;
	private double FPS;

	public Renderer(Pane target, double FPS) {
		this.levelLoader = LevelLoader.getInstance();
		this.canvas = new Canvas();
		this.camera = Camera.getInstance();
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
		// Clear the canvas
		GraphicsContext context = canvas.getGraphicsContext2D();
		context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

		// Update camera
		if (camera.getTarget() == null) camera.setTarget(levelLoader.getPlayer());
		camera.update(canvas);

		// Sort game objects based on layer
		List<GameObject> sortedObjects = levelLoader.getGameObjects()
				.stream()
				.sorted(Comparator.comparingInt(GameObject::getLayer))
				.toList();

		// Render visible game objects
		for (GameObject gameObject : sortedObjects) {
			Bounds intersection = camera.getVisibleIntersection(gameObject.getBounds());
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
			double destX = goPosX - camera.getViewBounds().getMinX();
			double destY = goPosY - camera.getViewBounds().getMinY();
			gameObject.render(context, sourceX, sourceY, sourceWidth, sourceHeight, destX, destY, goSize, goSize);
			gameObject.setRendered(true);
		}

		// Render UI of visible game objects
		sortedObjects.stream().filter(GameObject::isRendered).forEach(gameObject -> gameObject.renderUI(context));
	}

}
