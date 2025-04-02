package gameengine.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gameengine.App;
import gameengine.core.gameobjects.Enemy;
import gameengine.core.gameobjects.GameObject;
import gameengine.core.gameobjects.Player;
import gameengine.core.gameobjects.Tile;

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
            String configFile = Files.readString(Path.of(App.class.getResource(path + "config.json").toURI()));
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
        JSONObject mapTileTypes = map.getJSONObject("tileTypes");

        // Read the map from a text file and create tiles
        List<String> mapLines = null;
        try {
            mapLines = Files.readAllLines(Path.of(App.class.getResource(path + "map.txt").toURI()));
            for (int row = 0; row < rows; row++) {
                String[] tileNumbers = mapLines.get(row).split(" ");
                for (int col = 0; col < cols; col++) {
                    int tileType = Integer.parseInt(tileNumbers[col]);
                    JSONObject tileData = mapTileTypes.getJSONObject(Integer.toString(tileType));
                    String tileSprite = tileData.getString("sprite");

                    Tile tile = new Tile(col * tileSize, row * tileSize);
                    tile.setSprite(tileSprite);
                    Collider tileMovementCollider = parseMovementCollider(tileData, tile);
                    if (tileMovementCollider != null) tile.setMovementCollider(tileMovementCollider);

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
        String playerSprite = playerData.getString("sprite");

        player = new Player(playerStart.getDouble(0) * tileSize, playerStart.getDouble(1) * tileSize, playerScale,
                playerSpeed);
        player.setSprite(playerSprite);
        Collider playerMovementCollider = parseMovementCollider(playerData, player);
        if (playerMovementCollider != null) player.setMovementCollider(playerMovementCollider);

        gameObjects.add(player);

        // Load enemies
        JSONArray enemies = config.getJSONArray("enemies");
        for (int i = 0; i < enemies.length(); i++) {
            JSONObject enemyData = enemies.getJSONObject(i);
            JSONArray enemyStart = enemyData.getJSONArray("start");
            double enemyScale = enemyData.getDouble("scale");
            double enemySpeed = enemyData.getDouble("speed") * tileSize;
            String enemySprite = enemyData.getString("sprite");

            Enemy enemy = new Enemy(enemyStart.getDouble(0) * tileSize, enemyStart.getDouble(1) * tileSize, enemyScale,
                    enemySpeed);
            enemy.setSprite(enemySprite);
            Collider enemyMovementCollider = parseMovementCollider(enemyData, enemy);
            if (enemyMovementCollider != null) enemy.setMovementCollider(enemyMovementCollider);

            gameObjects.add(enemy);
        }
    }

    private Collider parseMovementCollider(JSONObject json, GameObject parent) {
        if (!json.has("movementCollider")) return null;

        JSONObject colliderData = json.getJSONObject("movementCollider");
        double parentSize = tileSize * parent.scale;
        double x = colliderData.getDouble("x") * parentSize;
        double y = colliderData.getDouble("y") * parentSize;
        double width = colliderData.getDouble("width") * parentSize;
        double height = colliderData.getDouble("height") * parentSize;
        return new Collider(parent, x, y, width, height);
    }

    // public void saveFile() {}
}
