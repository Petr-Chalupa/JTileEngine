package engine.gameobjects.blocks;

public enum TileType {
	GRASS("tile_sprite0.png", true),
	//
	WATER("tile_sprite1.png", false);

	private final String spritePath;
	private final boolean isSolid;

	TileType(String spritePath, boolean isSolid) {
		this.spritePath = spritePath;
		this.isSolid = isSolid;
	}

	public String getSpritePath() {
		return spritePath;
	}

	public boolean isSolid() {
		return isSolid;
	}
}
