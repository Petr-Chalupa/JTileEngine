package engine.gameobjects.blocks;

import engine.Engine;
import engine.gameobjects.Interactable;
import engine.gameobjects.entities.Entity;
import engine.gameobjects.entities.Player;
import engine.gameobjects.entities.Player.PlayerState;
import engine.gameobjects.items.*;
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
			Item item = null;
			switch (itemType) {
				case MEAT:
				case GRANULE:
				case TREAT:
					item = new Food(posX, posY, itemType == ItemType.MEAT ? Food.FoodType.MEAT : itemType == ItemType.GRANULE ? Food.FoodType.GRANULE : Food.FoodType.TREAT);
					break;
				case ARMOR:
					item = new Armor(posX, posY);
					break;
				case HUMAN:
					item = new Human(posX, posY);
					break;
				default:
					break;
			}
			if (item != null) inventory.addItem(item, i);
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