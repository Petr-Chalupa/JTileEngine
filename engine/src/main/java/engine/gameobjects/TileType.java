package engine.gameobjects;

public enum TileType {
    GRASS("/engine/img/tile_sprite0.png", true), WATER("/engine/img/tile_sprite1.png", false);

    private final String spritePath;
    private final boolean isSolid;

    TileType(String spritePath, boolean isSolid) {
        this.spritePath = spritePath;
        this.isSolid = isSolid;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public boolean isSolid() {
        return isSolid;
    }
}
