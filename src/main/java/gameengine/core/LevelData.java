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
        this.rows = map.getInt("rows");
        this.cols = map.getInt("cols");
        for (int i = 0; i < rows * cols; i++) {
            JSONObject tileData = mapTileTypes.getJSONObject(Integer.toString(mapTiles.getInt(i)));
            boolean tileSolid = tileData.getBoolean("solid");
            String tileSprite = tileData.getString("sprite");
            Tile tile = new Tile(i % cols, i / cols, tileSolid);
            tile.setSprite(tileSprite);
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
        gameObjects.add(this.player);
    }

    // public void saveFile() {}
}
