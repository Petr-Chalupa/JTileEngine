package engine.gameobjects;

import engine.utils.LevelLoader;
import javafx.geometry.Bounds;

public class Entity extends GameObject {
	protected final int maxHealth;
	private final LevelLoader levelLoader;
	protected double speed;
	protected int health;

	public Entity(double posX, double posY, int layer, double size, double speed, int health) {
		super(posX, posY, layer, size);
		this.speed = speed;
		this.maxHealth = health;
		this.health = health;
		this.levelLoader = LevelLoader.getInstance();
	}

	@Override
	public void update(double deltaTime) {
	}

	public void heal(int health) {
		this.health = Math.min(maxHealth, this.health + health);
	}

	protected boolean isInMap(double deltaX, double deltaY) {
		double tileSize = levelLoader.getTileSize();
		double maxX = (levelLoader.getCols() - 1) * tileSize;
		double maxY = (levelLoader.getRows() - 1) * tileSize;
		double newX = posX + deltaX;
		double newY = posY + deltaY;
		return newX > 0 && newX < maxX && newY > 0 && newY < maxY;
	}

	protected boolean canMove(double deltaX, double deltaY) {
		if (!isInMap(deltaX, deltaY)) return false;
		return levelLoader.getGameObjects()
				.stream()
				.filter(gameObject -> !gameObject.equals(this) && gameObject.isRendered())
				.allMatch(gameObject -> {
					Bounds intersection = movementCollider.getIntersection(gameObject.movementCollider, deltaX, deltaY);
					return intersection == null || (gameObject instanceof Tile && ((Tile) gameObject).getType().isSolid());
				});
	}

	protected void moveX(double dist) {
		posX += dist;
	}

	protected void moveY(double dist) {
		posY += dist;
	}

}
