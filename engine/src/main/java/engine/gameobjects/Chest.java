package engine.gameobjects;

import engine.core.Inventory;
import engine.core.Inventory.InventoryType;
import javafx.scene.canvas.GraphicsContext;

public class Chest extends GameObject implements Interactable {
    public enum State {
        OPEN,
        CLOSED,
    }

    private final Inventory inventory;
    private State state = State.CLOSED;

    public Chest(double posX, double posY, double size) {
        super(posX, posY, 1, size);
        this.inventory = new Inventory(InventoryType.CENTER, "Chest", 15, 5);

        setSprite("chest_sprite.jpg");
        setMovementCollider(0, 0, size, size);
    }

    public boolean isOpen() {
        return this.state == State.OPEN;
    }

    public Inventory getInventory() {
        return this.inventory;
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

    public void toggle(Entity user) {
        if (isOpen()) {
            this.inventory.close();
            state = State.CLOSED;
        } else {
            this.inventory.open();
            state = State.OPEN;
        }
    }

}
