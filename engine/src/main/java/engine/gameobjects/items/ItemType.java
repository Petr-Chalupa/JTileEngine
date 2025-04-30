package engine.gameobjects.items;

import engine.gameobjects.entities.Entity;

public enum ItemType {
	SWORD_BASIC("sword_sprite.png", 1, 1, 1000, new ItemSwordCommand(1, 50)),
	SWORD_STRONG("sword_sprite.png", 1, 2, 50, new ItemSwordCommand(3, 70)),
	ARMOR("armor_sprite.png", 1, 3, 1, new ItemSwordCommand(1, 50)), // todo
	MONEY("money_sprite.png", 10, 4, 1, new ItemSwordCommand(1, 50)), // todo
	MEAT("meat_sprite.png", 10, 5, 1, new ItemHealCommand(3)),
	GRANULE("granule_sprite.png", 10, 6, 1, new ItemHealCommand(1)),
	TREAT("treat_sprite.png", 10, 7, 1, new ItemHealCommand(5)),
	HUMAN("human_sprite.png", 1, 8, 1, new ItemSwordCommand(1, 50));// todo

	private final String spritePath;
	private final int stackSize;
	private final int price;
	private final int maxUses;
	private final ItemCommand command;

	ItemType(String spritePath, int stackSize, int price, int maxUses, ItemCommand command) {
		this.spritePath = spritePath;
		this.stackSize = stackSize;
		this.price = price;
		this.maxUses = maxUses;
		this.command = command;
	}

	public String getSpritePath() {
		return spritePath;
	}

	public int getStackSize() {
		return stackSize;
	}

	public int getPrice() {
		return price;
	}

	public int getMaxUses() {
		return maxUses;
	}

	public boolean use(Entity user) {
		return command.execute(user);
	}
}
