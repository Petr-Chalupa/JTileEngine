package engine.gameobjects.entities;

import engine.core.Healthbar;
import engine.core.Healthbar.HealthbarType;
import engine.gameobjects.items.Item;
import engine.gameobjects.items.ItemType;
import engine.utils.LevelLoader;

public class Enemy extends Entity {
	private double attackCooldownElapsed = 0;

	public Enemy(double posX, double posY, double size, double speed, double health) {
		super(posX, posY, 1, size, speed, health);
		this.healthbar = new Healthbar(this, HealthbarType.FLOAT);
		this.money = 1 + (int) (Math.random() * 5); // <1;5>

		setSprite("enemy_sprite.png");
		setCollider(0, 0, size, size);
	}

	@Override
	public void update(double deltaTime) {
		attackCooldownElapsed += deltaTime;
		double searchRange = 300;
		double attackRange = 100;
		double attackCooldown = 2;
		double damage = 1;

		Player player = LevelLoader.getInstance().getPlayer();
		double distance = player.getCollider().getDistanceTo(this.getCollider());
		if (distance <= searchRange) {
			double deltaX = Math.signum(player.getPosX() - this.getPosX()) * speed * deltaTime;
			double deltaY = Math.signum(player.getPosY() - this.getPosY()) * speed * deltaTime;
			boolean canMove = canMove(deltaX, deltaY);
			if (canMove) moveX(deltaX);
			if (canMove) moveY(deltaY);
		}
		if (distance <= attackRange && attackCooldownElapsed >= attackCooldown) {
			player.damage(damage);
			attackCooldownElapsed = 0;
		}
	}

	@Override
	public void renderUI(javafx.scene.canvas.GraphicsContext context) {
		healthbar.render(context);
	}

	@Override
	public void damage(double damage) {
		super.damage(damage);
		if (this.health == 0) {
			// Remove from the world
			LevelLoader.getInstance().getGameObjects().remove(this);
			// Drop money
			double offsetX = (Math.random() - 0.5) * 0.5 * size;
			double offsetY = (Math.random() - 0.5) * 0.5 * size;
			Item moneyItem = new Item(posX + offsetX, posY + offsetY, ItemType.MONEY, money);
			LevelLoader.getInstance().getGameObjects().add(moneyItem);
		}
	}
}
