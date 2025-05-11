package engine.core;

import engine.utils.DebugManager;

import java.util.EnumSet;

public class GameSettings {
	private static GameSettings instance;
	private final double tileSize = 64;

	private GameSettings() {
	}

	public static GameSettings getInstance() {
		if (instance == null) instance = new GameSettings();
		return instance;
	}

	public double getTileSize() {
		return tileSize;
	}

	public DebugManager.Features[] getDebugFeatures() {
		return DebugManager.Features.values();
	}

	public EnumSet<DebugManager.Features> getEnabledDebugFeatures() {
		return DebugManager.getInstance().getEnabledFeatures();
	}

	public void setDebugFeature(DebugManager.Features feature, boolean enabled) {
		if (enabled) DebugManager.getInstance().enableFeature(feature);
		else DebugManager.getInstance().disableFeature(feature);
	}

}