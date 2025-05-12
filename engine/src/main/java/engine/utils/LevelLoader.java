package engine.utils;

import engine.core.Camera;
import engine.core.GameStateManager;
import engine.core.LevelData;
import engine.gameobjects.GameObject;
import engine.gameobjects.blocks.*;
import engine.gameobjects.entities.Enemy;
import engine.gameobjects.entities.Entity;
import engine.gameobjects.entities.Player;
import engine.gameobjects.tiles.Tile;
import engine.gameobjects.tiles.TileType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class LevelLoader {
	private static LevelLoader instance;
	private final Map<String, LevelData> levels = new HashMap<>();
	private final Map<String, Path> levelPaths = new HashMap<>();
	private String currentLevelName;

	private LevelLoader() {
		ResourceManager resourceManager = ResourceManager.getInstance();
		loadAllLevelsMetadata(resourceManager.getBuiltinSavePath().resolve("levels"));
		loadAllLevelsMetadata(resourceManager.getUserSavePath().resolve("levels"));

		GameStateManager.getInstance().addListener((GameStateManager.GameState state) -> {
			if (state == GameStateManager.GameState.UNINITIALIZED) {
				levels.forEach((name, data) -> data.setLoaded(false));
				currentLevelName = null;
			}
		});
	}

	public static LevelLoader getInstance() {
		if (instance == null) instance = new LevelLoader();
		return instance;
	}

	public List<LevelData> getBuiltinLevels() {
		return levels.values().stream().filter(LevelData::isBuiltin).collect(Collectors.toList());
	}

	public List<LevelData> getImportedLevels() {
		return levels.values().stream().filter(level -> !level.isBuiltin()).collect(Collectors.toList());
	}

	public LevelData getCurrentLevel() {
		return currentLevelName != null ? levels.get(currentLevelName) : null;
	}

	public String getName() {
		LevelData level = getCurrentLevel();
		return level != null ? level.getName() : null;
	}

	public boolean isCompleted() {
		LevelData level = getCurrentLevel();
		return level != null && level.isCompleted();
	}

	public List<GameObject> getGameObjects() {
		LevelData level = getCurrentLevel();
		return level != null ? level.getGameObjects() : new ArrayList<>();
	}

	public int getRows() {
		LevelData level = getCurrentLevel();
		return level != null ? level.getRows() : 0;
	}

	public int getCols() {
		LevelData level = getCurrentLevel();
		return level != null ? level.getCols() : 0;
	}

	public Player getPlayer() {
		LevelData level = getCurrentLevel();
		return level != null ? level.getPlayer() : null;
	}

	public void setCurrentLevel(String name) {
		if (levels.containsKey(name)) {
			currentLevelName = name;
		}
	}

	public void loadLevel(String name) {
		LevelData level = levels.get(name);
		if (!level.isLoaded()) {
			try {
				Path configPath = levelPaths.get(name);
				loadLevelObjects(level, configPath);
			} catch (IOException e) {
				throw new RuntimeException("Failed to load level " + name, e);
			}
		}
		currentLevelName = name;
	}

	private void loadAllLevelsMetadata(Path levelsDir) {
		File[] dirs = levelsDir.toFile().listFiles(File::isDirectory);
		if (dirs == null) return;

		for (File dir : dirs) {
			Path configPath = dir.toPath().resolve("config.json");
			if (Files.exists(configPath)) {
				try {
					LevelData data = loadLevelMetadata(configPath);
					String name = data.getName();
					levels.put(name, data);
					levelPaths.put(name, configPath);
				} catch (IOException e) {
					throw new RuntimeException("Failed to load metadata from " + configPath, e);
				}
			}
		}
	}

	private LevelData loadLevelMetadata(Path configPath) throws IOException {
		String content = Files.readString(configPath);
		JSONObject obj = new JSONObject(content);

		LevelData level = new LevelData(
				obj.optString("name", configPath.getParent().getFileName().toString()),
				obj.optBoolean("builtin", false)
		);
		level.setAuthor(obj.optString("author", ""));
		level.setThumbnail(obj.optString("thumbnail", ""));
		level.setCompleted(obj.optBoolean("completed", false));
		level.setCreated(new Date(obj.optLong("created", 0)));
		level.setUpdated(new Date(obj.optLong("updated", 0)));
		level.setRows(obj.optInt("rows", 0));
		level.setCols(obj.optInt("cols", 0));
		return level;
	}

	private void loadLevelObjects(LevelData levelData, Path configPath) throws IOException {
		if (levelData.isLoaded()) return;
		levelData.clearGameObjects();

		String content = Files.readString(configPath);
		JSONObject obj = new JSONObject(content);

		// Player
		if (obj.has("player")) {
			JSONObject p = obj.getJSONObject("player");
			Player player = new Player(
					p.getDouble("posX"),
					p.getDouble("posY"),
					p.getDouble("size"),
					p.getDouble("speed"),
					p.getDouble("health")
			);
			player.setMoney(p.optInt("money", 0));
			player.setArmor(p.optDouble("armor", 0));
			levelData.addGameObject(player);
			Camera.getInstance().setTarget(player);
		}

		// Tiles
		for (Object item : obj.optJSONArray("tiles")) {
			JSONObject t = (JSONObject) item;
			Tile tile = new Tile(
					t.getDouble("posX"),
					t.getDouble("posY"),
					TileType.values()[t.getInt("type")]
			);
			levelData.addGameObject(tile);
		}

		// Blocks
		for (Object item : obj.optJSONArray("blocks")) {
			JSONObject b = (JSONObject) item;
			double x = b.getDouble("posX");
			double y = b.getDouble("posY");
			double size = b.getDouble("size");
			BlockType type = BlockType.values()[b.getInt("type")];

			Block block = switch (type) {
				case CHEST -> new Chest(x, y, size);
				case SHOP -> new Shop(x, y, size);
				case STONE -> new Stone(x, y, size);
				default -> new Block(x, y, size, type);
			};

			levelData.addGameObject(block);
		}

		// Entities
		for (Object item : obj.optJSONArray("entities")) {
			JSONObject e = (JSONObject) item;
			String type = e.optString("class", "Entity");
			double x = e.getDouble("posX");
			double y = e.getDouble("posY");
			double size = e.getDouble("size");
			double speed = e.getDouble("speed");
			double health = e.getDouble("health");

			Entity entity = switch (type) {
				case "Enemy" -> new Enemy(x, y, size, speed, health);
				default -> new Entity(x, y, 1, size, speed, health);
			};

			levelData.addGameObject(entity);
		}

		levelData.setLoaded(true);
	}

	public void saveLevel(String name) {
		try {
			LevelData levelData = levels.get(name);
			Path configPath = levelPaths.get(name);
			if (levelData == null || configPath == null) {
				throw new RuntimeException("Cannot save level: no data or path found for name '" + name + "'");
			}
			saveLevel(levelData, configPath);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save level " + name, e);
		}
	}

	private void saveLevel(LevelData levelData, Path path) throws IOException {
		JSONObject obj = new JSONObject();

		obj.put("name", levelData.getName());
		obj.put("author", levelData.getAuthor());
		obj.put("builtin", levelData.isBuiltin());
		obj.put("completed", levelData.isCompleted());
		obj.put("thumbnail", levelData.getThumbnail());
		obj.put("created", levelData.getCreated().getTime());
		obj.put("updated", levelData.getUpdated().getTime());
		obj.put("rows", levelData.getRows());
		obj.put("cols", levelData.getCols());

		JSONArray tiles = new JSONArray();
		for (Tile t : levelData.getTiles()) {
			JSONObject tile = new JSONObject();
			tile.put("posX", t.getPosX());
			tile.put("posY", t.getPosY());
			tile.put("type", t.getType().ordinal());
			tiles.put(tile);
		}
		obj.put("tiles", tiles);

		JSONArray blocks = new JSONArray();
		for (Block b : levelData.getBlocks()) {
			JSONObject block = new JSONObject();
			block.put("posX", b.getPosX());
			block.put("posY", b.getPosY());
			block.put("size", b.getSize());
			block.put("type", b.getType().ordinal());
			blocks.put(block);
		}
		obj.put("blocks", blocks);

		JSONArray entities = new JSONArray();
		for (Entity e : levelData.getEntities()) {
			if (e instanceof Player) continue;
			JSONObject ent = new JSONObject();
			ent.put("posX", e.getPosX());
			ent.put("posY", e.getPosY());
			ent.put("size", e.getSize());
			ent.put("speed", e.getSpeed());
			ent.put("health", e.getHealth());
			ent.put("class", e.getClass().getSimpleName());
			entities.put(ent);
		}
		obj.put("entities", entities);

		if (levelData.getPlayer() != null) {
			Player p = levelData.getPlayer();
			JSONObject player = new JSONObject();
			player.put("posX", p.getPosX());
			player.put("posY", p.getPosY());
			player.put("size", p.getSize());
			player.put("speed", p.getSpeed());
			player.put("health", p.getHealth());
			player.put("money", p.getMoney());
			player.put("armor", p.getArmor());
			player.put("class", "Player");
			obj.put("player", player);
		}

		Files.writeString(path, obj.toString(4));
	}
}
