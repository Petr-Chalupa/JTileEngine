package gameengine.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import gameengine.App;
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
        JSONObject json = null;
        try {
            String content = Files.readString(Path.of(App.class.getResource(path).toURI()));
            json = new JSONObject(content);
        } catch (IOException | URISyntaxException e) {
            System.err.println(e);
        }

        // Load level data
        JSONObject level = json.getJSONObject("level");
        this.name = level.getString("name");
        this.completed = level.getBoolean("completed");
        this.gameObjects.clear();

        // Load map data
        JSONObject map = json.getJSONObject("map");
        JSONArray mapTiles = map.getJSONArray("tiles");
        JSONObject mapTileTypes = map.getJSONObject("tileTypes");
        this.rows = map.getJSONArray("size").getInt(0);
        this.cols = map.getJSONArray("size").getInt(1);
        this.tileSize = map.getInt("tileSize");
        for (int i = 0; i < rows * cols; i++) {
            JSONObject tileData = mapTileTypes.getJSONObject(Integer.toString(mapTiles.getInt(i)));
            String tileSprite = tileData.getString("sprite");
            Tile tile = new Tile(i % cols, i / cols);
            tile.setSprite(tileSprite);
            if (tileData.has("movementCollider")) {
                JSONObject tileColliderData = tileData.getJSONObject("movementCollider");
                Collider tileCollider = new Collider(tile, tileColliderData.getDouble("x"),
                        tileColliderData.getDouble("y"), tileColliderData.getDouble("width"),
                        tileColliderData.getDouble("height"));
                tile.setMovementCollider(tileCollider);
            }
            gameObjects.add(tile);
        }

        // Load player data
        JSONObject player = json.getJSONObject("player");
        JSONArray playerStart = player.getJSONArray("start");
        double playerScale = player.getDouble("scale");
        double playerSpeed = player.getDouble("speed");
        String playerSprite = player.getString("sprite");
        this.player = new Player(playerStart.getDouble(0), playerStart.getDouble(1), playerScale, playerSpeed);
        this.player.setSprite(playerSprite);
        if (player.has("movementCollider")) {
            JSONObject playerColliderData = player.getJSONObject("movementCollider");
            Collider tileCollider = new Collider(this.player, playerColliderData.getDouble("x"),
                    playerColliderData.getDouble("y"), playerColliderData.getDouble("width"),
                    playerColliderData.getDouble("height"));
            this.player.setMovementCollider(tileCollider);
        }
        gameObjects.add(this.player);
    }

    // public void saveFile() {}
}
