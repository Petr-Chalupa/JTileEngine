package gameengine.core.gameobjects;

import gameengine.core.LevelData;

public class Tile extends GameObject {
    public boolean solid;

    public Tile(double posX, double posY, int layer, boolean solid) {
        super(posX, posY, layer, 1);

        this.solid = solid;
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
    }
}
