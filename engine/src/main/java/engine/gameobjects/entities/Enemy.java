package engine.gameobjects.entities;

import engine.core.Healthbar;
import engine.core.Healthbar.HealthbarType;
import engine.gameobjects.items.Item;
import engine.gameobjects.items.ItemType;
import engine.utils.LevelLoader;

public class Enemy extends Entity {
	private double attackCooldownElapsed = 0;
	private double patrolTimeElapsed = 0;
	private double patrolDirectionX = 0;
	private double patrolDirectionY = 0;

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
		// Movement
		if (distance <= searchRange) moveTowardsPlayer(deltaTime);
		else patrol(deltaTime);
		// Attacking
		if (distance <= attackRange && attackCooldownElapsed >= attackCooldown) {
			player.damage(damage);
			attackCooldownElapsed = 0;
		}
	}

	private void moveTowardsPlayer(double deltaTime) {
		Player player = LevelLoader.getInstance().getPlayer();
		double deltaX = Math.signum(player.getPosX() - this.getPosX()) * speed * deltaTime;
		double deltaY = Math.signum(player.getPosY() - this.getPosY()) * speed * deltaTime;
		if (canMove(deltaX, deltaY)) {
			moveX(deltaX);
			moveY(deltaY);
		}
	}

	private void patrol(double deltaTime) {
		patrolTimeElapsed += deltaTime;
		double patrolDuration = 2;

		if (patrolTimeElapsed >= patrolDuration) {
			int direction = (int) (Math.random() * 4);
			switch (direction) {
				case 0: // Up
					patrolDirectionX = 0;
					patrolDirectionY = -1;
					break;
				case 1: // Down
					patrolDirectionX = 0;
					patrolDirectionY = 1;
					break;
				case 2: // Right
					patrolDirectionX = 1;
					patrolDirectionY = 0;
					break;
				case 3: // Left
					patrolDirectionX = -1;
					patrolDirectionY = 0;
					break;
			}
			patrolTimeElapsed = 0;
		}
		double deltaX = patrolDirectionX * speed * deltaTime;
		double deltaY = patrolDirectionY * speed * deltaTime;
		if (canMove(deltaX, deltaY)) {
			moveX(deltaX);
			moveY(deltaY);
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
			LevelLoader.getInstance().getCurrentLevel().removeGameObject(this);

			// Drop money
			double offsetX = (Math.random() - 0.5) * 0.5 * size;
			double offsetY = (Math.random() - 0.5) * 0.5 * size;
			Item moneyItem = new Item(posX + offsetX, posY + offsetY, ItemType.MONEY, money);
			LevelLoader.getInstance().getCurrentLevel().addGameObject(moneyItem);
		}
	}
}
