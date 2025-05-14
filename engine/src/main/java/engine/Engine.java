package engine;

import engine.core.*;
import engine.core.GameStateManager.GameState;
import engine.utils.DebugManager;
import engine.utils.EngineLogger;
import engine.utils.LevelLoader;
import engine.utils.ResourceManager;
import javafx.scene.layout.Pane;

public class Engine {
	private static Engine instance;
	private final LevelLoader levelLoader;
	private final ResourceManager resourceManager;
	private final GameSettings gameSettings;
	private final GameStateManager gameStateManager;
	private final UIManager uiManager;
	private final DebugManager debugManager;
	private Renderer renderer;
	private Pane renderTarget;
	private double FPS;
	private InputHandler inputHandler;

	private Engine() {
		EngineLogger.setup();
		this.levelLoader = LevelLoader.getInstance();
		this.resourceManager = ResourceManager.getInstance();
		this.gameSettings = GameSettings.getInstance();
		this.gameStateManager = GameStateManager.getInstance();
		this.uiManager = UIManager.getInstance();
		this.debugManager = DebugManager.getInstance();
	}

	public static Engine getInstance() {
		if (instance == null) instance = new Engine();
		return instance;
	}

	private boolean checkInitialized(boolean throwErr) {
		boolean initialized = gameStateManager.getState() != GameState.UNINITIALIZED;
		if (!initialized && throwErr) {
			EngineLogger.severe("Engine must be initialized!");
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
		EngineLogger.info("FPS set to: " + fps);
	}

	public boolean isPaused() {
		return gameStateManager.getState() == GameState.PAUSED;
	}

	public void setPaused(boolean paused) {
		checkInitialized(true);
		if (paused) gameStateManager.setState(GameState.PAUSED);
		else gameStateManager.setState(GameState.RUNNING);
		EngineLogger.info(paused ? "Engine paused" : "Engine resumed");
	}

	public LevelLoader getLevelLoader() {
		return levelLoader;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public GameSettings getGameSettings() {
		return gameSettings;
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

	public void init(Pane target, double FPS) {
		if (checkInitialized(false)) return;
		EngineLogger.info("Initializing Engine");
		resourceManager.clearCache();
		uiManager.clearComponents();
		renderTarget = target;
		inputHandler = new InputHandler(renderTarget.getScene());
		renderer = new Renderer(renderTarget, FPS);
		renderer.start();
		gameStateManager.setState(GameState.RUNNING);
	}

	public void shutdown() {
		EngineLogger.info("Shutting down Engine");
		if (renderer != null) renderer.stop();
		gameStateManager.setState(GameState.UNINITIALIZED);
		EngineLogger.close();
	}

	public void loadLevel(String id) {
		checkInitialized(true);
		EngineLogger.info("Loading level: " + id);
		setPaused(true);
		levelLoader.loadLevel(id);
		setPaused(false);
	}

	public void saveLevel(String id) {
		checkInitialized(true);
		EngineLogger.info("Saving level: " + id);
		levelLoader.saveLevel(id);
	}

	public void gameOver() {
		checkInitialized(true);
		renderer.setPaused(true);
		gameStateManager.setState(GameState.GAME_OVER);
		EngineLogger.info("Game over");
	}

	public void levelComplete() {
		checkInitialized(true);
		renderer.setPaused(true);
		gameStateManager.setState(GameState.LEVEL_COMPLETE);
		EngineLogger.info("Level complete");
	}
}