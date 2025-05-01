package engine.gameobjects.blocks;

public enum BlockType {
	CHEST("chest_sprite.png", true),
	SHOP("shop_sprite.png", true),
	STONE("stone_sprite.png", true);

	private final String spritePath;
	private final boolean isSolid;

	BlockType(String spritePath, boolean isSolid) {
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
