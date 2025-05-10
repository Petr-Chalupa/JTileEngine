package engine.gameobjects.items;

import engine.gameobjects.entities.Entity;

public class Food extends Item {
	public enum FoodType {
		MEAT, GRANULE, TREAT
	}

	private double value;

	public Food(double posX, double posY, FoodType foodType) {
		super(posX, posY, foodType == FoodType.TREAT ? ItemType.TREAT : foodType == FoodType.GRANULE ? ItemType.GRANULE : ItemType.MEAT);

		switch (foodType) {
			case TREAT:
				this.name = "Treat";
				this.stackSize = 10;
				this.price = 3;
				this.value = 6;
			case MEAT:
				this.name = "Meat";
				this.stackSize = 2;
				this.price = 2;
				this.value = 3;
				break;
			case GRANULE:
				this.name = "Granule";
				this.stackSize = 2;
				this.price = 1;
				this.value = 1;
				break;
		}

		this.maxUses = 1;
		this.uses = maxUses;
	}

	@Override
	protected boolean useAction(Entity user) {
		boolean heal = user.getHealth() < user.getMaxHealth();
		if (heal) user.heal(value);

		return heal;
	}
}
