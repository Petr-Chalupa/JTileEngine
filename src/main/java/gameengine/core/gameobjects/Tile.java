package gameengine.core.gameobjects;

import gameengine.core.LevelData;
import javafx.scene.canvas.GraphicsContext;

public class Tile extends GameObject {
    public boolean solid;

    public Tile(double posX, double posY, boolean solid) {
        super(posX, posY, 1);

        this.solid = solid;
    }

    @Override
    public void update(double deltaTime, LevelData levelData) {
    }

    @Override
    public void render(GraphicsContext context, LevelData levelData) {
        double size = levelData.tileSize * scale;
        context.drawImage(sprite, posX * levelData.tileSize, posY * levelData.tileSize, size, size);
    }
}
