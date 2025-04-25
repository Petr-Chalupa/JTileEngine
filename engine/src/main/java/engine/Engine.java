package engine;

import engine.core.InputHandler;
import engine.core.Renderer;
import engine.utils.LevelLoader;
import engine.utils.ResourceManager;
import javafx.scene.layout.Pane;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Engine {
	public static final Logger LOGGER = Logger.getLogger(Engine.class.getName());
	private static Engine instance;
	private final LevelLoader levelLoader;
	private final ResourceManager resourceManager;
	private FileHandler logFileHandler;
	private boolean initialized = false;
	private Renderer renderer;
	private Pane renderTarget;
	private double FPS;
	private InputHandler inputHandler;

	private Engine() {
		this.levelLoader = LevelLoader.getInstance();
		this.resourceManager = ResourceManager.getInstance();
		setupLogger();
	}

	public static Engine getInstance() {
		if (instance == null) instance = new Engine();
		return instance;
	}

	public Pane getRenderTarget() {
		return renderTarget;
	}

	public double getFPS() {
		return FPS;
	}

	public void setFPS(double fps) {
		this.FPS = fps;
		if (initialized) renderer.setFPS(fps);
		LOGGER.info("FPS set to: " + fps);
	}

	public boolean isPaused() {
		return renderer == null || renderer.isPaused();
	}

	public void setPaused(boolean paused) {
		if (initialized) {
			renderer.setPaused(paused);
			LOGGER.info(paused ? "Engine paused" : "Engine resumed");
		}
	}

	public LevelLoader getLevelLoader() {
		return levelLoader;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public InputHandler getInputHandler() {
		return inputHandler;
	}

	public void setLogLevel(Level level) {
		LOGGER.setLevel(level);
		LOGGER.info("Log level set to: " + level);
	}

	private void setupLogger() {
		try {
			logFileHandler = new FileHandler("engine.log", true);
			logFileHandler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(logFileHandler);
			LOGGER.setLevel(Level.ALL);
		} catch (Exception e) {
			System.err.println("Failed to setup logger: " + e.getMessage());
		}
	}

	public void init(Pane target, double FPS) {
		LOGGER.info("Initializing Engine");
		renderTarget = target;
		inputHandler = new InputHandler(renderTarget.getScene());
		renderer = new Renderer(renderTarget, FPS);
		renderer.start();
		initialized = true;
	}

	public void shutdown() {
		LOGGER.info("Shutting down Engine");
		if (renderer != null) renderer.stop();
		if (logFileHandler != null) logFileHandler.close();
		initialized = false;
	}

	public void loadLevel(String path) {
		if (!initialized) {
			LOGGER.severe("Engine must be initialized!");
			throw new IllegalStateException("Engine not initialized");
		}
		LOGGER.info("Loading level: " + path);
		setPaused(true);
		levelLoader.loadFile(path);
		setPaused(false);
	}
}
