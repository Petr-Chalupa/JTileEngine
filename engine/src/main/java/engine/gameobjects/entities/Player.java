package engine.gameobjects.entities;

import engine.Engine;
import engine.core.Healthbar;
import engine.core.Healthbar.HealthbarType;
import engine.core.InputHandler;
import engine.core.Inventory;
import engine.core.Inventory.InventoryType;
import engine.gameobjects.Interactable;
import engine.gameobjects.blocks.Chest;
import engine.gameobjects.blocks.Shop;
import engine.gameobjects.items.Item;
import engine.gameobjects.items.ItemType;
import engine.utils.LevelLoader;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.Comparator;

public class Player extends Entity {
	public enum PlayerState {
		NORMAL, INTERACTING
	}

	private Inventory currentFocusedInventory;
	private InputHandler inputHandler;
	private PlayerState state = PlayerState.NORMAL;
	private Interactable currentInteractable;

	public Player(double posX, double posY, double size, double speed, double health) {
		super(posX, posY, 2, size, speed, health);
		this.maxInteractDist = 100;
		this.healthbar = new Healthbar(this, HealthbarType.LEFT_BOTTOM);
		this.inventory = new Inventory(this, InventoryType.BOTTOM, null, 5, 5);
		this.inventory.open();
		this.currentFocusedInventory = inventory;

		setSprite("player_sprite.png");
		setInputHandler();
		setCollider(0.1 * size, 0.6 * size, 0.8 * size, 0.4 * size);

		inventory.addItem(new Item(posX, posY, ItemType.SWORD_STRONG)); // Test only!
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
			if (state == PlayerState.NORMAL) currentFocusedInventory = inventory;
		});
		inputHandler.bindKeyPressed(KeyCode.Q, event -> {
			if (state == PlayerState.NORMAL) inventory.dropSelectedItem();
		});
		inputHandler.bindKeyPressed(KeyCode.TAB, event -> {
			if (state != PlayerState.INTERACTING) return;
			if (currentFocusedInventory == inventory && currentInteractable instanceof Chest chest) {
				currentFocusedInventory = chest.getInventory();
			} else if (currentFocusedInventory == inventory && currentInteractable instanceof Shop shop) {
				currentFocusedInventory = shop.getInventory();
			} else currentFocusedInventory = inventory;
		});
		inputHandler.bindKeyPressed(KeyCode.SPACE, event -> {
			if (state != PlayerState.INTERACTING) return;
			if (currentInteractable instanceof Chest chest) {
				if (currentFocusedInventory == inventory) inventory.transferSelectedItem(chest.getInventory());
				else chest.getInventory().transferSelectedItem(inventory);
			} else if (currentInteractable instanceof Shop shop) {
				shop.buyItem(this);
			}
		});
		inputHandler.bindMousePressed(MouseButton.PRIMARY, event -> {
			if (state == PlayerState.NORMAL) handleItemUse();
		});
		inputHandler.addMouseScrollCallback(event -> {
			int dir = (int) Math.signum(event.getDeltaY());
			currentFocusedInventory.selectMove(dir);
		});
	}

	public void setState(PlayerState state) {
		this.state = state;
	}

	@Override
	public void update(double deltaTime) {
		// Handle movement
		if (state == PlayerState.NORMAL) {
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

		// Render armor
		double dx = 50;
		double dy = (int) context.getCanvas().getHeight() - 75;
		context.save();
		context.setFill(Color.BLACK);
		context.setTextAlign(TextAlignment.CENTER);
		context.setTextBaseline(VPos.CENTER);
		context.setFont(new Font(20));
		context.fillText(armor + " ■", dx, dy);
		context.restore(); // Reset

		// Render money
		dx = 50;
		dy = (int) context.getCanvas().getHeight() - 100;
		context.save();
		context.setFill(Color.BLACK);
		context.setTextAlign(TextAlignment.CENTER);
		context.setTextBaseline(VPos.CENTER);
		context.setFont(new Font(20));
		context.fillText(money + "$", dx, dy);
		context.restore(); // Reset
	}

	@Override
	public void damage(double damage) {
		super.damage(damage);
		if (health == 0) Engine.getInstance().gameOver();
	}

	private Interactable getClosestInteractableObject() {
		LevelLoader levelLoader = Engine.getInstance().getLevelLoader();
		return (Interactable) levelLoader.getGameObjects()
				.stream()
				.filter(gameObject -> !gameObject.equals(this) && gameObject.isRendered()
						&& gameObject instanceof Interactable
						&& this.collider.getDistanceTo(gameObject.getCollider()) <= maxInteractDist)
				.min(Comparator.comparingDouble(gameObject -> this.collider.getDistanceTo(gameObject.getCollider())))
				.orElse(null);
	}

	private void handleObjectInteract() {
		currentInteractable = getClosestInteractableObject();
		if (currentInteractable != null) currentInteractable.interact(this);
	}

	private void handleItemUse() {
		Item selectedItem = inventory.getSelectedItem();
		if (selectedItem != null) {
			boolean hasAnotherUse = selectedItem.use(this);
			if (!hasAnotherUse) inventory.removeSelectedItem();
		}
	}

}
