package engine.utils;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

public class ResourceManager {
    private static ResourceManager instance;
    private final Map<String, Image> imgCache = new HashMap<>();

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
}
