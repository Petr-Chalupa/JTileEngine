package engine.gameobjects;

import engine.core.Inventory;
import engine.core.LevelData;

public class Shop extends GameObject {
    private Inventory inventory;

    public Shop(double posX, double posY, int layer, double size) {
        super(posX, posY, layer, size);
        this.inventory = new Inventory(this, 10);
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

}
