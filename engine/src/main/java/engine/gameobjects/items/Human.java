package engine.gameobjects.items;

import engine.gameobjects.blocks.Spawner;
import engine.gameobjects.entities.Entity;
import engine.utils.LevelLoader;

import java.util.Comparator;

public class Human extends Item {

	public Human(double posX, double posY) {
		super(posX, posY, ItemType.HUMAN);

		this.name = "Human";
		this.stackSize = 1;
		this.price = 7;
		this.maxUses = 1;
		this.uses = maxUses;
	}

	@Override
	public boolean useAction(Entity user) {
		double attackRange = 2;

		Spawner closestSpawnerInRange = LevelLoader.getInstance()
				.getCurrentLevel()
				.getBlocks()
				.stream()
				.filter(block -> block instanceof Spawner)
				.map(block -> (Spawner) block)
				.filter(spawner -> spawner.getCollider().getDistanceTo(user.getCollider()) <= attackRange)
				.min(Comparator.comparingDouble(s -> s.getCollider().getDistanceTo(user.getCollider())))
				.orElse(null);

		if (closestSpawnerInRange != null) {
			closestSpawnerInRange.destroy();
			return true;
		}
		return false;
	}
}
