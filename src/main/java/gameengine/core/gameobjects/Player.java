package gameengine.core.gameobjects;

import gameengine.App;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player extends GameObject {
    // public ImageView sprite;
    public double speed;

    public Player(double posX, double posY, double scale, double speed) {
        super(posX, posY, 0, scale);

        // Image playerImage = new
        // Image(App.class.getResourceAsStream("/gameengine/img/player_sprite.png"));
        // self = new ImageView(playerImage);

        // this.layer = 0;
        // this.scale = scale;
        // this.posX = posX;
        // this.posY = posY;
        this.speed = speed;
        // this.solid = true;
    }

    // @Override
    // public ImageView getSelf() {
    // return (ImageView) self;
    // }

    // @Override
    // public void rescale(double width, double height) {
    // getSelf().setFitWidth(width * scale);
    // getSelf().setFitHeight(height * scale);
    // }
}
