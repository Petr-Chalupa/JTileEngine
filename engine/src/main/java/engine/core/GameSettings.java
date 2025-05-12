package engine.core;

import engine.utils.ResourceManager;
import org.json.JSONObject;

public class GameSettings {
	private static GameSettings instance;
	private double tileSize;
	private boolean soundEnabled;

	private GameSettings() {
		load();
	}

	public static GameSettings getInstance() {
		if (instance == null) instance = new GameSettings();
		return instance;
	}

	public double getTileSize() {
		return tileSize;
	}

	public void setTileSize(double tileSize) {
		this.tileSize = tileSize;
	}

	public boolean isSoundEnabled() {
		return soundEnabled;
	}

	public void setSoundEnabled(boolean soundEnabled) {
		this.soundEnabled = soundEnabled;
	}

	public void load() {
		JSONObject settings = ResourceManager.getInstance().getGameSettings();
		tileSize = settings.optDouble("tile_size", 64);
		soundEnabled = settings.optBoolean("sound_enabled", false);
	}

	public void save() {
		JSONObject settings = new JSONObject();
		settings.put("tile_size", tileSize);
		settings.put("sound_enabled", soundEnabled);
		ResourceManager.getInstance().saveGameSettings(settings);
	}

}