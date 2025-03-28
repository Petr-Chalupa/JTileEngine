package gameengine.core.gameobjects;

public class Player extends GameObject {
    public double speed;

    public Player(double posX, double posY, double scale, double speed) {
        super(posX, posY, 0, scale);

        this.speed = speed;
    }
}
