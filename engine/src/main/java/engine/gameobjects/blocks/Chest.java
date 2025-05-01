package engine.gameobjects.blocks;

import engine.core.Inventory;
import engine.core.Inventory.InventoryType;
import engine.gameobjects.Interactable;
import engine.gameobjects.entities.Entity;
import engine.gameobjects.entities.Player;
import engine.gameobjects.items.Item;
import engine.gameobjects.items.ItemType;
import javafx.scene.canvas.GraphicsContext;

public class Chest extends Block implements Interactable {
	public enum ChestState {
		OPEN, CLOSED
	}
	
	private final Inventory inventory;
	private ChestState state = ChestState.CLOSED;

	public Chest(double posX, double posY, double size) {
		super(posX, posY, size, BlockType.CHEST);
		this.inventory = new Inventory(this, InventoryType.CENTER, "Chest", 15, 5);

		generateLoot();
	}

	public boolean isOpen() {
		return this.state == ChestState.OPEN;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public void generateLoot() {
		ItemType[] itemTypes = ItemType.values();
		for (int i = 0; i < inventory.getSize(); i++) {
			if (Math.random() < 0.3) {
				ItemType itemType = itemTypes[(int) (Math.random() * itemTypes.length)];
				inventory.addItem(new Item(posX, posY, itemType), i);
			}
		}
	}

	@Override
	public void interact(Entity user) {
		if (isOpen()) {
			this.inventory.close();
			state = ChestState.CLOSED;
			if (user instanceof Player player) player.setState(Player.PlayerState.NORMAL);
		} else {
			this.inventory.open();
			state = ChestState.OPEN;
			if (user instanceof Player player) player.setState(Player.PlayerState.INTERACTING);
		}
	}

	@Override
	public void renderUI(GraphicsContext context) {
		inventory.render(context);
	}

}
