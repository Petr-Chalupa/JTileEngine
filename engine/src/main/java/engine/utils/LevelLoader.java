package engine.utils;

import engine.Engine;
import engine.core.Camera;
import engine.core.GameStateManager;
import engine.core.LevelData;
import engine.gameobjects.GameObject;
import engine.gameobjects.blocks.*;
import engine.gameobjects.entities.Enemy;
import engine.gameobjects.entities.Entity;
import engine.gameobjects.entities.Player;
import engine.gameobjects.items.*;
import engine.gameobjects.tiles.Tile;
import engine.gameobjects.tiles.TileType;
import engine.ui.Inventory;
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

	public void loadLevel(String name) {
		LevelData level = levels.get(name);
		if (!level.isLoaded()) {
			try {
				Path configPath = levelPaths.get(name);
				loadLevelObjects(level, configPath);
			} catch (IOException e) {
				Engine.LOGGER.severe("Failed to load level " + name + ": " + e.getMessage());
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
					Engine.LOGGER.severe("Failed to load level metadata from " + configPath + ": " + e.getMessage());
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

		for (Object item : obj.optJSONArray("tiles")) {
			levelData.addGameObject(deserializeTile((JSONObject) item));
		}

		for (Object item : obj.optJSONArray("blocks")) {
			levelData.addGameObject(deserializeBlock((JSONObject) item));
		}

		for (Object item : obj.optJSONArray("entities")) {
			Entity entity = deserializeEntity((JSONObject) item);
			System.out.println(entity.getClass().getSimpleName() + " " + entity.getPosX() + ", " + entity.getPosY() + " " + entity.getSize());
			if (entity instanceof Player) Camera.getInstance().setTarget(entity);
			levelData.addGameObject(entity);
		}

		levelData.setLoaded(true);
	}

	public void saveLevel(String name) {
		try {
			LevelData levelData = levels.get(name);
			Path configPath = levelPaths.get(name);
			if (levelData == null || configPath == null) {
				Engine.LOGGER.severe("Cannot save level: no data or path found for name '" + name + "'");
				return;
			}
			saveLevel(levelData, configPath);
		} catch (IOException e) {
			Engine.LOGGER.severe("Failed to save level " + name + ": " + e.getMessage());
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
			tiles.put(serializeTile(t));
		}
		obj.put("tiles", tiles);

		JSONArray blocks = new JSONArray();
		for (Block b : levelData.getBlocks()) {
			blocks.put(serializeBlock(b));
		}
		obj.put("blocks", blocks);

		JSONArray entities = new JSONArray();
		for (Entity e : levelData.getEntities()) {
			entities.put(serializeEntity(e));
		}
		obj.put("entities", entities);

		Files.writeString(path, obj.toString(4));
	}

	/* --------------- SERIALIZATION --------------- */

	private JSONArray serializeInventory(Inventory inventory) {
		JSONArray obj = new JSONArray();
		for (int i = 0; i < inventory.getSize(); i++) {
			JSONArray slotObj = new JSONArray();
			inventory.getSlot(i).forEach(item -> slotObj.put(serializeItem(item)));
			obj.put(slotObj);
		}
		return obj;
	}

	private JSONObject serializeBlock(Block block) {
		JSONObject obj = new JSONObject();
		obj.put("posX", block.getPosX());
		obj.put("posY", block.getPosY());
		obj.put("size", block.getSize());
		obj.put("type", block.getType().ordinal());
		if (block instanceof Chest chest && chest.getInventory() != null) {
			obj.put("inventory", serializeInventory(chest.getInventory()));
		}
		if (block instanceof Shop shop && shop.getInventory() != null) {
			obj.put("inventory", serializeInventory(shop.getInventory()));
		}
		return obj;
	}

	private JSONObject serializeEntity(Entity entity) {
		JSONObject obj = new JSONObject();
		obj.put("posX", entity.getPosX());
		obj.put("posY", entity.getPosY());
		obj.put("size", entity.getSize());
		obj.put("speed", entity.getSpeed());
		obj.put("health", entity.getHealth());
		obj.put("money", entity.getMoney());
		obj.put("armor", entity.getArmor());
		obj.put("type", entity.getClass().getSimpleName());
		if (entity.getInventory() != null) obj.put("inventory", serializeInventory(entity.getInventory()));
		return obj;
	}

	private JSONObject serializeItem(Item item) {
		JSONObject obj = new JSONObject();
		obj.put("posX", item.getPosX());
		obj.put("posY", item.getPosY());
		obj.put("name", item.getName());
		obj.put("maxUses", item.getMaxUses());
		obj.put("uses", item.getUses());
		obj.put("stackSize", item.getStackSize());
		obj.put("price", item.getPrice());
		obj.put("type", item.getType().ordinal());
		return obj;
	}

	private JSONObject serializeTile(Tile tile) {
		JSONObject obj = new JSONObject();
		obj.put("posX", tile.getPosX());
		obj.put("posY", tile.getPosY());
		obj.put("type", tile.getType().ordinal());
		return obj;
	}

	/* -------------- DESERIALIZATION ------------ */

	private List<List<Item>> deserializeInventory(JSONArray obj) {
		List<List<Item>> items = new ArrayList<>();
		for (Object slotObj : obj) {
			List<Item> slot = new ArrayList<>();
			for (Object itemObj : (JSONArray) slotObj) {
				slot.add(deserializeItem((JSONObject) itemObj));
			}
			items.add(slot);
		}
		return items;
	}

	private Block deserializeBlock(JSONObject obj) {
		double x = obj.optDouble("posX", 0);
		double y = obj.optDouble("posY", 0);
		double size = obj.optDouble("size", 1);
		BlockType type = BlockType.values()[obj.optInt("type", 0)];
		Block block = switch (type) {
			case CHEST -> new Chest(x, y, size);
			case SHOP -> new Shop(x, y, size);
			case STONE -> new Stone(x, y, size);
		};
		if (block instanceof Chest chest && obj.has("inventory")) chest.getInventory().setItems(deserializeInventory(obj.getJSONArray("inventory")));
		if (block instanceof Shop shop && obj.has("inventory")) shop.getInventory().setItems(deserializeInventory(obj.getJSONArray("inventory")));
		return block;
	}

	private Entity deserializeEntity(JSONObject obj) {
		double x = obj.optDouble("posX", 0);
		double y = obj.optDouble("posY", 0);
		double size = obj.optDouble("size", 1);
		double speed = obj.optDouble("speed", 1);
		double health = obj.optDouble("health", 1);
		String type = obj.optString("type", "Entity");
		Entity entity = switch (type) {
			case "Player" -> new Player(x, y, size, speed, health);
			case "Enemy" -> new Enemy(x, y, size, speed, health);
			default -> new Entity(x, y, 1, size, speed, health);
		};
		entity.setArmor(obj.optDouble("armor", 0));
		entity.setMoney(obj.optInt("money", 0));
		if (entity.getInventory() != null && obj.has("inventory")) entity.getInventory().setItems(deserializeInventory(obj.getJSONArray("inventory")));
		return entity;
	}

	private Item deserializeItem(JSONObject obj) {
		double x = obj.optDouble("posX", 0);
		double y = obj.optDouble("posY", 0);
		String name = obj.optString("name", "Item");
		int maxUses = obj.optInt("maxUses", 1);
		int uses = obj.optInt("uses", 1);
		int stackSize = obj.optInt("stackSize", 1);
		ItemType type = ItemType.values()[obj.optInt("type", 0)];
		Item item = switch (type) {
			case SWORD_BASIC -> new Sword(x, y, Sword.SwordType.BASIC);
			case SWORD_STRONG -> new Sword(x, y, Sword.SwordType.STRONG);
			case ARMOR -> new Armor(x, y);
			case MONEY -> new Money(x, y, obj.optInt("price", 1));
			case MEAT -> new Food(x, y, Food.FoodType.MEAT);
			case GRANULE -> new Food(x, y, Food.FoodType.GRANULE);
			case TREAT -> new Food(x, y, Food.FoodType.TREAT);
			case HUMAN -> new Human(x, y);
		};
		item.setName(name);
		item.setMaxUses(maxUses);
		item.setUses(uses);
		item.setStackSize(stackSize);
		return item;
	}

	private Tile deserializeTile(JSONObject obj) {
		double x = obj.optDouble("posX", 0);
		double y = obj.optDouble("posY", 0);
		TileType type = TileType.values()[obj.optInt("type", 0)];
		return new Tile(x, y, type);
	}

}
