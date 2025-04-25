package engine.gameobjects;

import engine.core.Inventory;
import engine.core.Inventory.InventoryType;
import javafx.scene.canvas.GraphicsContext;

public class Shop extends GameObject {
    private final Inventory inventory;

    public Shop(double posX, double posY, double size) {
        super(posX, posY, 1, size);
        this.inventory = new Inventory(InventoryType.CENTER, "Shop", 10, 5);
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void render(GraphicsContext context, double sx, double sy, double sw, double sh, double dx, double dy,
                       double dw, double dh) {
        super.render(context, sx, sy, sw, sh, dx, dy, dw, dh);
        this.inventory.render(context, dx, dy);
    }
}
