package engine.utils;

import engine.gameobjects.GameObject;
import engine.gameobjects.blocks.Chest;
import engine.gameobjects.blocks.Tile;
import engine.gameobjects.blocks.TileType;
import engine.gameobjects.entities.Enemy;
import engine.gameobjects.entities.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LevelLoader {
	private static LevelLoader instance;
	// level
	private String name;
	private boolean completed;
	private final List<GameObject> gameObjects = new ArrayList<>();
	// map
	private int rows;
	private int cols;
	private int tileSize;
	// player
	private Player player;

	private LevelLoader() {
	}

	public static LevelLoader getInstance() {
		if (instance == null) instance = new LevelLoader();
		return instance;
	}

	public String getName() {
		return name;
	}

	public boolean isCompleted() {
		return completed;
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	public int getTileSize() {
		return tileSize;
	}

	public Player getPlayer() {
		return player;
	}

	public void loadFile(String path) {
		JSONObject config = ResourceManager.getInstance().getLevelConfig(path);

		// Load level data
		this.name = config.getString("name");
		this.completed = config.getBoolean("completed");
		this.gameObjects.clear();

		// Load map data
		JSONObject map = config.getJSONObject("map");
		this.rows = map.getInt("rows");
		this.cols = map.getInt("cols");
		this.tileSize = map.getInt("tileSize");

		// Read the map from a text file and create tiles
		List<String> mapLines = ResourceManager.getInstance().getLevelMap(path);
		for (int row = 0; row < rows; row++) {
			String[] tileNumbers = mapLines.get(row).split(" ");
			for (int col = 0; col < cols; col++) {
				int tileType = Integer.parseInt(tileNumbers[col]);
				Tile tile = new Tile(col * tileSize, row * tileSize, tileSize, TileType.values()[tileType]);
				gameObjects.add(tile);
			}
		}

		// Load player data
		JSONObject playerData = config.getJSONObject("player");
		JSONArray playerPos = playerData.getJSONArray("pos");
		double playerSize = playerData.getDouble("size");
		double playerSpeed = playerData.getDouble("speed") * tileSize;
		double playerHealth = playerData.getDouble("health");
		player = new Player(playerPos.getDouble(0) * tileSize, playerPos.getDouble(1) * tileSize, playerSize * tileSize,
				playerSpeed, playerHealth);
		gameObjects.add(player);

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
			gameObjects.add(enemy);
		}

		// Load chests
		JSONArray chests = config.getJSONArray("chests");
		for (int i = 0; i < chests.length(); i++) {
			JSONObject chestData = chests.getJSONObject(i);
			JSONArray chestPos = chestData.getJSONArray("pos");
			double chestSize = chestData.getDouble("size");
			Chest chest = new Chest(chestPos.getDouble(0) * tileSize, chestPos.getDouble(1) * tileSize,
					chestSize * tileSize);
			gameObjects.add(chest);
		}
	}

	// public void saveFile(){}
}
