package gameengine.core;

import gameengine.core.gameobjects.GameObject;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public class Collider {
    public GameObject parent;
    public Bounds box;

    public Collider(GameObject parent, double posX, double posY, double width, double height) {
        this.parent = parent;
        this.box = new BoundingBox(posX, posY, width, height);
    }

    public double getMinX() {
        return box.getMinX() + parent.posX;
    }

    public double getMinY() {
        return box.getMinY() + parent.posY;
    }

    public double getMaxX() {
        return box.getMaxX() + parent.posX;
    }

    public double getMaxY() {
        return box.getMaxY() + parent.posY;
    }

    public Bounds calculateIntersection(Collider c, double deltaX, double deltaY) {
        double minX = Math.max(getMinX() + deltaX, c.getMinX());
        double minY = Math.max(getMinY() + deltaY, c.getMinY());
        double maxX = Math.min(getMaxX() + deltaX, c.getMaxX());
        double maxY = Math.min(getMaxY() + deltaY, c.getMaxY());
        return (minX < maxX && minY < maxY) ? new BoundingBox(minX, minY, maxX - minX, maxY - minY) : null;
    }
}
