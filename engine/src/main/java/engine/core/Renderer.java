package engine.core;

import engine.gameobjects.GameObject;
import engine.gameobjects.entities.Entity;
import engine.utils.DebugManager;
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
	private final UIManager uiManager;
	private final DebugManager debugManager;
	private final Camera camera;
	private final Canvas canvas;
	private volatile boolean isPaused = false;
	private double FPS;

	public Renderer(Pane target, double FPS) {
		this.levelLoader = LevelLoader.getInstance();
		this.uiManager = UIManager.getInstance();
		this.debugManager = DebugManager.getInstance();
		this.camera = Camera.getInstance();
		this.canvas = new Canvas();
		this.FPS = FPS;

		target.getChildren().add(canvas);
		canvas.widthProperty().bind(target.widthProperty());
		canvas.heightProperty().bind(target.heightProperty());

		GameStateManager.getInstance().addListener((GameStateManager.GameState newState) -> {
			switch (newState) {
				case PAUSED:
				case GAME_OVER:
				case LEVEL_COMPLETE:
					setPaused(true);
					break;
				case RUNNING:
					setPaused(false);
					break;
				default:
					break;
			}
		});
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public GraphicsContext getGraphicsContext() {
		return canvas.getGraphicsContext2D();
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

	private void render() {
		// Clear the canvas
		GraphicsContext context = canvas.getGraphicsContext2D();
		context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

		// Update camera
		if (camera.getTarget() == null) camera.setTarget(levelLoader.getPlayer());
		camera.update(canvas);

		// Sort game objects based on the layer
		List<GameObject> sortedObjects = levelLoader.getGameObjects()
				.stream()
				.sorted(Comparator.comparingInt(GameObject::getLayer))
				.toList();

		// Render visible game objects
		for (GameObject gameObject : sortedObjects) {
			Bounds intersection = camera.getVisibleIntersection(gameObject.getRealBounds());
			if (intersection == null) {
				gameObject.setRendered(false);
				continue;
			}

			double tileSize = GameSettings.getInstance().getTileSize();
			double goSize = gameObject.getSize() * tileSize;
			double spriteWorldWidth = gameObject.getSprite().getWidth() / goSize;
			double spriteWorldHeight = gameObject.getSprite().getHeight() / goSize;
			double sourceX = (intersection.getMinX() - gameObject.getPosX() * tileSize) * spriteWorldWidth;
			double sourceY = (intersection.getMinY() - gameObject.getPosY() * tileSize) * spriteWorldHeight;
			double sourceWidth = intersection.getWidth() * spriteWorldWidth;
			double sourceHeight = intersection.getHeight() * spriteWorldHeight;
			double destX = intersection.getMinX() - camera.getViewBounds().getMinX();
			double destY = intersection.getMinY() - camera.getViewBounds().getMinY();
			double destWidth = intersection.getWidth();
			double destHeight = intersection.getHeight();
			context.save();
			if (gameObject instanceof Entity entity) {
				context.translate(destX + destWidth / 2, destY + destHeight / 2);
				context.scale(entity.getFacingDirectionX(), 1);
				context.translate(-(destX + destWidth / 2), -(destY + destHeight / 2));
			}
			context.drawImage(gameObject.getSprite(), sourceX, sourceY, sourceWidth, sourceHeight, destX, destY, destWidth, destHeight);
			context.restore();
			gameObject.setRendered(true);

			// Render enabled game object debug info
			debugManager.renderForGameObject(context, gameObject);
		}

		// Render UI of visible game objects
		uiManager.render(context, canvas);
		// Render UI debug info (if enabled)
		if (debugManager.isFeatureEnabled(DebugManager.Features.UI_REGIONS)) {
			debugManager.renderUIRegionsBounds(context, canvas);
		}
	}

}
