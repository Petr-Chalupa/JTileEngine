package engine.gameobjects.entities;

import engine.core.Healthbar;
import engine.core.Healthbar.HealthbarType;
import engine.utils.LevelLoader;

public class Enemy extends Entity {
	private final Healthbar healthbar;

	public Enemy(double posX, double posY, double size, double speed, double health) {
		super(posX, posY, 1, size, speed, health);
		this.healthbar = new Healthbar(this, HealthbarType.FLOAT);

		setSprite("enemy_sprite.png");
		setCollider(0, 0, size, size);
	}

	@Override
	public void update(double deltaTime) {
		double randomSignX = Math.random() > 0.5 ? 1 : -1;
		double randomSignY = Math.random() > 0.5 ? 1 : -1;

		double deltaX = randomSignX * speed * deltaTime;
		double deltaY = randomSignY * speed * deltaTime;

		boolean canMove = canMove(deltaX, deltaY);
		if (canMove && Math.random() > 0.5) moveX(deltaX);
		if (canMove && Math.random() > 0.5) moveY(deltaY);
	}

	@Override
	public void renderUI(javafx.scene.canvas.GraphicsContext context) {
		healthbar.render(context);
	}

	@Override
	public void damage(double damage) {
		super.damage(damage);
		if (this.health == 0) LevelLoader.getInstance().getGameObjects().remove(this);
		// drop loot
	}
}
