package gameengine.gameobjects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends GameObject {
    public Tile(double posX, double posY, double size, Color color, boolean solid) {
        self = new Rectangle();
        this.color = color;
        this.layer = 1;
        this.scale = 1;
        this.posX = posX;
        this.posY = posY;
        this.solid = solid;
    }
}
