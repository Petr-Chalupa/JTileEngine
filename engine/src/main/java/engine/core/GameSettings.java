package engine.core;

import engine.Engine;
import engine.utils.DebugManager;
import engine.utils.DebugManager.Features;
import engine.utils.ResourceManager;

import java.util.EnumSet;
import java.util.logging.Level;

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
		// Tile size
		tileSize = settings.optDouble("tile_size", 64);
		// Sounds
		soundEnabled = settings.optBoolean("sound_enabled", false);
		// Debug info
		DebugManager debugManager = DebugManager.getInstance();
		JSONObject debugInfo = settings.optJSONObject("debug_info", new JSONObject());
		for (String feature : debugInfo.keySet()) {
			debugManager.setFeature(Features.valueOf(feature), debugInfo.getBoolean(feature));
		}
		// Logging
		String loggingLevel = settings.optString("logging", "ALL");
		Engine.LOGGER.setLevel(Level.parse(loggingLevel));
	}

	public void save() {
		JSONObject settings = new JSONObject();
		// Tile size
		settings.put("tile_size", tileSize);
		// Sounds
		settings.put("sound_enabled", soundEnabled);
		// Debug info
		DebugManager debugManager = DebugManager.getInstance();
		settings.put("debug_info", new JSONObject());
		EnumSet<DebugManager.Features> features = debugManager.getEnabledFeatures();
		for (Features feature : features) {
			settings.getJSONObject("debug_info").put(feature.name(), debugManager.isFeatureEnabled(feature));
		}
		// Logging
		settings.put("logging", Engine.LOGGER.getLevel().getName());
		//
		ResourceManager.getInstance().saveGameSettings(settings);
	}

}