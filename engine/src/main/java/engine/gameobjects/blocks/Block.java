package engine.gameobjects.blocks;

import engine.gameobjects.GameObject;

public class Block extends GameObject {
	private final BlockType type;

	public Block(double posX, double posY, double size, BlockType type) {
		super(posX, posY, 1, size);
		this.type = type;

		setSprite(type.getSpritePath());
	}

	public BlockType getType() {
		return type;
	}

	public boolean isSolid() {
		return type.isSolid();
	}
}
