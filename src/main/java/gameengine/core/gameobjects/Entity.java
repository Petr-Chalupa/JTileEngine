package gameengine.core.gameobjects;

import gameengine.core.LevelData;

public class Entity extends GameObject {
    protected double speed;

    public Entity(double posX, double posY, int layer, double size, double speed) {
        super(posX, posY, layer, size);
        this.speed = speed;
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
    }

    protected boolean isInMapX(LevelData levelData, double deltaX, double deltaY) {
        return posX + deltaX > 0 && posX + deltaX < (levelData.cols - 1) * levelData.tileSize;
    }

    protected boolean isInMapY(LevelData levelData, double deltaX, double deltaY) {
        return posY + deltaY > 0 && posY + deltaX < (levelData.rows - 1) * levelData.tileSize;
    }

    protected boolean isOnSolidTile(LevelData levelData, double deltaX, double deltaY) {
        return levelData.gameObjects.stream().filter(gameObject -> gameObject instanceof Tile)
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
