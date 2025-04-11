package engine.gameobjects;

import engine.core.Inventory;
import javafx.scene.canvas.GraphicsContext;
import engine.core.Inventory.InventoryType;

public class Chest extends GameObject {
    private Inventory inventory;

    public Chest(double posX, double posY, int layer, double size) {
        super(posX, posY, layer, size);
        this.inventory = new Inventory(this, InventoryType.FLOATING, "Chest", 15, 5);
    }

    @Override
    public void update(double deltaTime) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void render(GraphicsContext context, double sx, double sy, double sw, double sh, double dx, double dy,
            double dw, double dh) {
        super.render(context, sx, sy, sw, sh, dx, dy, dw, dh);
        this.inventory.render(context, dx, dy);
    }
}
