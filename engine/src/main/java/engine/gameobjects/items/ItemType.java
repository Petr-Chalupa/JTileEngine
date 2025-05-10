package engine.gameobjects.items;

public enum ItemType {
	SWORD_BASIC("sword_sprite_basic.png"),
	SWORD_STRONG("sword_sprite_strong.png"),
	ARMOR("armor_sprite.png"),
	MONEY("money_sprite.png"),
	MEAT("meat_sprite.png"),
	GRANULE("granule_sprite.png"),
	TREAT("treat_sprite.png"),
	HUMAN("human_sprite.png");

	private final String spritePath;

	ItemType(String spritePath) {
		this.spritePath = spritePath;
	}

	public String getSpritePath() {
		return spritePath;
	}
}
