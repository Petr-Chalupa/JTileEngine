package engine.gameobjects;

import engine.core.Collider;
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
	protected double maxInteractDist;

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

	public int getLayer() {
		return layer;
	}

	public double getSize() {
		return size;
	}

	public Bounds getBounds() {
		return new BoundingBox(posX, posY, size, size);
	}

	public Collider getCollider() {
		return collider;
	}

	public double getMaxInteractDist() {
		return maxInteractDist;
	}

	public void setCollider(double posX, double posY, double width, double height) {
		this.collider = new Collider(this, posX, posY, width, height);
	}

	public void update(double deltaTime) {
	}

}
