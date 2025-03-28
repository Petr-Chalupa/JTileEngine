package gameengine.core.gameobjects;

import javafx.scene.image.Image;

public abstract class GameObject {
    protected Image sprite;
    public boolean rendered = false;
    public double posX;
    public double posY;
    public double posZ;
    public double width;
    public double height;
    public double scale;

    public GameObject(double posX, double posY, double posZ, double scale) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.scale = scale;
        this.sprite = new Image(getClass().getResourceAsStream("/gameengine/img/default_sprite.png"));
    }

    public Image getSprite() {
        return sprite;
    }

    public void setSprite(String spritePath) {
        try {
            this.sprite = new Image(getClass().getResourceAsStream(spritePath));
        } catch (NullPointerException e) {
            System.out.println("Invalid sprite path");
        }
    }

    public void moveX(double dist) {
        posX += dist;
    }

    public void moveY(double dist) {
        posY += dist;
    }
}
