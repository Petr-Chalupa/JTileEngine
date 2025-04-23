package engine.gameobjects;

import engine.Engine;
import engine.core.Inventory;
import engine.core.Inventory.InventoryType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class Chest extends GameObject {
    private Inventory inventory;

    public Chest(double posX, double posY, double size) {
        super(posX, posY, 1, size);
        this.inventory = new Inventory(InventoryType.CENTER, "Chest", 15, 5);

        setSprite("chest_sprite.jpg");
        setMovementCollider(0, 0, size, size);

        Engine.getInstance().getInputHandler().addMousePressedCallback((event) -> handleMousePress(event));
    }

    public boolean isOpen() {
        return this.inventory.isVisible();
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
        this.inventory.toggle();
    }

    private void handleMousePress(MouseEvent event) {
        if (!isOpen() || event.getButton() != MouseButton.PRIMARY) return;

        // select the clicked slot
    }

}
