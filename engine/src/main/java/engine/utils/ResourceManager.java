package engine.utils;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
	private static ResourceManager instance;
	private final Map<String, Image> imgCache = new HashMap<>();
	private final Path builtinSavePath;
	private final Path userSavePath;

	private ResourceManager() {
		builtinSavePath = Paths.get("engine/src/main/resources/engine/").toAbsolutePath();
		userSavePath = Paths.get(System.getenv("APPDATA"), "GameEngine");

		try {
			Files.createDirectories(userSavePath);
		} catch (IOException e) {
			throw new RuntimeException("Failed to create user levels directory", e);
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

	public Image getImg(String path) {
		return imgCache.computeIfAbsent(path, k -> {
			try {
				return new Image(getClass().getResource("/engine/img/" + path).toString());
			} catch (Exception e) {
				throw new RuntimeException("Failed to load sprite: " + path, e);
			}
		});
	}

	public void importLevel(File path, String levelName) throws IOException {
		Path targetDir = userSavePath.resolve(levelName);
		Files.createDirectories(targetDir);

		File configFile = new File(path, "config.json");
		File mapFile = new File(path, "map.txt");
		if (!configFile.exists() || !mapFile.exists()) {
			throw new IOException("Invalid level directory: missing required files [config.json, map.txt]");
		}

		Files.copy(configFile.toPath(), targetDir.resolve("config.json"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(mapFile.toPath(), targetDir.resolve("map.txt"), StandardCopyOption.REPLACE_EXISTING);
	}

}
