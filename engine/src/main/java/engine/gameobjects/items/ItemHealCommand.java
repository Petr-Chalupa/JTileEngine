package engine.gameobjects.items;

import engine.gameobjects.entities.Entity;

public class ItemHealCommand implements ItemCommand {
	private final int amount;

	public ItemHealCommand(int amount) {
		this.amount = amount;
	}

	@Override
	public boolean execute(Entity user) {
		user.heal(amount);
		return true;
	}
}
