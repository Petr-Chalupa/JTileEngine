package engine.gameobjects;

public class Enemy extends Entity {

	public Enemy(double posX, double posY, double size, double speed, int health) {
		super(posX, posY, 1, size, speed, health);

		setSprite("enemy_sprite.png");
		setMovementCollider(0, 0, size, size);
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

}
