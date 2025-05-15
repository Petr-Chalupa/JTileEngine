package engine.gameobjects.items;

import engine.Engine;
import engine.gameobjects.entities.Entity;
import engine.utils.LevelLoader;

public class Key extends Item {
	public Key(double posX, double posY) {
		super(posX, posY, ItemType.KEY);

		this.name = "Key";
		this.stackSize = 1;
		this.price = 1000;
		this.maxUses = 1;
		this.uses = maxUses;
	}

	@Override
	public void interact(Entity user) {
		LevelLoader.getInstance().getCurrentLevel().removeGameObject(this);
		Engine.getInstance().levelComplete();
	}
}
