package engine.gameobjects;

public class Tile extends GameObject {
    public boolean isSolid;

    public Tile(double posX, double posY, double size, TileType type) {
        super(posX, posY, 0, size);
        this.isSolid = type.isSolid();

        setSprite(type.getSpritePath());

        setMovementCollider(0, 0, size, size);
    }

    @Override
    public void update(double deltaTime) {
    }
}
