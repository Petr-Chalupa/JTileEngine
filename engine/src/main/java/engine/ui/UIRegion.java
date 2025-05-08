package engine.ui;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public enum UIRegion {
	TOP_LEFT(0, 0, 0.25, 0.25),
	TOP_CENTER(0.25, 0, 0.5, 0.1),
	TOP_RIGHT(0.75, 0, 0.25, 0.25),
	CENTER_LEFT(0, 0.25, 0.25, 0.5),
	CENTER_CENTER(0.25, 0.1, 0.5, 0.8),
	CENTER_RIGHT(0.75, 0.25, 0.25, 0.5),
	BOTTOM_LEFT(0, 0.75, 0.25, 0.25),
	BOTTOM_CENTER(0.25, 0.9, 0.5, 0.1),
	BOTTOM_RIGHT(0.75, 0.75, 0.25, 0.25),
	FLOAT(0, 0, 0, 0);

	private final double x;
	private final double y;
	private final double width;
	private final double height;

	UIRegion(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public Bounds getBounds(double canvasWidth, double canvasHeight) {
		if (this == FLOAT) return new BoundingBox(0, 0, 0, 0);

		double x = this.x * canvasWidth;
		double y = this.y * canvasHeight;
		double width = this.width * canvasWidth;
		double height = this.height * canvasHeight;
		return new BoundingBox(x, y, width, height);
	}

}
