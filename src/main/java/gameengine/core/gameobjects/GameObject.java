package gameengine.core.gameobjects;

import gameengine.core.LevelData;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class GameObject {
    protected Image sprite;
    public double posX;
    public double posY;
    public double scale;

    public GameObject(double posX, double posY, double scale) {
        this.posX = posX;
        this.posY = posY;
        this.scale = scale;
        this.sprite = new Image(getClass().getResourceAsStream("/gameengine/img/default_sprite.png"));
    }

    public abstract void update(double deltaTime, LevelData levelData);

    public abstract void render(GraphicsContext context, LevelData levelData);

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
