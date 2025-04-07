package engine.gameobjects;

import engine.core.Collider;
import engine.core.LevelData;
import engine.utils.ResourceManager;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class GameObject {
    protected Image sprite;
    public double posX;
    public double posY;
    public int layer;
    public double size;
    public Collider movementCollider;
    public Collider interactCollider;

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

    public Bounds getBounds() {
        return new BoundingBox(posX, posY, size, size);
    }

    public void setMovementCollider(double posX, double posY, double width, double height) {
        this.movementCollider = new Collider(this, posX, posY, width, height);
    }

    public void setInteractCollider(double posX, double posY, double width, double height) {
        this.movementCollider = new Collider(this, posX, posY, width, height);
    }

    public void render(GraphicsContext context, LevelData levelData, double sx, double sy, double sw, double sh,
            double dx, double dy) {
        context.drawImage(sprite, sx, sy, sw, sh, dx, dy, size, size);
    }

    public abstract void update(double deltaTime, LevelData levelData);

    // public abstract void interact(GameObject initiator);
}
