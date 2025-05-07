package engine.gameobjects.items;

import engine.gameobjects.entities.Entity;

public class ItemArmorCommand implements ItemCommand {
	private final int amount;

	public ItemArmorCommand(int amount) {
		this.amount = amount;
	}

	@Override
	public boolean execute(Entity user) {
		user.setArmor(user.getArmor() + amount);
		return false;
	}
}
