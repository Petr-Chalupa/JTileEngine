package gameengine.gameobjects;

import javafx.scene.Node;
import javafx.scene.paint.Color;

public abstract class GameObject {
    protected Node self;
    public boolean rendered = false;
    public boolean solid;
    public Color color;
    public int layer;
    public double scale;
    public double posX;
    public double posY;

    public abstract Node getSelf();

    public abstract void rescale(double width, double height);

    public void moveX(double dist) {
        posX += dist;
    }

    public void moveY(double dist) {
        posY += dist;
    }
}
