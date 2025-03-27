package gameengine.gameobjects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends GameObject {
    public Tile(double posX, double posY, double size, Color color, boolean solid) {
        self = new Rectangle();
        ((Rectangle) self).setFill(color);
        this.color = color;
        this.layer = 1;
        this.scale = 1;
        this.posX = posX;
        this.posY = posY;
        this.solid = solid;
    }

    @Override
    public Rectangle getSelf() {
        return (Rectangle) self;
    }

    @Override
    public void rescale(double width, double height) {
        getSelf().setWidth(width * scale);
        getSelf().setHeight(height * scale);
    }
}
