package gameengine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import gameengine.gameobjects.GameObject;
import gameengine.gameobjects.Player;
import gameengine.gameobjects.Tile;
import javafx.scene.paint.Color;

public class LevelData {
    public int tileRows = 12;
    public int tileCols = 16;
    public ArrayList<GameObject> gameObjects = new ArrayList<>();
    public Player player;

    public void parseFile(String path, int tileSize) {
        JSONObject json = null;

        try {
            String content = Files.readString(Path.of(App.class.getResource(path).toURI()));
            json = new JSONObject(content);
        } catch (IOException | URISyntaxException e) {
            System.err.println(e);
        }

        // read rows and cols from file //todo

        JSONArray jsonTiles = json.getJSONObject("map").getJSONArray("tiles");
        for (int i = 0; i < tileRows * tileCols; i++) {
            Color color = jsonTiles.getInt(i) == 0 ? Color.GREEN : Color.BLUE;
            gameObjects.add(new Tile((i % tileCols), (i / tileCols), tileSize, color, true));
        }

        JSONObject jsonPlayer = json.getJSONObject("player");
        player = new Player(jsonPlayer.getJSONArray("start").getDouble(0),
                jsonPlayer.getJSONArray("start").getDouble(1), tileSize, jsonPlayer.getDouble("speed"));
        gameObjects.add(player);
    }

    // public void serialize() {}
}
