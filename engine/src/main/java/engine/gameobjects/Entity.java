package engine.gameobjects;

import engine.utils.LevelLoader;

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
        return posX + deltaX > 0 && posX + deltaX < (levelLoader.cols - 1) * levelLoader.tileSize;
    }

    protected boolean isInMapY(double deltaX, double deltaY) {
        return posY + deltaY > 0 && posY + deltaX < (levelLoader.rows - 1) * levelLoader.tileSize;
    }

    protected boolean isOnSolidTile(double deltaX, double deltaY) {
        return levelLoader.gameObjects.stream().filter(gameObject -> gameObject instanceof Tile)
                .map(gameObject -> (Tile) gameObject).allMatch(tile -> tile.isSolid
                        || movementCollider.calculateIntersection(tile.movementCollider, deltaX, deltaY) == null);
    }

    protected void moveX(double dist) {
        posX += dist;
    }

    protected void moveY(double dist) {
        posY += dist;
    }

}
