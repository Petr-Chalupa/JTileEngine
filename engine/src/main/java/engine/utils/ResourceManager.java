package engine.utils;

import engine.Engine;
import javafx.scene.image.Image;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceManager {
	private static ResourceManager instance;
	private final Map<String, Image> imgCache = new HashMap<>();
	private final Path builtinSavePath;
	private final Path userSavePath;

	private ResourceManager() {
		builtinSavePath = Paths.get("engine/src/main/resources/engine/").toAbsolutePath();
		userSavePath = Paths.get(System.getenv("APPDATA"), "JTileEngine");

		try {
			Files.createDirectories(userSavePath);
		} catch (IOException e) {
			Engine.LOGGER.severe("Failed to create user levels directory: " + e.getMessage());
		}
	}

	public static ResourceManager getInstance() {
		if (instance == null) instance = new ResourceManager();
		return instance;
	}

	public void clearCache() {
		imgCache.clear();
	}

	public Path getBuiltinSavePath() {
		return builtinSavePath;
	}

	public Path getUserSavePath() {
		return userSavePath;
	}

	/**
	 * Gets an image from the supplied path from cache (loads it on the first request)
	 *
	 * @param path Path of the image
	 * @return Image
	 */
	public Image getImg(String path) {
		return imgCache.computeIfAbsent(path, k -> {
			try {
				return new Image(getClass().getResource("/engine/img/" + path).toString());
			} catch (Exception e) {
				Engine.LOGGER.severe("Failed to load image: " + path + ": " + e.getMessage());
				return null;
			}
		});
	}

	public List<String> getAllBuiltinImageNames() {
		try {
			return Files.list(builtinSavePath.resolve("img/")).map(path -> path.getFileName().toString()).toList();
		} catch (IOException e) {
			Engine.LOGGER.severe("Failed to load image names: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Loads the game settings
	 *
	 * @return The settings as JSON object
	 */
	public JSONObject getGameSettings() {
		try {
			Path settingsPath = userSavePath.resolve("settings.json");
			if (!Files.exists(settingsPath)) {
				Files.copy(builtinSavePath.resolve("default_settings.json"), settingsPath);
			}
			return new JSONObject(Files.readString(settingsPath));
		} catch (IOException e) {
			Engine.LOGGER.severe("Failed to load game settings: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Saves the game settings
	 *
	 * @param settings The settings JSON object to save
	 */
	public void saveGameSettings(JSONObject settings) {
		try {
			Path settingsPath = userSavePath.resolve("settings.json");
			Files.writeString(settingsPath, settings.toString(4));
		} catch (IOException e) {
			Engine.LOGGER.severe("Failed to save game settings: " + e.getMessage());
		}
	}

	/**
	 * Imports a level from the supplied path
	 *
	 * @param path      Path of the level directory
	 * @param levelName Name of the level
	 * @return True if the level was imported successfully, false otherwise
	 */
	public boolean importLevel(File path, String levelName) {
		try {
			Path targetDir = userSavePath.resolve("levels/" + levelName);
			Files.createDirectories(targetDir);

			File configFile = new File(path, "config.json");
			if (configFile.exists()) {
				Files.copy(configFile.toPath(), targetDir.resolve("config.json"), StandardCopyOption.REPLACE_EXISTING);
				return true;
			}

			throw new RuntimeException("Invalid level directory: missing required files [config.json]");
		} catch (IOException e) {
			Engine.LOGGER.severe("Failed to import level " + levelName + ": " + e.getMessage());
			return false;
		}
	}

}
