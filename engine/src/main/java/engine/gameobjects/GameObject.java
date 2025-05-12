package engine.gameobjects;

import engine.core.Collider;
import engine.core.GameSettings;
import engine.utils.ResourceManager;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;

public abstract class GameObject {
	protected final int layer;
	protected Image sprite;
	protected boolean isRendered;
	protected double posX;
	protected double posY;
	protected double size;
	protected Collider collider;

	public GameObject(double posX, double posY, int layer, double size) {
		this.posX = posX;
		this.posY = posY;
		this.layer = layer;
		this.size = size;

		setCollider(0, 0, size, size);
		setSprite("default_sprite.png");
	}

	public Image getSprite() {
		return sprite;
	}

	/**
	 * Tries to set a sprite image from the supplied sprite name - uses default sprite on error
	 *
	 * @param sprite The name of the desired sprite
	 */
	public void setSprite(String sprite) {
		try {
			this.sprite = ResourceManager.getInstance().getImg(sprite);
		} catch (Exception e) {
			System.err.println("Invalid sprite, default is being used:\n\t" + e);
		}
	}

	public boolean isRendered() {
		return isRendered;
	}

	public void setRendered(boolean rendered) {
		this.isRendered = rendered;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public int getLayer() {
		return layer;
	}

	public Bounds getBounds() {
		return new BoundingBox(posX, posY, size, size);
	}

	/**
	 * Calculates bounds of the object in pixel coordinates
	 *
	 * @return Bounds
	 */
	public Bounds getRealBounds() {
		double tileSize = GameSettings.getInstance().getTileSize();
		return new BoundingBox(posX * tileSize, posY * tileSize, size * tileSize, size * tileSize);
	}

	public Collider getCollider() {
		return collider;
	}

	public void setCollider(double posX, double posY, double width, double height) {
		this.collider = new Collider(this, posX, posY, width, height);
	}

	public void update(double deltaTime) {
	}

}
