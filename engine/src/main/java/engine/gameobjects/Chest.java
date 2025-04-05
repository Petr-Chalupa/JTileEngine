package engine.gameobjects;

import engine.core.Inventory;
import engine.core.LevelData;

public class Chest extends GameObject {
    private Inventory inventory;

    public Chest(double posX, double posY, int layer, double size) {
        super(posX, posY, layer, size);
        this.inventory = new Inventory(this, 5);
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

}
