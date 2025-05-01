package engine.gameobjects.items;

import engine.gameobjects.GameObject;
import engine.gameobjects.entities.Entity;
import engine.utils.LevelLoader;

import java.util.ArrayList;
import java.util.List;

public class ItemSwordCommand implements ItemCommand {
	int damage;
	double range;

	public ItemSwordCommand(int damage, double range) {
		this.damage = damage;
		this.range = range;
	}

	@Override
	public boolean execute(Entity user) {
		boolean hit = false;

		List<GameObject> gameObjectsCopy = new ArrayList<>(LevelLoader.getInstance().getGameObjects());
		for (GameObject obj : gameObjectsCopy) {
			if (obj instanceof Entity target && obj != user && target.getCollider() != null) {
				double distance = user.getCollider().getDistanceTo(target.getCollider());
				if (distance <= range) {
					target.damage(damage);
					hit = true;
				}
			}
		}

		return hit;
	}
}
