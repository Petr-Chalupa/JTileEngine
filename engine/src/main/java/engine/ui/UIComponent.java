package engine.ui;

import engine.gameobjects.GameObject;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;

public class UIComponent {
	protected final GameObject parent;
	protected final UIRegion region;
	protected final int layer;
	protected double width;
	protected double height;
	protected final double padding;

	public UIComponent(GameObject parent, UIRegion region, int layer, double width, double height, double padding) {
		this.parent = parent;
		this.region = region;
		this.layer = layer;
		this.width = width;
		this.height = height;
		this.padding = padding;
	}

	public GameObject getParent() {
		return parent;
	}

	public UIRegion getRegion() {
		return region;
	}

	public int getLayer() {
		return layer;
	}

	public double getPadding() {
		return padding;
	}

	public double calculateAvailableWidth(double regionWidth) {
		return Math.max(0, Math.min(regionWidth, regionWidth * width));
	}

	public double calculateAvailableHeight(double regionHeight) {
		return Math.max(0, Math.min(regionHeight, regionHeight * height));
	}

	public Bounds calculateBounds(Bounds regionBounds) {
		double regionWidth = regionBounds.getWidth();
		double regionHeight = regionBounds.getHeight();

		if (region == UIRegion.FLOAT) return new BoundingBox(0, 0, regionWidth, regionHeight);

		double availableWidth = calculateAvailableWidth(regionWidth);
		double availableHeight = calculateAvailableHeight(regionHeight);
		return centerBounds(regionBounds, new BoundingBox(0, 0, availableWidth, availableHeight));
	}

	public Bounds centerBounds(Bounds regionBounds, Bounds bounds) {
		double regionWidth = regionBounds.getWidth();
		double regionHeight = regionBounds.getHeight();

		double actualX = regionBounds.getMinX();
		double actualY = regionBounds.getMinY();
		switch (region) {
			case TOP_LEFT:
			case CENTER_LEFT:
			case BOTTOM_LEFT:
				actualX += padding;
				actualY += regionHeight - bounds.getHeight() - padding;
				break;
			case TOP_CENTER:
			case CENTER_CENTER:
			case BOTTOM_CENTER:
				actualX += (regionWidth - bounds.getWidth()) / 2;
				actualY += (regionHeight - bounds.getHeight()) / 2;
				break;
			case TOP_RIGHT:
			case CENTER_RIGHT:
			case BOTTOM_RIGHT:
				actualX += regionWidth - bounds.getWidth();
				actualY += regionHeight - bounds.getHeight();
				break;
		}

		return new BoundingBox(actualX, actualY, bounds.getWidth(), bounds.getHeight());
	}

	public void render(GraphicsContext context, double width, double height) {
	}
}
