package engine.gameobjects.blocks;

import engine.Engine;
import engine.gameobjects.Interactable;
import engine.gameobjects.entities.Entity;
import engine.gameobjects.entities.Player;
import engine.gameobjects.entities.Player.PlayerState;
import engine.gameobjects.items.Item;
import engine.gameobjects.items.ItemType;
import engine.ui.Inventory;
import engine.ui.UIRegion;

import java.util.Arrays;

public class Shop extends Block implements Interactable {
	private final Inventory inventory;
	private boolean isOpen = false;

	public Shop(double posX, double posY, double size) {
		super(posX, posY, size, BlockType.SHOP);
		this.inventory = new Inventory(this, UIRegion.CENTER_CENTER, 0, "Shop", 9, 3);

		Engine.getInstance().getUIManager().addComponent(this.inventory);

		generateLoot();
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public void generateLoot() {
		ItemType[] itemTypes = Arrays.stream(ItemType.values()).filter(type -> type != ItemType.MONEY).toArray(ItemType[]::new);
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemType itemType = itemTypes[(int) (Math.random() * itemTypes.length)];
			inventory.addItem(new Item(posX, posY, itemType), i);
		}
	}

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