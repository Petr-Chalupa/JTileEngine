package engine.gameobjects.items;

import engine.gameobjects.GameObject;
import engine.gameobjects.entities.Entity;
import engine.utils.LevelLoader;

public class Sword extends Item {
	public enum SwordType {
		BASIC, STRONG
	}

	private double damage;
	private double range;
	private int maxHitsPerAttack;

	public Sword(double posX, double posY, SwordType swordType) {
		super(posX, posY, swordType == SwordType.BASIC ? ItemType.SWORD_BASIC : ItemType.SWORD_STRONG);

		switch (swordType) {
			case STRONG:
				this.name = "Strong Sword";
				this.maxUses = 250;
				this.price = 10;
				this.damage = 5;
				this.range = 2.5;
				this.maxHitsPerAttack = 2;
				break;
			case BASIC:
				this.name = "Basic Sword";
				this.maxUses = 100;
				this.price = 2;
				this.damage = 1;
				this.range = 1;
				this.maxHitsPerAttack = 1;
				break;
		}

		this.stackSize = 1;
		this.uses = maxUses;
	}

	public double getRange() {
		return range;
	}

	@Override
	protected boolean useAction(Entity user) {
		int hits = 0;

		for (GameObject obj : LevelLoader.getInstance().getGameObjects()) {
			if (obj instanceof Entity target && obj != user) {
				double distance = user.getCollider().getDistanceTo(target.getCollider());
				if (distance <= range) {
					target.damage(damage);
					if (hits++ == maxHitsPerAttack) break;
				}
			}
		}

		return hits > 0;
	}
}
