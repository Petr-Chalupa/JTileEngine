package gameengine.core.gameobjects;

import gameengine.core.InputHandler;
import gameengine.core.LevelData;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

public class Player extends GameObject {
    private InputHandler inputHandler;
    public double speed;

    public Player(double posX, double posY, double scale, double speed) {
        super(posX, posY, scale);

        this.speed = speed;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
        if (inputHandler != null) {
            if (inputHandler.isKeyPressed(KeyCode.W)) moveY(-speed * deltaTime); // Up
            if (inputHandler.isKeyPressed(KeyCode.A)) moveX(-speed * deltaTime); // Left
            if (inputHandler.isKeyPressed(KeyCode.S)) moveY(speed * deltaTime); // Down
            if (inputHandler.isKeyPressed(KeyCode.D)) moveX(speed * deltaTime); // Right
        }
    }

    @Override
    public void render(GraphicsContext context, LevelData levelData) {
        double size = levelData.tileSize * scale;
        context.drawImage(sprite, posX * levelData.tileSize, posY * levelData.tileSize, size, size);
    }
}
