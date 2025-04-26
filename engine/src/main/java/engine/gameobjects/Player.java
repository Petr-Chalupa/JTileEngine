package engine.gameobjects;

import engine.Engine;
import engine.core.Healthbar;
import engine.core.Healthbar.HealthbarType;
import engine.core.InputHandler;
import engine.core.Inventory;
import engine.core.Inventory.InventoryType;
import engine.utils.LevelLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.Comparator;

public class Player extends Entity {
	public enum State {
		NORMAL,
		INTERACTING,
	}

	private final Healthbar healthbar;
	private final Inventory inventory;
	private Inventory currentFocusedInventory;
	private InputHandler inputHandler;
	private State state = State.NORMAL;
	private GameObject currentInteractable;

	public Player(double posX, double posY, double size, double speed, int health) {
		super(posX, posY, 2, size, speed, health);
		this.healthbar = new Healthbar(this, HealthbarType.LEFT_BOTTOM);
		this.inventory = new Inventory(InventoryType.BOTTOM, null, 5, 5);
		this.inventory.open();
		this.currentFocusedInventory = inventory;

		setSprite("player_sprite.png");
		setInputHandler();
		setMovementCollider(0.1 * size, 0.6 * size, 0.8 * size, 0.4 * size);
		setInteractCollider(-10, -10, size + 20, size + 20);
	}

	public void setInputHandler() {
		this.inputHandler = Engine.getInstance().getInputHandler();

		for (int i = 1; i <= 9; i++) {
			KeyCode digitKey = KeyCode.getKeyCode(String.valueOf(i));
			final int slotIndex = i - 1;
			inputHandler.bindKeyPressed(digitKey, event -> inventory.select(slotIndex));
		}
		inputHandler.bindKeyPressed(KeyCode.E, event -> {
			handleObjectInteract();
			if (state == State.NORMAL) currentFocusedInventory = inventory;
		});
		inputHandler.bindKeyPressed(KeyCode.TAB, event -> {
			if (state == State.INTERACTING && currentInteractable instanceof Chest chest) {
				if (currentFocusedInventory == inventory) currentFocusedInventory = chest.getInventory();
				else currentFocusedInventory = inventory;
			}
		});
		inputHandler.bindKeyPressed(KeyCode.SPACE, event -> {
			if (state == State.INTERACTING && currentInteractable instanceof Chest chest) {
				if (currentFocusedInventory == inventory) inventory.transferSelectedItem(chest.getInventory());
				else chest.getInventory().transferSelectedItem(inventory);
			}
		});
		inputHandler.bindMousePressed(MouseButton.PRIMARY, event -> {
			if (state == State.NORMAL) handleItemUse();
		});
		inputHandler.addMouseScrollCallback(event -> {
			int dir = (int) Math.signum(event.getDeltaY());
			currentFocusedInventory.selectMove(dir);
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

			if (canMove(deltaX, deltaY)) {
				moveX(deltaX);
				moveY(deltaY);
			}
		}
	}

	@Override
	public void renderUI(GraphicsContext context) {
		healthbar.render(context);
		inventory.render(context);
	}

	private GameObject getClosestInteractableObject() {
		LevelLoader levelLoader = Engine.getInstance().getLevelLoader();
		return levelLoader.getGameObjects()
				.stream()
				.filter(gameObject ->
						!gameObject.equals(this) &&
								gameObject.isRendered() &&
								gameObject instanceof Interactable &&
								interactCollider.getIntersection(gameObject.movementCollider, 0, 0) != null)
				.min(Comparator.comparingDouble(this::getDistance))
				.orElse(null);
	}

	private void handleObjectInteract() {
		currentInteractable = getClosestInteractableObject();
		if (currentInteractable == null) return;

		state = state == State.NORMAL ? State.INTERACTING : State.NORMAL;
		if (currentInteractable instanceof Chest chest) chest.toggle(this);
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

}
