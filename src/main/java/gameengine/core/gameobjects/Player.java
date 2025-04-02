package gameengine.core.gameobjects;

import gameengine.core.InputHandler;
import gameengine.core.LevelData;
import javafx.scene.input.KeyCode;

public class Player extends Entity {
    private InputHandler inputHandler;

    public Player(double posX, double posY, double scale, double speed) {
        super(posX, posY, 2, scale, speed);
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
        if (inputHandler == null) return;

        final double deltaX;
        final double deltaY;
        if (inputHandler.isKeyPressed(KeyCode.W))
            deltaY = -speed * deltaTime; // Up
        else if (inputHandler.isKeyPressed(KeyCode.S))
            deltaY = speed * deltaTime; // Down
        else
            deltaY = 0;
        if (inputHandler.isKeyPressed(KeyCode.A))
            deltaX = -speed * deltaTime; // Left
        else if (inputHandler.isKeyPressed(KeyCode.D))
            deltaX = speed * deltaTime; // Right
        else
            deltaX = 0;

        boolean isOnSolidTile = isOnSolidTile(levelData, deltaX, deltaY);
        boolean isInMapX = isInMapX(levelData, deltaX, deltaY);
        boolean isInMapY = isInMapY(levelData, deltaX, deltaY);
        if (isInMapX && isOnSolidTile) moveX(deltaX);
        if (isInMapY && isOnSolidTile) moveY(deltaY);
    }

}
