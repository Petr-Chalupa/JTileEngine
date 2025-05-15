package engine.gameobjects.tiles;

public enum TileType {
	GRASS("tile_sprite_grass.png", true),
	WATER("tile_sprite_water.png", false),
	LAVA("tile_sprite_lava.png", false),
	SAND("tile_sprite_sand.png", true);

	private final String spritePath;
	private final boolean isWalkable;

	TileType(String spritePath, boolean isWalkable) {
		this.spritePath = spritePath;
		this.isWalkable = isWalkable;
	}

	public String getSpritePath() {
		return spritePath;
	}

	public boolean isWalkable() {
		return isWalkable;
	}
}
