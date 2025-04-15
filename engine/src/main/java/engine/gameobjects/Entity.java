package engine.gameobjects;

import engine.utils.LevelLoader;
import javafx.geometry.Bounds;

public class Entity extends GameObject {
    protected double speed;
    private LevelLoader levelLoader;

    public Entity(double posX, double posY, int layer, double size, double speed) {
        super(posX, posY, layer, size);
        this.speed = speed;
        this.levelLoader = LevelLoader.getInstance();
    }

    @Override
    public void update(double deltaTime) {
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
                    else if (gameObject instanceof Tile && ((Tile) gameObject).getType().isSolid()) {
                        return true;
                    } else return false;
                });
    }

    protected void moveX(double dist) {
        posX += dist;
    }

    protected void moveY(double dist) {
        posY += dist;
    }

}
