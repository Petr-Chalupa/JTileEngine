package engine.core;

import engine.gameobjects.GameObject;
import engine.gameobjects.Item;

public class Inventory {
    public GameObject parent;
    public int size;
    public Item[] items;

    public Inventory(GameObject parent, int size) {
        this.parent = parent;
        this.size = size;
        this.items = new Item[size];
    }
}