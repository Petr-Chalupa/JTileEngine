package engine.core;

import engine.gameobjects.Item;
import engine.utils.LevelLoader;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    public enum InventoryType {
        CENTER, BOTTOM
    }

    private final InventoryType type;
    private final String name;
    private final int size;
    private final int cols;
    private final List<List<Item>> items;
    private boolean isVisible;
    private int selected = 0;
    private final int gap = 5;
    private final int nameSize = 20;

    public Inventory(InventoryType type, String name, int size, int cols) {
        this.type = type;
        this.name = name;
        this.size = size;
        this.cols = cols;

        this.items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.items.add(new ArrayList<>());
        }
    }

    public int getSize() {
        return size;
    }

    public Item getSelectedItem() {
        return items.get(selected).getFirst();
    }

    public int getSelected() {
        return selected;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void removeSelectedItem() {
        items.get(selected).removeFirst();
    }

    public void toggle() {
        isVisible = !isVisible;
    }

    public void select(int index) {
        this.selected = Math.max(0, Math.min(size - 1, index));
    }

    public void addItem(Item item) {
        List<Item> emptySlot = items.stream().filter(slot -> slot.isEmpty()).findFirst().orElse(null);
        List<Item> sameItemSlot = items.stream().filter(slot -> {
            if (slot.isEmpty()) return false;
            if (slot.getFirst().getType() != item.getType()) return false;
            return slot.size() != item.getType().getStackSize();
        }).findFirst().orElse(null);

        List<Item> slot = sameItemSlot != null ? sameItemSlot : emptySlot;
        if (slot != null) slot.add(item);
        else throw new RuntimeException("Inventory is full, can't add item"); // todo
    }

    public void render(GraphicsContext context, double dx, double dy) {
        if (!isVisible) return;

        int rows = Math.ceilDiv(size, cols);
        double slotSize = LevelLoader.getInstance().getTileSize();
        double width = cols * (slotSize + gap) + gap;
        double height = rows * (slotSize + gap) + gap + (name != null ? nameSize : 0);

        if (type == InventoryType.CENTER) {
            dx = (context.getCanvas().getWidth() - width) / 2;
            dy = (context.getCanvas().getHeight() - height) / 2;
        } else if (type == InventoryType.BOTTOM) {
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

        // Render slots with items, mark selected slot
        for (int i = 0; i < size; i++) {
            double slotX = dx + gap + (i % cols) * (slotSize + gap);
            double slotY = dy + gap + (i / cols) * (slotSize + gap);
            if (i == selected) {
                context.setStroke(Color.WHITE);
                context.setLineWidth(2);
            } else {
                context.setStroke(Color.GRAY);
                context.setLineWidth(1);
            }
            context.strokeRect(slotX, slotY, slotSize, slotSize);
            if (!items.get(i).isEmpty()) {
                items.get(i).getFirst().render(context, 0, 0, slotSize, slotSize, slotX, slotY, slotSize, slotSize);
                if (items.get(i).size() > 1) {
                    context.setFill(Color.WHITE);
                    context.setTextAlign(TextAlignment.RIGHT);
                    context.setFont(new Font(20));
                    context.fillText("" + items.get(i).size(), slotX + slotSize - gap, slotY + slotSize - gap);
                    context.setTextAlign(TextAlignment.LEFT); // Reset
                }
            }
        }
    }

}