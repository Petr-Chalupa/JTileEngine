package gameengine.core.gameobjects;

import gameengine.core.InputHandler;
import gameengine.core.LevelData;
import javafx.scene.input.KeyCode;

public class Player extends GameObject {
    private InputHandler inputHandler;
    public double speed;

    public Player(double posX, double posY, int layer, double scale, double speed) {
        super(posX, posY, layer, scale);

        this.speed = speed;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
        if (inputHandler != null) {
            double deltaX = 0;
            double deltaY = 0;
            if (inputHandler.isKeyPressed(KeyCode.W)) deltaY = -speed * deltaTime; // Up
            if (inputHandler.isKeyPressed(KeyCode.A)) deltaX = -speed * deltaTime; // Left
            if (inputHandler.isKeyPressed(KeyCode.S)) deltaY = speed * deltaTime; // Down
            if (inputHandler.isKeyPressed(KeyCode.D)) deltaX = speed * deltaTime; // Right

            if (posX + deltaX > 0 && posX + deltaX < levelData.cols - 1) moveX(deltaX);
            if (posY + deltaY > 0 && posY + deltaY < levelData.rows - 1) moveY(deltaY);
        }

        // Tile tile = (Tile) levelData.gameObjects.stream()
        // .filter(gameObject -> (gameObject.layer + 1 == this.layer)
        // && isPointInsideTile(posX, posY, gameObject.posX, gameObject.posY,
        // levelData.tileSize))
        // .findFirst().orElse(null);
        // if (tile != null) System.out.println(tile.solid);
    }

    // public boolean isPointInsideTile(double pointX, double pointY, double tileX,
    // double tileY, double tileSize) {
    // double tileLeft = tileX * tileSize;
    // double tileRight = tileLeft + tileSize;
    // double tileTop = tileY * tileSize;
    // double tileBottom = tileTop + tileSize;

    // return pointX * tileSize >= tileLeft && pointX * tileSize < tileRight &&
    // pointY * tileSize >= tileTop
    // && pointY * tileSize < tileBottom;
    // }
}
