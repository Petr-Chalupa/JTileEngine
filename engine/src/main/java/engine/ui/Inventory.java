package engine.ui;

import engine.Engine;
import engine.core.InputHandler;
import engine.gameobjects.GameObject;
import engine.gameobjects.blocks.Shop;
import engine.gameobjects.entities.Player;
import engine.gameobjects.items.Item;
import engine.utils.LevelLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.*;

public class Inventory extends UIComponent {
	private final String name;
	private final int size;
	private final int cols;
	private final List<InventorySlot> slots;
	private final Map<Integer, Bounds> slotBounds = new HashMap<>();
	private Bounds realBounds;
	private boolean isVisible;
	private int selected = 0;
	private int hoveredSlot = -1;
	private final double nameSize = 20;
	private final double tooltipSize = 10;

	public static class InventorySlot {
		private final List<Item> items = new ArrayList<>();

		public List<Item> getItems() {
			return Collections.unmodifiableList(items);
		}

		public boolean isEmpty() {
			return items.isEmpty();
		}

		public Item getFirst() {
			return isEmpty() ? null : items.getFirst();
		}

		public int getCount() {
			return items.size();
		}

		public boolean canAdd(Item item) {
			if (isEmpty()) return true;
			Item firstItem = getFirst();
			return firstItem.getType() == item.getType() && getCount() < firstItem.getStackSize();
		}

		public boolean add(Item item) {
			if (!canAdd(item)) return false;
			items.add(item);
			return true;
		}

		public Item remove() {
			if (isEmpty()) return null;
			return items.removeFirst();
		}

		public void clear() {
			items.clear();
		}
	}

	public Inventory(GameObject parent, UIRegion region, int layer, String name, int size, int cols) {
		super(parent, region, layer, region == UIRegion.CENTER_CENTER ? 0.5 : 0.9, region == UIRegion.CENTER_CENTER ? 0.5 : 0.9, 5);
		this.name = name;
		this.size = size;
		this.cols = cols;

		this.slots = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			this.slots.add(new InventorySlot());
		}
	}

	public int getSize() {
		return size;
	}

	public InventorySlot getSlot(int index) {
		return slots.get(index);
	}

	public Item getSelectedItem() {
		return slots.get(selected).getFirst();
	}

	public int getSelected() {
		return selected;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void removeSelectedItem() {
		slots.get(selected).remove();
	}

	public void dropSelectedItem() {
		Item item = slots.get(selected).remove();
		if (item == null) return;

		item.setPosX(parent.getPosX());
		item.setPosY(parent.getPosY());
		LevelLoader.getInstance().getCurrentLevel().addGameObject(item);
	}

	public void open() {
		isVisible = true;
	}

	public void close() {
		isVisible = false;
		hoveredSlot = -1;
	}

	public void select(int index) {
		this.selected = Math.max(0, Math.min(size - 1, index));
	}

	public void selectMove(int delta) {
		this.selected = Math.max(0, Math.min(size - 1, selected + delta));
	}

	public void setItems(List<InventorySlot> slots) {
		int maxSlotCount = Math.min(this.slots.size(), slots.size());
		for (int i = 0; i < maxSlotCount; i++) {
			this.slots.set(i, slots.get(i));
		}
	}

	public void clear() {
		for (InventorySlot slot : slots) {
			slot.clear();
		}
	}

	/**
	 * Adds an item to the first free slot
	 *
	 * @param item The item to add
	 * @return Boolean - if the addition was successful
	 */
	public boolean addItem(Item item) {
		return addItem(item, -1);
	}

	/**
	 * Adds an item to a specific slot or finds the best slot
	 *
	 * @param item  The item to add
	 * @param index The preferred slot index, or -1 for automatic placement
	 * @return Boolean - if the addition was successful
	 */
	public boolean addItem(Item item, int index) {
		// Try to add to a specific index
		if (index >= 0 && index < slots.size() && slots.get(index).add(item)) {
			return true;
		}
		// Find existing stack of the same type
		for (InventorySlot slot : slots) {
			if (!slot.isEmpty() && slot.getFirst().getType() == item.getType() && slot.add(item)) {
				return true;
			}
		}
		// Find any empty slot
		for (InventorySlot slot : slots) {
			if (slot.isEmpty() && slot.add(item)) {
				return true;
			}
		}
		// No place to add to found
		return false;
	}

	/**
	 * Transfers the selected item to another inventory
	 *
	 * @param target The target inventory to transfer to
	 */
	public void transferSelectedItem(Inventory target) {
		Item srcItem = getSelectedItem();
		if (srcItem != null) {
			boolean targetAddSuccess = target.addItem(srcItem, target.getSelected());
			if (targetAddSuccess) removeSelectedItem();
		}
	}

	private void updateHover() {
		hoveredSlot = -1;
		if (!isVisible) return;

		InputHandler inputHandler = Engine.getInstance().getInputHandler();
		if (inputHandler == null) return;
		double mouseX = inputHandler.getMouseX() - realBounds.getMinX();
		double mouseY = inputHandler.getMouseY() - realBounds.getMinY();

		for (Map.Entry<Integer, Bounds> entry : slotBounds.entrySet()) {
			if (entry.getValue().contains(mouseX, mouseY)) {
				hoveredSlot = entry.getKey();
				slots.get(hoveredSlot).isEmpty();
				return;
			}
		}
	}

	private double calculateSlotSize(double availableWidth, double availableHeight) {
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

		// Calculate real bounds of the inventory
		Canvas canvas = context.getCanvas();
		realBounds = calculateBounds(getRegion().getBounds(canvas.getWidth(), canvas.getHeight()));
		updateHover();

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

		// Render slots
		double slotSize = calculateSlotSize(width, height);
		slotBounds.clear();
		for (int i = 0; i < size; i++) {
			double slotX = padding + (i % cols) * (slotSize + padding);
			double slotY = (name != null ? nameSize : 0) + padding + (i / cols) * (slotSize + padding);
			slotBounds.put(i, new BoundingBox(slotX, slotY, slotSize, slotSize));
			renderSlot(context, i, slots.get(i), slotX, slotY, slotSize);
		}

		// Render tooltip for hovered slot
		if (hoveredSlot >= 0 && hoveredSlot < size && !slots.get(hoveredSlot).isEmpty()) {
			renderTooltip(context, slots.get(hoveredSlot), slotBounds.get(hoveredSlot));
		}
	}

	private void renderSlot(GraphicsContext context, int index, InventorySlot slot, double slotX, double slotY, double slotSize) {
		// Render hover effect
		if (index == hoveredSlot && !slot.isEmpty()) {
			context.setFill(new Color(0.5, 0.5, 1.0, 0.3));
			context.fillRect(slotX, slotY, slotSize, slotSize);
		}

		// Render slot border, mark selected slot
		if (index == selected) {
			context.setStroke(Color.WHITE);
			context.setLineWidth(2);
		} else {
			context.setStroke(Color.GRAY);
			context.setLineWidth(1);
		}
		context.strokeRect(slotX, slotY, slotSize, slotSize);

		// Render item if exists
		if (!slot.isEmpty()) renderItem(context, slot, slotSize, slotX, slotY);
	}

	private void renderItem(GraphicsContext context, InventorySlot slot, double slotSize, double slotX, double slotY) {
		Item item = slot.getFirst();

		// Render item sprite
		context.drawImage(item.getSprite(), slotX, slotY, slotSize, slotSize);

		// Render item count if multiple
		if (slot.getCount() > 1) {
			context.save();
			context.setFill(Color.WHITE);
			context.setTextAlign(TextAlignment.RIGHT);
			context.setFont(new Font(20));
			context.fillText("" + slot.getCount(), slotX + slotSize - 5, slotY + slotSize - 5);
			context.restore();
		}

		// Render item usages or price if applicable
		if (parent instanceof Player) {
			int maxUses = item.getMaxUses();
			if (maxUses > 1 && item.getUses() < maxUses) {
				renderUsageBar(context, item, slotX, slotY, slotSize);
			}
		} else if (parent instanceof Shop) {
			renderPrice(context, item, slotX, slotY, slotSize);
		}
	}

	private void renderUsageBar(GraphicsContext context, Item item, double slotX, double slotY, double slotSize) {
		double barHeight = slotSize - 4;
		double barWidth = 4;
		double percentage = item.getUses() / (double) item.getMaxUses();

		// Render background
		context.save();
		context.setFill(Color.DARKGRAY);
		context.fillRect(slotX + 2, slotY + 2, barWidth, barHeight);

		// Render uses
		Color usageColor;
		if (percentage > 0.6) usageColor = Color.GREEN;
		else if (percentage > 0.3) usageColor = Color.ORANGE;
		else usageColor = Color.RED;

		context.setFill(usageColor);
		context.fillRect(slotX + 2, slotY + 2 + (barHeight - barHeight * percentage), barWidth, barHeight * percentage);
		context.restore();
	}

	private void renderPrice(GraphicsContext context, Item item, double slotX, double slotY, double slotSize) {
		// Render background
		context.save();
		context.setFill(new Color(0, 0, 0, 0.6));
		context.fillRect(slotX, slotY, slotSize, slotSize);

		// Render price
		context.setFont(Font.font(slotSize / 4));
		context.setFill(Color.YELLOW);
		context.setTextAlign(TextAlignment.CENTER);
		context.setTextBaseline(VPos.CENTER);
		context.fillText(item.getPrice() + "$", slotX + slotSize / 2, slotY + slotSize / 2);
		context.restore();
	}

	private void renderTooltip(GraphicsContext context, InventorySlot slot, Bounds slotBounds) {
		Item item = slot.getFirst();

		// Get the item name
		String itemName = item.getName();
		if (itemName == null || itemName.isEmpty()) itemName = item.getType().toString();

		// Calculate dimensions
		Font tooltipFont = new Font(14);
		Text text = new Text(itemName);
		text.setFont(tooltipFont);
		double tooltipWidth = text.getLayoutBounds().getWidth() + 2 * padding;
		double tooltipHeight = tooltipSize + 2 * padding;
		double tooltipX = slotBounds.getMinX() + (slotBounds.getWidth() - tooltipWidth) / 2;
		double tooltipY = slotBounds.getMinY();

		// Render background
		context.save();
		context.setFill(Color.BLACK);
		context.fillRoundRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight, 5, 5);
		// Render border
		context.setStroke(Color.WHITE);
		context.setLineWidth(1);
		context.strokeRoundRect(tooltipX, tooltipY, tooltipWidth, tooltipHeight, 5, 5);
		// Render text
		context.setFill(Color.WHITE);
		context.setTextAlign(TextAlignment.LEFT);
		context.setTextBaseline(VPos.TOP);
		context.setFont(tooltipFont);
		context.fillText(itemName, tooltipX + padding, tooltipY);
		context.restore();
	}

}