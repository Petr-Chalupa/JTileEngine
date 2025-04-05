package engine.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

// import engine.App;
import engine.gameobjects.Enemy;
import engine.gameobjects.GameObject;
import engine.gameobjects.Player;
import engine.gameobjects.Tile;
import engine.gameobjects.TileType;

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
        JSONObject config = null;
        try {
            String configFile = Files
                    .readString(Path.of(getClass().getResource("/engine/" + path + "config.json").toURI()));
            config = new JSONObject(configFile);
        } catch (IOException | URISyntaxException e) {
            System.err.println(e);
            return;
        }

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
        List<String> mapLines = null;
        try {
            mapLines = Files.readAllLines(Path.of(getClass().getResource("/engine/" + path + "map.txt").toURI()));
            for (int row = 0; row < rows; row++) {
                String[] tileNumbers = mapLines.get(row).split(" ");
                for (int col = 0; col < cols; col++) {
                    int tileType = Integer.parseInt(tileNumbers[col]);

                    Tile tile = new Tile(col * tileSize, row * tileSize, tileSize, TileType.values()[tileType]);

                    gameObjects.add(tile);
                }
            }
        } catch (IOException | URISyntaxException e) {
            System.err.println(e);
            return;
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

    // public void saveFile() {}
}
