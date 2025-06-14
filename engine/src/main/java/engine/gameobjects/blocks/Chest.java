package engine.gameobjects.blocks;

import engine.Engine;
import engine.gameobjects.Interactable;
import engine.gameobjects.entities.Entity;
import engine.gameobjects.entities.Player;
import engine.ui.Inventory;
import engine.ui.UIRegion;

public class Chest extends Block implements Interactable {
	private final Inventory inventory;
	private boolean isOpen = false;

	public Chest(double posX, double posY, double size) {
		super(posX, posY, size, BlockType.CHEST);
		this.inventory = new Inventory(this, UIRegion.CENTER_CENTER, 1, "Chest", 15, 5);

		Engine.getInstance().getUIManager().addComponent(this.inventory);
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	public Inventory getInventory() {
		return this.inventory;
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

}