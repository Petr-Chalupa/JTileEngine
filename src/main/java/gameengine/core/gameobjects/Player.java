package gameengine.core.gameobjects;

import gameengine.core.Collider;
import gameengine.core.InputHandler;
import gameengine.core.LevelData;
import javafx.scene.input.KeyCode;

public class Player extends GameObject {
    private InputHandler inputHandler;
    public double speed;

    public Player(double posX, double posY, double scale, double speed) {
        super(posX, posY, 1, scale);

        this.speed = speed;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
        if (inputHandler == null) return;

        final double deltaX;
        final double deltaY;
        if (inputHandler.isKeyPressed(KeyCode.W)) deltaY = -speed * deltaTime; // Up
        else if (inputHandler.isKeyPressed(KeyCode.S)) deltaY = speed * deltaTime; // Down
        else
            deltaY = 0;
        if (inputHandler.isKeyPressed(KeyCode.A)) deltaX = -speed * deltaTime; // Left
        else if (inputHandler.isKeyPressed(KeyCode.D)) deltaX = speed * deltaTime; // Right
        else
            deltaX = 0;

        double newPosX = posX + deltaX;
        double newPosY = posY + deltaY;

        boolean isOnSolidTile = levelData.gameObjects.stream()
                .filter(gameObject -> (gameObject instanceof Tile && gameObject.movementCollider != null))
                .map(gameObject -> (Tile) gameObject).allMatch(tile -> {
                    // System.out.println(tile.movementCollider.box);
                    return movementCollider.calculateIntersection(tile.movementCollider, deltaX, deltaY) == null;
                });
        boolean isInMapX = newPosX > 0 && newPosX < levelData.cols - 1;
        boolean isInMapY = newPosY > 0 && newPosY < levelData.rows - 1;
        System.out.println(isOnSolidTile);

        if (isInMapX && isOnSolidTile) moveX(deltaX);
        if (isInMapY && isOnSolidTile) moveY(deltaY);
    }

    public void moveX(double dist) {
        posX += dist;
    }

    public void moveY(double dist) {
        posY += dist;
    }
}
