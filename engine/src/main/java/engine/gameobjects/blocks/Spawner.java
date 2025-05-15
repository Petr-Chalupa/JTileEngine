package engine.gameobjects.blocks;

import engine.gameobjects.entities.Enemy;
import engine.gameobjects.entities.Player;
import engine.gameobjects.items.Key;
import engine.gameobjects.tiles.Tile;
import engine.utils.LevelLoader;

import java.util.List;
import java.util.Random;

public class Spawner extends Block {
	private double spawnCooldownElapsed = 0;
	private final double activeRange = 5;
	private final double spawnRange = 3;

	public Spawner(double posX, double posY, double size) {
		super(posX, posY, size, BlockType.SPAWNER);
	}

	public double getActiveRange() {
		return activeRange;
	}

	public double getSpawnRange() {
		return spawnRange;
	}

	@Override
	public void update(double deltaTime) {
		spawnCooldownElapsed += deltaTime;
		double spawnCooldown = 15;

		Player player = LevelLoader.getInstance().getPlayer();
		if (player == null) return;
		double distance = player.getCollider().getDistanceTo(this.getCollider());

		if (distance < activeRange && spawnCooldownElapsed >= spawnCooldown) {
			spawnCooldownElapsed = 0;

			Random random = new Random();
			List<Tile> tiles = LevelLoader.getInstance()
					.getCurrentLevel()
					.getTiles()
					.stream()
					.filter(tile -> tile.isRendered()
							&& tile.getType().isWalkable()
							&& tile.getCollider().getDistanceTo(this.collider) > 1.5 * size
							&& tile.getCollider().getDistanceTo(this.collider) < spawnRange)
					.toList();
			Tile spawnTile = tiles.get(random.nextInt(tiles.size()));
			double randomSize = 0.5 + random.nextDouble() * 0.5;
			double randomSpeed = 0.3 + random.nextDouble() * 0.7;
			double randomHealth = 30 + random.nextDouble(71);
			Enemy enemy = new Enemy(spawnTile.getPosX(), spawnTile.getPosY(), randomSize, randomSpeed, randomHealth);

			LevelLoader.getInstance().getCurrentLevel().addGameObject(enemy);
		}
	}

	public void destroy() {
		List<Spawner> spawners = LevelLoader.getInstance()
				.getCurrentLevel()
				.getBlocks()
				.stream()
				.filter(block -> block instanceof Spawner).map(block -> (Spawner) block)
				.filter(spawner -> spawner != this)
				.toList();

		if (spawners.isEmpty()) {
			Key key = new Key(posX, posY);
			LevelLoader.getInstance().getCurrentLevel().removeGameObject(this);
			LevelLoader.getInstance().getCurrentLevel().addGameObject(key);
		}
	}
}
