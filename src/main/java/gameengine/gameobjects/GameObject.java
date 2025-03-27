package gameengine.gameobjects;

import javafx.scene.Node;
import javafx.scene.paint.Color;

public class GameObject {
    protected Node self;
    public boolean rendered = false;
    public boolean solid;
    public Color color;
    public int layer;
    public double scale;
    public double posX;
    public double posY;

    public Node getSelf() {
        return self;
    }

    public void moveX(double dist) {
        posX += dist;
    }

    public void moveY(double dist) {
        posY += dist;
    }
}
