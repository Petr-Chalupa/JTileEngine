package engine.core;

import engine.gameobjects.GameObject;
import engine.gameobjects.Item;
import engine.utils.LevelLoader;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Inventory {
    public enum InventoryType {
        FLOATING, FIXED
    };

    public GameObject parent;
    public InventoryType type;
    public String name;
    public int size;
    private int cols;
    public Item[] items;
    public boolean isVisible;
    public int selected = -1;
    private int gap = 5;
    private int nameSize = 20;

    public Inventory(GameObject parent, InventoryType type, String name, int size, int cols) {
        this.parent = parent;
        this.type = type;
        this.name = name;
        this.size = size;
        this.cols = cols;
        this.items = new Item[size];
    }

    public void toggle() {
        isVisible = !isVisible;
    }

    public void render(GraphicsContext context, double dx, double dy) {
        if (!isVisible) return;

        int rows = (int) Math.ceilDiv(size, cols);
        double slotSize = LevelLoader.getInstance().tileSize;
        double width = cols * (slotSize + gap) + gap;
        double height = rows * (slotSize + gap) + gap + (name != null ? nameSize : 0);

        if (type == InventoryType.FIXED) {
            dx = (context.getCanvas().getWidth() - width) / 2;
            dy = context.getCanvas().getHeight() - height - gap;
        }

        // Render background
        context.setFill(new Color(0, 0, 0, 0.75));
        context.fillRect(dx, dy, width, height);

        // Render name (centered)
        if (name != null) {
            context.setFill(Color.WHITE);
            context.setTextAlign(TextAlignment.CENTER);
            context.setTextBaseline(VPos.CENTER);
            context.fillText(name, dx + width / 2, dy + nameSize / 2);
            context.setTextAlign(TextAlignment.LEFT); // Reset
            context.setTextBaseline(VPos.BASELINE); // Reset
            dy += nameSize;
        }

        // Draw slots with items
        for (int i = 0; i < size; i++) {
            double slotX = dx + gap + (i % cols) * (slotSize + gap);
            double slotY = dy + gap + (i / cols) * (slotSize + gap);
            context.setStroke(Color.GRAY);
            context.strokeRect(slotX, slotY, slotSize, slotSize);
            if (items[i] != null) {
                items[i].render(context, 0, 0, slotSize, slotSize, slotX, slotY, slotSize, slotSize);
            }
        }
    }

}