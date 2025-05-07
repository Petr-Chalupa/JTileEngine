package engine.gameobjects.items;

import engine.gameobjects.entities.Entity;

public class ItemHealCommand implements ItemCommand {
	private final int amount;

	public ItemHealCommand(int amount) {
		this.amount = amount;
	}

	@Override
	public boolean execute(Entity user) {
		boolean heal = user.getHealth() < user.getMaxHealth();
		if (heal) user.heal(amount);
		return heal;
	}
}
