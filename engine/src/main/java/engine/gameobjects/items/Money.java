package engine.gameobjects.items;

import engine.gameobjects.entities.Entity;
import engine.utils.LevelLoader;

public class Money extends Item {

	public Money(double posX, double posY) {
		super(posX, posY, ItemType.MONEY);

		this.name = "Money";
		this.stackSize = 1;
		this.price = 1;
		this.maxUses = 1;
		this.uses = maxUses;
	}

	public Money(double posX, double posY, int price) {
		this(posX, posY);

		this.price = price;
	}

	@Override
	public void interact(Entity user) {
		user.setMoney(user.getMoney() + price);
		LevelLoader.getInstance().getCurrentLevel().removeGameObject(this);
	}
}
