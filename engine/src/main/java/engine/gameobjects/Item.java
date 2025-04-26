package engine.gameobjects;

public class Item extends GameObject {
	private final ItemType type;

	public Item(double posX, double posY, int layer, double size, ItemType type) {
		super(posX, posY, 1, size);
		this.type = type;

		setSprite(type.getSpritePath());
	}

	public ItemType getType() {
		return type;
	}

	public void use(Entity user) {
		type.use(user);
	}
}
