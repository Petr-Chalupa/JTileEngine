package engine.gameobjects;

import engine.core.LevelData;

public class Chest extends GameObject {

    public Chest(double posX, double posY, int layer, double size) {
        super(posX, posY, layer, size);
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

}
