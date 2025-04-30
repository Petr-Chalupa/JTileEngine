package engine.gameobjects.blocks;

import engine.core.Inventory;
import engine.core.Inventory.InventoryType;
import engine.gameobjects.GameObject;
import engine.gameobjects.entities.Entity;
import engine.gameobjects.entities.Player;
import engine.gameobjects.items.Item;
import engine.gameobjects.items.ItemType;
import javafx.scene.canvas.GraphicsContext;

public class Shop extends GameObject implements Interactable {
	public enum ShopState {
		OPEN, CLOSED
	}

	private final Inventory inventory;
	private ShopState state = ShopState.CLOSED;

	public Shop(double posX, double posY, double size) {
		super(posX, posY, 1, size);
		this.inventory = new Inventory(this, InventoryType.CENTER, "Shop", 9, 3);

		setSprite("shop_sprite.jpg");
		setCollider(0, 0, size, size);
		generateLoot();
	}

	public boolean isOpen() {
		return this.state == ShopState.OPEN;
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

	public void buyItem(Player buyer) {
		int itemPrice = inventory.getSelectedItem().getType().getPrice();
		if (buyer.getMoney() >= itemPrice) {
			inventory.transferSelectedItem(buyer.getInventory());
			buyer.setMoney(buyer.getMoney() - itemPrice);
		}
	}

	@Override
	public void interact(Entity user) {
		if (isOpen()) {
			this.inventory.close();
			state = ShopState.CLOSED;
		} else {
			this.inventory.open();
			state = ShopState.OPEN;
		}
	}

	@Override
	public void renderUI(GraphicsContext context) {
		inventory.render(context);
	}

}
