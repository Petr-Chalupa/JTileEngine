package gameengine.gameobjects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player extends GameObject {
    public double speed;

    public Player(double posX, double posY, double size, double speed) {
        self = new Rectangle();
        this.color = Color.RED;
        this.layer = 0;
        this.scale = 0.5;
        this.posX = posX;
        this.posY = posY;
        this.speed = speed;
        this.solid = true;
    }
}
