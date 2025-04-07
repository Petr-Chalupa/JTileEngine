package engine.gameobjects;

import engine.core.Inventory;
import engine.core.LevelData;
import javafx.scene.canvas.GraphicsContext;
import engine.core.Inventory.InventoryType;

public class Chest extends GameObject {
    private Inventory inventory;

    public Chest(double posX, double posY, int layer, double size) {
        super(posX, posY, layer, size);
        this.inventory = new Inventory(this, InventoryType.FLOATING, "Chest", 15, 5);
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void render(GraphicsContext context, LevelData levelData, double sx, double sy, double sw, double sh,
            double dx, double dy) {
        super.render(context, levelData, sx, sy, sw, sh, dx, dy);
        this.inventory.render(context, levelData, dx, dy);
    }
}
