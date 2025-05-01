package engine.gameobjects.items;

import engine.gameobjects.GameObject;
import engine.gameobjects.Interactable;
import engine.gameobjects.entities.Entity;
import engine.utils.LevelLoader;

public class Item extends GameObject implements Interactable {
	private final ItemType type;
	private int uses;
	private int price;

	public Item(double posX, double posY, ItemType type) {
		super(posX, posY, 1, 0.5 * LevelLoader.getInstance().getTileSize());
		this.type = type;
		this.uses = type.getMaxUses();
		this.price = type.getPrice();

		setSprite(type.getSpritePath());
	}

	public Item(double posX, double posY, ItemType type, int price) {
		this(posX, posY, type);
		this.price = price;
	}

	public ItemType getType() {
		return type;
	}

	public int getUses() {
		return uses;
	}

	public int getPrice() {
		return price;
	}

	public boolean use(Entity user) {
		if (uses == 0) return false;

		if (type.use(user)) uses--;
		return uses > 0;
	}

	@Override
	public void interact(Entity user) {
		// Item pickup
		if (type == ItemType.MONEY) {
			user.setMoney(user.getMoney() + price);
			LevelLoader.getInstance().getGameObjects().remove(this);
		} else if (user.getInventory().addItem(this)) {
			LevelLoader.getInstance().getGameObjects().remove(this);
		}
	}
}
