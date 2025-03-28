package gameengine.core.gameobjects;

public class Tile extends GameObject {
    public boolean solid;

    public Tile(double posX, double posY, boolean solid) {
        super(posX, posY, 1, 1);

        this.solid = solid;
    }
}
