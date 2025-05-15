package engine.gameobjects.entities;

import engine.core.UIManager;
import engine.gameobjects.items.Money;
import engine.ui.Healthbar;
import engine.ui.UIRegion;
import engine.utils.LevelLoader;

public class Enemy extends Entity {
	private final double searchRange = 5;
	private final double attackRange = 2;
	private double attackCooldownElapsed = 0;
	private double patrolTimeElapsed = 0;
	private double patrolDirectionX = 0;
	private double patrolDirectionY = 0;

	public Enemy(double posX, double posY, double size, double speed, double health) {
		super(posX, posY, 1, size, speed, health);
		this.healthbar = new Healthbar(this, UIRegion.FLOAT, 0, 200, 20, 5, false);
		this.money = 1 + (int) (Math.random() * 5); // <1;5>

		UIManager.getInstance().addComponent(this.healthbar);

		setSprite("enemy_sprite.png");
	}

	public double getSearchRange() {
		return searchRange;
	}

	public double getAttackRange() {
		return attackRange;
	}

	@Override
	public void update(double deltaTime) {
		attackCooldownElapsed += deltaTime;
		double attackCooldown = 2;
		double damage = 1;

		Player player = LevelLoader.getInstance().getPlayer();
		if (player == null) return;
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
		move(deltaX, deltaY);
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
		move(deltaX, deltaY);
	}

	@Override
	public void damage(double damage) {
		super.damage(damage);
		if (this.health == 0) {
			// Remove from the world
			LevelLoader.getInstance().getCurrentLevel().removeGameObject(this);
			UIManager.getInstance().removeComponent(this.healthbar);

			// Drop money
			double offsetX = (Math.random() - 0.5) * 0.5 * size;
			double offsetY = (Math.random() - 0.5) * 0.5 * size;
			Money moneyItem = new Money(posX + offsetX, posY + offsetY, money);
			LevelLoader.getInstance().getCurrentLevel().addGameObject(moneyItem);
		}
	}
}
