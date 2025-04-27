package engine.gameobjects.blocks;

import engine.core.Inventory;
import engine.core.Inventory.InventoryType;
import engine.gameobjects.GameObject;
import javafx.scene.canvas.GraphicsContext;

public class Shop extends GameObject {
	public enum State {
		OPEN,
		CLOSED,
	}

	private final Inventory inventory;
	private final State state = State.CLOSED;

	public Shop(double posX, double posY, double size) {
		super(posX, posY, 1, size);
		this.inventory = new Inventory(InventoryType.CENTER, "Shop", 10, 5);
	}

	@Override
	public void renderUI(GraphicsContext context) {
		inventory.render(context);
	}

}
