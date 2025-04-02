package gameengine.core.gameobjects;

import gameengine.core.LevelData;

public class Tile extends GameObject {
    public Tile(double posX, double posY) {
        super(posX, posY, 0, 1);
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
    }
}
