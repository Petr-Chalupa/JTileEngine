package engine.gameobjects.items;

import engine.gameobjects.entities.Entity;

public class Armor extends Item {
	private final double value;

	public Armor(double posX, double posY) {
		super(posX, posY, ItemType.ARMOR);

		this.name = "Armor";
		this.stackSize = 1;
		this.price = 3;
		this.value = 1;
		this.maxUses = 1;
		this.uses = maxUses;
	}

	@Override
	protected boolean useAction(Entity user) {
		user.setArmor(user.getArmor() + value);
		return true;
	}
}
