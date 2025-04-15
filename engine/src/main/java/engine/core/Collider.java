package engine.core;

import engine.gameobjects.GameObject;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public class Collider {
    private GameObject parent;
    private Bounds box;

    public Collider(GameObject parent, double posX, double posY, double width, double height) {
        this.parent = parent;
        this.box = new BoundingBox(posX, posY, width, height);
    }

    public double getMinX() {
        return box.getMinX() + parent.getPosX();
    }

    public double getMinY() {
        return box.getMinY() + parent.getPosY();
    }

    public double getMaxX() {
        return box.getMaxX() + parent.getPosX();
    }

    public double getMaxY() {
        return box.getMaxY() + parent.getPosY();
    }

    public Bounds getIntersection(Collider c, double deltaX, double deltaY) {
        double minX = Math.max(getMinX() + deltaX, c.getMinX());
        double minY = Math.max(getMinY() + deltaY, c.getMinY());
        double maxX = Math.min(getMaxX() + deltaX, c.getMaxX());
        double maxY = Math.min(getMaxY() + deltaY, c.getMaxY());
        return (minX < maxX && minY < maxY) ? new BoundingBox(minX, minY, maxX - minX, maxY - minY) : null;
    }
}
