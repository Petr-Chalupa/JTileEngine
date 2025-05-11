package engine.gameobjects.tiles;

import engine.gameobjects.GameObject;

public class Tile extends GameObject {
	private final TileType type;

	public Tile(double posX, double posY, TileType type) {
		super(posX, posY, 0, 1);
		this.type = type;

		setSprite(type.getSpritePath());
	}

	public TileType getType() {
		return type;
	}

	public boolean isWalkable() {
		return type.isWalkable();
	}
}
