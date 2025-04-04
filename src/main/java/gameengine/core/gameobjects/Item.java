package gameengine.core.gameobjects;

import gameengine.core.LevelData;

public class Item extends GameObject {
    // interface for objects that can be in inventory and be used by player or
    // placed on ground

    public Item(double posX, double posY, int layer, double size) {
        super(posX, posY, 1, size);
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
}
