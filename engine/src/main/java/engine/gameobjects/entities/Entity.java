package engine.gameobjects.entities;

import engine.gameobjects.GameObject;
import engine.gameobjects.blocks.Block;
import engine.gameobjects.items.Item;
import engine.gameobjects.tiles.Tile;
import engine.ui.Healthbar;
import engine.ui.Inventory;
import engine.utils.LevelLoader;
import javafx.geometry.Bounds;

public class Entity extends GameObject {
	protected final double maxHealth;
	private final LevelLoader levelLoader;
	protected Inventory inventory;
	protected Healthbar healthbar;
	protected double speed;
	protected double health;
	protected int money;
	protected double armor;
	protected int facingDirectionX = -1; // -1 = left, 1 = right

	public Entity(double posX, double posY, int layer, double size, double speed, double health) {
		super(posX, posY, layer, size);
		this.speed = speed;
		this.maxHealth = health;
		this.health = health;
		this.money = 0;
		this.armor = 0;
		this.levelLoader = LevelLoader.getInstance();
	}

	public Inventory getInventory() {
		return inventory;
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public double getHealth() {
		return health;
	}

	public double getSpeed() {
		return speed;
	}

	public int getMoney() {
		return money;
	}

	public double getArmor() {
		return armor;
	}

	public int getFacingDirectionX() {
		return facingDirectionX;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public void setArmor(double armor) {
		this.armor = armor;
	}

	public void heal(double health) {
		this.health = Math.min(maxHealth, this.health + health);
	}

	public void damage(double damage) {
		if (armor > 0 && Math.random() > 0.5) {
			double healthDamage = armor - damage < 0 ? damage - armor : 0; // How much damage won't be covered by armor
			armor = Math.max(0, armor - damage);
			health = Math.max(0, health - healthDamage);
		} else {
			health = Math.max(0, health - damage);
		}
	}

	protected boolean isInMap(double deltaX, double deltaY) {
		double mapWidth = levelLoader.getCols();
		double mapHeight = levelLoader.getRows();
		double newMinX = collider.getMinX() + deltaX;
		double newMinY = collider.getMinY() + deltaY;
		double newMaxX = collider.getMaxX() + deltaX;
		double newMaxY = collider.getMaxY() + deltaY;
		return newMinX >= 0 && newMaxX <= mapWidth && newMinY >= 0 && newMaxY <= mapHeight;
	}

	protected boolean canMove(double deltaX, double deltaY) {
		if (!isInMap(deltaX, deltaY)) return false;
		return levelLoader.getGameObjects()
				.stream()
				.filter(gameObject -> !gameObject.equals(this)
						&& gameObject.isRendered()
						&& !(gameObject instanceof Item))
				.allMatch(gameObject -> {
					Bounds intersection = collider.getIntersection(gameObject.getCollider(), deltaX, deltaY);
					return intersection == null
							|| (gameObject instanceof Block && !((Block) gameObject).isSolid())
							|| (gameObject instanceof Tile && ((Tile) gameObject).isWalkable());
				});
	}

	protected void move(double deltaX, double deltaY) {
		if (!canMove(deltaX, deltaY)) return;
		if (deltaX != 0) {
			posX += deltaX;
			facingDirectionX = (int) Math.signum(deltaX);
		}
		if (deltaY != 0) {
			posY += deltaY;
		}
	}

}
