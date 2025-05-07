package engine.utils;

import engine.core.Camera;
import engine.core.LevelData;
import engine.gameobjects.GameObject;
import engine.gameobjects.blocks.Chest;
import engine.gameobjects.blocks.Shop;
import engine.gameobjects.blocks.Stone;
import engine.gameobjects.entities.Enemy;
import engine.gameobjects.entities.Player;
import engine.gameobjects.tiles.Tile;
import engine.gameobjects.tiles.TileType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LevelLoader {
	private static LevelLoader instance;
	private final Map<String, LevelData> levels = new HashMap<>();
	private LevelData currentLevel;

	private LevelLoader() {
		loadAllLevels();
	}

	public static LevelLoader getInstance() {
		if (instance == null) instance = new LevelLoader();
		return instance;
	}

	public List<LevelData> getAllLevels() {
		return new ArrayList<>(levels.values());
	}

	public List<LevelData> getBuiltinLevels() {
		return levels.values().stream().filter(LevelData::isBuiltin).collect(Collectors.toList());
	}

	public List<LevelData> getImportedLevels() {
		return levels.values().stream().filter(level -> !level.isBuiltin()).collect(Collectors.toList());
	}

	public LevelData getCurrentLevel() {
		return currentLevel;
	}

	public String getName() {
		return currentLevel != null ? currentLevel.getName() : null;
	}

	public boolean isCompleted() {
		return currentLevel != null && currentLevel.isCompleted();
	}

	public List<GameObject> getGameObjects() {
		return currentLevel != null ? currentLevel.getGameObjects() : new ArrayList<>();
	}

	public int getRows() {
		return currentLevel != null ? currentLevel.getRows() : 0;
	}

	public int getCols() {
		return currentLevel != null ? currentLevel.getCols() : 0;
	}

	public int getTileSize() {
		return currentLevel != null ? currentLevel.getTileSize() : 0;
	}

	public Player getPlayer() {
		return currentLevel != null ? currentLevel.getPlayer() : null;
	}

	private void loadLevelConfig(LevelData levelData) {
		try {
			JSONObject config = ResourceManager.getInstance().getLevelConfig(levelData.getPath());
			levelData.setCompleted(config.getBoolean("completed"));
			if (config.has("thumbnail")) levelData.setThumbnailPath(config.getString("thumbnail"));
		} catch (Exception e) {
			throw new RuntimeException("Failed to load config data of level " + levelData.getName(), e);
		}
	}

	private void loadAllLevels() {
		try {
			ResourceManager resourceManager = ResourceManager.getInstance();

			File builtinURL = new File(getClass().getResource("/engine/levels/").toURI());
			for (String name : resourceManager.getLevelsFromDir(builtinURL)) {
				LevelData levelData = new LevelData(builtinURL.getPath() + "/" + name, name, true);
				loadLevelConfig(levelData);
				levels.put(name, levelData);
			}

			File importedURL = new File(resourceManager.getUserLevelsPath().toUri());
			for (String name : resourceManager.getLevelsFromDir(importedURL)) {
				LevelData levelData = new LevelData(importedURL.getPath() + "/" + name, name, false);
				loadLevelConfig(levelData);
				levels.put(name, levelData);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to load levels", e);
		}
	}

	public void loadLevel(String name) {
		if (currentLevel != null) currentLevel.clearGameData();

		LevelData levelData = levels.get(name);
		if (levelData == null) throw new RuntimeException("Level " + name + " not found");
		currentLevel = levelData;

		JSONObject config = ResourceManager.getInstance().getLevelConfig(levelData.getPath());

		// Load map data
		JSONObject map = config.getJSONObject("map");
		int rows = map.getInt("rows");
		int cols = map.getInt("cols");
		int tileSize = map.getInt("tileSize");
		levelData.setRows(rows);
		levelData.setCols(cols);
		levelData.setTileSize(tileSize);

		// Read the map from a text file and create tiles
		List<String> mapLines = ResourceManager.getInstance().getLevelMap(levelData.getPath());
		for (int row = 0; row < rows; row++) {
			String[] tileNumbers = mapLines.get(row).split(" ");
			for (int col = 0; col < cols; col++) {
				int tileType = Integer.parseInt(tileNumbers[col]);
				Tile tile = new Tile(col * tileSize, row * tileSize, tileSize, TileType.values()[tileType]);
				levelData.addGameObject(tile);
			}
		}

		// Load player data
		JSONObject playerData = config.getJSONObject("player");
		JSONArray playerPos = playerData.getJSONArray("pos");
		double playerSize = playerData.getDouble("size");
		double playerSpeed = playerData.getDouble("speed") * tileSize;
		double playerHealth = playerData.getDouble("health");
		Player player = new Player(playerPos.getDouble(0) * tileSize, playerPos.getDouble(1) * tileSize,
				playerSize * tileSize, playerSpeed, playerHealth);
		levelData.addGameObject(player);
		Camera.getInstance().setTarget(player);

		// Load enemies
		JSONArray enemies = config.getJSONArray("enemies");
		for (int i = 0; i < enemies.length(); i++) {
			JSONObject enemyData = enemies.getJSONObject(i);
			JSONArray enemyPos = enemyData.getJSONArray("pos");
			double enemySize = enemyData.getDouble("size");
			double enemySpeed = enemyData.getDouble("speed") * tileSize;
			double enemyHealth = enemyData.getDouble("health");
			Enemy enemy = new Enemy(enemyPos.getDouble(0) * tileSize, enemyPos.getDouble(1) * tileSize,
					enemySize * tileSize, enemySpeed, enemyHealth);
			levelData.addGameObject(enemy);
		}

		// Load chests
		JSONArray chests = config.getJSONArray("chests");
		for (int i = 0; i < chests.length(); i++) {
			JSONObject chestData = chests.getJSONObject(i);
			JSONArray chestPos = chestData.getJSONArray("pos");
			double chestSize = chestData.getDouble("size");
			Chest chest = new Chest(chestPos.getDouble(0) * tileSize, chestPos.getDouble(1) * tileSize,
					chestSize * tileSize);
			levelData.addGameObject(chest);
		}

		// Load shops
		JSONArray shops = config.getJSONArray("shops");
		for (int i = 0; i < shops.length(); i++) {
			JSONObject shopData = shops.getJSONObject(i);
			JSONArray shopPos = shopData.getJSONArray("pos");
			double shopSize = shopData.getDouble("size");
			Shop shop = new Shop(shopPos.getDouble(0) * tileSize, shopPos.getDouble(1) * tileSize, shopSize * tileSize);
			levelData.addGameObject(shop);
		}

		// Load stones
		JSONArray stones = config.getJSONArray("stones");
		for (int i = 0; i < stones.length(); i++) {
			JSONObject stoneData = stones.getJSONObject(i);
			JSONArray stonePos = stoneData.getJSONArray("pos");
			double stoneSize = stoneData.getDouble("size");
			Stone stone = new Stone(stonePos.getDouble(0) * tileSize, stonePos.getDouble(1) * tileSize,
					stoneSize * tileSize);
			levelData.addGameObject(stone);
		}
	}

}
