package engine.ui;

import engine.gameobjects.GameObject;
import engine.gameobjects.blocks.Shop;
import engine.gameobjects.entities.Player;
import engine.gameobjects.items.Item;
import engine.utils.LevelLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends UIComponent {
	private final String name;
	private final int size;
	private final int cols;
	private final List<List<Item>> items;
	private boolean isVisible;
	private int selected = 0;
	private final double nameSize = 20;

	public Inventory(GameObject parent, UIRegion region, int layer, String name, int size, int cols) {
		super(parent, region, layer, region == UIRegion.CENTER_CENTER ? 0.5 : 0.9, region == UIRegion.CENTER_CENTER ? 0.5 : 0.9, 5);
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

	public void dropSelectedItem() {
		if (items.get(selected).isEmpty()) return;
		Item item = items.get(selected).removeFirst();
		item.setPosX(parent.getPosX());
		item.setPosY(parent.getPosY());
		LevelLoader.getInstance().getCurrentLevel().addGameObject(item);
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
			Item firstItem = slot.getFirst();
			if (firstItem.getType() == item.getType() && slot.size() < firstItem.getStackSize()) {
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
				Item firstItem = slot.getFirst();
				if (firstItem.getType() == item.getType() && slot.size() < firstItem.getStackSize()) {
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

	public double calculateSlotSize(double availableWidth, double availableHeight) {
		int rows = Math.ceilDiv(size, cols);
		double usableHeight = availableHeight - (name != null ? nameSize : 0);
		return Math.min((availableWidth - padding * (cols + 1)) / cols, (usableHeight - padding * (rows + 1)) / rows);
	}

	@Override
	public Bounds calculateBounds(Bounds regionBounds) {
		double regionWidth = regionBounds.getWidth();
		double regionHeight = regionBounds.getHeight();

		double availableWidth = calculateAvailableWidth(regionWidth);
		double availableHeight = calculateAvailableHeight(regionHeight);
		double slotSize = calculateSlotSize(availableWidth, availableHeight);

		int rows = Math.ceilDiv(size, cols);
		double actualWidth = cols * slotSize + (cols + 1) * padding;
		double actualHeight = rows * slotSize + (rows + 1) * padding + (name != null ? nameSize : 0);

		return centerBounds(regionBounds, new BoundingBox(0, 0, actualWidth, actualHeight));
	}

	@Override
	public void render(GraphicsContext context, double width, double height) {
		if (!isVisible) return;

		// Render background
		context.setFill(new Color(0, 0, 0, 0.75));
		context.fillRect(0, 0, width, height);

		// Render name (centered)
		if (name != null) {
			context.save();
			context.setFill(Color.WHITE);
			context.setTextAlign(TextAlignment.CENTER);
			context.setTextBaseline(VPos.CENTER);
			context.fillText(name, width / 2, nameSize / 2);
			context.restore();
		}

		double slotSize = calculateSlotSize(width, height);

		// Render slots with items, mark the selected slot
		for (int i = 0; i < size; i++) {
			double slotX = padding + (i % cols) * (slotSize + padding);
			double slotY = (name != null ? nameSize : 0) + padding + (i / cols) * (slotSize + padding);
			if (i == selected) {
				context.setStroke(Color.WHITE);
				context.setLineWidth(2);
			} else {
				context.setStroke(Color.GRAY);
				context.setLineWidth(1);
			}
			context.strokeRect(slotX, slotY, slotSize, slotSize);
			if (!items.get(i).isEmpty()) renderItem(context, items.get(i), slotSize, slotX, slotY);
		}
	}

	private void renderItem(GraphicsContext context, List<Item> slot, double slotSize, double slotX, double slotY) {
		// Render item
		context.drawImage(slot.getFirst().getSprite(), slotX, slotY, slotSize, slotSize);

		// Render item count if multiple
		if (slot.size() > 1) {
			context.save();
			context.setFill(Color.WHITE);
			context.setTextAlign(TextAlignment.RIGHT);
			context.setFont(new Font(20));
			context.fillText("" + slot.size(), slotX + slotSize - 5, slotY + slotSize - 5);
			context.restore();
		}

		if (parent instanceof Player) {
			// Render item usages if multiple
			int maxUses = slot.getFirst().getMaxUses();
			if (maxUses > 1 && slot.getFirst().getUses() < maxUses) {
				double barHeight = slotSize - 4;
				double barWidth = 4;
				double percentage = slot.getFirst().getUses() / (double) maxUses;
				// Render background
				context.setFill(Color.DARKGRAY);
				context.fillRect(slotX + 2, slotY + 2, barWidth, barHeight);
				// Render uses
				if (percentage > 0.6) context.setFill(Color.GREEN);
				else if (percentage > 0.3) context.setFill(Color.ORANGE);
				else context.setFill(Color.RED);
				context.fillRect(slotX + 2, slotY + 2 + (barHeight - barHeight * percentage), barWidth, barHeight * percentage);
			}
		}

		if (parent instanceof Shop) {
			// Render item price
			context.save();
			context.setFill(new Color(0, 0, 0, 0.6));
			context.fillRect(slotX, slotY, slotSize, slotSize);
			double fontSize = slotSize / 4;
			context.setFont(Font.font(null, javafx.scene.text.FontWeight.BOLD, fontSize));
			context.setFill(Color.YELLOW);
			context.setTextAlign(TextAlignment.CENTER);
			context.setTextBaseline(VPos.CENTER);
			context.fillText(slot.getFirst().getPrice() + "$", slotX + slotSize / 2, slotY + slotSize / 2);
			context.restore();
		}
	}

}