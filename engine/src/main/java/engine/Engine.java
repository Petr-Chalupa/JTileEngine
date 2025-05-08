package engine;

import engine.core.GameStateManager;
import engine.core.GameStateManager.GameState;
import engine.core.InputHandler;
import engine.core.Renderer;
import engine.core.UIManager;
import engine.utils.DebugManager;
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
	private final GameStateManager gameStateManager;
	private final UIManager uiManager;
	private final DebugManager debugManager;
	private FileHandler logFileHandler;
	private Renderer renderer;
	private Pane renderTarget;
	private double FPS;
	private InputHandler inputHandler;

	private Engine() {
		this.levelLoader = LevelLoader.getInstance();
		this.resourceManager = ResourceManager.getInstance();
		this.gameStateManager = GameStateManager.getInstance();
		this.uiManager = UIManager.getInstance();
		this.debugManager = DebugManager.getInstance();
		setupLogger();
	}

	public static Engine getInstance() {
		if (instance == null) instance = new Engine();
		return instance;
	}

	private boolean checkInitialized(boolean throwErr) {
		boolean initialized = gameStateManager.getState() != GameState.UNINITIALIZED;
		if (!initialized && throwErr) {
			LOGGER.severe("Engine must be initialized!");
			throw new IllegalStateException("Engine not initialized");
		}
		return initialized;
	}

	public Pane getRenderTarget() {
		return renderTarget;
	}

	public double getFPS() {
		return FPS;
	}

	public void setFPS(double fps) {
		this.FPS = fps;
		if (checkInitialized(false)) renderer.setFPS(fps);
		LOGGER.info("FPS set to: " + fps);
	}

	public boolean isPaused() {
		return gameStateManager.getState() == GameState.PAUSED;
	}

	public void setPaused(boolean paused) {
		checkInitialized(true);
		if (paused) gameStateManager.setState(GameState.PAUSED);
		else gameStateManager.setState(GameState.RUNNING);
		LOGGER.info(paused ? "Engine paused" : "Engine resumed");
	}

	public LevelLoader getLevelLoader() {
		return levelLoader;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public GameStateManager getGameStateManager() {
		return gameStateManager;
	}

	public UIManager getUIManager() {
		return uiManager;
	}

	public DebugManager getDebugManager() {
		return debugManager;
	}

	public Renderer getRenderer() {
		checkInitialized(true);
		return renderer;
	}

	public InputHandler getInputHandler() {
		checkInitialized(true);
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
		if (checkInitialized(false)) return;
		LOGGER.info("Initializing Engine");
		resourceManager.clearCache();
		uiManager.clearComponents();
		renderTarget = target;
		inputHandler = new InputHandler(renderTarget.getScene());
		renderer = new Renderer(renderTarget, FPS);
		renderer.start();
		gameStateManager.setState(GameState.RUNNING);
	}

	public void shutdown() {
		LOGGER.info("Shutting down Engine");
		if (renderer != null) renderer.stop();
		if (logFileHandler != null) logFileHandler.close();
		gameStateManager.setState(GameState.UNINITIALIZED);
	}

	public void loadLevel(String name) {
		checkInitialized(true);
		LOGGER.info("Loading level: " + name);
		setPaused(true);
		levelLoader.loadLevel(name);
		setPaused(false);
	}

	public void gameOver() {
		checkInitialized(true);
		renderer.setPaused(true);
		gameStateManager.setState(GameState.GAME_OVER);
		LOGGER.info("Game over");
	}

	public void levelComplete() {
		checkInitialized(true);
		renderer.setPaused(true);
		gameStateManager.setState(GameState.LEVEL_COMPLETE);
		LOGGER.info("Level complete");
	}
}