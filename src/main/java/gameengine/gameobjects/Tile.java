package gameengine.gameobjects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends GameObject {
    public Tile(double posX, double posY, double size, Color color, boolean solid) {
        self = new Rectangle(size, size);
        ((Rectangle) self).setViewOrder(1);
        ((Rectangle) self).setX(posX);
        ((Rectangle) self).setY(posY);
        ((Rectangle) self).setFill(color);

        this.solid = solid;
    }
}
