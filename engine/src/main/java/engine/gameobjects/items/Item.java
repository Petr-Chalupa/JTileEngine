package engine.gameobjects.items;

import engine.gameobjects.GameObject;
import engine.gameobjects.entities.Entity;

public class Item extends GameObject {
	private final ItemType type;
	private int uses;

	public Item(double posX, double posY, int layer, double size, ItemType type) {
		super(posX, posY, 1, size);
		this.type = type;
		this.uses = type.getMaxUses();

		setSprite(type.getSpritePath());
	}

	public ItemType getType() {
		return type;
	}

	public int getUses() {
		return uses;
	}

	public boolean use(Entity user) {
		if (uses == 0) return false;

		if (type.use(user)) uses--;
		return uses > 0;
	}
}
