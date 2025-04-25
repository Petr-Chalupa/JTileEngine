package engine.gameobjects;

import engine.Engine;
import engine.core.InputHandler;
import engine.core.Inventory;
import engine.core.Inventory.InventoryType;
import engine.utils.LevelLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

import java.util.Comparator;

public class Player extends Entity {
    public enum State {
        NORMAL,
        INTERACTING,
    }

    private final Inventory inventory;
    private InputHandler inputHandler;
    private State state = State.NORMAL;
    private GameObject currentInteractable;

    public Player(double posX, double posY, double size, double speed, int health) {
        super(posX, posY, 2, size, speed, health);
        this.inventory = new Inventory(InventoryType.BOTTOM, null, 5, 5);
        this.inventory.open();

        setSprite("player_sprite.png");
        setInputHandler();
        setMovementCollider(0.1 * size, 0.6 * size, 0.8 * size, 0.4 * size);
        setInteractCollider(-10, -10, size + 20, size + 20);
    }

    public void setInputHandler() {
        this.inputHandler = Engine.getInstance().getInputHandler();

        this.inputHandler.addPressedCallback((event) -> {
            KeyCode code = event.getCode();

            if (code.isDigitKey()) {
                inventory.select(inputHandler.getDigit(code) - 1); // First slot mapped to 1
                return;
            } else if (code == KeyCode.I) handleObjectInteract();

            if (state == State.NORMAL) {
                if (code == KeyCode.E) handleItemUse();
            } else if (state == State.INTERACTING) {
                if (code == KeyCode.LEFT || code == KeyCode.RIGHT) {
                    int dir = code == KeyCode.LEFT ? -1 : 1;
                    if (currentInteractable instanceof Chest chest) chest.getInventory().selectMove(dir);
                }
            }
        });

        this.inputHandler.addMouseScrollCallback((event) -> {
            int dir = (int) Math.signum(event.getDeltaY());
            inventory.selectMove(dir);
        });
    }

    @Override
    public void update(double deltaTime) {
        // Handle movement
        if (state == State.NORMAL) {
            final double deltaX;
            final double deltaY;
            if (inputHandler.isKeyPressed(KeyCode.W)) deltaY = -speed * deltaTime; // Up
            else if (inputHandler.isKeyPressed(KeyCode.S)) deltaY = speed * deltaTime; // Down
            else deltaY = 0;
            if (inputHandler.isKeyPressed(KeyCode.A)) deltaX = -speed * deltaTime; // Left
            else if (inputHandler.isKeyPressed(KeyCode.D)) deltaX = speed * deltaTime; // Right
            else deltaX = 0;

            boolean canMove = canMove(deltaX, deltaY);
            boolean isInMapX = isInMapX(deltaX, deltaY);
            boolean isInMapY = isInMapY(deltaX, deltaY);
            if (isInMapX && canMove) moveX(deltaX);
            if (isInMapY && canMove) moveY(deltaY);
        }
    }

    @Override
    public void render(GraphicsContext context, double sx, double sy, double sw, double sh, double dx, double dy,
                       double dw, double dh) {
        super.render(context, sx, sy, sw, sh, dx, dy, dw, dh);
        this.inventory.render(context, dx, dy);
    }

    private void handleItemUse() {
        Item selectedItem = inventory.getSelectedItem();
        if (selectedItem != null) {
            boolean isUsableOnce = selectedItem.getType().use(this);
            if (isUsableOnce) {
                inventory.removeSelectedItem();
            }
        }
    }

    private void handleObjectInteract() {
        currentInteractable = getClosestInteractableObject();
        if (currentInteractable == null) return;

        if (currentInteractable instanceof Chest chest) {
            chest.toggle(this);
            state = chest.isOpen() ? State.INTERACTING : State.NORMAL;
        }
    }

    private GameObject getClosestInteractableObject() {
        LevelLoader levelLoader = Engine.getInstance().getLevelLoader();
        return levelLoader.getGameObjects()
                .stream()
                .filter(gameObject ->
                        gameObject != this &&
                                gameObject.isRendered() &&
                                gameObject instanceof Interactable &&
                                interactCollider.getIntersection(gameObject.movementCollider, 0, 0) != null)
                .min(Comparator.comparingDouble(this::getDistance))
                .orElse(null);
    }

}
