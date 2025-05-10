package engine.gameobjects.items;

public class Human extends Item {

	public Human(double posX, double posY) {
		super(posX, posY, ItemType.HUMAN);

		this.name = "Human";
		this.stackSize = 1;
		this.price = 7;
		this.maxUses = 1;
		this.uses = maxUses;
	}

}
