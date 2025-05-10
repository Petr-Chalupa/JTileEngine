package engine.gameobjects.items;

import engine.gameobjects.GameObject;
import engine.gameobjects.Interactable;
import engine.gameobjects.entities.Entity;
import engine.utils.LevelLoader;

public class Item extends GameObject implements Interactable {
	private final ItemType itemType;
	protected String name;
	protected int maxUses;
	protected int uses;
	protected int stackSize;
	protected int price;

	public Item(double posX, double posY, ItemType itemType) {
		super(posX, posY, 1, 0.5 * LevelLoader.getInstance().getTileSize());

		this.itemType = itemType;

		setSprite(itemType.getSpritePath());
	}

	public ItemType getType() {
		return itemType;
	}

	public String getName() {
		return name;
	}

	public int getMaxUses() {
		return maxUses;
	}

	public int getUses() {
		return uses;
	}

	public int getStackSize() {
		return stackSize;
	}

	public int getPrice() {
		return price;
	}

	public void use(Entity user) {
		if (uses == 0) return;
		if (useAction(user)) uses--;
	}

	protected boolean useAction(Entity user) {
		return false;
	}

	@Override
	public void interact(Entity user) {
		// Item pickup
		if (user.getInventory().addItem(this)) {
			LevelLoader.getInstance().getCurrentLevel().removeGameObject(this);
		}
	}
}
