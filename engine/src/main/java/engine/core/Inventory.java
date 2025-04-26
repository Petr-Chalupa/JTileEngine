package engine.core;

import engine.gameobjects.Item;
import engine.gameobjects.ItemType;
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
		List<Item> slot = items.get(selected);
		return slot.isEmpty() ? null : items.get(selected).getFirst();
	}

	public int getSelected() {
		return selected;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void removeSelectedItem() {
		if (items.get(selected).isEmpty()) return;
		items.get(selected).removeFirst();
	}

	public void open() {
		isVisible = true;
	}

	public void close() {
		isVisible = false;
	}

	public void select(int index) {
		this.selected = Math.max(0, Math.min(size - 1, index));
	}

	public void selectMove(int delta) {
		this.selected = Math.max(0, Math.min(size - 1, selected + delta));
	}

	public boolean addItem(Item item) {
		return addItem(item, -1); // First free slot
	}

	public boolean addItem(Item item, int index) {
		// Try to add to a specific index
		if (index >= 0 && index < items.size()) {
			List<Item> slot = items.get(index);
			if (slot.isEmpty()) {
				slot.add(item);
				return true;
			}
			ItemType firstItemType = slot.getFirst().getType();
			if (firstItemType == item.getType() && slot.size() < firstItemType.getStackSize()) {
				slot.add(item);
				return true;
			}
		}
		// Try to find a place to add
		for (List<Item> slot : items) {
			if (slot.isEmpty()) {
				slot.add(item);
				return true;
			} else {
				ItemType firstItemType = slot.getFirst().getType();
				if (firstItemType == item.getType() && slot.size() < firstItemType.getStackSize()) {
					slot.add(item);
					return true;
				}
			}
		}
		// No place to add found
		return false;
	}

	public void transferSelectedItem(Inventory target) {
		Item srcItem = getSelectedItem();
		if (srcItem != null) {
			boolean targetAddSuccess = target.addItem(srcItem, target.getSelected());
			if (targetAddSuccess) removeSelectedItem();
		}
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