package engine.gameobjects;

import engine.core.InputHandler;
import engine.core.Inventory;
import engine.core.LevelData;
import javafx.scene.input.KeyCode;

public class Player extends Entity {
    private InputHandler inputHandler;
    private Inventory inventory;

    public Player(double posX, double posY, double size, double speed) {
        super(posX, posY, 2, size, speed);
        this.inventory = new Inventory(this, 10);

        setSprite("player_sprite.png");

        setMovementCollider(0.1 * size, 0.6 * size, 0.8 * size, 0.4 * size);
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
        if (inputHandler == null) return;

        // Handle movement
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

        // Handle inventory
    }

}
