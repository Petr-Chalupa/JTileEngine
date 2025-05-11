package engine.core;

import engine.gameobjects.GameObject;
import engine.gameobjects.blocks.Block;
import engine.gameobjects.entities.Entity;
import engine.gameobjects.entities.Player;
import engine.gameobjects.tiles.Tile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LevelData {
	// Base config data
	private String name;
	private String author;
	private boolean builtin;
	private boolean completed;
	private String thumbnail;
	private Date created;
	private Date updated;
	private int rows;
	private int cols;
	// Game data
	private boolean isLoaded;
	private final List<GameObject> gameObjects = new ArrayList<>();
	private final List<Tile> tiles = new ArrayList<>();
	private final List<Block> blocks = new ArrayList<>();
	private final List<Entity> entities = new ArrayList<>();
	private Player player;

	public LevelData(String name, boolean builtin) {
		this.name = name;
		this.builtin = builtin;
		this.completed = false;
		this.thumbnail = "default_sprite.png";
	}

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return author;
	}

	public boolean isBuiltin() {
		return builtin;
	}

	public boolean isCompleted() {
		return completed;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public Date getCreated() {
		return created;
	}

	public Date getUpdated() {
		return updated;
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public List<GameObject> getGameObjects() {
		return new ArrayList<>(gameObjects);
	}

	public List<Tile> getTiles() {
		return new ArrayList<>(tiles);
	}

	public List<Block> getBlocks() {
		return new ArrayList<>(blocks);
	}

	public List<Entity> getEntities() {
		return new ArrayList<>(entities);
	}

	public Player getPlayer() {
		return player;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setBuiltin(boolean builtin) {
		this.builtin = builtin;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public void setThumbnail(String thumbnail) {
		if (thumbnail == null || thumbnail.isBlank()) return;
		this.thumbnail = thumbnail;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public void setLoaded(boolean loaded) {
		isLoaded = loaded;
	}

	public void addGameObject(GameObject gameObject) {
		this.gameObjects.add(gameObject);
		if (gameObject instanceof Player) this.player = (Player) gameObject;
	}

	public void removeGameObject(GameObject gameObject) {
		this.gameObjects.remove(gameObject);
		if (this.player == gameObject) this.player = null;
	}

	public void addTile(Tile tile) {
		this.tiles.add(tile);
	}

	public void addBlock(Block block) {
		this.blocks.add(block);
	}

	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
