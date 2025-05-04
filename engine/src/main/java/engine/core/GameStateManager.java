package engine.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameStateManager {
	public enum GameState {
		UNINITIALIZED,
		RUNNING,
		PAUSED,
		GAME_OVER,
		LEVEL_COMPLETE,
	}

	private static GameStateManager instance;
	private GameState state = GameState.UNINITIALIZED;
	private final List<Consumer<GameState>> listeners = new ArrayList<>();

	public static GameStateManager getInstance() {
		if (instance == null) instance = new GameStateManager();
		return instance;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
		for (Consumer<GameState> listener : listeners) {
			listener.accept(state);
		}
	}

	public void addListener(Consumer<GameState> callback) {
		listeners.add(callback);
	}

}
