package gameengine.core.gameobjects;

import gameengine.core.Collider;
import gameengine.core.LevelData;
import javafx.scene.image.Image;

public abstract class GameObject {
    protected Image sprite;
    public double posX;
    public double posY;
    public int layer;
    public double scale;
    public Collider movementCollider;
    public Collider interactCollider;

    public GameObject(double posX, double posY, int layer, double scale) {
        this.posX = posX;
        this.posY = posY;
        this.layer = layer;
        this.scale = scale;
        setSprite("/gameengine/img/default_sprite.png");
    }

    public Image getSprite() {
        return sprite;
    }

    public void setSprite(String spritePath) {
        try {
            this.sprite = new Image(getClass().getResourceAsStream(spritePath));
        } catch (NullPointerException e) {
            System.out.println("Invalid sprite path");
        }
    }

    public void setMovementCollider(Collider collider) {
        this.movementCollider = collider;
    }

    public void setInteractCollider(Collider collider) {
        this.interactCollider = collider;
    }

    public abstract void update(double deltaTime, LevelData levelData);

    // public static Bounds calculateIntersection(Bounds a, Bounds b) {
    // double minX = Math.max(a.getMinX(), b.getMinX());
    // double minY = Math.max(a.getMinY(), b.getMinY());
    // double maxX = Math.min(a.getMaxX(), b.getMaxX());
    // double maxY = Math.min(a.getMaxY(), b.getMaxY());
    // return (minX < maxX && minY < maxY) ? new BoundingBox(minX, minY, maxX -
    // minX, maxY - minY) : null;
    // }

}
