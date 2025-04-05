package engine.core;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import engine.gameobjects.Enemy;
import engine.gameobjects.GameObject;
import engine.gameobjects.Player;
import engine.gameobjects.Tile;
import engine.gameobjects.TileType;
import engine.utils.ResourceManager;

public class LevelData {
    // level
    public String name;
    public boolean completed;
    public ArrayList<GameObject> gameObjects = new ArrayList<>();
    // map
    public int rows;
    public int cols;
    public int tileSize;
    // player
    public Player player;

    public void loadFile(String path) {
        JSONObject config = ResourceManager.getInstance().getLevelConfig(path);

        // Load level data
        this.name = config.getString("name");
        this.completed = config.getBoolean("completed");
        this.gameObjects.clear();

        // Load map data
        JSONObject map = config.getJSONObject("map");
        this.rows = map.getInt("rows");
        this.cols = map.getInt("cols");
        this.tileSize = map.getInt("tileSize");

        // Read the map from a text file and create tiles
        List<String> mapLines = ResourceManager.getInstance().getLevelMap(path);
        for (int row = 0; row < rows; row++) {
            String[] tileNumbers = mapLines.get(row).split(" ");
            for (int col = 0; col < cols; col++) {
                int tileType = Integer.parseInt(tileNumbers[col]);
                Tile tile = new Tile(col * tileSize, row * tileSize, tileSize, TileType.values()[tileType]);
                gameObjects.add(tile);
            }
        }

        // Load player data
        JSONObject playerData = config.getJSONObject("player");
        JSONArray playerStart = playerData.getJSONArray("start");
        double playerScale = playerData.getDouble("scale");
        double playerSpeed = playerData.getDouble("speed") * tileSize;
        player = new Player(playerStart.getDouble(0) * tileSize, playerStart.getDouble(1) * tileSize,
                tileSize * playerScale, playerSpeed);
        gameObjects.add(player);

        // Load enemies
        JSONArray enemies = config.getJSONArray("enemies");
        for (int i = 0; i < enemies.length(); i++) {
            JSONObject enemyData = enemies.getJSONObject(i);
            JSONArray enemyStart = enemyData.getJSONArray("start");
            double enemyScale = enemyData.getDouble("scale");
            double enemySpeed = enemyData.getDouble("speed") * tileSize;
            Enemy enemy = new Enemy(enemyStart.getDouble(0) * tileSize, enemyStart.getDouble(1) * tileSize,
                    tileSize * enemyScale, enemySpeed);
            gameObjects.add(enemy);
        }
    }
}
