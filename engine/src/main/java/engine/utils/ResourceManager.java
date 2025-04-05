package engine.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import javafx.scene.image.Image;

public class ResourceManager {
    private static ResourceManager instance;
    private final Map<String, Image> imgCache = new HashMap<>();
    private final Path userLevelsPath;

    private ResourceManager() {
        try {
            userLevelsPath = Paths.get(System.getenv("APPDATA"), "GameEngine", "levels");
            Files.createDirectories(userLevelsPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create user levels directory", e);
        }
    }

    public static ResourceManager getInstance() {
        if (instance == null) instance = new ResourceManager();
        return instance;
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

    public JSONObject getLevelConfig(String levelName) {
        String configString = getLevelData(levelName, "config.json");
        return new JSONObject(configString);
    }

    public List<String> getLevelMap(String levelName) {
        String mapString = getLevelData(levelName, "map.txt");
        return Arrays.stream(mapString.split("\\R")).filter(line -> !line.isEmpty()).collect(Collectors.toList());
    }

    private String getLevelData(String levelName, String fileName) {
        try {
            URL bultinLevelURL = getClass().getResource("/engine/levels/" + levelName + "/" + fileName);
            if (bultinLevelURL != null) return Files.readString(Path.of(bultinLevelURL.toURI()));

            Path userLevelPath = userLevelsPath.resolve(levelName).resolve(fileName);
            if (Files.exists(userLevelPath)) return Files.readString(userLevelPath);

            throw new RuntimeException("Level not found: " + levelName + " - " + fileName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load level: " + levelName + " - " + fileName, e);
        }
    }

    public void importLevel(File path, String levelName) throws IOException {
        Path targetDir = userLevelsPath.resolve(levelName);
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
