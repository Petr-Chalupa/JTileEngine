package engine.core;

import engine.gameobjects.GameObject;
import engine.gameobjects.entities.Player;

import java.util.ArrayList;
import java.util.List;

public class LevelData {
	// Base config data
	private final String path;
	private final String name;
	private final boolean builtin;
	private boolean completed;
	private String thumbnailPath;
	// Game data
	private final List<GameObject> gameObjects = new ArrayList<>();
	private int rows;
	private int cols;
	private int tileSize;
	private Player player;

	public LevelData(String path, String name, boolean builtin) {
		this.path = path;
		this.name = name;
		this.builtin = builtin;
		this.completed = false;
		this.thumbnailPath = "default_sprite.png";
	}

	public String getName() {
		return name;
	}

	public boolean isBuiltin() {
		return builtin;
	}


	public String getPath() {
		return path;
	}

	public boolean isCompleted() {
		return completed;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public List<GameObject> getGameObjects() {
		return new ArrayList<>(gameObjects);
	}

	public void addGameObject(GameObject gameObject) {
		this.gameObjects.add(gameObject);
		if (gameObject instanceof Player) this.player = (Player) gameObject;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	public int getTileSize() {
		return tileSize;
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	public Player getPlayer() {
		return player;
	}

	public void clearGameData() {
		this.gameObjects.clear();
		this.player = null;
	}

}
