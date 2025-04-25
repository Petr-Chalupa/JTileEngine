package engine.gameobjects;

import engine.core.Collider;
import engine.utils.ResourceManager;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class GameObject {
    protected Image sprite;
    protected boolean isRendered;
    protected double posX;
    protected double posY;
    protected int layer;
    protected double size;
    protected Collider movementCollider;
    protected Collider interactCollider;

    public GameObject(double posX, double posY, int layer, double size) {
        this.posX = posX;
        this.posY = posY;
        this.layer = layer;
        this.size = size;
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

    public double getPosY() {
        return posY;
    }

    public double getDistance(GameObject gameObject) {
        double dx = posX - gameObject.posX;
        double dy = posY - gameObject.posY;
        return dx * dx + dy * dy;
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

    public void setMovementCollider(double posX, double posY, double width, double height) {
        this.movementCollider = new Collider(this, posX, posY, width, height);
    }

    public void setInteractCollider(double posX, double posY, double width, double height) {
        this.interactCollider = new Collider(this, posX, posY, width, height);
    }

    public void render(GraphicsContext context, double sx, double sy, double sw, double sh, double dx, double dy,
                       double dw, double dh) {
        context.drawImage(sprite, sx, sy, sw, sh, dx, dy, dw, dh);
    }

    public abstract void update(double deltaTime);

    // public abstract void interact(GameObject initiator);
}
