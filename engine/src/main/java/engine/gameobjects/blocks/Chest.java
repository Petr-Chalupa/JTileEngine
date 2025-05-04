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
	private final Inventory inventory;
	private boolean isOpen = false;

	public Chest(double posX, double posY, double size) {
		super(posX, posY, size, BlockType.CHEST);
		this.inventory = new Inventory(this, InventoryType.CENTER, "Chest", 15, 5);

		generateLoot();
	}

	public boolean isOpen() {
		return this.isOpen;
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

	public void toggle(Entity user) {
		if (isOpen) close(user);
		else open(user);
	}

	public void open(Entity user) {
		if (!isOpen) {
			this.inventory.open();
			isOpen = true;
			if (user instanceof Player player) player.setState(Player.PlayerState.INTERACTING);
		}
	}

	public void close(Entity user) {
		if (isOpen) {
			this.inventory.close();
			isOpen = false;
			if (user instanceof Player player) player.setState(Player.PlayerState.NORMAL);
		}
	}

	@Override
	public void interact(Entity user) {
		toggle(user);
	}

	@Override
	public void renderUI(GraphicsContext context) {
		inventory.render(context);
	}
}