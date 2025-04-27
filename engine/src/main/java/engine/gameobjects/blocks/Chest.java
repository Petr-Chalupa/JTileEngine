package engine.gameobjects.blocks;

import engine.core.Inventory;
import engine.core.Inventory.InventoryType;
import engine.gameobjects.GameObject;
import engine.gameobjects.entities.Entity;
import engine.gameobjects.items.Item;
import engine.gameobjects.items.ItemType;
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
		setCollider(0, 0, size, size);
		generateLoot();
	}

	public boolean isOpen() {
		return this.state == State.OPEN;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public void generateLoot() {
		int slots = inventory.getSize();
		ItemType[] itemTypes = ItemType.values();
		for (int i = 0; i < slots; i++) {
			if (Math.random() < 0.3) {
				ItemType itemType = itemTypes[(int) (Math.random() * itemTypes.length)];
				inventory.addItem(new Item(posX, posY, 1, 1, itemType), i);
			}
		}
	}

	@Override
	public void interact(Entity user) {
		if (isOpen()) {
			this.inventory.close();
			state = State.CLOSED;
		} else {
			this.inventory.open();
			state = State.OPEN;
		}
	}

	@Override
	public void renderUI(GraphicsContext context) {
		inventory.render(context);
	}

}
