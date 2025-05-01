package engine.gameobjects.tiles;

import engine.gameobjects.GameObject;

public class Tile extends GameObject {
	private final TileType type;

	public Tile(double posX, double posY, double size, TileType type) {
		super(posX, posY, 0, size);
		this.type = type;

		setSprite(type.getSpritePath());
		setCollider(0, 0, size, size);
	}

	public TileType getType() {
		return type;
	}

	public boolean isWalkable() {
		return type.isWalkable();
	}
}
