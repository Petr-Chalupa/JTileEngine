package engine.gameobjects.blocks;

import engine.Engine;
import engine.gameobjects.Interactable;
import engine.gameobjects.entities.Entity;
import engine.gameobjects.entities.Player;
import engine.gameobjects.entities.Player.PlayerState;
import engine.gameobjects.items.*;
import engine.ui.Inventory;
import engine.ui.UIRegion;

public class Shop extends Block implements Interactable {
	private final Inventory inventory;
	private boolean isOpen = false;
	private double refreshCooldownElapsed = 0;

	public Shop(double posX, double posY, double size) {
		super(posX, posY, size, BlockType.SHOP);
		this.inventory = new Inventory(this, UIRegion.CENTER_CENTER, 1, "Shop", 9, 3);

		Engine.getInstance().getUIManager().addComponent(this.inventory);

		generateLoot();
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	private void generateLoot() {
		inventory.clear();

		ItemType[] itemTypes = ItemType.values();
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemType itemType = itemTypes[(int) (Math.random() * itemTypes.length)];
			Item item = switch (itemType) {
				case MEAT, GRANULE, TREAT ->
						new Food(posX, posY, itemType == ItemType.MEAT ? Food.FoodType.MEAT : itemType == ItemType.GRANULE ? Food.FoodType.GRANULE : Food.FoodType.TREAT);
				case ARMOR -> new Armor(posX, posY);
				case HUMAN -> new Human(posX, posY);
				default -> null;
			};
			if (item != null) inventory.addItem(item, i);
			else i--; // Repeat for this slot
		}
	}

	@Override
	public void update(double deltaTime) {
		refreshCooldownElapsed += deltaTime;
		double refreshCooldown = 30;

		if (!isOpen && refreshCooldownElapsed >= refreshCooldown) {
			refreshCooldownElapsed = 0;
			generateLoot();
		}
	}

	/**
	 * Tries to buy the currently selected item
	 *
	 * @param buyer Player that performs the trade
	 */
	public void buyItem(Player buyer) {
		if (inventory.getSelectedItem() == null) return;
		int itemPrice = inventory.getSelectedItem().getPrice();
		if (buyer.getMoney() >= itemPrice) {
			inventory.transferSelectedItem(buyer.getInventory());
			buyer.setMoney(buyer.getMoney() - itemPrice);
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
			if (user instanceof Player player) player.setState(PlayerState.INTERACTING);
		}
	}

	public void close(Entity user) {
		if (isOpen) {
			this.inventory.close();
			isOpen = false;
			if (user instanceof Player player) player.setState(PlayerState.NORMAL);
		}
	}

	@Override
	public void interact(Entity user) {
		toggle(user);
	}

}