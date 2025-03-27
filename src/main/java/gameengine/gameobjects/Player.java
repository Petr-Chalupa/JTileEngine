package gameengine.gameobjects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player extends GameObject {
    public Player(double posX, double posY, double size) {
        self = new Rectangle(size, size);
        ((Rectangle) self).setViewOrder(0);
        ((Rectangle) self).setX(posX);
        ((Rectangle) self).setY(posY);
        ((Rectangle) self).setFill(Color.RED);

        this.solid = true;
    }
}
