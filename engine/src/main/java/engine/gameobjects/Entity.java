package engine.gameobjects;

import java.util.List;
import java.util.stream.Collectors;

import engine.utils.LevelLoader;
import javafx.geometry.Bounds;

public class Entity extends GameObject {
    protected double speed;
    protected boolean isMovementLocked = false;
    protected final int maxHealth;
    protected int health;
    private final LevelLoader levelLoader;

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

    protected boolean isInMapX(double deltaX, double deltaY) {
        return posX + deltaX > 0 && posX + deltaX < (levelLoader.getCols() - 1) * levelLoader.getTileSize();
    }

    protected boolean isInMapY(double deltaX, double deltaY) {
        return posY + deltaY > 0 && posY + deltaX < (levelLoader.getRows() - 1) * levelLoader.getTileSize();
    }

    protected boolean canMove(double deltaX, double deltaY) {
        return levelLoader.getGameObjects()
                .stream()
                .filter(gameObject -> !gameObject.equals(this) && gameObject.isRendered())
                .allMatch(gameObject -> {
                    Bounds intersection = movementCollider.getIntersection(gameObject.movementCollider, deltaX, deltaY);
                    if (intersection == null) return true;
                    else return gameObject instanceof Tile && ((Tile) gameObject).getType().isSolid();
                });
    }

    protected List<GameObject> getObjectsInRange() {
        return levelLoader.getGameObjects().stream().filter(gameObject -> {
            if (gameObject.equals(this) || !gameObject.isRendered()) return false;
            Bounds intersection = interactCollider.getIntersection(gameObject.movementCollider, 0, 0);
            return intersection != null;
        }).collect(Collectors.toList());
    }

    protected void moveX(double dist) {
        posX += dist;
    }

    protected void moveY(double dist) {
        posY += dist;
    }

}
